package player;

import util.Point;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import util.Observer;
import resources.Images;
import resources.PlayerTheme.PrincessTheme;

public class PrincessView extends PlayerView<Princess> implements Observer {
	
	private ImageView prinv;	
	//private Princess prin;
	
	public PrincessView(Princess prin) {
		super(prin);
	//	this.prin = prin;
		
		addContent();
		fixlayout();
		move();
		resize();
	}
	
	protected void addContent() {
		prinv = new ImageView(Images.princess);
	}

	private void fixlayout() {

		DropShadow s = new DropShadow();
		s.colorProperty().set(Color.WHITE);
		s.setBlurType(BlurType.GAUSSIAN);
		
		prinv.setEffect(s);
		prinv.setPreserveRatio(true);
	}

	@Override
	protected void resize() {		
		if (player.getSize().getX() < player.getSize().getY())
			prinv.setFitWidth(player.getSize().getX());
		else
			prinv.setFitHeight(player.getSize().getY());		
	}

	public ImageView getPrinV() {
		return prinv;
	}
	
	@Override
	protected void move() {
		prinv.setTranslateX(player.get_pos().getX()+(player.getSize().getX()-prinv.getLayoutBounds().getWidth())/2);
		prinv.setTranslateY(player.get_pos().getY()+(player.getSize().getY()-prinv.getLayoutBounds().getHeight())/2); 	
	}

	@Override
	protected void updateTheme() {
		prinv.setImage(player.getTheme().getBall());
	}

	@Override
	protected void rotate() {
		// TODO Auto-generated method stub
		
	}
	
	protected void updateOpacity() {
		if (player.isTransparent())
			prinv.setOpacity(0.5);
		else
			prinv.setOpacity(1);
	}
	
}
