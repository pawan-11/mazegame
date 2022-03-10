package maze;

import resources.MazeTheme.WarMazeTheme;
import block.WarBlock;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import util.Observer;
import util.Point;
import util.Util;
import vc.GameMenu;


public class WarMazeView extends MazeView<WarMaze> {

	private Rectangle color_bg; 
	private ImageView bg;
	private boolean editing_mode = false;

	protected Canvas types_view; 
	private GraphicsContext types_g;
	
	public WarMazeView() { //WarMaze maze
		super();
		types_view = new Canvas();
		types_g = types_view.getGraphicsContext2D();
		
		bg = new ImageView();
		color_bg = new Rectangle();
	}
	
	public void changeMaze(WarMaze maze) {
		super.changeMaze(maze);
	}
	
	protected void resize() {
		types_view.setWidth(maze.getWidth());
		types_view.setHeight(maze.getHeight());
		
		super.resize_help();
	}
	
	public void setLayout(float xmarg, float ymarg) {
		super.setLayout(xmarg, ymarg);
		types_view.setLayoutX(xmarg);
		types_view.setLayoutY(ymarg);
		
		bg.setLayoutX(xmarg);
		bg.setLayoutY(ymarg);
		color_bg.setLayoutX(xmarg);
		color_bg.setLayoutY(ymarg);
	}
	
	protected void draw() {		
		walls_g.clearRect(0, 0, walls_view.getWidth(), walls_view.getHeight());
		
		draw(0, maze.getRows()-1);
	}
	
	private void draw(int start_row, int last_row) {
		
		if (GameMenu.classic) {		
			color_bg.setWidth(maze.getWidth()-maze.getBlock_w()*2);
			color_bg.setHeight(maze.getHeight()-maze.getBlock_h()*2);
			color_bg.setTranslateX(maze.getBlock_w());
			color_bg.setTranslateY(maze.getBlock_h());
		}
		else {
			bg.setFitWidth(maze.getWidth()-maze.getBlock_w()*2);
			bg.setFitHeight(maze.getHeight()-maze.getBlock_h()*2);
			bg.setTranslateX(maze.getBlock_w());
			bg.setTranslateY(maze.getBlock_h());
		}
		
		walls_g.clearRect(0, 0, walls_view.getWidth(), walls_view.getHeight());
		types_g.clearRect(0, 0, types_view.getWidth(), types_view.getHeight());
		
		for (int row = start_row; row <= last_row; row++) //do using 2 threads
			for (int col = 0; col < maze.getCols(); col++)
				new WarBlockView(row, col);
	}
	
	protected void updateTheme() {
		color_bg.setFill(Color.web(maze.getTheme().getMazeBgColor()));
		bg.setImage(maze.getTheme().getMazeBg());
		//warblockviews observe and update
	}
	
	public Canvas getTypeV() {
		return types_view;
	}
	
	public Node getImageBg() {
		return bg;
	}
	
	public Rectangle getColorBg() {
		return color_bg;
	}
	
	public void update(String msg) {
		super.update(msg);
	}
	
	private class WarBlockView extends BlockView implements Observer {
		
	
		private WarBlockView(int row, int col) {
			super(maze.getBlock(row, col), row, col);
			
			redraw();
			//draw(row, col);
		}
		
		private WarBlock b;
		public void draw(int row, int col) { //simply draw		
			b = maze.getBlock(row, col);
		//	String type = b.getType();
			int w = maze.getBlock_w();
			int h = maze.getBlock_h();
			
			//if (GameMenu.classic)
				super.drawWalls(row, col, b.getTheme().getWallColor(), walls_g);
			//else
			//	super.drawWalls(row, col, b.getTheme().getWall(), walls_g);

			if (row%2 == col%2)
				types_g.setFill(Color.web(b.getTheme().getGroundColor1()));
			else
				types_g.setFill(Color.web(b.getTheme().getGroundColor2()));
			if (maze.isInMaze(row, col))
				types_g.fillRect(col*w, row*h, w, h);		
		}
		
		public void redraw() {
			walls_g.clearRect((col-0.5)*maze.getBlock_w(), (row-0.5)*maze.getBlock_h(), maze.getBlock_w()*2, maze.getBlock_h()*2);
			super.redraw_();
		}
		
		@Override
		public void update(String msg) { //in case of theme update
			redraw();
		}
	}

}

