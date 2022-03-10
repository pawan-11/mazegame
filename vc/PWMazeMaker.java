package vc;

import resources.ModeTheme.PWTheme;
import resources.Images;
import resources.Theme;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import gamemode.GameMode;
import maze.MWMaze;
import maze.Maze;
import maze.MazeView;
import java.util.ArrayList;
import maze.PWMaze;

import block.PWBlock;
import player.PWPlayer;
import player.Princess;
import player.PrincessView;
import util.ImageButton;
import util.Util;
import gamemode.PrincessWorld;
import maze.PWMazeView;

public class PWMazeMaker extends MazeMaker<PWMaze, PWMazeView, PWPlayer> {

	private PWBlock block_ex = new PWBlock("null", false, false, false, false); //default selected block
	private HBox settings;
	private String type = "null";
	protected PWTheme theme;
	
	public PWMazeMaker(PrincessWorld pw, MazeMakerManager manager) {
		super(pw, manager);
		this.mode = pw;
		this.theme = pw.getTheme();
		
		//maze = new PWMaze(PWMaze.create_maze(4, 4, new PWBlock("closed")));
		maze = pw.getMaze().duplicate();
		addContent();
		addEvents();
		fixlayout();	
	}

	protected void addContent() {
		super.initContent();

		initSettings();
		mv = new PWMazeView();
		mv.setEditing(true); //to show invisible walls
		
		imagebg.setImage(Theme.pw_themes[0].getBg());
		colorbg.setFill(Color.web(Theme.pw_themes[0].getBgColor()));
		
		this.getChildren().addAll(colorbg, imagebg, mv.getTypeV(), mv.getWallV(), mv.getShoreV(), 
				settings, backbutton, savebutton, rotatebutton, deletebutton);
	}

	private void initSettings() {
		settings = new HBox();
		int i =0;
		
		String colors[] = {"yellow","blue", "gray","brown","cyan", "black"};
		Image images[] = {Images.blankcell, Images.bridge, Images.closedoor, Images.opendoor, Images.water, Images.castleblock};
		String[] types = {"null","bridge", "closed","open","liquid","block"}; 
		
		for (String type: types) {
			
			ImageButton bv = new ImageButton();
			if (GameMenu.classic)
				bv.setStyle("-fx-background-color:"+colors[i]+";");
			else
				bv.setImage(images[i]);
			
			settings.getChildren().addAll(bv); //FIXME
			bv.setOnMouseClicked(m->{
				this.type = type;
			});
			i++;
		}
	}
	
	public void addEvents() {
		super.initEvents();	
		rotatebutton.setOnMouseClicked((e)->{  //this too
			maze.getBlock(row,col).rotate();
		});
		
		savebutton.setOnMouseClicked((e)->{  //move this to mazemaker
			maze.fix(); //new //FIX, some wall views not showing up
			mode.addMaze(maze.duplicate(), save_idx, replace); 
			replace = true; //if saving again, then replace
			manager.updateMazesGrid();
		});

		mv.getShoreV().setOnMouseDragged((m)->{		
			row = maze.getRow(m.getY());
			col = maze.getCol(m.getX());
			
			if (!m.isShiftDown())
				maze.changeBlock(row, col, type);	
		});
		
		mv.getShoreV().setOnMouseClicked(m->{
			row = maze.getRow(m.getY());
			col = maze.getCol(m.getX());
			
			if (m.isShiftDown()) {
				maze.getBlock(row, col).next();
			//	maze.fixAround(row, col);
			}
			else
				maze.changeBlock(row, col, type);
			
		});
		
	}
	
	public void resize(int width, int height) { 
		super.resize(width, height);

		int k = GameMenu.getK(width, height);
		for (Node n: settings.getChildren())
			((ImageButton)n).resize(k, k);
		
		this.layout();
		settings.setLayoutX(width-settings.getWidth()-k);
		settings.setLayoutY(/*pwmv.getLayoutY()-*/k*2);
	}
	
	@Override
	public void updateTheme() {
		super.help_updateTheme();
	}
	
	protected void showMaze(PWMaze pwmaze, int save_idx, boolean replace) {
		pwmaze.setTheme(Theme.pw_themes[0].getMazeTheme());
		super.showMaze(pwmaze, save_idx, replace);
		pwmaze.setTheme(theme.getMazeTheme()); //default	
		
	}
	
	@Override
	public void oldMaze(int maze_idx, int save_idx, boolean replace) {
		showMaze(mode.getMaze(maze_idx).duplicate(), save_idx, replace);
	}

	@Override
	public void newMaze(int rows, int cols, int save_idx) {
		showMaze(new PWMaze(PWMaze.create_maze(rows, cols, block_ex)), save_idx, false);
	}

}
