package gamemode;

import java.io.FileWriter;
import java.util.ArrayList;
import block.WarBlock;
import fileIO.WarCreator;
import resources.ModeTheme.WarTheme;
import resources.Music;
import resources.Theme;
import strategy.WarStrategy;
import strategy.Command.AttackCommand;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;
import util.Clock;
import util.KeyBoard;
import util.Observer;
import vc.GameMenu;
import vc.Settings;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import util.Point;
import util.Util;
import javafx.util.Duration;
import maze.WarMaze;
import player.Bullet;
import player.Powerup;
import player.WarPlayer;


public class MazeWar extends GameMode<WarMaze, WarPlayer, WarTheme> implements Observer { 


	private int max_maze_idx = 0, score = 0;
	private Clock clock;
	private WarCreator warCreator; //warcreator
	//private ArrayList<WarPlayer> bots;
	private ArrayList<Bullet> bullets;
	private WarPlayer lastMoved; //keeps track of most recently moved player, to draw trail
	
	private WarPlayer first_player;
	private int humans = 0; //all humans, disabled or not
	private Point explosion = new Point(); //an explosion occured at this point
	
	private ArrayList<Powerup> powerups;
	
	public MazeWar() {
		super();
		
		clock = new Clock();
		bullets = new ArrayList<Bullet>();
		powerups = new ArrayList<Powerup>();
		
		init_timers();
		
		warCreator = new WarCreator(this);
		warCreator.parse();
		//loadProgress(0, 0);
		
		addPlayer(new WarPlayer("human", new WarStrategy.WarMouseStrategy(this), 1));
		
		setup();	
	}

	protected void setup() {
		cleanUp();	
		setup(players); //put players in position	
		
		changed("setted up");
		set_countdown(3);
	}

	public void start() {
		changed("started"); //let mouse strat know
		game_timer.play();
		clock.play();		
	}

	public void pause() {
		//setCelebration(false); 	TODO	
		countdown.pause(); //pause();
		game_timer.pause();
		clock.pause();
	}

	public void cleanUp() {
		pause();
		bullets.clear();
		changed("bullets");
	//	changed("teleported players"); //to delete drawings
	}

	public void resume() {
		if ((countdown.getStatus().compareTo(Status.PAUSED) == 0 ||
				countdown.getStatus().compareTo(Status.STOPPED) == 0) && countdown_secs > 0) {
			set_countdown(3);
			countdown.play();
		}
		else
			start();
	}
	
	public void addPlayer(WarPlayer p) {	
		setup(p);
		super.addPlayer(p);
		
		if (p.getType().equals("human")) {
			humans++;
			changed("humans alive"); //to let bots know of the new enemy human
		}		
		
		//TODO: remove
		if (p.getType().equals("human")) {
			first_player = p;
			KeyBoard speedboard = this.addKeyboard(speedkeys);	
			speedboard.clearObservers();
			speedboard.addObserver("mazewar", this);
		}
	}
	
	public void removePlayers() { //TODO: remove strategy observers of these players
		humans = 0;
		if (maze_idx < max_maze_idx)
			super.removePlayers(); //remove all players, and let user do whatever they want with this completed maze
		else {	
			removeHumans();
			removeExcessBots(); //dont remove the bots required to complete the maze
		}
	}
	
	public void removeHumans() {
		humans = 0;
		int i = 0;
		while (i < players.size()) {
			if (players.get(i).getType().equals("human")) {
				players.remove(i);
				changed("removed player "+i); 
			}
			else
				i++;
		}
		
		board_listeners.clear();
		mouse_listeners.clear();
		changed("humans alive");
	}
	
	private void addMinimumBots() {
		for (int i = getBots(); i < getBots(maze_idx); i++)
			addPlayer(new WarPlayer("bot", new WarStrategy.WarBfsStrategy(this), 0));	
			
	}
	
	private int removeExcessBots() {
		//theres players.size()-humans bots
		int bots_left = getBots();
		int i = 0;
		while (i < players.size() && bots_left > this.getBots(getMazeIdx())) { //
			if (players.get(i).getType().equals("bot")) {
				players.remove(i);
				changed("removed player "+i);
				bots_left--;
			}				
			else
				i++;
		}
		return bots_left;
	}
	
	public void addBullet(Bullet b) {
		setup(b);
		bullets.add(b);
		changed("added bullet");
	}
	
	private void removeBullet(int i) {
		bullets.remove(i);
		changed ("removed bullet "+i); 
	}
	
	private void addPowerup(Powerup p) {
		powerups.add(p);
		changed("added powerup");
	}
	
	private void removePowerup(int i) {
		powerups.remove(i);
		changed("removed powerup "+ i);
	}
	
	public void addMaze(WarMaze maze, int idx, boolean replace) {
		super.addMaze(maze, idx, replace);
	}

	protected void switchMaze(WarMaze warmaze) {	
		super.switchMaze(warmaze);
		
		double divider = (double)this.get_mazes_size()/Theme.war_themes.length;	
		setTheme(Theme.war_themes[(int)(maze_idx/divider)]);
		
		removeExcessBots();
		addMinimumBots();
		
		setup(players);
		
		changed("maze");
	}
	
	private void setup(ArrayList<WarPlayer> players) {
		for (WarPlayer p: players)
			setup(p);		
		changed("humans alive"); //for humans are revived, let bots know
	}
	
	private void setup(WarPlayer p) { //setup to play in mazewar
		if (p.getType().equals("bot")) {
			p.setMaxHealth(maze_idx*50+GameMenu.bot_health); 
			p.setBulletLimit(maze_idx+GameMenu.bot_ammo);
		}
		else if (p.getType().equals("human")) {
			p.setMaxHealth(maze_idx*50+GameMenu.human_health); 
			p.setBulletLimit(maze_idx+GameMenu.human_ammo);
		}
		p.setRecoverTime(health_gap*3); //so doesnt heal right away after taking damage
		
		maze.setup(p);
		p.setTheme(theme.getPlayerThemes()[p.getId()%theme.getPlayerThemes().length]); 
		//^ does when switching maze, and setting theme as well, could remove it from there
	}
	
	private void setup(Bullet b) {
		b.setTheme(Theme.bullet_theme);  //todo: could add bullet theme in wartheme
		b.setBulletDamage(GameMenu.bullet_damage);
		b.setMoveLimit((maze.getRows()*maze.getCols()));
	}
	
	private WarPlayer p;
	private Bullet b;
	private Powerup powerup;
	private MoveCommand m_cmd;
	private RotateCommand r_cmd;
	private AttackCommand<WarPlayer, MazeWar> a_cmd;
	
	private int health_counter = 0, reload_counter = 0, powerup_counter = 0,
			reload_gap = 50, health_gap = 50, powerup_gap = 400; 
	//smaller the gaps, the faster players regenerate health/reload
	private Timeline aftermath_timer;

	
	protected void init_timers() {
		super.init_timers();
		
		game_timer = new Timeline();
		game_timer.setCycleCount(Animation.INDEFINITE);
		game_timer.getKeyFrames().add(new KeyFrame(Duration.millis(GameMenu.frame_rate), e-> {

			//idea: execute knock cmds in another loop
			int i;
			for (i = 0; i < players.size(); i++) { //human to bot
				p = players.get(i);

				if (p.isDisabled()) 
					continue;
				
				int j;			
				for (j = 0; j < players.size(); j++) { 
					if (j == i || players.get(j).isDisabled())
						continue;
				
					if (p.reactTo(players.get(j))) { //in each other
						m_cmd = p.getMoveCmd(p.get_pos().deg_ang(players.get(j).get_pos())+180);  //knock_cmd
						maze.edit(m_cmd); //knock p back
						super.edit(m_cmd); 
						m_cmd.execute(p);
					}
				}
				
				m_cmd = p.getMoveCmd(); //player moves
				maze.edit(m_cmd); //stops on walls
				super.edit(m_cmd); //stops on bounds			    
				m_cmd.execute(p);
				
				r_cmd = p.getRotateCmd();
				r_cmd.execute(p);
				
				/*p.setHealth(p.getHealth()-1);
				if (p.getHealth() == 0)
					p.revive();
				p.setAmmo(p.getAmmo()-1);
				if (p.getAmmo() == 0)
					p.reload();
				*/
				a_cmd = p.getAttackCmd();
				a_cmd.execute(p, this);

				//award passive player heatlh
				if (health_counter == 0 && p.getAmmo() >= p.getBulletLimit()/3) {
					p.heal(0.05);
				}
				if (reload_counter == 0) {
					p.reload(0.15);
				}
				p.incrementRecoverCounter();
				
				lastMoved = p;
				if (GameMenu.war_draw)
					changed("draw"); //very cpu consuming feature drawing is
				//maze.reactTo(p);
				
				for (j = 0; j < bullets.size(); j++) { //check if any bullet hit this player p
					if (!(b = bullets.get(j)).isDisabled() && p.reactTo(b)) {
						b.setDisabled(true);
						p.setHealth(p.getHealth()-b.getDamage());
							
						if (p.isDisabled()) {
							is_game_over();
							if (p.getType().equals("human"))
								changed("humans alive"); //for bot strats
						}
					}
				}
				
				j = 0;
				while (j < powerups.size()) { //check if p collected any powerup
					powerup = powerups.get(j);
					if (powerup.reactTo(p)) {
						//TODO: could add some visual effects to player
					}
					j++;
				} 
			}	
			
			i = 0;
			while (i < bullets.size()) { //bullet commands
				b = bullets.get(i);
				if (b.isDisabled())
					removeBullet(i);
				else {
					i++;		
					m_cmd = b.getMoveCmd();
					maze.edit(m_cmd); //stops on walls
					super.edit(m_cmd); //stops on bounds			    
					m_cmd.execute(b);
				}
			}
			
			
			
			health_counter = (health_counter+1)%health_gap;
			reload_counter = (reload_counter+1)%reload_gap;
			powerup_counter = (powerup_counter+1)%powerup_gap;
		}));
		
		aftermath_timer = new Timeline();
		aftermath_timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e->{	
		}));
	}
	
	private boolean is_game_over() 	{
		if (getPlayersAlive("bot") == 0) {
				
			if (can_count_score()) {
				max_maze_idx += 1;
				aftermath_timer.setOnFinished(e->{
					cleanUp(); //pauses and clears bullets
					setCelebration(true);
					changed("maze_idx");
				}); 
			}
			else {
				aftermath_timer.setOnFinished(e->{
					setCelebration(true);
				});
			}
			aftermath_timer.setCycleCount(2);
			aftermath_timer.play();
			return true;
		}
		else if (getPlayersAlive("human") == 0) { //TODO: track the amount of bots humans killed
			aftermath_timer.setOnFinished(e->{
				play(); //setup and countdown
			});
			aftermath_timer.setCycleCount(2);
			aftermath_timer.play();
			return true;
		}
		return false;
	}

	public void resize(int width, int height) {
		ArrayList<Point> p_positions = maze.getPositions(players);	
		ArrayList<Point> b_positions = maze.getPositions(bullets);		
		
		border = (int)(0.04*height); //change to getbordermargin

		super.help_resize(width, height);
		maze.setup(players, p_positions);
		maze.moveBullets(bullets, b_positions);
		
		maze.resize(this.getMazeWidth(width), this.getMazeHeight(height));
		changed("resized");
	}

	public void setTheme(WarTheme theme) {

		super.setTheme(theme);
		maze.setTheme(theme.getMazeTheme());

		for (WarPlayer p: players) {
			p.setTheme(theme.getPlayerThemes()[p.getId()%theme.getPlayerThemes().length]);
		}
		for (Bullet b: bullets)
			b.setTheme(Theme.bullet_theme);
		
	
	}

	public void loadProgress(int max_maze_idx, int score) { //load tutorial boolean.
		this.max_maze_idx = max_maze_idx;
		this.score = score;
		
		if (this.get_mazes_size() == 0) {
			this.maze = new WarMaze(WarMaze.create_maze(6, 6, new WarBlock("", true, true, false, false)));
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

	//return minimum number of bots that should be in this maze
	private int getBots(int maze_idx) {
		return (int)(2*Math.log(maze_idx+1)+1);
	}
	
	private int getBots() { //bots in list of players in this mazewar, alive or dead 
		return players.size()-humans; 
	}
	
	private int getPlayersAlive(String type) {
		int alive = 0;
		for (WarPlayer p: players)
			if (p.getType().equals(type) && !p.isDisabled())
				alive++;
		return alive;
	}
	
	public boolean can_count_score() {
		return maze_idx == max_maze_idx;
	}
	
	@Override
	public String getFileName() {
		return "mazewar.txt";
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
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
		perms[1] = maze_idx < mazes.size()-1 && maze_idx < max_maze_idx;
		return perms;
	}

	public int getMazeIdx() {
		return maze_idx;
	}

	public Clock getClock() {
		return clock;
	}
	
	public WarPlayer getLastMoved() {
		return lastMoved;
	}
	
	@Override
	public void save() {
		
		try {
			FileWriter writer = warCreator.getWriter();
			//if (writer == null) return;
			max_maze_idx = Math.min(max_maze_idx, this.get_mazes_size());
			max_maze_idx = Math.max(max_maze_idx, 0);
			writer.write(getMaxMazeIdx()+","+getScore()+"\n");
			for (WarMaze m: mazes) {
				if (!m.save(writer)) System.out.println("war maze save failed");
			}
			writer.close();
		}
		catch (Exception io) {
			System.out.println("Save MazeWar Error");
			io.printStackTrace();
		}
		
	}
	

	public int getMazeWidth(int width) {
		return (int)(0.8*width);
	}

	public int getMazeHeight(int height) {
		return (int)(0.7*height);
	}

	@Override
	public MediaPlayer getMusic() {
		return Music.war_music;
	}

	KeyCode[] speedkeys = {KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.R};
	@Override
	public void update(String msg) {
		int x = Integer.parseInt(msg);
		if (x == speedkeys.length-1)
			first_player.resetSpeed();
		else {
			//first_player.multiplySpeed(x);
			if (x == 0)
				first_player.addSpeed(-0.5);
			else 
				first_player.addSpeed(0.5);
		}
			
	}

	
}

