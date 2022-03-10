package maze;
 
import block.Block;
import resources.BlockTheme;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import util.Observer;
import vc.GameMenu;

abstract public class MazeView<M extends Maze<? extends Block<?>,?,?>> implements Observer {
	
	protected Canvas walls_view;
	protected GraphicsContext walls_g;
	protected M maze;
	
	public MazeView() {	
		walls_view = new Canvas();
		walls_g = walls_view.getGraphicsContext2D();
		walls_g.setImageSmoothing(false);
	}
	
	protected void resize_help() {
		walls_view.setWidth(maze.getWidth());  ////
		walls_view.setHeight(maze.getHeight());
		draw();
	}
	
	public Canvas getWallV() {
		return walls_view;
	}
	
	public void setLayout(float xmarg, float ymarg) {
		walls_view.setLayoutX(xmarg);
		walls_view.setLayoutY(ymarg);
	}
	
	public void changeMaze(M maze) {
		this.maze = maze;
		resize(); //draws
		maze.addObserver("view", this);	
		
		updateTheme();
	}
	
	public void update(String msg) {
		if (msg.equals("resized"))
			resize();
		else if (msg.equals("theme"))
			updateTheme();
	}
	
	abstract protected void updateTheme();
	abstract protected void draw();
	abstract protected void resize();
	
	protected static Rotate r;
	protected static Affine a;
	//static int drew = 0;

	abstract class BlockView implements Observer {
		
		
		protected int row, col;
		//int drew = 0;
		public BlockView(Block<?> block, int row, int col) {
			block.addObserver("view", this);
			this.row = row;
			this.col = col;
		}
		
		
		public void drawWalls(int row, int col, String color, GraphicsContext gc) {
			int w = maze.getBlock_w();
			int h = maze.getBlock_h();
			Block<?> b = maze.getBlock(row, col);
			BlockTheme bt = b.getTheme();
			
			int lw = w < h?(int)(w/10+1):(int)(h/10+1); 
			
			gc.setStroke(Color.web(bt.getWallColor()));
			gc.setLineWidth(lw);
			
			if (b.isLeft())
				gc.strokeLine(col*w, row*h, col*w, (row+1)*h);
			if (b.isTop())
				gc.strokeLine(col*w, row*h, (col+1)*w, row*h);
			if (b.isRight())
				gc.strokeLine((col+1)*w, row*h, (col+1)*w, (row+1)*h);
			if (b.isBottom()) 
				gc.strokeLine(col*w, (row+1)*h, (col+1)*w, (row+1)*h);	
			
		}
		
		
		public void drawWalls(int row, int col, Image wall, GraphicsContext gc) { //draw walls for block on (row,col)
			int w = maze.getBlock_w();
			int h = maze.getBlock_h();
			Block<?> b = maze.getBlock(row, col);
		//	BlockTheme bt = b.getTheme();
			int lw = w < h?2*(int)(w/12+1):2*(int)(h/12+1); 
				
			if (b.isLeft()) {
				gc.drawImage(wall, col*w-lw/2, row*h, lw, h);
				//gc.drawImage(wall, col*w-lw/2, row*h-lw/2, lw, h+lw);
			}
			if (b.isTop()) {
				gc.save();
				r = new Rotate(90, (col+1)*w, (row)*h);
				gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
				a = gc.getTransform();
				a.appendTranslation(w, 0);
				gc.setTransform(a);
				gc.drawImage(wall, col*w-lw/2, row*h-lw/2+2, lw, w+lw-2);
				gc.restore();
			}
			if (b.isRight()) {
				gc.save();
				r = new Rotate(180, (col)*w, (row)*h);
				gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
				a = gc.getTransform();
				a.appendTranslation(-w, -h);
				gc.setTransform(a);
				gc.drawImage(wall, col*w-lw/2, row*h, lw, h);
				gc.restore();
			}
			if (b.isBottom()) {
				gc.save();
				r = new Rotate(-90, (col)*w, (row)*h);
				gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
				a = gc.getTransform();
				a.appendTranslation(-h, 0);
				gc.setTransform(a);
				gc.drawImage(wall, col*w-lw/2, row*h-lw/2+2, lw, w+lw-2);
				gc.restore();
			}
		}
		
		
		public void redraw_() { //redraw this block and its surroundings, clear if need to
			//walls_g.clearRect((col)*w, (row)*h, w, h);
			draw(row+1, col-1);
			draw(row+1,col);
			draw(row+1,col+1);
			draw(row, col+1);
			draw(row, col-1);
			draw(row-1, col-1);
			draw(row-1, col);
			draw(row-1, col+1);
			
			draw(row, col);
		}
		
	//	protected void finalize() {
		//	System.out.println("block destroyed");
	//	}
	
		abstract protected void draw(int row, int col);
		abstract public void redraw();
		
		@Override
		public void update(String msg) {	
			if (msg.equals("theme")) 
				redraw();
			else if (msg.equals("walls"))
				redraw();
		}
		
	}
}
