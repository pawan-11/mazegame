package gamemode;

import java.util.ArrayList;
import resources.ModeTheme;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import util.Point;
import vc.GameMenu;
import javafx.util.Duration;
import maze.Maze;
import player.Player;
import strategy.Command.MoveCommand;
import util.KeyBoard;
import util.MouseInfo;
import util.Observable;

public abstract class GameMode<M extends Maze<?,P,?>, P extends Player<?>, MT extends ModeTheme<?,?>> extends Observable { 
	//computer strategies observe this gamemode to observe changes to players and mazes to change tactics
	
	protected int width, height, border = 0;
	protected int xmarg, ymarg;
	protected Timeline game_timer, countdown;
	protected int maze_idx = 0;
	protected ArrayList<KeyBoard> board_listeners = new ArrayList<KeyBoard>();
	protected ArrayList<MouseInfo> mouse_listeners = new ArrayList<MouseInfo>();
	protected M maze;
	protected ArrayList<M> mazes = new ArrayList<M>();
	protected ArrayList<P> players = new ArrayList<P>();
	protected int countdown_secs = 3; //countdown seconds
	protected MT theme;
	protected boolean celebrate;
	
	
	//TODO: add tutorial for each mode, 
	public GameMode() {
		
	}
	
	public void play() {
		setup();
		countdown.play();
	}
	
	
	public void addMaze(M maze, int idx, boolean replace) {
		
		if (idx < 0 || idx > mazes.size())
			return;
		else {
			if (replace) 
				mazes.remove(idx);
			mazes.add(idx, maze);
			
			//TODO: remove this v?
			if (idx <= maze_idx) //in case maze added is before the current
				switchMaze(maze_idx);
		}
	}

	
	public void deleteMaze(int idx) {
		if (idx > -1 && idx < getMazes().size()) {
			mazes.remove(idx); 	//save();
			if (!switchMaze(maze_idx))
				switchMaze(maze_idx-1);
		}
	}
	
	public void nextMaze() {
		if (switchMaze(maze_idx+1))
			countdown.play();
	}
	
	public void prevMaze() {
		switchMaze(maze_idx-1);
		countdown.play();
	}
	
	
	protected void addPlayer(P p) { 
		if (p.getType().equals("bot")) {
			players.add(p); //0,p
			changed("added player "+(players.size()-1));
		}
		else {//humans up front
			players.add(0,p);	
			changed("added player "+0);
		}
		 //adds new player view
	}
	
	public void removePlayers() {
		players.clear();
		board_listeners.clear();
		mouse_listeners.clear();	
		setCelebration(false);
		changed("players");
	}
	

	protected boolean switchMaze(int maze_idx) {
		if (!isValid(maze_idx)) return false;
		set_maze_idx(maze_idx);
		
		switchMaze(mazes.get(maze_idx));
		return true;
	}
	
	protected void switchMaze(M maze) {
		cleanUp();	
		this.maze = maze;
		maze.resize(getMazeWidth(width), getMazeHeight(height));		
		setup();
		
		xmarg = (width-maze.getWidth())/2;
		ymarg = (height-maze.getHeight())/2;
	}
	
	protected void init_timers() {
		countdown = new Timeline();
		countdown.setCycleCount(Animation.INDEFINITE);
		//countdown.setDelay(Duration.seconds(1));
		countdown.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e-> {
			if (GameMenu.show_running)
				System.out.println("countdown running");
			countdown_secs -= 1;
			changed("countdown");
		}));
		countdown.setOnFinished(f-> {
			countdown_secs = 0;
			start();
		});
	}
	
	public boolean isValid(int idx) {
		return idx < getMazes().size() && idx >= 0;
	}

	protected void help_resize(int width, int height) {
		this.width = width;
		this.height = height;	
		
		maze.resize(getMazeWidth(width), getMazeHeight(height));
		xmarg = (width-maze.getWidth())/2;
		ymarg = (height-maze.getHeight())/2;		
	}

	public KeyBoard addKeyboard(KeyCode[] codes) {
		for (KeyBoard board: board_listeners) {
			if (board.listensTo(codes)) //check if this keyboard listens to same keycodes as requested ones
				return board;
		}
		board_listeners.add(new KeyBoard(codes));
		return board_listeners.get(board_listeners.size()-1);
	}
	
	public void addMouse(MouseInfo mouse) {
		if (!mouse_listeners.contains(mouse))
			mouse_listeners.add(mouse);
	}
	
	public ArrayList<KeyBoard> getKeyboards() {
		return board_listeners;
	}
	
	public ArrayList<MouseInfo> getMouses() {
		return mouse_listeners;
	}
	
	protected void setCelebration(boolean celebrate) {
		this.celebrate = celebrate;
		changed("celebration");
	}
	
	public void setTheme(MT theme) {
		this.theme = theme;
	//	maze.setTheme(theme.getMazeTheme());
		
		//for (P p: players) {
			//p.setTheme(theme.getPlayerThemes()[p.getId()]);
		
		changed("theme");
	}
	
	protected void edit(MoveCommand cmd) {
		float final_x = cmd.getFinalPos().getX();
		float final_y = cmd.getFinalPos().getY();
		int radius = cmd.getRadius();

		if (final_y-radius < -ymarg+border)
			cmd.changeY(-ymarg+border+radius);
		else if (final_y+radius > height-ymarg-border)
			cmd.changeY(height-ymarg-border-radius);
		if (final_x+radius > width-xmarg-border)
			cmd.changeX(width-xmarg-border-radius);
		else if (final_x-radius < -xmarg+border)
			cmd.changeX(-xmarg+border+radius);
	}
	
	protected void set_maze_idx(int idx) {
		this.maze_idx = idx;
		changed("permissions");
	}
		
	protected void set_countdown(int secs) {
		countdown.stop(); 
		countdown_secs = secs;
		countdown.setCycleCount(secs);
		changed("countdown");
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
		super.create_list("strat", Integer.MAX_VALUE);
	}
	
	public boolean getCelebrate() {
		return celebrate;
	}
	
	public MT getTheme() {
		return theme;
	}
	
	public M getMaze() {
		return maze;
	}
	
	public ArrayList<P> getPlayers() {
		return players;
	}
	
	public ArrayList<M> getMazes() {
		return mazes;
	}
	
	public M getMaze(int maze_idx) {
		return mazes.get(maze_idx);
	}
	
	public int getCountdown() {
		return countdown_secs;
	}
	
	public int getBorder() {
		return border;
	}
	
	public float getXmarg() {
		return xmarg;
	}
	
	public float getYmarg() {
		return ymarg;
	}
	
	public int get_mazes_size() {
		return getMazes().size();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public abstract void resize(int width, int height);
	public abstract void start();
	public abstract void cleanUp();
	protected abstract void setup();
	public abstract void pause();
	public abstract void resume();
	public abstract void save();
	public abstract String getFileName();
	public abstract int getMazeWidth(int width);
	public abstract int getMazeHeight(int height);
	public abstract MediaPlayer getMusic();
}