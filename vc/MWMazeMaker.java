package vc;

import resources.ModeTheme.MWTheme;
import resources.Theme;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import util.Goal;
import util.MouseInfo;
import util.Point;
import util.Util;
import maze.MWMaze;
import block.MWBlock;
import maze.MWMazeView;
import player.MWPlayer;
import player.MWPlayerView;
import gamemode.MazeWorld;


public class MWMazeMaker extends MazeMaker<MWMaze, MWMazeView, MWPlayer> {

	private MWPlayer player;
	private Group pv;
	protected MWBlock block_ex = new MWBlock();
	protected MWTheme theme;
	protected Goal goal;
	protected Group gview;
	
	public MWMazeMaker(MazeWorld mw, MazeMakerManager manager) {
		super(mw, manager);
		
		this.theme = mw.getTheme();
		this.maze = mw.getMaze().duplicate();
		
		addContent();
		addEvents();
		fixlayout();
	}
	
	protected void addContent() {
		super.initContent();
		
		imagebg.setImage(Theme.mw_themes[0].getBg());
		colorbg.setFill(Color.web(Theme.mw_themes[0].getBgColor()));
		
		goal = new Goal();
		GoalView gv = new GoalView(goal);
		mv = new MWMazeView();
		
		player = new MWPlayer("",null);
		player.setId(1);
		player.setTheme(theme.getPlayerThemes()[player.getId()]);
		pv = new Group(new MWPlayerView(player).getBall());
		
		gview = new Group(gv.getEvent(), gv.getBacknet(), gv.getGoal()); //gv.getGlow()
		
		this.getChildren().addAll(colorbg, imagebg, mv.getWallV(), pv, gview);
		this.getChildren().addAll(backbutton, savebutton, rotatebutton, deletebutton);
	}
	
	
	protected void addEvents() {
		super.initEvents();
		
		rotatebutton.setOnMouseClicked((e)->{
			maze.getBlock(row, col).rotate();
			maze.fixAround(row, col);
		});
		savebutton.setOnMouseClicked((e)->{
			maze.fix();
			mode.addMaze(maze.duplicate(), save_idx, replace);
			replace = true;
			manager.updateMazesGrid();
		});
		pv.setOnMouseReleased(m->{
			maze.change_start_pos(new Point(maze.getRow(m.getY()), maze.getCol(m.getX())));
			maze.setup(player);
		});
		mv.getWallV().setOnMouseClicked((m)->{		 //	mv.getWallsView()
			row = maze.getRow(m.getY());
			col = maze.getCol(m.getX());

			if (m.isSecondaryButtonDown() || m.isShiftDown())
				maze.getBlock(row, col).opposite();
			else 
				maze.getBlock(row, col).next();
			maze.fixAround(row, col);		
			maze.setup(goal);
		});
		
		gview.setOnMouseReleased(m->{	
			maze.change_goal_pos(new Point(maze.getRow(m.getY()), maze.getCol(m.getX())));
			maze.setup(goal);
		});
	}
	
	protected void fixlayout() {	
		super.fixlayout();
	}
	
	public void resize(int width, int height) {
		Point p_pos = player.get_pos();
		Point pos = new Point(maze.getRow(p_pos.getY()), maze.getCol(p_pos.getX()));
		
		super.resize(width, height);
		maze.setup(player, pos);
		maze.setup(goal);
		
		pv.setLayoutX(xmarg);
		pv.setLayoutY(ymarg);
		gview.setLayoutX(xmarg);
		gview.setLayoutY(ymarg);
	}
	
	@Override
	public void updateTheme() {
		super.help_updateTheme();
	}
	
	protected void showMaze(MWMaze mwmaze, int save_idx, boolean replace) {
		mwmaze.fix();
		mwmaze.setTheme(Theme.mw_themes[0].getMazeTheme());
		super.showMaze(mwmaze, save_idx, replace);
		
		mwmaze.setTheme(theme.getMazeTheme()); //default	
		player.setTheme(theme.getPlayerThemes()[player.getId()]);
		mwmaze.setup(player);
		mwmaze.setup(goal);
		
		pv.setLayoutX(xmarg);
		pv.setLayoutY(ymarg);
		gview.setLayoutX(xmarg);
		gview.setLayoutY(ymarg);
	}
	
	public void oldMaze(int maze_idx, int save_idx, boolean replace) {
		showMaze(mode.getMazes().get(maze_idx).duplicate(), save_idx, replace);
	}
	
	@Override
	public void newMaze(int rows, int cols, int save_idx) {
		showMaze(new MWMaze(MWMaze.create_maze(rows, cols, block_ex), new Point(0, 0), new Point(rows-1,cols-1)),save_idx, false);
	}
}
