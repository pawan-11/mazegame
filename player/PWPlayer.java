package player;

import resources.PlayerTheme.PWPlayerTheme;
import util.Point;
import vc.GameMenu;
import strategy.PWStrategy;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;

public class PWPlayer extends Player<PWPlayerTheme> {

	private float speed;
	private int rotatespeed;
	private Point last_pos; //keep track of last position
	//private String msg_recieved; //personalized messages from princess?
	private PWStrategy strat;
	
	public PWPlayer(String type, PWStrategy pwstrat) {
		super(type);
		this.strat = pwstrat;
		this.last_pos = this.get_pos().copy();
	}
	
	public PWPlayer(String type, PWStrategy pwstrat, int id) {
		this(type, pwstrat);
		this.id = id;
	}
	
	@Override
	public Point get_translate(int deg_ang) {
		return translate.copy((float)(Math.cos(Math.toRadians(deg_ang))*speed),  -(float)(Math.sin(Math.toRadians(deg_ang))*speed));
	}

	@Override
	public void resize(int new_rad) {
		this.radius = new_rad;
		this.speed = (float)radius/9+GameMenu.pwspeed;
		this.rotatespeed = (int)Math.abs(130/(radius*1.3))+1; //should be around 5-20
		
		changed("resized");
	}

	@Override
	public void move(Point pos) {
		
		//if (!(this.pos.distance(pos) < 0.03)) {
		if (!(Math.abs(pos.getX()-this.pos.getX()) < 0.1 && Math.abs(pos.getY()-this.pos.getY()) < 0.1)) {  //0.1 or minspeed/40 			
			last_pos.copy(this.pos);
			this.pos.copy(pos);

			//report.moved = true;
			changed("moved");
		}
		else {
			//report.moved = false;
		}
	}
	
	
	@Override
	public void hitReport(boolean hit_x, boolean hit_y) { //hit vertical wall
		report.hit_x = hit_x;
		report.hit_y = hit_y;
	}

	public void reset_prev_pos() {
		last_pos.copy(pos);
	}
	
	public Point get_prev_pos() {
		return last_pos;
	}
	
	public int getRotateSpeed() {
		return rotatespeed;
	}
	
	public void rotate(int angle) { 
	//	int prev_ang = this.angle;
		super.rotate(angle);
	//	report.rotated = prev_ang == this.angle; //strat does not use it yet
	}

	public int get_rotate(float angle2) {
		int rotate = 0; //moves toward angle2 in degrees
		float angle1 = this.getAngle();

		if (Math.abs(angle1-angle2)< rotatespeed) {
			rotate = 0;		
		}
		else if (angle1 - angle2 > 180) {
			rotate = rotatespeed;
		}
		else if (angle1 - angle2 <= -180) {
			rotate = -rotatespeed;
		}
		else if (angle1 > angle2) {
			rotate = -rotatespeed;
		}
		else 
			rotate = rotatespeed;
		return rotate;
	}
	
    public MoveCommand getMoveCmd() {
		return strat.get_move(this);
	}
    
	@Override
	public MoveCommand getMoveCmd(int deg_ang) {
		return strat.get_move(this, deg_ang);	
	}
	
	public RotateCommand getRotateCmd() {
		return strat.get_rotate(this);
	}
	
	public MoveCommand getSlideMove(MoveCommand cmd) {
		return strat.slide_move(this, cmd);
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
}
