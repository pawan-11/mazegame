package player;

import resources.Images;
import resources.PlayerTheme.MWPlayerTheme;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import player.MWPlayer;
import util.Point;
import vc.GameMenu;

public class WarPlayerView extends PlayerView<WarPlayer> {

	private ImageView ballv, health_ico, ammo_ico;
	private Text health, ammo;
	private HBox stats;
	private int stats_width;
	
	private IntegerProperty healthproperty, ammoproperty;
	
	public WarPlayerView(WarPlayer player) {
		super(player);

		addContent();
		addLayout();
		resize();
		
		updateTheme();
		rotate();
		move();
		updateHealth();
		updateAmmo();
	}
	
	protected void addContent() {
		ballv = new ImageView();
		stats = new HBox();
		health_ico = new ImageView(Images.health_icon);
		ammo_ico = new ImageView(Images.bullet);
		health = new Text();
		ammo = new Text();
		
		healthproperty = new SimpleIntegerProperty();
		ammoproperty = new SimpleIntegerProperty();
		
	//	health.textProperty().bind(healthproperty.asString());
	//	ammo.textProperty().bind(ammoproperty.asString());
		
		stats.getChildren().addAll(health_ico, health, ammo_ico, ammo);
	}
	
	protected void addLayout() {		
		if (GameMenu.lowQ) {
			ballv.setCache(true);
			ballv.setCacheHint(CacheHint.ROTATE);
			ballv.setSmooth(false);
			stats.setCache(true);
			stats.setCacheHint(CacheHint.SPEED);	
			ammo.setCache(true);
			ammo.setCacheHint(CacheHint.SPEED);
			ammo.setSmooth(false);
			health.setCache(true);
			health.setCacheHint(CacheHint.SPEED);
			health.setSmooth(false);
			health_ico.setCache(true);
			health_ico.setCacheHint(CacheHint.SPEED);
			health_ico.setSmooth(false);
			ammo_ico.setCache(true);
			ammo_ico.setCacheHint(CacheHint.SPEED);
			ammo_ico.setSmooth(false);
		}
		
		stats.setAlignment(Pos.CENTER);
		stats.setSpacing(1);
	//	stats.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, new Insets(1))));
	
	//	stats.setStyle("-fx-background-color: #d2d4d2(60%);"); //-fx-background-color: #d2d4d2; 
		//ammo_ico.setTranslateX(5);
		//ammo.setTranslateX(5);
	//	health.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1px;"); //TODO
	//	health.setStroke(Color.BLACK);
	//	health.setFill(Color.WHITE);
		health.setStrokeType(StrokeType.OUTSIDE);
		health.setTextAlignment(TextAlignment.CENTER);
		
	//	ammo.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1px;"); //TODO
	//	ammo.setStroke(Color.BLACK);
	//	ammo.setFill(Color.WHITE);
		ammo.setStrokeType(StrokeType.OUTSIDE);
		ammo.setTextAlignment(TextAlignment.CENTER);
	}
	
	
	protected void resize() {
		int k = player.getRadius();
		ballv.setFitWidth(k*2.5);
		ballv.setFitHeight(k*2.5);
		
		health_ico.setFitWidth(k*0.8);
		health_ico.setFitHeight(k*0.8);
		ammo_ico.setFitWidth(k/2);
		ammo_ico.setFitHeight(k/2);
		
		ammo.setFont(Font.font(GameMenu.game_font, k+2));
		health.setFont(Font.font(GameMenu.game_font, k+2));
		
		stats.layout(); 
		stats.setMinWidth(0); //so its not computed v here
		stats_width = (int)stats.getLayoutBounds().getWidth()+5;
		stats.setMinWidth(stats_width);
	}
	
	public ImageView getBall() {
		return ballv;
	}
	
	public HBox getStats() {
		return stats;
	}
	
	@Override
	public void update(String msg) {
		if (msg.equals("health"))
			updateHealth(); //its these mofos FIXME
		else if (msg.equals("ammo"))
			updateAmmo();
		else
			super.update(msg);
	}
	
	private void updateHealth() {
		//healthproperty.set(player.getHealth());
		health.setText(player.getHealth()+"");
		ballv.setVisible(player.getHealth() != 0);
		stats.setVisible(player.getHealth() != 0);
	}
	
	private void updateAmmo() {
	//	ammoproperty.set(player.getAmmo());
		ammo.setText(player.getAmmo()+"");
	}
	
	protected void rotate() {
		ballv.setRotate(-player.getAngle());
	}
	
	@Override
	protected void move() {
		ballv.setTranslateX(player.get_pos().getX()-ballv.getFitWidth()/2);
		ballv.setTranslateY(player.get_pos().getY()-ballv.getFitWidth()/2);
		
		stats.setTranslateX(ballv.getTranslateX()+ballv.getFitWidth()/2-(stats_width)/2);
		stats.setTranslateY(ballv.getTranslateY()-player.getRadius()*2); //5
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
