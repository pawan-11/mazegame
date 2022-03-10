package vc;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import resources.Images;
import util.Clock;
import util.Observer;

public class ClockView extends HBox implements Observer {

	private Clock clock;
	private Text text;
	private ImageView icon;
	
	public ClockView(Clock clock) {
		this.clock = clock;
		clock.addObserver("view", this);
		
		text = new Text();
		text.setFill(Color.WHITE);
		
		icon = new ImageView(Images.clock);
		update();
		
		this.setEffect(new DropShadow(2, Color.BLACK));
		this.getChildren().addAll(icon, text);
		this.setAlignment(Pos.CENTER);
		this.setSpacing(5);
	}
	

	public void update() {
		int total_seconds = clock.getSeconds();
		text.setText(double_digit(total_seconds/60)+":"+double_digit(total_seconds%60));
	}
	
	private String double_digit(int digit) {
		if (digit < 10)
			return "0"+digit;
		return digit+"";
	}
	
	public void resize(int k) {
		text.setFont(Font.font(GameMenu.game_font, k/1.5));
		icon.setFitWidth(k/1.5);
		icon.setFitHeight(k/1.5);
	}
	
	@Override
	public void update(String msg) {
		update(); //"time"
	}
	
}
