package gamemode;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;

import block.MWBlock;
import fileIO.MWCreator;
import resources.ModeTheme.MWTheme;
import resources.Music;
import resources.Theme;
import strategy.MWStrategy.MWMouseStrategy;
import strategy.Command.MoveCommand;
import strategy.MWStrategy;
import util.Clock;
import util.Goal;
import vc.GameMenu;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.MediaPlayer;
import util.Point;
import util.Util;
import javafx.util.Duration;
import maze.MWMaze;
import player.MWPlayer;


public class MazeWorld extends GameMode<MWMaze, MWPlayer, MWTheme>  { //cam add more generic paramters, Theme, Strategy

	//TODO: make players move once/twice, to set them up in their starting position? yes

	private int max_maze_idx = 0, score = 0;
	private Clock clock;
	private String report; //= "Put the ball in the net!";
	private Timeline score_timer;
	private MWCreator mwCreator;
	private Goal goal;

	public MazeWorld() {
		clock = new Clock();
		goal = new Goal();

		init_timers();
		
		mwCreator = new MWCreator(this);
		mwCreator.parse();

		addPlayer(new MWPlayer("human", new MWMouseStrategy(this), 1));
		if (GameMenu.pilot_mode)
			addPlayer(new MWPlayer("bot", new MWStrategy.MWBfsStrategy(this), 0));	
		
		setup();	
	}

	protected void setup() {
		cleanUp();
		
		maze.setup(players); //put players in position
		maze.setup(goal); //put goal in position
		
		changed("setted up");
		
		clock.setTime(getMaxTime());		
		set_countdown(3);
	}

	public void start() {
		game_timer.play();
		clock.play();
	}

	public void pause() {
		setCelebration(false);
		countdown.pause();
		autoswitch.stop();
		
		game_timer.pause();
		clock.pause();
	}

	public void cleanUp() {
		pause();
	}

	public void resume() {
		if (!maze.reactTo(players))	{	//if not allready finished, can be used if pausing to settings
			if (countdown_secs > 0)
				countdown.play();
			else
				start();
		}
	}

	public void addPlayer(MWPlayer p) {	
		p.setTheme(theme.getPlayerThemes()[p.getId()%theme.getPlayerThemes().length]);
		super.addPlayer(p);
		maze.setup(p);
	}


	public void addMaze(MWMaze maze, int idx, boolean replace) {
		super.addMaze(maze, idx, replace);
	}


	private MWPlayer p;
	private MoveCommand cmd;
	
	protected void init_timers() {
		super.init_timers();
		
		game_timer = new Timeline();
		game_timer.setCycleCount(Animation.INDEFINITE);
		game_timer.getKeyFrames().add(new KeyFrame(Duration.millis(GameMenu.frame_rate), e-> {

			//idea: execute knock cmds in another loop
			if (GameMenu.show_running)
				System.out.println("mw running");

			for (int p_idx = 0; p_idx < players.size(); p_idx++) { //human to bot
				p = players.get(p_idx);

				if (p.isDisabled()) 
					continue;

				if (goal.reactTo(p)) {
					p.setDisabled(true);
					if (p.getType().equals("human") || GameMenu.pilot_mode)
						maze_completed();
					break;
				}
				
								
				for (int y = 0; y < players.size(); y++) { 
					if (y == p_idx || players.get(y).isDisabled())
						continue;
				
					if (p.reactTo(players.get(y))) { //in each other
						cmd = p.getMoveCmd(p.get_pos().deg_ang(players.get(y).get_pos())+180);  //knock_cmd
												
						maze.edit(cmd); //knock p back
						super.edit(cmd); 
						cmd.execute(p);
						
						p.setknocked(true);
						players.get(y).setknocked(true); //could have a better design solution
						//do not want to get knocked back by same player twice, or cant go forward
					}
				}
				
				cmd = p.getMoveCmd(); //player moves
				maze.edit(cmd); //stops on walls
				super.edit(cmd); //stops on mode play area bounds			    
				
				if (p.isKnocked() && p.getType().equals("human")) { //slide past if it got knocked
					p.slideMove(cmd); //slides if it hit wall
					maze.edit(cmd);
					super.edit(cmd);
					p.setknocked(false); 
				}
			
				cmd.execute(p);
			}
		}));

		score_timer = new Timeline();
		score_timer.getKeyFrames().add(new KeyFrame(Duration.millis(80), e->{
			if (GameMenu.show_running)
				System.out.println("score running");
			score += 1;
			changed("score");
		}));
		score_timer.setOnFinished(e->{
			changed("maze_idx");
		});
		
		autoswitch = new Timeline();
		autoswitch.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e->{		
		}));
		autoswitch.setOnFinished(e->{
			this.nextMaze();	
		});
		autoswitch.setCycleCount(3);
	}
	
	private Timeline autoswitch;

	protected void switchMaze(MWMaze mwmaze) { //when we add a maze, set its theme?, or the null blocks are in  mazemakers!!
		autoswitch.stop(); //to avoid switching again if user pressed next or back
		
		super.switchMaze(mwmaze);		

		setReport("Put the ball in the goal!");
		setCelebration(false);

		double divider = (int)Math.ceil((double)this.get_mazes_size()/Theme.mw_themes.length);		
		setTheme(Theme.mw_themes[(int)(maze_idx/divider)]);
		changed("maze");
	}


	private void maze_completed() 	{
		pause();
		int seconds = clock.getSeconds();
		String report = getReport(seconds);
		
		if (can_count_score()) {
			score_timer.setCycleCount(seconds);
			score_timer.play();
			clock.fastplay();
			report += " [+"+seconds+"]";
			max_maze_idx += 1; //done right here, in case user exits before score timer finishes
			if (seconds == 0) changed("maze_idx");  //TODO: is this needed
		}
		
		if (GameMenu.pilot_mode)
			autoswitch.play();
		setReport(report);
		setCelebration(true);
	}
	

	public void resize(int width, int height) {
		ArrayList<Point> positions = maze.getPositions(players);	
		border = (int)(0.02*height); //change to getbordermargin

		super.help_resize(width, height);
		maze.setup(players, positions);
		maze.setup(goal);
		goal.reactTo(players);
		changed("resized");
	}

	public void setTheme(MWTheme theme) {
		super.setTheme(theme);
		maze.setTheme(theme.getMazeTheme());

		for (MWPlayer p: players) {
			p.setTheme(theme.getPlayerThemes()[p.getId()%theme.getPlayerThemes().length]);
		}
	}

	public void loadProgress(int max_maze_idx, int score) { //load tutorial boolean.
		this.max_maze_idx = max_maze_idx;
		this.score = score;

		if (this.get_mazes_size() == 0) {
			this.maze = new MWMaze(MWMaze.create_maze(5, 5, new MWBlock(true, true, true, true)), new Point(0,0), new Point(4,4));
			maze.fix();
			set_maze_idx(0);
			switchMaze(maze);
		}
		else {
			if (max_maze_idx >= mazes.size()) {
				max_maze_idx = mazes.size();
				switchMaze(mazes.size()-1);
			}
			else {
				if (max_maze_idx < 0) { 
					max_maze_idx = 0;
				}	
				switchMaze(max_maze_idx);
			}
		}
		changed("score");
	}

	public boolean can_count_score() {
		return maze_idx == max_maze_idx;
	}

	private void setReport(String report) {
		this.report = report;
		changed("report");
	}

	@Override
	public String getFileName() {
		return "mazeworld.txt";
	}

	public int getScore() {
		return score;
	}

	public int getMaxMazeIdx() {
		return max_maze_idx;
	}

	public boolean[] getPermissions() {
		boolean[] perms = {false, false};
		perms[0] = this.maze_idx>0;
		perms[1] = !can_count_score() && maze_idx < this.get_mazes_size()-1;
		return perms;
	}

	public int getMazeIdx() {
		return maze_idx;
	}

	public Clock getClock() {
		return clock;
	}

	public String getReport() {
		return report;
	}

	public boolean getCelebrate() {
		return celebrate;
	}

	@Override
	public void save() {
		try {
			FileWriter writer = mwCreator.getWriter();
			//if (writer == null) return;
			max_maze_idx = Math.min(max_maze_idx, this.get_mazes_size());
			max_maze_idx = Math.max(max_maze_idx, 0);

			writer.write(getMaxMazeIdx()+","+getScore()+"\n");
			for (MWMaze m: mazes) {
				if (!m.save(writer)) System.out.println("maze world save failed");
			}
			writer.close();
		}
		catch (Exception io) {
			System.out.println("Save MazeWorld Error");
			io.printStackTrace();
		}
	}

	private String poormsgs[] = {"You did it", "You solved the maze", "Decent finish",
			"Well done", "Good job", "Not bad"};
	private String okmsgs[] = {"Great job", "You are fast", "GOAL!",
			"Diego Costaaa", "Benzemaaa", "Great!", "Nailed it!"};
	private String goodmsgs[] = {"RONALDO!", "MESSI!", "NEYMAR!",
			"SPEED OF LIGHT!", "AUBAMEYANG!", "MORTIS!", "LEBRON JAMES!", "LAMBORGHINI!", "AMAZING!"};

	private String getReport(int seconds) {
		String msg;
		if (seconds > getMaxTime()/3)
			msg = goodmsgs[(int)(Math.random()*goodmsgs.length)];
		else if (seconds > getMaxTime()/4)
			msg = okmsgs[(int)(Math.random()*okmsgs.length)];
		else 
			msg = poormsgs[(int)(Math.random()*poormsgs.length)];
		return msg;
	}

	public Goal getGoal() {
		return goal;
	}

	private int getMaxTime() {
		return (int)((maze.getRows()+maze.getCols())*1.5);
	}

	public int getMazeWidth(int width) {
		//return width;
		return (int)(0.85*width); //0.75
	}

	public int getMazeHeight(int height) {
	//	return height;
		return (int)(0.8*height); //0.7
	}

	@Override
	public MediaPlayer getMusic() {
		return Music.mw_music;
	}
	
}

