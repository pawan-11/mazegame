package vc;

import resources.Images;
import resources.ModeTheme;
import resources.Theme;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import maze.Maze;
import block.Block;
import gamemode.GameMode;
import maze.MazeView;
import player.Player;
import util.ImageButton;

abstract public class MazeMaker<M extends Maze<? extends Block<?>,P, ?>, MV extends MazeView<M>, P extends Player<?>> extends Pane {
	
	protected int width, height, xmarg, ymarg;
	protected ImageButton savebutton, rotatebutton, deletebutton, backbutton;
	protected MazeMakerManager manager;
	protected int save_idx = -1;
	protected boolean replace = false;
	protected int row, col;
	
	protected M maze;
	protected GameMode<M, P, ?> mode;
	protected MV mv;
	
	protected ImageView imagebg;
	protected Rectangle colorbg;
	
	public MazeMaker(GameMode<M, P, ?> mode, MazeMakerManager manager) { 
		this.manager = manager;
		this.mode = mode;
	}

	protected void initContent() {
		imagebg = new ImageView();
		colorbg = new Rectangle();
		
		backbutton = new ImageButton(Images.greennextbutton);
		savebutton = new ImageButton(Images.savebutton);		
		rotatebutton = new ImageButton(Images.rotatebutton);
		deletebutton = new ImageButton(Images.deletebutton);
	}

	protected void initEvents() {

		deletebutton.setOnMouseClicked((e)->{
			mode.deleteMaze(save_idx); //use linked list for efficiency
			manager.updateMazesGrid();
			manager.updateSetting(mode.get_mazes_size(), true);
			backbutton.getOnMouseClicked().handle(null);
		});
		backbutton.setOnMouseClicked((e)->{
			manager.updateSetting(mode.get_mazes_size(), true);
			this.getScene().setRoot(manager);
			manager.requestFocus();
		});	
		
	}
	
	protected void fixlayout() {	
		backbutton.setRotate(180);
		this.setStyle("-fx-background-color: #EF8128;");
		this.setStyle("-fx-background-color: #FFFFFF;");
	}
	

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		int k = GameMenu.getK(width, height);
			
		backbutton.resize(k,k);
		rotatebutton.resize(k,k);
		deletebutton.resize(k,k);
		savebutton.resize(k*3,k);
		
		this.layout();
		
		backbutton.setLayoutX(5);
		backbutton.setLayoutY(5);
		savebutton.setLayoutX(width-savebutton.getLayoutBounds().getWidth()-5);
		savebutton.setLayoutY(5);
		rotatebutton.setLayoutX(savebutton.getLayoutX()-rotatebutton.getLayoutBounds().getWidth()-10);
		rotatebutton.setLayoutY(5);
		deletebutton.setLayoutX(rotatebutton.getLayoutX()-deletebutton.getLayoutBounds().getWidth()-10);
		deletebutton.setLayoutY(5);
		
		maze.resize(mode.getMazeWidth(width), mode.getMazeHeight(height));
		
		xmarg = (width-getMaze().getWidth())/2;
		ymarg = (height-getMaze().getHeight())/2;
		
		mv.setLayout(xmarg, ymarg);
	}
	
	
	protected void showMaze(M maze, int save_idx, boolean replace) {
		this.maze = maze;
		maze.unfix();
		
		this.replace = replace;
		this.save_idx = save_idx;
		row = 0; col = 0; //selected block row,col
		maze.resize(mode.getMazeWidth(width), mode.getMazeHeight(height));
		
	
		mv.changeMaze(maze);
		xmarg = (width-maze.getWidth())/2;
		ymarg = (height-maze.getHeight())/2;
		
		mv.setLayout(xmarg, ymarg);
	}
	
	protected M getMaze() {
		return maze;
	}
	
	protected MV getMV() {
		return mv;
	}
	
	public void help_updateTheme() {
		colorbg.setVisible(GameMenu.classic);
		imagebg.setVisible(!GameMenu.classic);
		colorbg.setVisible(false);
		imagebg.setVisible(false);
	}
	
	
	abstract public void updateTheme();
	abstract public void oldMaze(int maze_idx, int save_idx, boolean replace);
	abstract protected void addContent();
	abstract public void newMaze(int rows, int cols, int save_idx);
}
