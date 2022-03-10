package maze;

import resources.MazeTheme.PWMazeTheme;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import block.PWBlock;
import util.Observer;
import util.Util;
import vc.GameMenu;


public class PWMazeView extends MazeView<PWMaze> {

	private Rectangle color_bg; //use this as pink background? yes!!
	private ImageView bg;
	private boolean editing_mode = false;

	//Add layers of canvases. One for each block type!, etc. bridges are over normal blocks, a layer for Walls!! will not have to use redraw() :O
	protected Canvas types_view; 
	protected GraphicsContext types_g;
	protected Canvas shores_view; 
	protected GraphicsContext shores_g;
	
	public PWMazeView() { //PWMaze maze
		super();
		types_view = new Canvas();
		types_g = types_view.getGraphicsContext2D();
		shores_view = new Canvas();
		shores_g = shores_view.getGraphicsContext2D();
		
		bg = new ImageView();
		color_bg = new Rectangle();		
	}
	
	public void changeMaze(PWMaze pwmaze) {
		super.changeMaze(pwmaze);	 //draws	
	//	draw();
	}
	
	protected void resize() {
		types_view.setWidth(maze.getWidth());
		types_view.setHeight(maze.getHeight());
		shores_view.setWidth(maze.getWidth());
		shores_view.setHeight(maze.getHeight());
		
		super.resize_help();
	}
	
	public void setLayout(float xmarg, float ymarg) {
		super.setLayout(xmarg, ymarg);
		types_view.setLayoutX(xmarg);
		types_view.setLayoutY(ymarg);
		shores_view.setLayoutX(xmarg);
		shores_view.setLayoutY(ymarg);
		
		bg.setLayoutX(xmarg);
		bg.setLayoutY(ymarg);
		color_bg.setLayoutX(xmarg);
		color_bg.setLayoutY(ymarg);
	}
	
	protected void updateTheme() {
		PWMazeTheme theme = maze.getTheme();
		color_bg.setVisible(GameMenu.classic);
		bg.setVisible(!GameMenu.classic);
		
		if (GameMenu.classic) {
			color_bg.setFill(Color.web(theme.getMazeBgColor()));
		}
		else
			bg.setImage(theme.getMazeBg());
	}
	
	public Canvas getTypeV() {
		return types_view;
	}
	
	public Canvas getShoreV() {
		return shores_view;
	}
	
	public Node getColorBg() {
		return color_bg;
	}
	
	public Node getImageBg() {
		return bg;
	}
	
	@Override
	public void draw() { //move parts to super class
		
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
		shores_g.clearRect(0, 0, shores_view.getWidth(), shores_view.getHeight());

    	for (int row = 0; row < maze.getRows(); row++) {
			for (int col = 0; col < maze.getCols(); col++) {
				new PWBlockView(row, col);
			}
		}
	}
	
	public void setEditing(boolean editing) { //used to see walls or not, useful for editing but ugly in game
		this.editing_mode = editing;
	}
	
	private int w;
	private int h;
	private class PWBlockView extends BlockView implements Observer {
				
		private PWBlockView(int row, int col) {
			super(maze.getBlock(row, col), row, col);
			redraw();
		}

		private PWBlock b;
		protected void draw(int row, int col) { //simply draw
			b = maze.getBlock(row, col);
			w = maze.getBlock_w();
			h = maze.getBlock_h();
			if (b.getType().equals("open")) {
				if (GameMenu.classic)
					types_g.setFill(Color.TRANSPARENT);
				else {
					types_g.drawImage(b.getTheme().getOpenDoor(), col*w, row*h, w, h);
				}
			}
			else if (b.getType().equals("closed")) {
				if (GameMenu.classic)
					types_g.setFill(Color.web(b.getTheme().getDoorColor()));
				else {
					types_g.drawImage(b.getTheme().getClosedoor(), col*w, row*h, w, h);
				}
			}
			else if (b.getType().equals("block")) {
				if (GameMenu.classic)
					types_g.setFill(Color.web(b.getTheme().getBlockColor()));
				else {
					types_g.drawImage(b.getTheme().getBlock(), col*w, row*h, w, h);
				}
			}
			else if (b.getType().equals("liquid")) {
				if (GameMenu.classic) {
					types_g.setFill(Color.web(b.getTheme().getLiquidColor()));
				}
				else {
					types_g.drawImage(b.getTheme().getLiquid(), col*w, row*h, w, h);
				}
			}
			else if (b.getType().equals("bridge")) {
				if (GameMenu.classic)
					types_g.setFill(Color.web(b.getTheme().getBridgeColor()));
				else if (b.isTop()) { //rotate bridge accordingly
					types_g.save();
					
					a = types_g.getTransform();
					a.appendRotation(90, (col)*w, (row)*h);
					a.appendTranslation(0, -w);
					types_g.setTransform(a);	
					types_g.drawImage(b.getTheme().getBridge(), col*w, row*h, h, w);
					types_g.restore();
				}
				else {
					types_g.drawImage(b.getTheme().getBridge(), col*w, row*h, w, h);
				}
			}
			
			if (GameMenu.classic && !b.getType().equals("null")) {
				types_g.fillRect(col*w, row*h, w, h);
			}				

			if (b.getType().equals("liquid") || editing_mode) { //in editing mode, the walls should always be shown
				if (GameMenu.classic)
					super.drawWalls(row, col, b.getTheme().getShoreColor(), shores_g);	
				else
					super.drawWalls(row, col, b.getTheme().getShore(), shores_g);	
			}
		//	else {
		//		if (GameMenu.classic)
		//			super.drawWalls(row, col, b.getTheme().getWallColor(), walls_g);	
		//		else
		//			super.drawWalls(row, col, b.getTheme().getWall(), walls_g);	
		//	}
		}
		
		
		public void redraw() { //redraw this block and its surroundings, clear if need to
			int w = maze.getBlock_w();
			int h = maze.getBlock_h();
			
			walls_g.clearRect((col-0.5)*w, (row-0.5)*h, w*2, h*2);
			shores_g.clearRect((col-0.5)*w, (row-0.5)*h, w*2, h*2);
			types_g.clearRect((col-0.5)*w, (row-0.5)*h, w*2, h*2);
		//	types_g.clearRect((col)*w, (row)*h, w, h);
			
			super.redraw_();
		}
		
		@Override
		public void update(String msg) {
			if (msg.equals("type"))
				redraw();
			else 
				super.update(msg);
		}
	}
}
