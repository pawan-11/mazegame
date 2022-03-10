package vc;

import gamemode.GameMode;
import gamemode.GameModeVC;
import gamemode.MazeWar;
import gamemode.MazeWarVC;
import gamemode.MazeWorld;
import gamemode.PrincessWorld;
import gamemode.MazeWorldVC;
import gamemode.PrincessWorldVC;
import resources.Images;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import resources.Music;
import util.ImageButton;
import util.Util;

public class GameMenu extends Pane {
	
	private VBox vbox;
	private ImageView menuiv, titleiv;
	private Rectangle color_bg;
	private ImageButton mazeworldiv, princessiv, mazewariv, makeriv;
	private MazeWorld mw;
	private MazeWorldVC mwvc;
	private PrincessWorld pw;
	private PrincessWorldVC pwvc;
	private MazeWar war;
	private MazeWarVC warvc;
	
	private GameSettings settings;
	private MazeMakerManager manager;
	private MediaPlayer mp;
	private GameMode<?,?,?> mode;
	
	public static final String game_font = "ChalkBoard SE";
	public static boolean lowQ = true, classic = false, tutorial = false, music = true, contact = true, show_running = false, 
			print_missing_imgs = false, war_draw = false, pilot_mode = true, bg_slides = true;
	public static int bot_ammo = 5, human_ammo = 15, human_health = 1000, bot_health = 200, bullet_damage = 40,
			bullet_move_limit = 200, recover_time = 20;
	public static final int frame_rate = 25;
	public static int mwspeed = 1, pwspeed = 1, warspeed = 1;
	public static double imgbg_duration = 5, colorbg_duration = 2;
	
	
	//gone back to menu.
	public GameMenu() {	
	    mp = Music.menu_music;

		init_timers();
		addContent();
		addEvents();
		fixLayout();		
	}
	
	
	private void addContent() {	
		mw = new MazeWorld();
		pw = new PrincessWorld();
		war = new MazeWar();
		
		menuiv = new ImageView();	
		color_bg = new Rectangle();
		
		titleiv = new ImageView(Images.title);
		mazeworldiv = new ImageButton(Images.mazeworldbutton); //rename score goal? like find princess
		princessiv = new ImageButton(Images.princessbutton);	
		mazewariv = new ImageButton(Images.mazewarbutton);

		makeriv = new ImageButton(Images.mazemakerbutton);	
		
		vbox = new VBox();
		vbox.getChildren().addAll(titleiv, mazeworldiv, princessiv, mazewariv, makeriv);
		
		manager = new MazeMakerManager(mw, pw, war);
		manager.setBackScreen(this);
		
		settings = new GameSettings(); //updates theme, and music

		mwvc = new MazeWorldVC(mw);
		pwvc = new PrincessWorldVC(pw);
		warvc = new MazeWarVC(war);
		mwvc.setBackScreen(this);
		pwvc.setBackScreen(this);
		warvc.setBackScreen(this);
		
		this.getChildren().addAll(color_bg, menuiv, vbox, settings);	
	}
	
	
	
	private void addEvents() {
		mazeworldiv.setOnMouseClicked(e->{
			mode = mw;
			show(mwvc);
			mw.play();
		});
		princessiv.setOnMouseClicked(e->{
			mode = pw;
			show(pwvc);
			pw.play();
		});
		mazewariv.setOnMouseClicked(e->{
			mode = war;
			show(warvc);
			war.play();
		});

		makeriv.setOnMouseClicked(e->{
			getScene().setRoot(manager);
			manager.requestFocus();
		});
	}
	
	
	private void fixLayout() {	
		vbox.setAlignment(Pos.CENTER);
		titleiv.setOpacity(0.8);
	//	menuiv.setPreserveRatio(true);
		this.setStyle("-fx-background-color: #000000;"); //0fff39
	} 
	
	public void resize(int width, int height) {		
		int k = getK(width, height);
        int h = k*2, w = k*6;
        
        menuiv.setFitWidth(width);
        menuiv.setFitHeight(height);
		color_bg.setWidth(width);
		color_bg.setHeight(height);
		
		titleiv.setFitWidth(w*3);
		titleiv.setFitHeight(h*3);
		mazeworldiv.resize(w, h);
		princessiv.resize(w, h);
		mazewariv.resize(w, h);
		makeriv.resize(w, h);	
        vbox.setSpacing(k/2);
        
        mw.resize(width, height);
        pw.resize(width, height);
        war.resize(width, height);
        
        manager.resize(width, height);
        settings.resize(width, height, k);
        
        this.layout();
        vbox.setLayoutX(width/2-vbox.getLayoutBounds().getWidth()/2);
        vbox.setLayoutY(height/2-vbox.getLayoutBounds().getHeight()/2);

        settings.setLayoutX(width-settings.getLayoutBounds().getWidth()-k/2);
        settings.setLayoutY(k/2);
	}
	
	private void setTheme() { //could set menu themes based on season
		//set menu theme
		//TODO: set whole game theme, based on calendar date
		if (!bg_slides) {
			menuiv.setImage(Images.menubgs.get(0)); 
			color_bg.setFill(Color.CORNFLOWERBLUE);
		}
		
		color_bg.setVisible(classic);
		menuiv.setVisible(!classic);	
		mw.setTheme(mw.getTheme());
		pw.setTheme(pw.getTheme());
		war.setTheme(war.getTheme());
		manager.updateTheme();
	}

	private Timeline imgbg_timer, colorbg_timer;
	private int bg_idx = 0;
	private void init_timers() {
		imgbg_timer = new Timeline();
		imgbg_timer.setCycleCount(Animation.INDEFINITE);
		imgbg_timer.getKeyFrames().add(new KeyFrame(Duration.seconds(imgbg_duration), e-> {
			menuiv.setImage(Images.menubgs.get(bg_idx));
			bg_idx = (bg_idx+1)%Images.menubgs.size();
		}));	
		colorbg_timer = new Timeline();
		colorbg_timer.setCycleCount(Animation.INDEFINITE);
		colorbg_timer.getKeyFrames().add(new KeyFrame(Duration.seconds(colorbg_duration), e-> {
			color_bg.setFill(Color.web("#"+Util.getRandomColor()));			
		}));
	}
	public void show() {
		if (classic)
			colorbg_timer.play();
		else
			imgbg_timer.play();
		
		mode.getMusic().stop();
		if (music)
			mp.play();
	}
	
	private void show(GameModeVC<?,?,?> vc) {
		colorbg_timer.pause();
		imgbg_timer.pause();
		
		mp.pause();
		if (music)
			mode.getMusic().play();
		getScene().setRoot(vc);
		vc.requestFocus();
	}
	
	public void save() {
		mw.save();
		pw.save();
		war.save();
	}
	
	public static int getK(int width, int height) {
		return height < width? height/22+1:width/22+1;
	}
	
	private class GameSettings extends VBox {
		
		private ImageButton settings_btn, music_btn, sounds_btn, reset_btn, theme_btn;
		
		private GameSettings() {
			addContent();
			addEvents();
			addLayout();
			
			updateMusic();
			updateTheme();
			show(false);
		}
		
		private void addContent() {
			settings_btn = new ImageButton(Images.settingsbutton);
			sounds_btn = new ImageButton(Images.soundsonbtn);
			reset_btn = new ImageButton(Images.resetbtn);
			music_btn = new ImageButton();
			theme_btn = new ImageButton();
			
			this.getChildren().addAll(settings_btn, music_btn, theme_btn, reset_btn, sounds_btn);
		}
		
		private void addEvents() {
			settings_btn.setOnMouseClicked(m->{
				show(!music_btn.isVisible());
			});
			music_btn.setOnMouseClicked(m->{
				music = !music;
				updateMusic();
			});
			sounds_btn.setOnMouseClicked(m->{
				mw.loadProgress(30, 0);
				pw.loadProgress(30);
				war.loadProgress(30, 0);
			});
			reset_btn.setOnMouseClicked(m->{
				mw.loadProgress(0, 0);
				pw.loadProgress(0);
				war.loadProgress(0, 0);
			});
			theme_btn.setOnMouseClicked(m->{
				classic = !classic;
				updateTheme();
			});
			
		}
		
		private void addLayout() {
			this.setAlignment(Pos.CENTER);
		//	this.setStyle("-fx-background-color: #0ff0f0;");
		}
		
		private void resize(int width, int height, int k) {
			this.setMaxSize(width, height);
			settings_btn.resize(k, k);
			music_btn.resize(k*3, k);
			sounds_btn.resize(k*3, k);
			reset_btn.resize(k*3, k);
			theme_btn.resize(k*3, k);
			
			settings_btn.setTranslateX(k);
		}
		
		private void updateMusic() {
			if (music) {
				mp.play();
				music_btn.setImage(Images.musiconbtn);
			}
			else {
				mp.pause();
				music_btn.setImage(Images.musicoffbtn);
			}
		}
		
		private void updateTheme() {
			if (classic) {
				theme_btn.setImage(Images.imagesonbtn);
				imgbg_timer.stop();
				colorbg_timer.playFrom(Duration.millis(colorbg_duration*1000-1));
			}
			else {
				theme_btn.setImage(Images.colorsonbtn);
				colorbg_timer.stop();
				imgbg_timer.playFrom(Duration.millis(imgbg_duration*1000-1));
			}
			setTheme();
		}
		
		private void show(boolean show) {
			music_btn.setVisible(show);
			sounds_btn.setVisible(show);
			reset_btn.setVisible(show);
			theme_btn.setVisible(show);
		}
	}
	
	
	public class Credits extends StackPane {

		private ImageView bg;
		private Text names;

		
		public Credits() {
			addContent();
			addEvents();
			addLayout();
			
			updateTheme();
		}
		
		
		protected void addContent() {
			bg = new ImageView(Images.creditsbg);
			
			names = new Text("");
			
			this.getChildren().addAll(bg);
		}
		

		
		//private int k = 0;
		protected void addEvents() {

		}
		
		
		public void addLayout() { //screen dimensions changed
			//bg.setOpacity(0.7);
			bg.setPreserveRatio(true);
		//	this.setStyle("-fx-background-color: #AF8128;");	

		}
		
		
		protected void resize(int width, int height) {
			int k;
			if (height < width)
				k = height/22+1;
			else
				k = width/22+1;
			
			bg.setFitWidth(width);
			bg.setFitHeight(height);
			
			this.setMaxSize(width, height);
		//	float xmarg = 0;
		//	float ymarg = 0;
		}
		
		
		public void updateTheme() { //for whole application theme, ex snowy, summer
			//String theme = Window.theme;
		}
		
	}

}