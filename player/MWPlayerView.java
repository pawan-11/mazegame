package player;

import resources.PlayerTheme.MWPlayerTheme;
import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;
import player.MWPlayer;
import util.Point;
import vc.GameMenu;

public class MWPlayerView extends PlayerView<MWPlayer> {

	private ImageView ballv, propellor, parachute;
	
	public MWPlayerView(MWPlayer player) {
		super(player);

		addContent();
		addLayout();
		resize();
		
		update_enhancers();
		updateTheme();
		rotate();
		move();
	}
	
	protected void addContent() {
		ballv = new ImageView();
		propellor = new ImageView();	
		parachute = new ImageView();		
	}
	
	protected void addLayout() {
		//ballv.setVisible(false);
		//propellor.setVisible(false);
		//parachute.setVisible(false);
		
		if (GameMenu.lowQ) {
			ballv.setCache(true);
			ballv.setCacheHint(CacheHint.ROTATE);
			ballv.setSmooth(false);
			parachute.setCache(true);
			parachute.setCacheHint(CacheHint.SPEED);
			parachute.setSmooth(false);
			propellor.setCache(true);
			propellor.setCacheHint(CacheHint.SPEED);
			propellor.setSmooth(false);
		}
	}
	
	
	protected void resize() {
		
		float rad = player.getRadius();
		ballv.setFitWidth(rad*2);
		ballv.setFitHeight(rad*2);
		
		propellor.setFitWidth(rad*3);
		propellor.setFitHeight(rad*2);
	
		parachute.setFitWidth(rad*2);
		parachute.setFitHeight(rad*4);
	}
	
	public ImageView getBall() {
		return ballv;
	}
	
	public ImageView getParachute() {
		return parachute;
	}
	
	
	
	@Override
	public void update(String msg) {
		//System.out.println(msg);
		super.update(msg);
		if (msg.equals("enhancers"))
			update_enhancers();
	}
	
	protected void rotate() {
		ballv.setRotate(player.getAngle());
	
	}
	
	protected void update_enhancers() {
	//	propellor.setVisible(player.show_propellor());
		parachute.setVisible(player.show_parachute());
	}
	
	protected void updateTheme() { 
		ballv.setImage(player.getTheme().getBall());
		parachute.setImage(player.getTheme().getParachute());
		propellor.setImage(player.getTheme().getPropellor());
	}	

	protected void move() {
		ballv.setTranslateX(player.get_pos().getX()-player.getRadius());
		ballv.setTranslateY(player.get_pos().getY()-player.getRadius());

	//	propellor.setTranslateX(player.get_pos().getX()-propellor.getFitWidth()/2);
	//	propellor.setTranslateY(player.get_pos().getY()-player.getRadius()*3);
		parachute.setTranslateX(player.get_pos().getX()-parachute.getFitWidth()/2);
		parachute.setTranslateY(player.get_pos().getY()-player.getRadius()*4);	
	}

	protected void updateOpacity() {
		if (player.isTransparent())
			ballv.setOpacity(0.8);
		else
			ballv.setOpacity(1);
	}
}
