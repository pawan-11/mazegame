package strategy;

import java.util.ArrayList;
import gamemode.MazeWar;
import gamemode.PrincessWorld;
import util.Point;
import util.Util;
import javafx.scene.input.KeyCode;
import maze.WarMaze;
import player.Bullet;
import player.Player.ActivityReport;
import player.WarPlayer;
import strategy.Command.AttackCommand;
import strategy.Command.BulletCommand;
import strategy.Command.MoveCommand;
import strategy.Command.NoAttackCommand;
import strategy.Command.RotateCommand;
import strategy.Strategy.Attacker;
import strategy.Strategy.Mover;
import strategy.Strategy.Rotator;
import util.KeyBoard;
import util.MouseInfo;
import util.Observer;

abstract public class WarStrategy implements Mover<WarPlayer>, Rotator<WarPlayer>, Attacker<WarPlayer, MazeWar>{ 
	
	protected Point init_pos = new Point(), final_pos = new Point(); 
	protected MoveCommand m_cmd = new MoveCommand();
	protected RotateCommand r_cmd = new RotateCommand();
	protected BulletCommand b_cmd = new BulletCommand();
	protected NoAttackCommand<WarPlayer, MazeWar> null_cmd = new NoAttackCommand<WarPlayer, MazeWar>();
	
	public WarStrategy(MazeWar war) {}
	
	//many bots using this one is no good cuz shot and rotate counters are shared 
	
	protected int shot_counter = 0, reload = 5; 
	
	public MoveCommand get_move(WarPlayer p, int deg_ang) {
		return m_cmd.init(init_pos.copy(p.get_pos()), final_pos.copy(p.get_pos()).add(p.get_translate(deg_ang).multiply(0.6)), p.getRadius()); //0.8
	}
	
	protected int turnVertical(WarPlayer p) { //assuming p hit a vertical wall (facing horizontally)
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
	
	protected int turnHorizontal(WarPlayer p) { //assuming p hit a horizontal wall (facing vertically)
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
	
	public static class WarMouseStrategy extends WarStrategy implements Observer {

		private MouseInfo mouse;
		private boolean bulletRequested = false; //used to fire if a mouse has been clicked and released fast
		
		public WarMouseStrategy(MazeWar war) {
			super(war);
			this.mouse = MouseInfo.mouse_info;
			war.addMouse(mouse);
			
			war.addObserver("strat", this);
			mouse.addObserver("strat", this);
		}
		
		@Override
		public MoveCommand get_move(WarPlayer p) {	
			final_pos.copy(p.get_pos());
			if (!(p.get_pos().distance(mouse.get_pos()) <= p.getRadius()*2))
				final_pos.add(p.get_translate(p.getAngle()));
			
			return m_cmd.init(init_pos.copy(p.get_pos()), final_pos, p.getRadius()); //change to edit an old one
		}
		
		double precision = 1.5; //TODO: make precision dependent on maze size?
		@Override
		public RotateCommand get_rotate(WarPlayer p) { 		
			
			int rotate = 0;
			ActivityReport report = p.getReport();
			if (p.get_pos().distance(mouse.get_pos()) <= p.getRadius()/2) //stay still if mouse is on tank
				rotate = 0;
			//else if (p.get_pos().distance(mouse.get_pos()) <= p.getRadius()*2) {
			else if (p.get_pos().deg_ang(mouse.get_pos()) < p.getRotateSpeed()*2) { //slow down rotating to aim at mouse
				int mouse_ang  = p.get_pos().deg_ang(mouse.get_pos());
				rotate = (int)(p.get_rotate(mouse_ang)/precision);
			}
			else if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1)); //works i guess
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p);
			else {
				int mouse_ang  = p.get_pos().deg_ang(mouse.get_pos());
				rotate = p.get_rotate(mouse_ang);
				if (Math.abs(p.getAngle()-mouse_ang) < 1) //stop rotating when accurate enough
					rotate = 0;
				if (Math.abs(p.getAngle()-mouse_ang) < Math.abs(rotate)) {//accuracy
					rotate = mouse_ang-p.getAngle(); //PERFECT :), nice to have accuracy bby
				}
					
			}
			
			return new RotateCommand(p.getAngle(), p.getAngle()+rotate);
		}
		
		public AttackCommand<WarPlayer, MazeWar> get_attack(WarPlayer p) {
			shot_counter = (shot_counter+1)%reload;
			if (shot_counter == 0 && p.getAmmo() > 0 && (mouse.isPressed() || bulletRequested)) {
				bulletRequested = false;
				b_cmd.loadNewBullet(new Bullet(BulletStrategy.linear_strat, p.getAngle()));
				return b_cmd;
			}
			return null_cmd;
		}
		
		@Override
		public void update(String msg) {
			if (msg.equals("clicked"))
				bulletRequested = true;
			else if (msg.equals("started"))
				bulletRequested = false; //to stop bullet being from fired during next maze click
			//	precision = war.getMaze().getRows()/3;
		}

	}

	
	public static class WarKeyStrategy extends WarStrategy implements Observer {

		
		private final KeyCode codes[][] = {{KeyCode.LEFT, KeyCode.UP, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.M}, 
				{KeyCode.A, KeyCode.W, KeyCode.D, KeyCode.S, KeyCode.Q}};
		protected KeyBoard board;
		private boolean bulletRequested = false;
		
		public WarKeyStrategy(MazeWar war, int key) {
			super(war);
			this.reload = 10;
			this.board = war.addKeyboard(codes[key]);
			board.addObserver("strat", this);
			
		}

		public MoveCommand get_move(WarPlayer p) {
			if (board.codes_status[1])
				return m_cmd.init(p.get_pos(), final_pos.copy(p.get_pos()).add(p.get_translate(p.getAngle())), p.getRadius());
			else if (board.codes_status[3])
				return m_cmd.init(p.get_pos(), final_pos.copy(p.get_pos()).add(p.get_translate(p.getAngle()+180)), p.getRadius());
			else
				return m_cmd.init(init_pos.copy(p.get_pos()), final_pos.copy(p.get_pos()), p.getRadius());
		}
		
		private int precision = 4;
		public RotateCommand get_rotate(WarPlayer p) {
			int rotate = 0;
			int speed = p.getRotateSpeed();  //could use get_rotate(ang) instead no?
			ActivityReport report = p.getReport();
			if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1));
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p);
			else {
				if (board.codes_status[0]) //left key down
					if (!board.codes_status[1] && !board.codes_status[3]) //so top and bottom are not pressed, 
						//so lets be accurate and slow down rotation
						rotate += speed/precision;
					else
						rotate += speed;
				if (board.codes_status[2]) 
					if (!board.codes_status[1] && !board.codes_status[3])
						rotate -= speed/precision;
					else
						rotate -= speed;
			}
			
			return new RotateCommand(p.getAngle(), p.getAngle()+rotate);
		}
		
		public AttackCommand<WarPlayer, MazeWar> get_attack(WarPlayer p) {
			shot_counter = (shot_counter+1)%reload;
			if (shot_counter == 0 && p.getAmmo() > 0 && (board.codes_status[4] || bulletRequested)) {
				bulletRequested = false;
				b_cmd.loadNewBullet(new Bullet(BulletStrategy.linear_strat, p.getAngle()));
				return b_cmd;
			}
			return null_cmd;
		}

		@Override
		public void update(String msg) {
			if (msg.equals("4")) //pressed the shoot key, alternatively could check which key is pressed in loop
				bulletRequested = true;
		}
		
		
	}
	
	public static class WarBfsStrategy extends WarStrategy implements Observer {

		private WarMaze m;
		private MazeWar war;
		private ArrayList<Point> points = new ArrayList<Point>(); //path for player to follow
		private int cell_idx = 0;
		private Point dest = new Point();
		private ArrayList<WarPlayer> enemies; //to know if to shoot
		
		
		int detected_range = 15, covid_distance = 4;
		
		public WarBfsStrategy(MazeWar war) { //TODO: add WarPlayer p to constructor
			super(war);
			this.war = war;
			war.addObserver("strat", this);
			m = war.getMaze();
			
			this.enemies = new ArrayList<WarPlayer>();
			
			refreshEnemies();
			doBfs();
		}
		
		
		@Override
		public MoveCommand get_move(WarPlayer p) {
			
			//if reached the block that we wanted to reach then move on to next
			if (cell_idx+1 < points.size() && m.getPos(p.get_pos()).distance(points.get(cell_idx)) == 0) 
				cell_idx += 1;
			if (enemies.size() > 0 && getClosestEnemy(p).get_pos().distance(p.get_pos()) > p.getRadius()*covid_distance)
				return m_cmd.init(init_pos.copy(p.get_pos()), 
						final_pos.copy(p.get_pos()).add(p.get_translate(p.getAngle()).multiply(0.7)), p.getRadius()); //TODO: generalize 0.7
			else
				return m_cmd.init(init_pos.copy(p.get_pos()), final_pos.copy(p.get_pos()), p.getRadius());
		}
		
		private int rotate_counter = 0, angle = 0;
		@Override
		public RotateCommand get_rotate(WarPlayer p) {
		
			WarPlayer closest;
			
			
			if (rotate_counter == 0)
				angle = (int)(Math.random()*360); 
			
			if (enemies.size() > 0 && (closest = getClosestEnemy(p)) != null) {
				float d = closest.get_pos().distance(p.get_pos());
				if (d < p.getRadius()*detected_range) {
				//	if (d > p.getRadius()*covid_distance) {
					angle =  p.get_pos().deg_ang(closest.get_pos());
				//	}
				}
			}		
			
			int rotate = (int)(p.get_rotate(angle)/1.2); //instead have bot rotate speed reduced TODO 
			rotate_counter = (rotate_counter+1)%20;
			
			ActivityReport report = p.getReport();
			if (report.hit_x() && report.hit_y())
				rotate = (int)(turnVertical(p)*(Math.random()*3+1));
			else if (report.hit_x())
				rotate = turnVertical(p);
			else if (report.hit_y())
				rotate = turnHorizontal(p); //turn these to += ?
			else if (cell_idx < points.size()) { //rotate towards destination
				dest = points.get(cell_idx).flip().add(0.5, 0.5).multiply(m.getBlock_w(), m.getBlock_h());
				rotate = p.get_rotate( p.get_pos().deg_ang(dest));				
			}
												// rotate, for sharp instant turns
			return new RotateCommand(p.getAngle(), p.getAngle()+rotate); //if someone wants to save their rotatecommands, they better make a copy
			//or else it will be modified
		}
		
		
		public AttackCommand<WarPlayer, MazeWar> get_attack(WarPlayer p) {
			//if determine the bullet path at angle p.getangle will hit a human player 
		//	if (bullets_alive < p.getBulletLimit()) {
			shot_counter = (shot_counter+1)%reload;
			if (enemies.size() > 0 && shot_counter == 0 && p.getAmmo() > 0 && 
					p.get_pos().distance(getClosestEnemy(p).get_pos()) < p.getRadius()*detected_range) {
				b_cmd.loadNewBullet(new Bullet(BulletStrategy.linear_strat, p.getAngle()));
				return b_cmd; 
			}

			return null_cmd;
		}
		
		private WarPlayer getClosestEnemy(WarPlayer p) { //do bfs to get to the closest enemy player and path to it?
			WarPlayer closest = enemies.get(0);
			float closest_d = Short.MAX_VALUE;
			for (WarPlayer enemy: enemies) {
				float d = enemy.get_pos().distance(p.get_pos());
				if (d < closest_d) {
					closest_d = d;
					closest = enemy;
				}
			}
			return closest;
		}
		
	
		public void doBfs() { //could make this strategy observe mazewar, for any changes to players, maze etc.
		
		}

		public void refreshEnemies() {
			enemies.clear();
			for (WarPlayer wp: war.getPlayers())
				if (wp.getType().equals("human") && !wp.isDisabled())
					enemies.add(wp);
		}
		
		@Override
		public void update(String msg) {
			if (msg.equals("setted up")) {
				m = war.getMaze();
				refreshEnemies(); //because dead humans get revived when maze changes
				doBfs();
			}
			else if (msg.equals("humans alive")) { //enemies changed
				refreshEnemies();
			}
		}
	}

}
