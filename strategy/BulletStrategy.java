package strategy;

import gamemode.GameMode;
import player.Bullet;
import strategy.Command.MoveCommand;
import strategy.Strategy.Mover;
import util.Point;
import util.Util;

public class BulletStrategy implements Mover<Bullet> {

	public static BulletStrategy linear_strat = new BulletStrategy(); //the normal linear bouncy bullet strat most bullets can use
	protected Point final_pos = new Point();
	protected MoveCommand m_cmd = new MoveCommand();
	
	private BulletStrategy() {
	}
	
	@Override
	public MoveCommand get_move(Bullet b) {	
		int angle = b.getAngle();

		if (b.getReport().hit_y()) { //hits horizontal wall
			int new_ang = 0;
			if (angle >= 270) {
				new_ang = 360-angle;
			}
			else if (angle >= 180) { 
				new_ang = 180-(angle-180);
			}
			else if (angle >= 90) {
				new_ang = 180+180-angle;
			}
			else {
				new_ang = 360-(angle);
			}
			b.rotate(new_ang);
		}
		else if (b.getReport().hit_x()) { //hit vertical wall
			int new_ang = 0;
			if (angle >= 270) {
				new_ang = 180+360-angle;
			}
			else if (angle >= 180) {
				new_ang = 360-(angle-180);
			}
			else if (angle >= 90) {
				new_ang = 180-angle;
			}
			else {
				new_ang = 180-angle;
			}

			b.rotate(new_ang);
		}
		
		return get_move(b, b.getAngle());
	}

	@Override
	public MoveCommand get_move(Bullet b, int deg_ang) {
		return m_cmd.init(b.get_pos(), final_pos.copy(b.get_pos()).add(b.get_translate(deg_ang)), b.getRadius());
	}
}
