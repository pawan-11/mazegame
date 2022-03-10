package player;

import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import resources.PlayerTheme.BulletTheme;
import util.Util;
import vc.GameMenu;

public class BulletView extends PlayerView<Bullet> {

	private ImageView ballv;
	
	public BulletView(Bullet b) {
		super(b);

		addContent();
		addLayout();
		resize();
		
		updateTheme();
		rotate();
		move();
	}
	
	protected void addContent() {
		ballv = new ImageView();
	}
	
	protected void addLayout() {
		if (GameMenu.lowQ) { 
			ballv.setCache(true);
		//	ballv.setCacheHint(CacheHint.SPEED); //TODO: test which cache is best, speed makes view invisible on 90 degrees
			ballv.setSmooth(false);
		}
	}
	
	
	protected void resize() {
		ballv.setFitWidth(player.getRadius()*2);
		ballv.setFitHeight(player.getRadius()*2);
	}
	
	public ImageView getBall() {
		return ballv;
	}
	
	@Override
	public void update(String msg) {
		super.update(msg);
		if (msg.equals("disable")) {
			ballv.setVisible(!player.isDisabled());
		}
	}
	
	protected void rotate() {
		ballv.setRotate(player.getAngle());
	}
	
	protected void updateTheme() { 
		ballv.setImage(player.getTheme().getBall());
	}	

	protected void move() {
		ballv.setTranslateX(player.get_pos().getX()-player.getRadius());
		ballv.setTranslateY(player.get_pos().getY()-player.getRadius());
	
	}

	protected void updateOpacity() {
		if (player.isTransparent())
			ballv.setOpacity(0.8);
		else
			ballv.setOpacity(1);
	}
}
