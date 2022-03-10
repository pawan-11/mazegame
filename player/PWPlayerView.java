package player;

import resources.PlayerTheme.PWPlayerTheme;
import util.Point;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import player.PWPlayer;
import vc.GameMenu;

public class PWPlayerView extends PlayerView<PWPlayer> {

	private ImageView ballv;
	
	public PWPlayerView(PWPlayer player) {
		super(player);
		
		addContent();
		resize();
		updateTheme();
		rotate();
		move();
	}

	@Override
	protected void addContent() {
		ballv = new ImageView();
        if (GameMenu.lowQ) {
        	ballv.setCache(true);
        	ballv.setCacheHint(CacheHint.DEFAULT);
        	ballv.setSmooth(false);
        }
	}
	
	public ImageView getBall() {
		return ballv;
	}
	

	@Override
	protected void rotate() {
		ballv.setRotate(-player.getAngle());		
	}
	
	@Override
	protected void resize() {
		float rad = player.getRadius();		
		
		ballv.setFitWidth(rad*3); //*3
		ballv.setFitHeight(rad*3); //*3
	}

	@Override
	protected void move() {
		ballv.setTranslateX(player.get_pos().getX()-ballv.getFitWidth()/2);
		ballv.setTranslateY(player.get_pos().getY()-ballv.getFitWidth()/2);
	}

	@Override
	protected void updateTheme() {
		ballv.setImage(player.getTheme().getBall());
	}

	protected void updateOpacity() {
		if (player.isTransparent())
			ballv.setOpacity(0.8);
		else
			ballv.setOpacity(1);
	}
}
