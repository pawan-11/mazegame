package strategy;

import gamemode.GameMode;
import player.Player;
import strategy.Command.AttackCommand;
import strategy.Command.MoveCommand;
import strategy.Command.RotateCommand;
import util.Point;

abstract public class Strategy {
	
	//variable local to slide_move
	protected static Point slide_pos = new Point();
	
	//slide cmd because it hit a wall
	public static void slide_move(Player<?> p, MoveCommand cmd, double power) {
		slide_pos.copy(cmd.getFinalPos());
		int angle = cmd.getInitPos().deg_ang(slide_pos);
		
		if (cmd.stoppedX())  { //stopped in horizontal direction, so hit vertical wall {
			if (angle < 180)
				slide_pos.add(p.get_translate(90).multiply(power)); //power/2 or power*2 or power
			else
				slide_pos.add(p.get_translate(270).multiply(power));
		}
		if (cmd.stoppedY()) { //hit horizontal wall
			if (angle < 90 || angle > 270)
				slide_pos.add(p.get_translate(0).multiply(power));
			else
				slide_pos.add(p.get_translate(180).multiply(power));
		}
		cmd.init(cmd.getInitPos(), slide_pos, p.getRadius()); //TODO, cmd 
	}
	
	public interface Mover<P extends Player<?>> {
		public MoveCommand get_move(P p);
		public MoveCommand get_move(P p, int rad_ang);
	}
	
	public interface Rotator<P extends Player<?>> {
		public RotateCommand get_rotate(P p); 
	}
	
	public interface Attacker<P extends Player<?>, G extends GameMode<?,?,?>> {
		public AttackCommand<P, G> get_attack(P p); 
	}
	
}