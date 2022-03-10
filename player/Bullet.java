package player;

import resources.PlayerTheme.BulletTheme;
import util.Point;
import util.Util;
import strategy.BulletStrategy;
import strategy.Command.MoveCommand;

public class Bullet extends Player<BulletTheme> {

	
	private float speed;
	private BulletStrategy strat; //default its a linear bullet
	private int damage, move_limit, moved; //the amount times bullet has moved, used to determine lifespan
	private int distance = 0;
	//could add a milliseconds delay time to release the bullet
	
	public Bullet(BulletStrategy bstrat, int angle) {
		super("bullet"); //one observer for a view, another for the player that shot it
		this.strat = bstrat;
		this.angle = angle;
		this.moved = 0;
		this.move_limit = 200;
	}
	
	@Override
	public Point get_translate(int angle) {
		translate.copy((float)(Math.cos(Math.toRadians(angle))*speed), -(float)(Math.sin(Math.toRadians(angle))*speed));
		return translate;
	}

	@Override
	public void resize(int new_rad) {
		this.radius = new_rad;		
		this.speed = (float)(radius*1.5)+1;		
		changed("resized");
	}

	float d;
	@Override
	public void move(Point pos) {
		d = (float)Math.sqrt(Math.pow(Math.abs(pos.getX()-this.pos.getX()), 2)+
				Math.pow(Math.abs(pos.getY()-this.pos.getY()), 2));

		//if (!(Math.abs(pos.getX()-this.pos.getX()) < 0.1 && Math.abs(pos.getY()-this.pos.getY()) < 0.1)) {
		if (d > 0.01) {
			if (d < speed*2) {
				this.distance += d;
				moved += 1;
				if (moved > move_limit)
					this.setDisabled(true);
			}
			this.pos.copy(pos);
			changed("moved");
		}
	}
	
	public void setBulletDamage(int damage) {
		this.damage = damage;
		//changed("damage"); no visual effects on view yet that depend on bullet damage
	}
	
	public void setMoveLimit(int move_limit) { //or set distance limit
		this.move_limit = move_limit;
	}

	public void resetDistance() {
		this.distance = 0;
	}
	
	public int getDistance() { //return distance travelled
		return this.distance;
	}
	
	public BulletStrategy getStrat() {
		return strat;
	}
	
    public MoveCommand getMoveCmd() {
		return strat.get_move(this);
	}
    
	@Override
	public MoveCommand getMoveCmd(int deg_ang) {
		return strat.get_move(this, deg_ang);	
	}
	
	public int getDamage() {
		return damage;
	}
	
	public ActivityReport getReport() {
		return report;
	}
	
	public void setDisabled(boolean disable) {
		super.setDisabled(disable);
		//reset distance if want to reuse
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	//	super.create_list("shooter", 1);
	}
	
}
