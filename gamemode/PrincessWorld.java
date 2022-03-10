package gamemode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import block.PWBlock;
import fileIO.PWCreator;
import resources.ModeTheme.PWTheme;
import resources.Music;
import resources.Theme;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.scene.media.MediaPlayer;
import util.Point;
import javafx.util.Duration;
import maze.PWMaze;
import player.Princess;
import player.MWPlayer;
import player.PWPlayer;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;
import strategy.PWStrategy;
import vc.GameMenu;

public class PrincessWorld extends GameMode<PWMaze, PWPlayer, PWTheme> {

	private String report = "";
	private PWCreator pwCreator; 
	private boolean prin_hiding, hints_enabled = false;
	private Timeline msgs_timer, found_timer, celebration_timer;
	private PWPlayer winner, attention_player; //the player princess talks about
	private Princess prin;
	//private boolean on_settings = false; //TODO: use, blacken everything other than settings, add another settings button

	
	public PrincessWorld() {
		maze = new PWMaze(PWMaze.create_maze(4, 4, new PWBlock("closed")));
		prin = new Princess();
		
		init_timers();
		pwCreator = new PWCreator(this);
		pwCreator.parse();
		
		addPlayer(new PWPlayer("human", new PWStrategy.PWMouseStrategy(this), 1), false);	
		//addPlayer(new PWPlayer("bot", new PWStrategy.PWFwStrategy()), true);	
		
		setup();
	}


	public void setTheme(PWTheme theme) { //////MOVE SOME PARTS TO PARENT CLASS

		super.setTheme(theme);
		maze.setTheme(theme.getMazeTheme());
		
		for (PWPlayer p: players) {
			p.setTheme(theme.getPlayerThemes()[p.getId()]);
		}
	}
	
	@Override
	public void resize(int width, int height) {
		ArrayList<Point> positions = maze.getPositions(players);
		Point prin_pos = maze.getPos(prin.get_pos());
		
		border = width < height?(int)(0.04*width):(int)(0.04*height);
		
		super.help_resize(width, height); //resize maze
		
		maze.setup(players, positions);
		maze.setup(prin, prin_pos);
		
		setCelebration(celebrate); //resize it
		changed("resized");
	}

	protected void switchMaze(PWMaze pwmaze) {	
		super.switchMaze(pwmaze);
		
		setReport("Princess: Can you find me?");
		double divider = (int)Math.ceil((this.get_mazes_size()+1)/Theme.pw_themes.length);
		setTheme(Theme.pw_themes[(int)(maze_idx/divider)]);
		changed("maze");
	}

	protected void setup() {
		cleanUp();
		
		maze.setup(prin);
		maze.setup(players);
		prin_hiding = true;
		
		changed("setted up");
		changed("hiding"); //does princess layout to hide               
		
		set_countdown(3);
	}
	
	@Override
	public void start() {
		game_timer.play();
		msgs_timer.play();
	}
	
	@Override
	public void pause() {
		countdown.pause();
		game_timer.stop();	
		celebration_timer.stop();
		msgs_timer.stop();
		found_timer.stop();
	}
	
	@Override
	public void resume() {
		if (prin_hiding) { //!prin.reactTo(players)) {
			if ((countdown.getStatus().compareTo(Status.PAUSED) == 0 ||
					countdown.getStatus().compareTo(Status.STOPPED) == 0) && countdown_secs > 0) {
				set_countdown(3);
				countdown.play();
			}
			else
				start();
			
			setReport("Let's play");
		}
	}
	
	public void cleanUp() { //called when this princess world is being left, or maze is being left. close open doors etc. 
		//TODO:(open all doors that were closed)? i.e. change each block back to its fix_type, can be useful in battle mode
		pause();
		maze.closeDoorsTo(players); 
		
		setCelebration(false);
		setReport("You will not be able to find me!");
	}
	
	
	private PWPlayer p; //heap over stack
	private MoveCommand cmd;
	private RotateCommand r_cmd;
	
	protected void init_timers() {
		super.init_timers();
		
		game_timer = new Timeline();
		game_timer.setCycleCount(Animation.INDEFINITE);
		game_timer.getKeyFrames().add(new KeyFrame(Duration.millis(GameMenu.frame_rate), e-> { 

			if (GameMenu.show_running)
				System.out.println("pw running");
			
			for (int p_idx = 0; p_idx < players.size(); p_idx++) {
				p = players.get(p_idx);
				
				
				if (prin.reactTo(p)) {		
					winner = p;
					maze_completed();
					break;
				}
			    
				for (int y = 0; y < players.size(); y++) {
					if (y == p_idx)
						continue;
					if (p.reactTo(players.get(y))) {
						cmd = p.getMoveCmd(p.get_pos().deg_ang(players.get(y).get_pos())+180); 
						maze.edit(cmd); //knock p back
						super.edit(cmd); 
						cmd.execute(p);
					}
				}	
				
				cmd = p.getMoveCmd();
				maze.edit(cmd);
				edit(cmd);
			    cmd.execute(p);
			    
				r_cmd = p.getRotateCmd();
				r_cmd.execute(p);
				
				maze.reactTo(p); //open door close door etc.
			}
		}));
		
		msgs_timer = new Timeline();
		msgs_timer.setCycleCount(Animation.INDEFINITE);
		int duration = 4;
		if (hints_enabled)
			duration = 2;
		msgs_timer.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), e-> {	
			if (GameMenu.show_running)
				System.out.println("msgs timer running");
			if (attention_player != null)
				setReport(getMsg(attention_player));
		}));
		
		
		found_timer = new Timeline();
		found_timer.setCycleCount(40);	
		found_timer.setDelay(Duration.seconds(1));
		found_timer.getKeyFrames().add(new KeyFrame(Duration.millis(50), e-> {
			if (GameMenu.show_running)
				System.out.println("found timer running");
			changed("hiding");
			prin.resize(prin.getSize().multiply(1.02));
		}));
		found_timer.setOnFinished(e->{
			
			if (winner.getType().equals("human")) {
				setCelebration(true);
				celebration_timer.setCycleCount((maze.getRows()+maze.getCols())/4);
			}
			else {
				celebration_timer.setCycleCount((maze.getRows()+maze.getCols())/8);
			}
			celebration_timer.play();
			msgs_timer.play();
		});
		
		celebration_timer = new Timeline();
		celebration_timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e->{
			if (GameMenu.show_running)
				System.out.println("celebration timer running");
		}));
		celebration_timer.setOnFinished(k->{
			maze.closeDoorsTo(players);
			setCelebration(false);
			play();
		});
	}
	
	public void maze_completed() { //p found the princess , change to private
		pause();
		prin_hiding = false;
		
		setReport(getMsg(winner));
		
		found_timer.play();
		msgs_timer.stop();
	}
	
	public void addPlayer(PWPlayer p, boolean all_new) {
		p.setTheme(theme.getPlayerThemes()[p.getId()]);
		super.addPlayer(p);
		
		if (all_new)
			maze.setup(players);
		else
			maze.setup(p);
		
		attention_player = players.get(players.size()-1);
	}
	
	public void removePlayers() {
		maze.closeDoorsTo(players); //close open doors
		super.removePlayers();
	}
	
	
	public boolean[] getPermissions() {
		boolean[] perms = {false, false};
		perms[0] = this.maze_idx > 0;
		perms[1] = maze_idx < this.get_mazes_size()-1;
		return perms;
	}
	
	public void set_hints(boolean hints_enabled) {
		this.hints_enabled = hints_enabled;
	}
	
	public void loadProgress(int maze_idx) {
	//	System.out.println("LOADED + "+maze_idx + " before maze_idx: "+maze_idx);
		
		if (this.get_mazes_size() == 0) {
			this.maze = new PWMaze(PWMaze.create_maze(5, 5, new PWBlock()));
			maze.fix();
			set_maze_idx(0);
			switchMaze(maze);
		}
		else if (maze_idx < 0) { 
			switchMaze(0);
		}
        else if (maze_idx >= mazes.size())
        	switchMaze(mazes.size()-1);
		else
			switchMaze(maze_idx);
		
		changed("score");
	}
	
	
	@Override
	public void save() {
		try {
			FileWriter writer = pwCreator.getWriter();
			if (writer == null || this.get_mazes_size() == 0) return;
			maze_idx = Math.min(maze_idx, this.get_mazes_size()-1);
			maze_idx = Math.max(maze_idx, 0);
			writer.write(maze_idx+"\n");
			for (PWMaze m: mazes) {
				if (!m.save(writer)) System.out.println("princess world save failed");
			}
			writer.close();
		}
		catch (IOException io) {
			System.out.println("Save PrincessWorld Error");
			io.printStackTrace();
		}
	}
	
	private String enter_msgs[] = {"Welcome to my castle", "Hi there"};
	private String close_msgs[] = {"You are close!", "You are near"};
	private String far_msgs[] = {"You are not close", "You are far away"};
	private String taunt_msgs[] = {"I am the best at hiding", "You cannot find me", "I am in a good hiding spot"};
	private String found_msgs[] = {	"You found me!","Well done", "You are a good seeker"};
	private String bot_found_msgs[] = {	"Uh oh! the Bot found me!","Unlucky", "Better luck next time", "Let's play again"};

	
	private String getMsg(PWPlayer p) {
		String msg;
		
		int distance = getDistance(p, prin);

		if (!maze.isInMaze(p.get_pos())) //p is outside of maze
			msg = enter_msgs[(int)(Math.random()*enter_msgs.length)];
		else if (!prin_hiding)
			if (p.getType().equals("human"))
				msg = found_msgs[(int)(Math.random()*found_msgs.length)];
			else
				msg = bot_found_msgs[(int)(Math.random()*bot_found_msgs.length)];
		else if (hints_enabled) {
			if (distance < (maze.getRows()+maze.getCols())/4)
				msg = close_msgs[(int)(Math.random()*close_msgs.length)];
			else 
				msg = far_msgs[(int)(Math.random()*far_msgs.length)];
		}
		else 
			msg = taunt_msgs[(int)(Math.random()*taunt_msgs.length)];
		
		return msg;
	}

	private int getDistance(PWPlayer p, Princess prin) {
	//	Point prin_pos = ;
	//	Point p_pos = maze.getPos(p.get_pos());
		return (int)maze.getPos(prin.get_pos()).distance(maze.getPos(p.get_pos()));
	}
	
	
	public void setReport(String report) {
		this.report = report;
		changed("report");
	}
	
	@Override
	public String getFileName() {
		return "princessworld.txt";
	}
	
	public int getMazeIdx() {
		return maze_idx;
	}
	
	public Princess getPrincess() {
		return prin;
	}
	
	public String getReport() {
		return report;
	}
	
	public boolean isPrinHiding() {
		return prin_hiding;
	}
	
	public PWPlayer getAttPlayer() {
		return attention_player;
	}
	
	public PWPlayer getWinner() {
		return winner;
	}
	
	public int getMazeWidth(int width) {
		return (int)(0.8*width);
	}
	
	public int getMazeHeight(int height) {
		return (int)(0.75*height);
	}
	
	@Override
	public MediaPlayer getMusic() {
		return Music.pw_music;
	}

}
