package player;

import gamemode.MazeWar;
import resources.Theme;
import resources.PlayerTheme.WarPlayerTheme;
import util.Observer;
import util.Point;
import util.Util;
import vc.GameMenu;
import strategy.WarStrategy;
import strategy.Command.AttackCommand;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;

public class WarPlayer extends Player<WarPlayerTheme> { 

	private float speed;
	private int rotatespeed;
	private WarStrategy strat;
	private int ammo, bullet_limit;
	private int health, max_health;
	private int recover_time, recover_counter; //time to recover and start healing after taking damage
	
	
	public WarPlayer(String type, WarStrategy strat) { //type is human, bot, etc
		super(type);
		
		setStrategy(strat);
		rotate(0);
		setMaxHealth(100);
		setAmmo(10);
	}
	
	public WarPlayer(String type, WarStrategy warstrat, int id) {
		this(type, warstrat);
		this.setId(id);
	}
	
	public boolean reactTo(Bullet b) { 
		//TODO: add special case for it bullet is about to die (at its move limit), then use its explosion radius
		return Math.abs(b.get_pos().getX()-this.pos.getX()) < this.radius &&    //DAM U v
				Math.abs(b.get_pos().getY()-this.pos.getY()) < this.radius && b.getDistance() > this.radius*2;
	}
	
	@Override
	public Point get_translate(int deg_ang) {
		return translate.copy((float)(Math.cos(Math.toRadians(deg_ang))*speed), -(float)(Math.sin(Math.toRadians(deg_ang))*speed));
	}
	
	@Override
	public void resize(int new_rad) {
		this.radius = new_rad;
		
		resetSpeed();
		this.rotatespeed = (int)Math.abs(140/(radius))+1; 
		
		changed("resized");
	}

	@Override
	public void move(Point pos) {
		
		if (!(Math.abs(pos.getX()-this.pos.getX()) < 0.1 && Math.abs(pos.getY()-this.pos.getY()) < 0.1)) {  //0.1 or minspeed/40 
			this.pos.copy(pos);
			changed("moved");
		}
	}
	
	public void setup(Bullet b, Point pos) {
		b.move(get_pos());
		b.resize(radius/3+1);
	}
	
	public void multiplySpeed(double x) {
		this.speed *= x;
	}
	
	public void addSpeed(double x) {
		this.speed += x;
		if (this.speed < 0)
			this.speed = 0;
	}
	
	public void resetSpeed() {
		this.speed = (float)radius/8+GameMenu.warspeed; //15
	}
	
	public void setHealth(int new_health) {
		if (this.health != new_health ) {
			
			if (new_health <= 0) {
				new_health = 0;
				this.setDisabled(true);
			}
			else if (new_health > max_health)
				new_health = max_health;
			if (new_health < this.health)
				recover_counter = recover_time; //needs to recover before healing
			
			health = new_health; //could add shield protection by some percentage
			changed("health");
		}
	}
	
	public void setMaxHealth(int new_max_health) {
		if (new_max_health > 0)
			this.max_health = new_max_health;
		setHealth(health); //update
	}
	
	public boolean addBullet(Bullet b) { //add this bullet to the chamber
		if (getAmmo() > 0) {
			setAmmo(ammo-1);
			setup(b, get_pos());
		//	b.addObserver("shooter", this); //if want to limit bullets per player, use maybe in another mode
			return true;
		}
		return false;
	}
	
	public void heal(double percentage) { //add percentage of max health, precondition: 0 <= percentage <= 1
		if (recover_counter == 0) //recovered
			setHealth(getHealth()+(int)Math.round(percentage*this.max_health));
	}
	
	public void revive() {
		setHealth(this.max_health);
		recover_counter = 0;
		this.setDisabled(false);
	}

	private void setAmmo(int ammo) {
		if (ammo < 0)
			ammo = 0;
		else if (ammo > this.getBulletLimit())
			ammo = getBulletLimit();
		if (ammo != this.ammo) {
			this.ammo = ammo;
			changed("ammo");
		}
	}
	
	public void incrementRecoverCounter() {
		if (recover_counter != 0)
			recover_counter--;
	}
	
	public void setBulletLimit(int new_limit) {
		this.bullet_limit = new_limit;
	}
	public void setRecoverTime(int recover_time) {
		this.recover_time = recover_time;
	}
	public void setStrategy(WarStrategy strat) {
		this.strat = strat;
	}
	public void reload() {
		setAmmo(this.getBulletLimit());
	}
	public void reload(double percentage) {
		setAmmo(getAmmo()+(int)Math.round(percentage*bullet_limit));
	}
	public void reload(int ammo) {
		setAmmo(this.ammo+ammo);
	}
	
	public int get_rotate(float angle2) {
		int rotate = 0; //moves toward angle2 in degrees
		float angle1 = this.getAngle();
	//	if (Math.abs(angle1-angle2) < rotatespeed) 
	//		rotate = 0;
		
		if (angle1 - angle2 > 180) {
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
	
	public int getRotateSpeed() {
		return rotatespeed;
	}
    
	public WarStrategy getStrat() {
		return strat;
	}
	
	@Override
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
	
	public AttackCommand<WarPlayer, MazeWar> getAttackCmd() {
		return strat.get_attack(this);
	}
	
	
	public int getBulletLimit() {
		return bullet_limit;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public int getHealth() {
		return health;
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
	
}
