package vc;

import resources.ModeTheme.PWTheme;
import resources.ModeTheme.WarTheme;
import resources.Images;
import resources.Theme;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import gamemode.GameMode;
import gamemode.MazeWar;
import maze.MWMaze;
import maze.Maze;
import maze.MazeView;
import java.util.ArrayList;
import maze.PWMaze;

import block.PWBlock;
import block.WarBlock;
import player.PWPlayer;
import player.Princess;
import player.PrincessView;
import player.WarPlayer;
import util.ImageButton;
import gamemode.PrincessWorld;
import maze.PWMazeView;
import maze.WarMaze;
import maze.WarMazeView;

public class WarMazeMaker extends MazeMaker<WarMaze, WarMazeView, WarPlayer> {

	private WarBlock block_ex = new WarBlock("null", false, false, false, false); //default selected block
	private HBox settings;
	private String type = "null";
	protected WarTheme theme;
	
	public WarMazeMaker(MazeWar war, MazeMakerManager manager) {
		super(war, manager);
		this.mode = war;
		this.theme = war.getTheme();
		
		//maze = new PWMaze(PWMaze.create_maze(4, 4, new PWBlock("closed")));
		maze = war.getMaze().duplicate();
		addContent();
		addEvents();
		fixlayout();	
	}

	protected void addContent() {
		super.initContent();

		imagebg.setImage(Theme.war_themes[0].getBg());
		colorbg.setFill(Color.web(Theme.war_themes[0].getBgColor()));
		
		initSettings();
		mv = new WarMazeView();
		//mv.setEditing(true); 
		
		this.getChildren().addAll(colorbg, imagebg, mv.getWallV(), settings);
		this.getChildren().addAll(backbutton, savebutton, rotatebutton, deletebutton);
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
		
			settings.getChildren().addAll(bv);
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
			maze.fixAround(row, col);
		});
		
		savebutton.setOnMouseClicked((e)->{  //move this to mazemaker
			maze.fix(); //new
			mode.addMaze(maze.duplicate(), save_idx, replace);
			replace = true;
			manager.updateMazesGrid();
		});
		
		mv.getWallV().setOnMouseClicked((m)->{		 //	mv.getWallsView()
			row = maze.getRow(m.getY());
			col = maze.getCol(m.getX());

			if (m.isSecondaryButtonDown() || m.isShiftDown())
				maze.getBlock(row, col).opposite();
			else 
				maze.getBlock(row, col).next();
			maze.fixAround(row, col);
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
	
	protected void showMaze(WarMaze warmaze, int save_idx, boolean replace) {
		warmaze.setTheme(Theme.war_themes[0].getMazeTheme());
		super.showMaze(warmaze, save_idx, replace);		
	}
	
	@Override
	public void oldMaze(int maze_idx, int save_idx, boolean replace) {
		showMaze(mode.getMaze(maze_idx).duplicate(), save_idx, replace);
	}

	@Override
	public void newMaze(int rows, int cols, int save_idx) {
		showMaze(new WarMaze(WarMaze.create_maze(rows, cols, block_ex)), save_idx, false);
	}

}
