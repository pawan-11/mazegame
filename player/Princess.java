package player;

import java.util.ArrayList;
import resources.PlayerTheme.PrincessTheme;
import javafx.geometry.Point2D;
import player.Player;
import strategy.Command.MoveCommand;
import util.Point;

public class Princess extends Player<PrincessTheme> {
	
	private Point size = new Point(10, 15);

	public Princess() {
		super("princess");
	}
	
	public boolean reactTo(ArrayList<? extends Player<?>> players) {
		boolean found = false;
		for (Player<?> p: players)
			found = reactTo(p) || found;
		return found;
	}
	
	public boolean reactTo(Player<?> p) { //hugs p if p is close	
		return (pos.getX() < p.get_pos().getX() && p.get_pos().getX() < pos.getX()+size.getX() &&
				p.get_pos().getY() > pos.getY() && p.get_pos().getY() < pos.getY()+size.getY());
	}
	
	public int getDistance(Player<?> p) {
		return (int)p.get_pos().distance(pos);
	}
	
	public void resize(double width, double height) {
		size.copy((int)Math.ceil(width), (int)Math.ceil(height));
		changed("resized");
	}
	
	public void resize(Point size) {
		resize(size.getX(), size.getY());
	}
	
	public void move(Point pos) {
		this.pos.copy(pos);
		changed("moved");
	}
	
	public void rotate(int angle) {
		this.angle = angle;
		changed("rotated");
	}
	
	private Point size_copy = new Point();
	public Point getSize()  {
		return size_copy.copy(size);
	}

	@Override
	public Point get_translate(int deg_ang) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hitReport(boolean hit_x, boolean hit_y) {
		
	}

	@Override
	public void resize(int new_rad) { //fix, by generalizing player to rectangle, instead of circle
		this.radius = new_rad;
		changed("resized");
	}

	@Override
	public MoveCommand getMoveCmd(int deg_ang) {
		return null;
	}

	@Override
	public MoveCommand getMoveCmd() {
		return null;
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
}

