package player;

import util.Observable;
import util.Point;
import util.Util;
import resources.PlayerTheme;
import strategy.Command.MoveCommand;

abstract public class Player<PT extends PlayerTheme> extends Observable {
	
	protected Point pos;
	protected int radius;
	protected int angle = 0; 
	protected PT theme;
	protected int id = 0;
	protected String type; //, name;
	protected boolean disable = false;
	protected boolean transparent = false; //true for when this player overlaps with another
	protected ActivityReport report;

	protected Point translate = new Point();
	
	public Player(String type) {
		this.type = type;
		pos = new Point(20,20);
		this.report = new ActivityReport();
	}
	
	 //rotate command uses this	
	public void rotate(int angle) {
		//TODO: maybe adding if statement to check if angle is new, will be more efficient
		angle = (int)Math.floorMod(angle, 360);
		if (this.angle != angle) {
			this.angle = angle;
			changed("angle");
		}
	}
	
	public void hitReport(boolean hit_x, boolean hit_y) { //reported by movecommand in execution
		report.hit_x = hit_x;
		report.hit_y = hit_y;
	}
	
	public int getAngle() {	
		return angle;
	}
	
	public int getRadius() {
		return radius;
	}
	
	private Point pos_copy = new Point();
	public Point get_pos() {
		return pos_copy.copy(this.pos); //returning a copy of position, so outsiders do not modify
	}	
	
	public PT getTheme() {
		return theme;
	}
	
	public void setTheme(PT theme) {
		//if (this.theme != theme) {
		this.theme = theme;
		changed("theme"); //tells view to adapt theme
	}
	
	public void setId(int id) { //id used to assign the desired color theme
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public ActivityReport getReport() { 
		return report;
	}
	
	public void setDisabled(boolean disable) {
		this.disable = disable;
		changed("disable");
	}

	public boolean isDisabled() {
		return disable;
	}
	
	public void setTransparent(boolean tp) { //change to opacity for multiple players overlap?
		this.transparent = tp; //opacity/=2
	//	changed("transparency");
	}
	
	public boolean isTransparent() {
		return transparent;
	}
	
	public void setknocked(boolean k) {
		this.knocked = k;
	}
	
	private boolean knocked = false;
	public boolean isKnocked() {
		return knocked;
	}
	
	public boolean reactTo(Player<?> p) { //true if player p is overlapping with position
		return p.get_pos().distance(get_pos()) < radius*2; //1.7
	}
	
	abstract public MoveCommand getMoveCmd();
	abstract public MoveCommand getMoveCmd(int deg_ang);
	abstract public Point get_translate(int deg_ang);
	abstract public void resize(int new_rad);
	abstract public void move(Point pos);
	
	public class ActivityReport { //report of this player's last actions
		protected boolean hit_x = false, hit_y = false;
		private ActivityReport() {}
		public boolean hit_x() {return hit_x;}
		public boolean hit_y() {return hit_y;}
		public String toString() {
			return "hit_x: "+hit_x+" hit_y:"+hit_y;
		}
	}
}

