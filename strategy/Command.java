package strategy;

import gamemode.GameMode;
import gamemode.MazeWar;
import player.Bullet;
import player.Player;
import player.WarPlayer;
import resources.Theme;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;
import util.Point;
import util.Util;

public interface Command<P extends Player<?>> {

	public void execute(P p);
	
	public class MoveCommand implements Command<Player<?>> {

		private Point init_pos, final_pos;
		private int radius;
		private boolean hit_x = false, hit_y = false;
		
		//move circle of radius from init_pos to final_pos
		public MoveCommand(Point init_pos, Point final_pos, int radius) {  
			init(init_pos, final_pos, radius);
		}
		
		public MoveCommand() {
			this(new Point(), new Point(), 0);
		}
		
		public MoveCommand init(Point init_pos, Point final_pos, int radius) {
			this.init_pos = init_pos;
			this.final_pos = final_pos;
			this.radius = radius;
		//	Util.print("mc init "+ getInitPos()+" final:"+getFinalPos()+"\n");
			return this;
		}
		
		public void changeX(float final_x) {
			float old_d = init_pos.distance(final_pos);
			final_pos.setX(final_x);
			if (init_pos.distance(final_pos) < old_d) //check if distance is reduced or increased
				hit_x = true;
		}
		
		public void changeY(float final_y) {
			float old_d = init_pos.distance(final_pos);
			final_pos.setY(final_y);
			if (init_pos.distance(final_pos) < old_d)
				hit_y = true;
		}
			
		public void execute(Player<?> p) {	
			p.move(final_pos);
			
			p.hitReport(hit_x, hit_y);
			hit_x = (hit_y = false);
		}
		
		public int getRadius() {
			return radius;
		}
		
		private Point init_pos_copy = new Point();
		public Point getInitPos() {
			return init_pos_copy.copy(init_pos);
		}
		
		private Point final_pos_copy = new Point();
		public Point getFinalPos() {
			return final_pos_copy.copy(final_pos);
		}
		
		public boolean stoppedX() {
			return hit_x;
		}
		
		public boolean stoppedY() {
			return hit_y;
		}
	}

	
	public class RotateCommand implements Command<Player<?>> {
		
		private int init_angle, final_angle; //init_angle if rotatecommand should be edited back to init_angle
		
		public RotateCommand(int init_angle, int final_angle) { //in degrees
			init(init_angle, final_angle);
		}
		
		public RotateCommand() {
			this(0,0);
		}
		
		public RotateCommand init(int init_angle, int final_angle) {
			this.init_angle = init_angle;
			this.final_angle = final_angle;
			return this;
		}
		
		public void execute(Player<?> p) {	
			p.rotate(final_angle);
		}
	}
	
	public interface AttackCommand<P extends Player<?>, G extends GameMode<?,?,?>> {
		
		public void execute(P p, G mode);
		
	}
	
	public class NoAttackCommand<P extends Player<?>, G extends GameMode<?,?,?>> implements AttackCommand<P, G> {
		@Override
		public void execute(P p, G mode) {
		}
	}
	
	public class BulletCommand implements AttackCommand<WarPlayer, MazeWar> { 

		private Bullet b; // = new Bullet(BulletStrategy.strat, 90);
		
		public BulletCommand() {
			
		}
		
		public void loadNewBullet(Bullet b) {
			this.b = b;
		}
		
		Point pt = new Point(50,50);
		@Override
		public void execute(WarPlayer p, MazeWar mode) {
			if (p.addBullet(b))
		//	b.move(pt);
		//	b.resize(20);
		//	b.setTheme(Theme.bullet_theme);
			mode.addBullet(b);
		}
	
		
	}
	
}

