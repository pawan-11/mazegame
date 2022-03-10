package util;

import util.Point;

import java.util.ArrayList;

import player.Player;
import util.Observable;

public class Goal extends Observable {
	
	private Point goal_pos, goal_size;
	private int degrees = 0;

	public Goal() {
		goal_pos = new Point(50,50);
		goal_size = new Point(30,20);
	}
	
	private Point adjust_pos = new Point();
	private void adjust(Player<?> p) { //p scored
		
		if (degrees == 0)
			p.move(adjust_pos.copy(goal_pos.getX()+p.getRadius(),goal_pos.getY()+goal_size.getY()-p.getRadius()));
		else if (degrees == 90)
			p.move(adjust_pos.copy(goal_pos.getX()+goal_size.getX()/2-p.getRadius()/2, goal_pos.getY()+p.getRadius()));
		else if (degrees == 180)
			p.move(adjust_pos.copy(goal_pos.getX()+goal_size.getX()-p.getRadius(), goal_pos.getY()+goal_size.getY()-p.getRadius()));
		else if (degrees == -90)
			p.move(adjust_pos.copy(goal_pos.getX()+goal_size.getX()-p.getRadius(), goal_pos.getY()+goal_size.getY()-p.getRadius()));
	}

	public boolean reactTo(Player<?> p) {
	//	Point pos = p.get_pos();
		if (goal_pos.getX() < p.get_pos().getX() && p.get_pos().getX() < goal_pos.getX()+goal_size.getX() &&
				goal_pos.getY() < p.get_pos().getY() && p.get_pos().getY() < goal_pos.getY()+goal_size.getY()) {
			adjust(p);
			return true;
		}
		return false;
	}
	
	public boolean reactTo(ArrayList<? extends Player<?>> players) {
		boolean inGoal = false;
		for (Player<?> p: players) {
			inGoal = reactTo(p) || inGoal;
		}
		return inGoal;
	}
	
	public void resize(int width, int height) {
		goal_size.copy(width, height);
		changed("resized");
	}
	
	public void move(Point pos) {
		this.goal_pos.copy(pos);
		changed("moved");
	}
	
	public void rotate(int degrees) {
		this.degrees = degrees;
		changed("rotated");
	}
	
	public Point get_pos()  {
		return goal_pos;
	}
	
	public Point getSize()  {
		return goal_size;
	}
	
	public int getDegrees()  {
		return degrees;
	}
	
	public String toString() {
		
		return "Goal pos:"+goal_pos+" size:"+goal_size;
	}

	@Override
	public void create_lists() {
		super.create_list("view", 1); //only one goal view allowed to observe
	}
}

