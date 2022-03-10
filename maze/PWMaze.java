package maze;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import block.PWBlock;
import resources.Theme;
import resources.MazeTheme.PWMazeTheme;
import strategy.Command.MoveCommand;
import util.Point;
import util.Util;
import player.Princess;
import player.PWPlayer;

public class PWMaze extends Maze<PWBlock, PWPlayer, PWMazeTheme> {

	
	public PWMaze(PWBlock[][] blocks) {
		super(blocks);
	}
	
	public void resize(int width, int height) {
		super.resize(width, height);
		changed("resized");
	}

	public void setTheme(PWMazeTheme theme) {
		super.setTheme(theme);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				getBlock(row, col).setTheme(theme.getBlockTheme());
			}
		}
		empty_block.setTheme(theme.getBlockTheme());
	}
	
	@Override
	public PWMaze duplicate() {
		//m.unfix();
		return new PWMaze(dup_blocks());
	}

	protected PWBlock[][] dup_blocks() {
		PWBlock blocks_dup[][] = new PWBlock[rows][cols];

		for (int i = 0; i < blocks.length; i++)
			for (int j = 0; j < blocks[0].length; j++)
				blocks_dup[i][j] = blocks[i][j].duplicate();
		return blocks_dup;
	}
	
	@Override
	public void setup(ArrayList<PWPlayer> players) {	 //change to add player at entrances
	 	setup(players, new Point(rows, cols/2));
		for (PWPlayer p: players)
			p.rotate(90);
	}
	
	public void setup(PWPlayer p) {
		p.rotate(90);
		setup(p, new Point(rows, cols/2).flip().multiply(block_w, block_h).add(block_w/2, block_h/2));
	}

	public void closeDoorsTo(ArrayList<PWPlayer> players) {
		for (PWPlayer p: players) 
			closeDoorTo(p);
	}
	
	private void closeDoorTo(PWPlayer p) {
		getBlock(getRow(p.get_pos().getY()), getCol(p.get_pos().getX())).closeDoor();
	}
	
	public boolean reactTo(ArrayList<PWPlayer> players) {
		for (PWPlayer p: players) {
			reactTo(p);
		}
		return false;
	}

	
	public boolean reactTo(PWPlayer p) {	
		int row = getRow(p.get_pos().getY());
		int col = getCol(p.get_pos().getX());

		int row2 = getRow(p.get_prev_pos().getY());
		int col2 = getCol(p.get_prev_pos().getX());
		
		
		if (row != row2 || col != col2) {
			//System.out.println("opening");
			getBlock(row2,col2).closeDoor(); //TODO: FIX!
			getBlock(getRow(p.get_pos().getY()), getCol(p.get_pos().getX())).openDoor();
			p.reset_prev_pos();
		}
		return false;
	}

	public void setup(Princess prin) { //assigns new position for princess to hide in
		ArrayList<Point> closed_doors = new ArrayList<Point>();

		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				if (getBlock(r,c).getType().equals("closed"))
					closed_doors.add(new Point(r,c));
		
		Point random_door;
		
		if (closed_doors.isEmpty()) random_door = new Point(2,2); //no place to hide door		
		else random_door = closed_doors.get((int)(Math.random()*(closed_doors.size()))); //row, col
		closed_doors.clear();
		
		setup(prin, random_door);
	}
	
	public void setup(Princess prin, Point pos) { //pos is in (row, col) form
		prin.resize(block_w, block_h);
		prin.move(pos.flip().multiply(block_w, block_h)); //pos.getY()*block_w, pos.getX()*block_h);
	}

	private PWBlock empty_block = new PWBlock("null", false, false, false, false);
	public PWBlock getBlock(int row, int col) {
		empty_block.setSquare(false);
		return isValid(row, col)? blocks[row][col]:empty_block; //could change to global
	}

	@Override
	public boolean save(FileWriter writer) {
		fix();
		try {
			writer.write("blocks:"+(rows)+","+(cols)+"\n");

			this.write_blocks(writer);
			writer.write("end\n");
		}
		catch(IOException i) {
			System.out.println("Error write");
			return false;
		}
		return true;
	}

	@Override
	protected void write_blocks(FileWriter writer) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				blocks[i][j].write(writer);
			}
		}
	
	}
	
	public static PWBlock[][] create_maze(int rows, int cols, PWBlock block_ex) {
		
		boolean left = block_ex.isLeft();
		boolean top = block_ex.isTop();
		boolean right = block_ex.isRight();
		boolean bottom = block_ex.isBottom();
		String type = block_ex.getType();
		
		PWBlock blocks[][] = new PWBlock[rows+4][cols+4];
		
		for (int row = 2; row < rows+2; row++)
			for (int col = 2; col < cols+2; col++)
				blocks[row][col] = new PWBlock(type, left, top, right, bottom);
			

		//4 sides
		for (int row = 1; row < rows+3; row++) {
			blocks[row][1] = new PWBlock("liquid",true, false, true, false);
			blocks[row][cols+2] = new PWBlock("liquid",true, false, true, false);
		}
	
		for (int col = 1; col < cols+3; col++) {
			blocks[1][col] = new PWBlock("liquid",false, true, false, true);
			blocks[rows+2][col] = new PWBlock("liquid",false, true, false, true);
		}
		blocks[1][1] = new PWBlock("liquid", true, true, false, false);
		blocks[1][cols+2] = new PWBlock("liquid", false, true, true, false);
		blocks[rows+2][1] = new PWBlock("liquid", true, false, false, true);
		blocks[rows+2][cols+2] = new PWBlock("liquid", false, false, true, true);	
		
		for (int row = 0; row < blocks.length; row++) {
			blocks[row][0] = new PWBlock("null");
			blocks[row][cols+3] = new PWBlock("null");
		}
	
		for (int col = 0; col < blocks[0].length; col++) {
			blocks[0][col] = new PWBlock("null");
			blocks[rows+3][col] = new PWBlock("null");
		}
		
		return blocks;
	}
	
	public void fix() {

		for (int i = 0; i < rows; i++) { //TODO: remove walls that are not around liquid nor block
			for (int j = 0; j < cols; j++) {
				fix(i, j); //adds walls based on walls in surrounding blocks
			}
		}
		
		for (int col = 0; col < cols; col++) {
		//	pwblocks[0][col].fix_type("null");
		//	pwblocks[rows-1][col].fix_type("null");
			blocks[0][col].setSquare(false);
			blocks[rows-1][col].setSquare(false);
			fix(0, col);
			fix(rows-1, col);
		}
		for (int row = 0; row < rows; row++) {
		//	pwblocks[row][0].fix_type("null");
		//	pwblocks[row][cols-1].fix_type("null");
			blocks[row][0].setSquare(false);	
			blocks[row][cols-1].setSquare(false);
			fix(row, 0);
			fix(row, cols-1);
		}
	}
	
	
	protected int getBallRadius() {		
		int rad = (block_h < block_w)?(int)(block_h/4)+1:(int)(block_w/4)+1;
		
		if (rad < 5) {
			rad = (int)(block_w/3)+1;
			if (block_h < block_w) {
				rad = (int)(block_h/3)+1;
			}
		}
		return rad;
	}
	
	public void addBridge(int row, int col) {
		PWBlock b = getBlock(row, col);
		
	//	b.set(1, true);
	//	b.set(3, true);
		b.fix_type("bridge");
	}
	
	public void addBlock(int row, int col) {
		PWBlock b = getBlock(row, col);
		b.setSquare(true);
		b.fix_type("block");
	}
	
	public void addWater(int row, int col) {
		PWBlock b = getBlock(row, col);
		b.fix_type("liquid");
	//	b.setSquare(false);
	}
	
	public void changeBlock(int row, int col, String type) {
		if (type.equals("block"))
			addBlock(row, col);
		else if (type.equals("bridge"))
			addBridge(row, col);
		else if (type.equals("liquid")) {
			addWater(row,col);
		}
		else if (type.equals("null")) {
			getBlock(row, col).fix_type(type);
			getBlock(row, col).setSquare(false);
		}
		else {
			getBlock(row, col).fix_type(type);
		//	getBlock(row, col).setSquare(false);
		}
	}
	
	private Point translate = new Point();
	public void edit(MoveCommand cmd) {
		Point grid_pos = this.getPos(cmd.getFinalPos());
		if (this.getBlock((int)grid_pos.getX(), (int)grid_pos.getY()).getFixType().equals("liquid")) {
		//	double ang = cmd.getInitPos().rad_ang(cmd.getFinalPos());
			translate.copy(cmd.getFinalPos().subtract(cmd.getInitPos()).multiply(1.5));
			cmd.changeX((float)(cmd.getInitPos().getX()+translate.getX()));
			cmd.changeY((float)(cmd.getInitPos().getY()+translate.getY()));
		}
		super.edit(cmd);
	}
	
	
	public PWMazeTheme getTheme() {
		return theme;
	}
}
