 package maze;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import block.MWBlock;
import resources.Theme;
import resources.MazeTheme.MWMazeTheme;
import resources.MazeTheme.PWMazeTheme;
import util.Goal;
import util.Point;
import player.MWPlayer;
import strategy.Command.MoveCommand;

public class MWMaze extends Maze<MWBlock, MWPlayer, MWMazeTheme> {

	private Point start_pos, goal_pos;
	private final MWBlock[][] mwblocks; //TODO: make block nested class
	
	public MWMaze(MWBlock[][] blocks, Point start_pos, Point goal_pos) {	
		super(blocks);
		this.mwblocks = blocks;
				
		change_start_pos(start_pos);
		change_goal_pos(goal_pos);
	}
	
	public void setTheme(MWMazeTheme theme) {
		super.setTheme(theme); 
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				getBlock(row, col).setTheme(theme.getBlockTheme());
			}
		}
		empty_block.setTheme(theme.getBlockTheme());
	}
	
	public void setup(ArrayList<MWPlayer> players) {
		setup(players, this.get_start_pos());
	}
	
	public void setup(MWPlayer p) {
		setup(p, this.get_start_pos().flip().multiply(block_w, block_h).add(block_w/2, block_h/2));
	}
	
	public void setup(Goal goal) {
		goal.move(goal_pos.copy().flip().multiply(block_w, block_h));//goal_pos.getY()*getBlock_w(), goal_pos.getX()*getBlock_h());
		goal.resize(getBlock_w(), getBlock_h());
		
		MWBlock b = getBlock((int)goal_pos.getX(), (int)goal_pos.getY());
		
		if (!b.isLeft()) 
			goal.rotate(0);
		else if (!b.isTop()) 
			goal.rotate(90);
		else if (!b.isRight()) 
			goal.rotate(180);
		else if (!b.isBottom())
			goal.rotate(-90);
	}
	
	public void change_goal_pos(Point new_pos) {
		
		this.goal_pos = isValid((int)start_pos.getX(), (int)start_pos.getY())?new_pos:goal_pos;
	}

	public void change_start_pos(Point new_pos) { //position in row,col form
		//make sure start_pos is in valid position
		this.start_pos = isValid((int)new_pos.getX(), (int)new_pos.getY())?new_pos:start_pos;	
	}
	
	public Point get_start_pos() {
		return start_pos.copy();
	}
	
	public Point get_goal_pos() {
		return goal_pos.copy();
	}

	public boolean reactTo(MWPlayer p) { // so far no use, could add more cool features with this
		return false;
	}
	
	public boolean reactTo(ArrayList<MWPlayer> players) {
		boolean bool = false;
		for (MWPlayer p: players) 
			bool = reactTo(p) && !bool;
		
		return bool;
	}
	
	public void edit(MoveCommand mc) {
		super.edit(mc);
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
		changed("resized");
	}
	
	
	public boolean save(FileWriter writer) {
		try {
			writer.write("blocks:"+(getRows())+","+(getCols())+","
					+((int)start_pos.getX())+","
					+((int)start_pos.getY())+","
					+((int)goal_pos.getX())+","+((int)goal_pos.getY())+"\n");
			
			write_blocks(writer);	
			writer.write("end\n");
		}
		catch(IOException i) {
			System.out.println("Error write mwmaze");
			return false;
		}
		return true;
	}

	protected void write_blocks(FileWriter writer) {
		try {
			for (int i = 0; i < getRows(); i++)
				for (int j = 0; j < getCols(); j++) {
					int l=0, t=0, r=0, b=0;
					if (mwblocks[i][j].isLeft() || getBlock(i,j-1).isRight()) l = 1;
					if (mwblocks[i][j].isRight() || getBlock(i,j+1).isLeft()) r = 1;
					if (mwblocks[i][j].isTop() || getBlock(i-1,j).isBottom()) t = 1;
					if (mwblocks[i][j].isBottom() || getBlock(i+1,j).isTop()) b =1;
					writer.write(l+""+t+""+r+""+b+"\n");
				}
		}
		catch(IOException i) {
			System.out.println("Error writing blocks");
		}
	}
	
	public void fix() {
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				fix(i, j);
			}
		}
		
		for (int col = 0; col < cols; col++) {
			blocks[0][col].setSquare(false);
			blocks[rows-1][col].setSquare(false);
			fix(0, col);
			fix(rows-1, col);
		}
		for (int row = 0; row < rows; row++) {
			blocks[row][0].setSquare(false);	
			blocks[row][cols-1].setSquare(false);
			fix(row, 0);
			fix(row, cols-1);
		}
	}
	
	public MWMaze duplicate() {
		return new MWMaze(dup_blocks(), start_pos.copy(), goal_pos.copy());
	}
	
	private MWBlock empty_block = new MWBlock(false, false, false, false);
	public MWBlock getBlock(int row, int col) {
		empty_block.setSquare(false);
		return isValid(row, col)? mwblocks[row][col]: empty_block;	

	}
	
	
	protected MWBlock[][] dup_blocks() {
		MWBlock blocks_dup[][] = new MWBlock[getRows()][getCols()];
		
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				blocks_dup[i][j] = mwblocks[i][j].duplicate();
			}
		}
		return blocks_dup;
	}
	
	
	public static MWBlock[][] create_maze(int rows, int cols, MWBlock block_ex) {
		
		if (rows < 2 || cols < 2) {
			cols = 2;
			rows = 2;
		}
		boolean left = block_ex.isLeft();
		boolean top = block_ex.isTop();
		boolean right = block_ex.isRight();
		boolean bottom = block_ex.isBottom();
		
		MWBlock blocks[][] = new MWBlock[rows][cols];
		for (int row = 1; row < blocks.length-1; row++)
			for (int col = 1; col < blocks[0].length-1; col++)
				blocks[row][col] = new MWBlock(left, top, right, bottom);
			

		//4 sides
		for (int row = 0; row < blocks.length; row++) {
			blocks[row][0] = new MWBlock(false, false, !left, false);
			blocks[row][cols-1] = new MWBlock(!right, false, false, false);
		}
	
		for (int col = 0; col < blocks[0].length; col++) {
			blocks[0][col] = new MWBlock(false, false, false, !top);
			blocks[rows-1][col] = new MWBlock(false, !bottom, false, false);
		}
		
		blocks[0][0] = new MWBlock();
		blocks[0][cols-1] = new MWBlock();
		blocks[rows-1][0] = new MWBlock();
		blocks[rows-1][cols-1] = new MWBlock();	
		
		return blocks;
	}
	
	protected int getBallRadius() {		
		int rad = (block_h < block_w)?(int)(block_h/3.5)+1:(int)(block_w/3.5)+1;
		
		if (rad < 5) {
			rad = (int)((block_w-1)/2.7)+1;
			if (block_h < block_w) {
				rad = (int)((block_h-1)/2.7)+1;
			}
		}
		return rad;
	}
}
