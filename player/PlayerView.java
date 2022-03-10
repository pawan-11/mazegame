package player;

import util.Observer;

abstract public class PlayerView<P extends Player<?>> implements Observer {

	protected P player;
	
	public PlayerView(P player) {
		this.player = player;
		player.addObserver("view", this);
	}
	
	abstract protected void addContent();
	abstract protected void resize();
	abstract protected void move();
	abstract protected void updateTheme();
	abstract protected void rotate();
	abstract protected void updateOpacity();


	@Override
	public void update(String msg) {
		if (msg.equals("moved")) // moved  x,y
			move();
		else if (msg.equals("angle"))
			rotate(); //rotated
		else if (msg.equals("resized"))
			resize();
		else if (msg.equals("theme"))
			updateTheme();
		else if (msg.equals("transparency"))
			updateOpacity();
	}
	
	

}
