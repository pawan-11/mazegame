package player;

import resources.PlayerTheme.WarPlayerTheme;
import strategy.Command.MoveCommand;
import util.Point;

abstract public class Powerup extends Player<WarPlayerTheme> {

	public Powerup() {
		super("powerup");
	}
		
	@Override
	public boolean reactTo(Player<?> p) {
		return false;
	}

	@Override
	public MoveCommand getMoveCmd() {
		return null;
	}
	
	@Override
	public MoveCommand getMoveCmd(int deg_ang) {
		return null;
	}
	
	@Override
	public Point get_translate(int deg_ang) {
		return null;
	}

	@Override
	public void resize(int new_rad) {		
	}

	@Override
	public void move(Point pos) {		
	}
	
	@Override
	public void create_lists() {			
	}
	
	//abstract public int getLifeSpan();

	public static class Speedup extends Powerup { //move faster
	
		public Speedup() {
			
		}
		
		public boolean reactTo(WarPlayer p) {
			if (super.reactTo(p)) {
				p.multiplySpeed(1.5);
				return true;
			}
			return false;
		}

	
	}
	
	public static class Fastfire extends Powerup { //shootfaster
	
		public Fastfire() {
			
		}
	}
	
	public static class Revive extends Powerup { //shootfaster
		 
		public Revive() {
			
		}
		
		public boolean reactTo(WarPlayer p) {
			if (super.reactTo(p)) {
				p.heal(0.5);
				return true;
			}
			return false;
		}
	}
	
	public static class Reload extends Powerup { //shootfaster
		
		public Reload() {
			
		}
	}
	
}
