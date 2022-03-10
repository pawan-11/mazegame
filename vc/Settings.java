package vc;

import java.util.ArrayList;

import gamemode.GameMode;
import resources.Images;
import resources.PlayerTheme;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import util.ImageButton;
import util.ImageButton.LockImageButton;
import util.MouseInfo;
import util.ObsInteger;
import util.Util;

abstract public class Settings<S, G extends GameMode<?,?,?>> extends VBox { //G extends Strategy<?,G>

	protected ObsInteger theme_id = new ObsInteger(1); //when it changes, the selected theme imagebutton changes
	protected ObsInteger strat_id = new ObsInteger(0);
	protected String type;
	protected G mode;
	
	protected ImageButton players, addbutton, addhuman, addbot, clear;
	protected HBox controlbox, ballbox, addlayer;
	protected StackPane controllayer;
	
	protected Timeline inactivity;
	protected int seconds = 8; 
	
	protected ArrayList<ImageButton> balls = new ArrayList<ImageButton>();
	protected ArrayList<ImageButton> controls = new ArrayList<ImageButton>();
	
	public Settings(G mode) {
		this.mode = mode;
	}
	
	protected void initContent() {		
		players = new ImageButton(Images.addplayer);
		
		addhuman = new ImageButton(Images.addhuman);
		addbot = new ImageButton(Images.addbot);
		clear = new ImageButton(Images.clearbutton);
		addbutton = new ImageButton(Images.addbutton);

		makeControlBox();
		makeBallBox();
		
		addlayer = new HBox();
		addlayer.getChildren().addAll(addhuman, addbot, clear);

		addlayer.setAlignment(Pos.BASELINE_CENTER);

		controllayer = new StackPane();	
		controllayer.getChildren().addAll(controlbox);
		controllayer.setAlignment(Pos.BASELINE_CENTER);
		
		this.setAlignment(Pos.CENTER);
		
		inactivity = new Timeline();
		inactivity.setCycleCount(seconds); //seconds
		inactivity.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e-> {
			
		}));
		inactivity.setOnFinished(f->{
		//	if (!MouseInfo.mouse_info.get_pos())
			players.getOnMouseClicked().handle(null);
		});
	}
	
	protected void clicked() {
		inactivity.stop();
		inactivity.playFromStart();
	}
	
	protected void onPlayers() {
		addhuman.setVisible(!addhuman.isVisible());
		addbot.setVisible(!addbot.isVisible());
		clear.setVisible(!clear.isVisible());
		ballbox.setVisible(false);
		controlbox.setVisible(false);
		addbutton.setVisible(false);
		
		if (addhuman.isVisible())
			clicked();
		else
			inactivity.stop();
	}
	
	protected void onAddHuman() {
		type = "human";
		theme_id.setVal(1);
		strat_id.setVal(0);
		
		controlbox.setVisible(!controlbox.isVisible());
		ballbox.setVisible(controlbox.isVisible());
		addbutton.setVisible(controlbox.isVisible());
		
		clicked();
	}
	
	protected void onAddBot() {
		type = "bot";
		strat_id.setVal(-1);

		//setStrategy(-1);//bot strat
		theme_id.setVal(0);
		
		addbutton.setVisible(false);
		ballbox.setVisible(false);
		controlbox.setVisible(false);
		addbutton.getOnMouseClicked().handle(null);
		
		clicked();
	}

	protected void onClear() {
		mode.removePlayers();
		clicked();
	}
	
	public void resize_help(int width, int height, int k) {
		super.resize(width, height);
		players.setTranslateX(0);
		players.resize(k*3, k);
		addhuman.resize(k*2, k*2/3);
		clear.resize(k*2, k*2/3);
		addbutton.resize(k*2, k*2/3);
		addbot.resize(k*2, k*2/3);
		
		for (ImageButton b: balls) {
			b.resize((int)(k*0.6), (int)(k*0.6));
		}
		for (ImageButton c: controls) {
			c.resize((int)(k), (int)(k*0.8));
		}
	}
	
	protected void initLayout() {
		hide();
		
		this.setAlignment(Pos.CENTER);
	}
	
	protected void hide() {
		controlbox.setVisible(false);
		ballbox.setVisible(false);
		addbutton.setVisible(false);
		addhuman.setVisible(false);
		addbot.setVisible(false);
		clear.setVisible(false);
	}
	
	protected void makeControlBox(Image controlimgs[]) {
		controlbox = new HBox();
		controlbox.setAlignment(Pos.CENTER);
		
		int code = 0;
		for (Image img: controlimgs) {
			
			LockImageButton kb = new LockImageButton(img);
			HBox.setMargin(kb, new Insets(5));
			
			controls.add(kb);
			controlbox.getChildren().add(kb);
			kb.observeInteger(code, strat_id);
			
			kb.setOnMouseClicked(m->{
				strat_id.setVal(kb.getid());		
				clicked();
			});
			code++;
		}
		
	}
	
	
	protected<PT extends PlayerTheme> void makeBallBox(PT [] themes) {
		ballbox = new HBox();
		ballbox.setAlignment(Pos.CENTER);
		//chosen_player = new ImageButton();
		
		for (int id = 1; id < themes.length; id++) {  //theme id, 0 reserved for bot
			PT theme = themes[id];
			LockImageButton ball = new LockImageButton(theme.getBall());
			
			balls.add(ball);
			ballbox.getChildren().add(ball);
			
			ball.observeInteger(id, theme_id);
			
			ball.setOnMouseClicked(m->{
				theme_id.setVal(ball.getid());
				clicked();
			});
		}
	}
	
	abstract public void updateTheme();
	
	abstract protected S getStrategy(int key);
	abstract protected void addContent();
	abstract protected void addEvents();
	abstract protected void fixLayout();
	abstract public void resize(int width, int height, int k);
	abstract protected void makeControlBox();
	abstract protected void makeBallBox();
}
