package strategy;

import java.util.ArrayList;
import java.util.Collections;
import util.Queue;
import util.Util;
import gamemode.MazeWorld;
import util.Point;
import javafx.scene.input.KeyCode;
import maze.MWMaze;
import player.MWPlayer;
import strategy.Command.MoveCommand;
import strategy.Strategy.Mover;
import util.KeyBoard;
import util.MouseInfo;
import util.Observer;

abstract public class MWStrategy implements Mover<MWPlayer> {

	protected double power;
	protected Point final_pos = new Point();
	protected MoveCommand m_cmd = new MoveCommand();
	
	public MWStrategy(MazeWorld mw, double power) { //to balance mouse, key, bot strat
		this.power = power;
	}

	public MWStrategy(MazeWorld mw) {
		this(mw, 1);
	}

	public void setPower(float power) {
		this.power = power;
	}
	
	public MoveCommand get_move(MWPlayer p, int deg_ang) { //used by gamemode to knock back player
		//adjustable .multiply power, 1
		return m_cmd.init(p.get_pos(), final_pos.copy(p.get_pos()).add(p.get_translate(deg_ang).multiply(power)), p.getRadius());  
	}

	public void slide_move(MWPlayer p, MoveCommand cmd) {
		Strategy.slide_move(p, cmd, 1.5); //power, 1, 0.5
	}
	

	public static class MWMouseStrategy extends MWStrategy {

		private static MouseInfo mouse = MouseInfo.mouse_info;

		public MWMouseStrategy(MazeWorld mw) { 
			super(mw, 1);
			mw.addMouse(mouse);
		}
		
		public MoveCommand get_move(MWPlayer p) {	
			final_pos.copy(p.get_pos());
			if (!(p.get_pos().distance(mouse.get_pos()) <= p.getRadius()/2))
				final_pos.add(p.get_translate(p.get_pos().deg_ang(mouse.get_pos())));
			
			return m_cmd.init(p.get_pos(), final_pos, p.getRadius());
		}

	}

	public static class MWKeyStrategy extends MWStrategy {

		private static final KeyCode codes[][] = {{KeyCode.LEFT, KeyCode.UP, KeyCode.RIGHT}, 
				{KeyCode.A, KeyCode.W, KeyCode.D}, 
				{KeyCode.G, KeyCode.Y, KeyCode.J}};
		protected KeyBoard board;

		public MWKeyStrategy(MazeWorld mw, int key) {
			super(mw);
			this.board = mw.addKeyboard(codes[key]);
		}

		@Override
		public MoveCommand get_move(MWPlayer p) {
			
			final_pos.copy(p.get_pos());
			
			if (board.codes_status[1]) 
				final_pos = final_pos.add(p.get_translate(90)); //up
			else
				final_pos = final_pos.add(p.get_translate(270));
			
			if (board.codes_status[2]) 
				final_pos = final_pos.add(p.get_translate(0));
			if (board.codes_status[0]) 
				final_pos = final_pos.add(p.get_translate(180));

			return m_cmd.init(p.get_pos(), final_pos, p.getRadius());
		}

	}

	public static class MWBfsStrategy extends MWStrategy implements Observer  {

		private MazeWorld mw;
		private MWMaze m;
		private ArrayList<Point> shortest_path = new ArrayList<Point>();
		private int cell_idx = 0; //current cell the strategy is aiming to get to
		private int passed_idx = 0; //furthest achieved cell
		private int attempts = 0; //number of move attempts made to get to middle of cell_idx
		private Point dest = new Point();

		public MWBfsStrategy(MazeWorld mw) {
			super(mw, 0.6);
			this.mw = mw;
			mw.addObserver("strat", this);
			m = mw.getMaze();
			
			doBfs(m.get_start_pos(), m.get_goal_pos());
		}

		@Override
		public MoveCommand get_move(MWPlayer p) { 
			
			if (m.getPos(p.get_pos()).distance(shortest_path.get(cell_idx)) == 0) // && cell_idx == passed_idx+1
				passed_idx = cell_idx;
			
			if (p.get_pos().distance(dest) < p.getRadius()/2 && cell_idx+1 < shortest_path.size()) { //dest is just point coordinates of center of cell_idx cell
				attempts = 0;
				cell_idx += 1;
				dest.copy(shortest_path.get(cell_idx)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
			}
			else if (attempts > 30) { 
				Point pos = m.getPos(p.get_pos());
				//case 1: ball is at its wanted position but is unable to get to middle
				if (pos.distance(shortest_path.get(cell_idx)) == 0 ) { 
					//just let it go to next one without going to middle
					if (cell_idx == passed_idx && cell_idx+1 < shortest_path.size())
						cell_idx = passed_idx+1;
				}
				else { //could be stuck anywhere around cell_idx, but cant get to cell_idx, try to get to passed_idx
					if (cell_idx != passed_idx)
						cell_idx = passed_idx;
					else if (passed_idx > 0)
						cell_idx = passed_idx-1;
					else if (passed_idx+1 < shortest_path.size())
						cell_idx = passed_idx+1;
				}
				
				dest.copy(shortest_path.get(cell_idx)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
				
				attempts = 0;
			}
			
			attempts += 1;
			//if (cell_idx == shortest_path.size()) shortest_path.clear();
			return m_cmd.init(p.get_pos(), 
					final_pos.copy(p.get_pos()).add(p.get_translate(p.get_pos().deg_ang(dest)).multiply(power)), p.getRadius());		
		}

		@Override
		public MoveCommand get_move(MWPlayer p, int deg_ang) { //to knock back
			
			if (m.getPos(p.get_pos()).distance(shortest_path.get(cell_idx)) == 0) { //if knocked to aimed square
				passed_idx = cell_idx;
				if (cell_idx+1 < shortest_path.size()) {
					cell_idx += 1;
					passed_idx=cell_idx; //
					dest.copy(shortest_path.get(cell_idx)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());

					attempts = 0;
				}
			}
			
		//	m_cmd = super.get_move(p, deg_ang);
		//	Util.print(m_cmd.getInitPos()+"->"+m_cmd.getFinalPos());
			return super.get_move(p, deg_ang);
		}


		private void doBfs(Point goal_pos, Point start_pos) {
			shortest_path.clear();
			cell_idx = (passed_idx = 0);

			//bfs calculations from start to finish
			
			int start_cell = (int)(start_pos.getX()*m.getCols()+start_pos.getY()); //start_row*rows + start_col
			int goal_cell = (int)(goal_pos.getX()*m.getCols()+goal_pos.getY());

			int graph[][] = new int[m.getRows()*m.getCols()][7];

			for (int cell = 0; cell < graph.length; cell++) { //initialization
				int row = cell/m.getCols();
				int col = cell%m.getCols();

				graph[cell][0] = (m.getBlock(row, col).isLeft() || col-1< 0)?1:0; //1 for obstacle to the left
				graph[cell][1] = (m.getBlock(row, col).isTop()|| row-1<0)?1:0;
				graph[cell][2] = (m.getBlock(row, col).isRight()||col+1>=m.getCols())?1:0;
				graph[cell][3] = (m.getBlock(row, col).isBottom()||row+1>=m.getRows())?1:0;
				graph[cell][4] = 'w'; //white
				graph[cell][5] = Integer.MAX_VALUE; //maximum distance from start_pos
				graph[cell][6] = -1; //parent cell
			}
			
			Queue<Integer> queue = new Queue<Integer>();
			queue.add(start_cell);
			graph[start_cell][4] = 'g'; //gray
			graph[start_cell][5] = 0;
			int cell = start_cell;

			while (!queue.isEmpty() && cell != goal_cell) { 
				cell = queue.pop();

				int[] neighbours = {cell-1, cell-m.getCols(), cell+1, cell+m.getCols()};

				int i = 0;
				for (int n: neighbours) {
					int row = n/m.getCols();
					int col = n%m.getCols();

					if (m.isValid(row, col) && graph[cell][i] == 0 && graph[n][4] == 'w') {
						graph[n][4] = 'g';
						graph[n][5] = graph[cell][5]+1;
						graph[n][6] = cell;
						queue.add(n);
					}
					i++;
				}
				graph[cell][4] = 'b'; //mark it black
			}

			if (cell != goal_cell) {
				System.out.println("bfs could not find goal :(");
			}

			shortest_path.add(new Point(goal_cell/m.getCols(), goal_cell%m.getCols()));
			while (cell != start_cell && cell != -1) {
				cell = graph[cell][6];
				shortest_path.add(new Point(cell/m.getCols(), cell%m.getCols())); //row,col cell to go to
			}			
			//Collections.reverse(shortest_path); //TODO: could switch goal and start pos, to get reversed path
			dest.copy(shortest_path.get(0)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
		}

		@Override
		public void update(String msg) { //gamemode updates
			if (msg.equals("setted up")) {
				this.m = mw.getMaze(); 
				doBfs(m.get_start_pos(), m.get_goal_pos()); 
				//do this with another thread because it takes time for algorithms to solve large mazes
			}
			else if (msg.equals("resized")) {
				dest.copy(shortest_path.get(0)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
			}
		}
	}
}

