package strategy;

import java.util.ArrayList;
import gamemode.PrincessWorld;
import util.Point;
import javafx.scene.input.KeyCode;
import maze.PWMaze;
import player.MWPlayer;
import player.PWPlayer;
import player.Player;
import player.Player.ActivityReport;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;
import strategy.Strategy.Mover;
import strategy.Strategy.Rotator;
import util.KeyBoard;
import util.MouseInfo;
import util.Observer;

abstract public class PWStrategy implements Mover<PWPlayer>, Rotator<PWPlayer>  { 
	
	protected Point init_pos = new Point(), final_pos = new Point(); //used to avoid initializing a Point everytime in get_move, TODO: try static
	protected MoveCommand m_cmd = new MoveCommand();
	protected RotateCommand r_cmd = new RotateCommand();
	
	public PWStrategy(PrincessWorld pw) {}
	
	public MoveCommand get_move(PWPlayer p) {	
		return m_cmd.init(init_pos.copy(p.get_pos()), final_pos.copy(p.get_pos()).add(p.get_translate(p.getAngle())), p.getRadius());
	}
	
	public MoveCommand get_move(PWPlayer p, int deg_ang) { //knock back command
		return m_cmd.init(init_pos.copy(p.get_pos()), final_pos.copy(p.get_pos()).add(p.get_translate(deg_ang).multiply(0.6)), p.getRadius()); //0.8
	}
	
	public MoveCommand slide_move(PWPlayer p, MoveCommand cmd) {
		return m_cmd.init(cmd.getInitPos(), cmd.getFinalPos().add(p.get_translate(p.getAngle()).multiply(0.5)), p.getRadius());
	}
	
	protected int turnVertical(PWPlayer p) { //assuming p hit a vertical wall (facing horizontally)
		int rotation = 0;
		int angle = p.getAngle();
		int rotatespeed = p.getRotateSpeed();
		
		if (angle > 270)
			rotation = -(rotatespeed);
		else if (angle > 180)
			rotation = (rotatespeed);
		else if (angle > 90)
			rotation = -(rotatespeed);
		else
			rotation = (rotatespeed);
		
		return rotation;
	}
	
	protected int turnHorizontal(PWPlayer p) { //assuming p hit a horizontal wall (facing vertically)
		int rotation = 0;
		int angle = p.getAngle();
		int rotatespeed = p.getRotateSpeed();
		
		if (angle > 270)
			rotation = (rotatespeed);
		else if (angle > 180)
			rotation = -(rotatespeed);
		else if (angle > 90)
			rotation = (rotatespeed);
		else
			rotation = -(rotatespeed);
		
		return rotation;
	}
	
	public static class PWMouseStrategy extends PWStrategy {

		private MouseInfo mouse;
		
		
		public PWMouseStrategy(PrincessWorld pw) {
			super(pw);
			this.mouse = MouseInfo.mouse_info;
			pw.addMouse(mouse);
		}
		
		//in get_move, override and add if want to enable stopping when mouse in on pwplayer
		// final_pos = !(p.get_pos().distance(mouse.get_pos()) <= p.getRadius())?
		//	p.get_pos().add(p.get_translate(Math.toRadians(p.getAngle()))): p.get_pos();	
			
		@Override
		public RotateCommand get_rotate(PWPlayer p) { 						
			int rotate = 0;
			ActivityReport report = p.getReport();
			if (p.get_pos().distance(mouse.get_pos()) <= p.getRadius())
				rotate = p.getRotateSpeed()/2;
			else if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1)); //works i guess
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p);
			else {
				int mouse_ang  = p.get_pos().deg_ang(mouse.get_pos());
				rotate = p.get_rotate(mouse_ang);
			}
			
			return r_cmd.init(p.getAngle(), p.getAngle()+rotate);
		}

	}

	
	public static class PWKeyStrategy extends PWStrategy {

		
		private final KeyCode codes[][] = {{KeyCode.LEFT, KeyCode.RIGHT}, 
				{KeyCode.A, KeyCode.D}, 
				{KeyCode.G, KeyCode.J}};
		protected KeyBoard board;
		
		public PWKeyStrategy(PrincessWorld pw, int key) {
			super(pw);
			this.board = pw.addKeyboard(codes[key]);
		}
		
		public RotateCommand get_rotate(PWPlayer p) {
			int rotate = 0;
			int speed = p.getRotateSpeed(); 
			ActivityReport report = p.getReport();
			
			if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1));
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p);
			else {
				if (board.codes_status[0]) //left key down
					rotate += speed*0.8;
				if (board.codes_status[1]) 
					rotate -= speed*0.8;
			}
			
			return r_cmd.init(p.getAngle(), p.getAngle()+rotate);
		}
		
	}
	
	public static class PWRandomStrategy extends PWStrategy implements Observer {

		private PrincessWorld pw;
		private PWMaze m;
		private ArrayList<Point> points = new ArrayList<Point>();
		private int cell_idx = 0; //cell_idx the player has passed in points list
		private Point dest = new Point(); //the destination x,y where p is aiming for
		

		public PWRandomStrategy(PrincessWorld pw) {
			super(pw);
			this.pw = pw;
			this.m = pw.getMaze();
			pw.addObserver("strat", this);
			
			doRandomShit();
		}
		
		@Override
		public MoveCommand get_move(PWPlayer p) {
			if ( cell_idx+1 < points.size() && m.getPos(p.get_pos()).distance(points.get(cell_idx)) == 0) //reached the block that we wanted to reach so move on to next
				cell_idx += 1;
			
			return this.get_move(p, p.getAngle());
		}
	
		@Override
		public RotateCommand get_rotate(PWPlayer p) {
			int rotate = 0;
			ActivityReport report = p.getReport();
			if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1));
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p); //turn these to += ?
			else if (cell_idx < points.size()) {
				dest.copy(points.get(cell_idx)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
				rotate = p.get_rotate(p.get_pos().deg_ang(dest));				
			}
			
			return r_cmd.init(p.getAngle(), p.getAngle()+rotate);
		}
		
		public void doRandomShit() {	
			cell_idx = 0;
			points.clear();
			
			for (int row = 0; row < m.getRows(); row++) {
				for (int col = 0; col < m.getCols(); col++) {
					if (m.getBlock(row, col).getType().equals("closed"))
						points.add(new Point(row, col));
				}
			}
		}
		
		@Override
		public void update(String msg) {
			if (msg.equals("setted up")) { //get hints of where princess is or something
				m = pw.getMaze();
				doRandomShit();
			}
		}
	}
	
	public static class PWFwStrategy extends PWStrategy implements Observer{ 
		//idea: use floyd warshall to find all pair shortest paths
		
		private PrincessWorld pw;
		private PWMaze m;
		private ArrayList<Point> path = new ArrayList<Point>();
		private int path_idx = 0;
		private Point dest = new Point();
		
		public PWFwStrategy(PrincessWorld pw) {
			super(pw);
			this.pw = pw;
			m = pw.getMaze();
			pw.addObserver("strat", this);
			
			doFw();
		}
		
		@Override
		public MoveCommand get_move(PWPlayer p) {
			
			//Point door = path.get(path_idx);
			if (m.getPos(p.get_pos()).distance(path.get(path_idx)) == 0 && path_idx < path.size()) 
				path_idx += 1;
			
			
			return m_cmd.init(p.get_pos(), final_pos.copy(p.get_pos()).add(p.get_translate(p.getAngle())), p.getRadius());
		}
	
		@Override
		public RotateCommand get_rotate(PWPlayer p) {
			
			int rotate = 0;
			dest = path.get(path_idx); 
			dest.copy(path.get(path_idx)).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
			ActivityReport report = p.getReport();
			if (report.hit_x() && report.hit_y()) 
				rotate = (int)(turnVertical(p)*(Math.random()*3+1));
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p);
			else		
				rotate = p.get_rotate(p.get_pos().deg_ang(dest)); //rotate towards the destination				
			
			return r_cmd.init(p.getAngle(), p.getAngle()+rotate);
		}
	
		public void doFw() {
			path_idx = 0;
			path.clear();
			
			for (int row = 0; row < m.getRows(); row++) {
				for (int col = 0; col < m.getCols(); col++) {
					if (m.getBlock(row, col).getType().equals("closed"))
						path.add(new Point(row, col));
				}
			}
		}



		@Override
		public void update(String msg) {
			if (msg.equals("maze")) {
				m = pw.getMaze();
				doFw();
			}
		}
	
	}
}
