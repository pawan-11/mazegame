package maze;

import resources.MazeTheme.MWMazeTheme;
import resources.Theme;
import resources.BlockTheme.MWBlockTheme;
import resources.BlockTheme.PWBlockTheme;
import block.MWBlock;
import block.PWBlock;
import util.Observer;
import util.Util;
import vc.GameMenu;
import vc.GoalView;

public class MWMazeView extends MazeView<MWMaze> {

	//private GoalView gv;
	
	public MWMazeView() {
		super();
	}
	
	public void changeMaze(MWMaze maze) {
		super.changeMaze(maze);
	}
	
	protected void resize() {
		super.resize_help();
	}
	
	public void setLayout(float xmarg, float ymarg) {
		super.setLayout(xmarg, ymarg);
		
	}
	
	protected void updateTheme() {
	//	MWMazeTheme theme = maze.getTheme();
		//ground or somethin, blockviews are taken care of
	}
	
	private void draw(int start_row, int last_row) {
		for (int row = start_row; row < last_row+1; row++) { //do using 2 threads
			for (int col = 0; col < maze.getCols(); col++) {
				new MWBlockView(maze.getBlock(row, col), row, col);
			}
		}
	}
	
	protected void draw() {		
		walls_g.clearRect(0, 0, walls_view.getWidth(), walls_view.getHeight());
		
		draw(0, maze.getRows()-1);
	/*	int threads = 2;
		int start_row = 0;
		int last_row = maze.getRows()/threads;
		
		for (int thread = 0; thread < threads; thread++) {
			final int s_row = start_row;
			final int l_row = last_row;
	    	Runnable r = () -> { 
	    		draw(s_row, l_row);
	    	};
	    	start_row = last_row+1;
	    	last_row = (maze.getRows()-last_row)/(threads-thread); //divide remaining rows among remaining threads
	    	
	    	Thread t = new Thread(r);
	    	t.start();
		}
    	//draw goal
    	
    	//System.gc(); //old block views can get collected
	*/
	}
	
	
	private class MWBlockView extends BlockView implements Observer {
		
		private MWBlock block;
		private MWBlockView(MWBlock block, int row, int col) {
			super(block, row, col);
			this.block = block;
			//redraw();
			draw(row, col);
		}
		
		public void draw(int row, int col) { //simply draw
			
			if (GameMenu.classic)
				super.drawWalls(row, col, block.getTheme().getWallColor(), walls_g);
			else {
				super.drawWalls(row, col, block.getTheme().getWall(), walls_g);
			}
		}
		
		public void redraw() {
			walls_g.clearRect((col-0.5)*maze.getBlock_w(), (row-0.5)*maze.getBlock_h(), maze.getBlock_w()*2, maze.getBlock_h()*2);
			super.redraw_();
		}
		
		
		@Override
		public void update(String msg) {
			redraw();
		}
	}

}
