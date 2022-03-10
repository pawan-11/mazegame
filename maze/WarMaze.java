 package maze;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import block.MWBlock;
import block.WarBlock;
import resources.Theme;
import resources.MazeTheme.MWMazeTheme;
import resources.MazeTheme.PWMazeTheme;
import resources.MazeTheme.WarMazeTheme;
import util.Goal;
import util.Point;
import player.Bullet;
import player.MWPlayer;
import player.PWPlayer;
import player.Player;
import player.WarPlayer;
import strategy.Command.MoveCommand;

public class WarMaze extends Maze<WarBlock, WarPlayer, WarMazeTheme> {

	private final WarBlock[][] warblocks;
	
	public WarMaze(WarBlock[][] blocks) {	
		super(blocks);
		this.warblocks = blocks;		
	}
	
	public void setTheme(WarMazeTheme theme) {
		super.setTheme(theme); 
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				getBlock(row, col).setTheme(theme.getBlockTheme());	
			}
		}
		empty_block.setTheme(theme.getBlockTheme());
	}
	
	public void setup(ArrayList<WarPlayer> players) {
		for (WarPlayer p: players) {
			setup(p);
		}
	}
	
	public void setup(WarPlayer p) { //TODO: what positions should players start in war maze for battle
		Point pos = new Point();

		setup(p, randomize(pos));
		p.rotate(90);
		p.revive(); //to play in this maze, revive health
		p.reload();
	}

	public void setup(Bullet b, Point pos) { //pos is in row,col form		
		b.move(pos.flip().multiply(block_w, block_h).add(block_w/2, block_h/2));
		b.resetDistance();
	}
	
	public void moveBullets(ArrayList<Bullet> bullets, ArrayList<Point> positions) {
		if (bullets.size() != positions.size()) return;		
		for (int idx = 0; idx < bullets.size(); idx++)
			setup(bullets.get(idx), positions.get(idx));
			
	}
	
	public boolean reactTo(WarPlayer p) { // so far no use, could add more cool features with this
		return false;
	}

	public boolean reactTo(ArrayList<WarPlayer> players) { //not used rn
		boolean bool = false;
		for (WarPlayer p: players) 
			bool = reactTo(p) && !bool;
		
		return bool;
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
		changed("resized");
	}
	
	
	public boolean save(FileWriter writer) {
		fix();
		try {
			writer.write("blocks:"+(getRows())+","+(getCols())+"\n");
			write_blocks(writer);	
			writer.write("end\n");
		}
		catch(IOException i) {
			System.out.println("Error write warmaze");
			return false;
		}
		return true;
	}

	protected void write_blocks(FileWriter writer) {
		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getCols(); j++) 
				blocks[i][j].write(writer);

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
	
	public WarMaze duplicate() {
		return new WarMaze(dup_blocks());
	}
	
	private WarBlock empty_block = new WarBlock("", false, false, false, false);
	public WarBlock getBlock(int row, int col) {
		empty_block.setSquare(false);
		return isValid(row, col)? blocks[row][col]: empty_block; 
	}
	
	
	protected WarBlock[][] dup_blocks() {
		WarBlock blocks_dup[][] = new WarBlock[getRows()][getCols()];
		
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				blocks_dup[i][j] = warblocks[i][j].duplicate();
			}
		}
		return blocks_dup;
	}
	
	
	public static WarBlock[][] create_maze(int rows, int cols, WarBlock block_ex) {
		
		if (rows < 2 || cols < 2) {
			cols = 2;
			rows = 2;
		}
		boolean left = block_ex.isLeft();
		boolean top = block_ex.isTop();
		boolean right = block_ex.isRight();
		boolean bottom = block_ex.isBottom();
		
		WarBlock blocks[][] = new WarBlock[rows][cols];
		for (int row = 0; row < blocks.length; row++)
			for (int col = 0; col < blocks[0].length; col++)
				blocks[row][col] = new WarBlock("ground", left, top, right, bottom);
				
	//	blocks[0][0] = new WarBlock();
	//	blocks[0][cols-1] = new WarBlock();
	//	blocks[rows-1][0] = new WarBlock();
	//	blocks[rows-1][cols-1] = new WarBlock();	
		
		return blocks;
	}
	
	protected Point getRandomPos() {
		return randomize(new Point());
	}
	
	protected Point randomize(Point pos) {
		int col = (int)(Math.random()*cols);
		int row = (int)(Math.random()*rows);
		return pos.copy(col, row).multiply(block_w, block_h).add(block_w/2, block_h/2);
	}
	
	protected int getBallRadius() {		
		int rad = (block_h < block_w)?(int)(block_h/4)+1:(int)(block_w/4)+1;
		
		if (rad < 5) {
			rad = (int)((block_w-1)/3)+1;
			if (block_h < block_w) {
				rad = (int)((block_h-1)/3)+1;
			}
		}
		return rad;
	}
	
	public void edit(MoveCommand cmd) {
		//TODO: check for if hitting top or bottom of vertical wall, and left or right of horizontal wall
		super.edit(cmd);
	}
}