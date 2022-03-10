package player;

import resources.PlayerTheme.MWPlayerTheme;
import strategy.Command.MoveCommand;
import strategy.MWStrategy;
import util.Point;
import util.Util;
import vc.GameMenu;

public class MWPlayer extends Player<MWPlayerTheme> {

	protected int rotatev; //base rotation speed
	protected boolean show_propellor = false;
	protected boolean show_parachute = false;	
	protected int falling_moves = 0;
	protected float minspeed, maxspeed, mindownspeed;
	protected float upspeed, rightspeed, leftspeed, downspeed, acc;
	private MWStrategy strat;
	
	public MWPlayer(String type, MWStrategy mwstrat) {
		super(type);
		this.strat = mwstrat;
	}
	
	public MWPlayer(String type, MWStrategy mwstrat, int id) {
		this(type, mwstrat);
		setId(id);	
	}
	
//	public boolean reactTo(Player<?> p) { 
//		return p.get_pos().distance(get_pos()) < radius*1.7; //1.7
//	}
	
	public void move(Point pos) {
		if (Math.abs(pos.getX()-this.pos.getX()) < 0.005 && Math.abs(pos.getY() - this.pos.getY()) < 0.005) {
			rotate(angle+rotatev/4);
			hitReport(true, true);
		}
		else {
			accelerate(this.pos.deg_ang(pos));
			this.pos.copy(pos);
			changed("moved");		
		}
	}
	
	public void resize(int new_rad) {
		this.radius = new_rad;
		
		acc = (float)(radius*0.03); //can change to adjust to maxspeed-minspeed
		this.maxspeed = (float)(radius/2+GameMenu.mwspeed); //0.5 not 1?
		this.minspeed = (float)(radius/5+GameMenu.mwspeed);
		mindownspeed = (float)(radius/15+GameMenu.mwspeed);
		downspeed = mindownspeed;
		
		rotatev = 15; //(float)(radius/1.5);
		
		hitReport(true, true);
		
		changed("resized");
	}
	
	/** 
	 * @param angle player is now moving in this direction, 0 <= angle <= 359
	 */
	public void accelerate(int angle) {
		int rotation = 0;
		
		if (angle < 45 || angle > 315) {

			if (rightspeed < maxspeed) {
				rightspeed += acc;
				rotation -= rotatev/2;
			}
			leftspeed = minspeed;
			rotation += 3*rotatev/2;
		}
		else if (angle < 215 && angle > 135) {
			if (leftspeed < maxspeed) {
				leftspeed += acc;
				rotation += rotatev/2; //rotatev
			}
			rotation += 3*rotatev/2;
			rightspeed = minspeed;
		}
		if (angle > 180) {
			
			if (downspeed < 1.5*maxspeed) {	
				downspeed += acc;
				rotation += rotatev/3;
			}

			rotation = rotatev;	
			update(true, false);
			upspeed = minspeed;
		}
		else  { 
			if (upspeed < maxspeed) {
				upspeed += acc;
				rotation -= rotatev/2;
			}
			update(false, true);
			rotation -= rotatev/2;
			downspeed = mindownspeed;
		}
		
		rotate((int)(this.angle+rotation));
	}
	
	public void hitReport(boolean hit_x, boolean hit_y) {
		if (hit_x) {
			leftspeed = minspeed;
			rightspeed = minspeed;
			rotate(angle+rotatev/3);
		}
		if (hit_y) {
			upspeed = minspeed;
			downspeed = mindownspeed;
			rotate(angle-rotatev/3);
			update(false, false);
		}
	}
	
	
	public void update(boolean show_parachute, boolean show_propellor) {
		falling_moves = show_parachute?falling_moves+1:0;
		this.show_parachute = falling_moves > 10?true:false;
		this.show_propellor = show_propellor;
		
		changed("enhancers");
	}
	
	
	public Point get_translate(int deg_ang) {
		double rad_ang = Math.toRadians(deg_ang);
		deg_ang = Math.floorMod(deg_ang, 360);
		
		float translate_x = (deg_ang < 90 || deg_ang > 270)?(float)(Math.cos(rad_ang)*rightspeed): (float)(Math.cos(rad_ang)*leftspeed);
		float translate_y = deg_ang < 180?-(float)(Math.sin(rad_ang)*upspeed):-(float)(Math.sin(rad_ang)*downspeed);
		return translate.copy(translate_x, translate_y);
	}
	
	public boolean show_parachute() {  // better name: free_falling
		return show_parachute;
	}

	public boolean show_propellor() {  // propelling up
		return show_propellor;
	}
	
	public void setDisabled(boolean disable) {
		super.setDisabled(disable);
		update(false, false);
	}

	public MoveCommand getMoveCmd() {
		return strat.get_move(this);
	}
	
	public MoveCommand getMoveCmd(int deg_ang) {
		return strat.get_move(this, deg_ang);
	}
	 
	public void slideMove(MoveCommand cmd) {
		strat.slide_move(this, cmd);
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 5);
	}
}
