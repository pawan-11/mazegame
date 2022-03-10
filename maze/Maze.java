package maze;

import java.io.FileWriter;
import java.util.ArrayList;
import block.Block;
import resources.MazeTheme;
import player.PWPlayer;
import player.Player;
import strategy.Command.MoveCommand;
import util.Observable;
import util.Point;
import util.Util;

abstract public class Maze<B extends Block<?>, P extends Player<?>, MT extends MazeTheme<?>> extends Observable {

	protected final B[][] blocks;
	protected MT theme;
	protected int rows = 1, cols = 1, width, height;
	protected int block_w, block_h; 
	
	protected Maze(B[][] blocks) {
		this.blocks = blocks;
		
		rows = blocks.length;
		cols = blocks[0].length;
	}
	
	public void setup(Player<?> p, Point pos) {		
		p.resize(getBallRadius());
		p.move(pos);
		p.setDisabled(false);
	}
	
	public void setup(ArrayList<? extends Player<?>> players, Point start_pos) {
		int max_radius = this.getBallRadius();
		
		int size = players.size();
		int x_size = (int)Math.ceil(size/Math.sqrt(size));
		int y_size = (int)Math.ceil(Math.sqrt(size));
		float x_pad = (float)(block_w-max_radius*2)/x_size; 
		float y_pad = (float)(block_h-max_radius*2)/y_size;
		
		Point top_left = start_pos.flip().multiply(block_w, block_h).add(max_radius, max_radius);
		
		if (players.size() > 0)
			setup(players.get(0), top_left.copy().add(block_w/2-max_radius, block_h/2-max_radius));
		
		int idx = 1;
		for (int y = 0; y < y_size; y++) {
			for (int x = 0; x < x_size; x++) {
				if (idx == players.size()) {
					x = x_size;
					y = y_size;
					break;
				}
				setup(players.get(idx), top_left.copy().add(x*x_pad, y*y_pad));
				idx++;
				
			}
		}
	}
	
	public void setup(ArrayList<? extends Player<?>> players, ArrayList<Point> positions) {
		if (players.size() != positions.size()) return;		
		for (int idx = 0; idx < players.size(); idx++)
			setup(players.get(idx), positions.get(idx).flip().multiply(block_w, block_h).add(block_w/2, block_h/2));
	}
	
	public ArrayList<Point> getPositions(ArrayList<? extends Player<?>> players) {	
		ArrayList<Point> positions = new ArrayList<Point>();
		for (Player<?> p: players)
			positions.add(getPos(p.get_pos()).copy());
		return positions;
	}
	
	private Point pos_tmp = new Point();
	public Point getPos(Point pos) { //position in x,y to row,col
		return this.pos_tmp.copy(getRow(pos.getY()), getCol(pos.getX()));
	}
	
	public void resize(int width, int height) {
		block_w = (int)width/cols;
		block_h = (int)height/rows;	
		this.width = block_w*cols;
		this.height = block_h*rows;
	}
	
	public int getRow(double y) {
		return (int)Math.floor(y/block_h);
	}
	
	public int getCol(double x) {
		return (int)Math.floor(x/block_w);
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}	
	
	public int getBlock_w() {
		return block_w;
	}
	
	public int getBlock_h() {
		return block_h;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean isValid(int row, int col) {
		return row > -1 && row < rows && col > -1 && col < cols;
	}
		
	public boolean isInMaze(int row, int col) {	
		return row > 0 && row < rows-1 && col > 0 && col < cols-1;
	}
	
	public boolean isInMaze(Point pos) {
		return this.isInMaze(getRow(pos.getY()), getCol(pos.getX()));
	}

	public void setTheme(MT theme) {
		this.theme = theme;
		changed("theme");
	}
	
	public MT getTheme() {
		return theme;
	}

	
	public void fixAround(int row, int col) { //fix the blocks around (row, col) block
		B b = getBlock(row,col);
		getBlock(row-1,col).set(3, b.isTop());
		getBlock(row+1,col).set(1, b.isBottom());
		getBlock(row, col-1).set(2, b.isLeft());
		getBlock(row,col+1).set(0, b.isRight());
	}
	
	public void fix(int row, int col) { //fix the block (row,col) based on the blocks around it
		B b = getBlock(row, col);
		b.set(getBlock(row, col-1).isRight() || b.isLeft(), getBlock(row-1, col).isBottom() || b.isTop(),
				getBlock(row, col+1).isLeft() || b.isRight(), getBlock(row+1, col).isTop() || b.isBottom());
	}
	
	public void unfix() { //TODO: do to make old mazes easier to edit
		
	}
	
	public B[][] getBlocks() {
		return blocks;
	}
	
	abstract public void fix();
	abstract public boolean reactTo(ArrayList<P> players);
	abstract public boolean reactTo(P player);
	abstract public void setup(ArrayList<P> players);
	abstract public void setup(P p);
	abstract public B getBlock(int row, int col);
	abstract protected void write_blocks(FileWriter writer);
	abstract public boolean save(FileWriter writer);
	abstract public Maze<B, P, MT> duplicate();
	abstract protected int getBallRadius();

	
	public void edit(MoveCommand cmd) { //could add friction if it touches walls, by cmd.addFriction, cmd does final_pos.multiply(mu)
		int radius = cmd.getRadius();
		boolean crossed_bottom = false, crossed_top = false;
		Point init_pos = cmd.getInitPos();
		Point final_pos = cmd.getFinalPos();		
		float final_x = final_pos.getX();
		float final_y = final_pos.getY();
		int init_row = this.getRow(init_pos.getY());
		int init_col = this.getCol(init_pos.getX());
		
		B center = getBlock(init_row, init_col);
		
		if (final_y-radius < init_row*block_h) { //hit top wall
			if (center.isTop()) {
				cmd.changeY((init_row*block_h)+radius);
			}
			else
				crossed_top = true;
		}
		else if (final_y+radius > (init_row+1)*block_h) {
			if (center.isBottom()) {
				cmd.changeY(((init_row+1)*block_h-radius));
			}
			else
				crossed_bottom = true;
		}
		if (final_x-radius < init_col*block_w) {		
			if (center.isLeft()) {
				cmd.changeX(init_col*block_w+radius);
			}
			else {
				if (crossed_top) { //top left				
					Block<?> top_left = getBlock(init_row-1,init_col-1);
					if (top_left.isRight() && top_left.isBottom()) {
						float xdiff = (float)(init_col*block_w-final_x+radius);
						float ydiff = (float)(init_row*block_h-final_y+radius);
						if (xdiff < ydiff)
							cmd.changeX(init_col*block_w+radius);
						else
							cmd.changeY((init_row*block_h)+radius);
					}
				}
				else if (crossed_bottom) { //bottom left
					Block<?> bottom_left = getBlock(init_row+1,init_col-1);
					if (bottom_left.isRight() && bottom_left.isTop()) {
						float xdiff = (float)(init_col*block_w-final_x+radius);
						float ydiff = (float)(final_y+radius-(init_row+1)*block_h);
						if (xdiff < ydiff)
							cmd.changeX(init_col*block_w+radius);
						else
							cmd.changeY((init_row+1)*block_h-radius);
					}
				}
			}
		}
		else if (final_x+radius > (init_col+1)*block_w) { 
			if (center.isRight()) {
				cmd.changeX((init_col+1)*block_w-radius);
			}
			else {
				if (crossed_top) { //top right
					Block<?> top_right = getBlock(init_row-1,init_col+1);
					if (top_right.isLeft() && top_right.isBottom()) {
						float xdiff = (float)(final_x+radius-(init_col+1)*block_w);
						float ydiff = (float)(init_row*block_h-final_y+radius);
						if (xdiff < ydiff)
							cmd.changeX((init_col+1)*block_w-radius);
						else 
							cmd.changeY((init_row*block_h)+radius);
					}
				}
				else if (crossed_bottom) { //bottom right
					Block<?> bottom_right = getBlock(init_row+1,init_col+1);
					if (bottom_right.isLeft() && bottom_right.isTop()) {
						float xdiff = (float)(final_x+radius - (init_col+1)*block_w);
						float ydiff = (float)(final_y+radius-(init_row+1)*block_h);
						if (xdiff < ydiff)
							cmd.changeX((init_col+1)*block_w-radius);
						else
							cmd.changeY((init_row+1)*block_h-radius);
					}
				}
			}
		}
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
	
	/*
	public void clearObservers() { //removes references to old block views so gc can collect 
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				blocks[row][col].clearObservers();
		System.gc();
	}
	*/
}




