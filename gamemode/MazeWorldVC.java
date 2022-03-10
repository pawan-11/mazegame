package gamemode;

import resources.Images;
import resources.ModeTheme.MWTheme;

import java.util.ArrayList;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import maze.MWMazeView;
import player.MWPlayer;
import player.MWPlayerView;
import player.WarPlayerView;
import util.ImageButton;
import util.Point;
import util.Util;
import vc.ClockView;
import vc.GameMenu;
import vc.GoalView;
import vc.MWSettings;

public class MazeWorldVC extends GameModeVC<MWMazeView, MWPlayerView, MazeWorld> {

	private ImageView confetti;
	protected ImageButton nextButton, backButton;
	private Text mazeround, score, report;
	private Rectangle roundbg, reportbg;
	private ClockView cv;
	private GoalView gv;
	//private MWMazeView mv;
	//private MWSettings settings;
	
	public MazeWorldVC(MazeWorld mw) {
		super(mw);
		
		addContent();
		addEvents();
		addLayout();
		
		updateMV();
		updatePlayers();
		updateCelebration();
		updateScore();
		updateReport();
		updateButtons();
		updateTheme();
		updateCountdown();
	}
	
	
	protected void addContent() {
		super.initContent();
 		
		nextButton = new ImageButton();  
		backButton = new ImageButton();
		confetti = new ImageView(Images.mw_confetti);
		
		
		mazeround = new Text("");
		score = new Text("");
		report = new Text("");		
		roundbg = new Rectangle();
		reportbg = new Rectangle();
		settings = new MWSettings(mode);
		mv = new MWMazeView();
		mv.changeMaze(mode.getMaze());
		gv = new GoalView(mode.getGoal());

		
		cv = new ClockView(mode.getClock());
		
		this.getChildren().addAll(gamebg, colorborder, colorbg, gv.getBacknet(), pvs, confetti, gv.getGlow(),
				gv.getGoal(), mv.getWallV(), border, reportbg, roundbg, 
				cv, countdown, score, report, mazeround, settings);
		this.getChildren().addAll(backButton, nextButton, homeButton);
	}
	
	//private int k = 0;
	protected void addEvents() {
		super.initEvents();
		backButton.setOnMouseClicked(e->{
			mode.prevMaze();
		});
		nextButton.setOnMouseClicked((e)->{		
			mode.nextMaze();
		});
	}
	
	public void addLayout() { //screen dimensions changed
		super.initLayout();
		gamebg.setOpacity(0.7);
		border.setOpacity(0.7);
		confetti.setVisible(false);
		
		if (GameMenu.lowQ) {
			confetti.setSmooth(false);
			confetti.setCache(true);
			confetti.setCacheHint(CacheHint.DEFAULT); //FIXME
		}
		confetti.setDisable(true);
		
		mazeround.setEffect(new DropShadow(3, Color.BLACK));
		mazeround.setTextAlignment(TextAlignment.CENTER);
		mazeround.setFill(Color.WHITE);
		
		roundbg.setOpacity(0.5);
		roundbg.setFill(Color.BLACK);

		score.setFill(Color.WHITE);	
		score.setTextAlignment(TextAlignment.CENTER);
		score.setEffect(new DropShadow(1, Color.BLACK));	
	
		report.setTextAlignment(TextAlignment.CENTER);
		report.setFill(Color.WHITE);
		report.setEffect(new DropShadow(2, Color.BLACK));
	
		reportbg.setOpacity(0.4);
		reportbg.setFill(Color.BLACK);
		colorbg.setStrokeType(StrokeType.OUTSIDE);

		this.setStyle("-fx-background-color: #ffffff;");	 //EF8128
	}
	
	
	protected void resize() {
		int width = mode.getWidth();
		int height = mode.getHeight();
		int k = getK(width, height);
		
		super.resize_help(width, height);
		
		cv.resize(k);
		settings.resize(width, height, k);
		score.setWrappingWidth(k*5);
		score.setFont(Font.font(GameMenu.game_font, k/2));
		mazeround.setWrappingWidth(k*5);
		mazeround.setFont(Font.font(GameMenu.game_font, 0.8*k));	
		report.setWrappingWidth(k*10);
		report.setFont(Font.font(GameMenu.game_font, k/2));	
		confetti.setFitWidth(width);
		confetti.setFitHeight(height);
		nextButton.resize(k, k);
		backButton.resize(k,k);
		
		this.layout(); //for getwidth to be used

		roundbg.setWidth(mazeround.getLayoutBounds().getWidth());
		roundbg.setHeight(k*2.5);
		reportbg.setWidth(report.getLayoutBounds().getWidth());
		reportbg.setHeight(k*2.5);
		
		nextButton.setLayoutX(width-nextButton.getLayoutBounds().getWidth()-k/2); ///
		nextButton.setLayoutY(5);	
		backButton.setRotate(180);
		backButton.setLayoutX(nextButton.getLayoutX()-backButton.getLayoutBounds().getWidth()-k/4); ///
		backButton.setLayoutY(5);
		
		score.setLayoutX(width/2-score.getWrappingWidth()/2);
		score.setLayoutY(height-k*0.5);			
		mazeround.setLayoutX(width/2-mazeround.getWrappingWidth()/2);
		mazeround.setLayoutY(k);	
		report.setLayoutX(width/2-report.getWrappingWidth()/2);
		report.setLayoutY(height-k*1.5);
		roundbg.setLayoutX(mazeround.getLayoutX()-(roundbg.getWidth()-mazeround.getLayoutBounds().getWidth())/2);
		roundbg.setLayoutY(mazeround.getLayoutY()-k/2);
		roundbg.setLayoutY(0);
		roundbg.setArcWidth(k/2);
		roundbg.setArcHeight(k/2);
		reportbg.setLayoutX(report.getLayoutX()-(reportbg.getWidth()-report.getLayoutBounds().getWidth())/2);
		reportbg.setLayoutY(report.getLayoutY()-k);
		reportbg.setArcWidth(k/2);
		reportbg.setArcHeight(k/2);
		settings.setLayoutX(backButton.getLayoutX()-settings.getWidth()/2-k*2);
		settings.setLayoutY(backButton.getLayoutY()-1);//-k);
		
		cv.setLayoutX(width/2-cv.getWidth()/2);
		cv.setLayoutY(k*1.3);
		
		float xmarg = mode.getXmarg();
		float ymarg = mode.getYmarg();
		
		mv.setLayout(xmarg, ymarg);
		pvs.setLayoutX(xmarg);
		pvs.setLayoutY(ymarg);
		gv.getGlow().setLayoutX(xmarg);
		gv.getGlow().setLayoutY(ymarg);
		gv.getBacknet().setLayoutX(xmarg);
		gv.getBacknet().setLayoutY(ymarg);
		gv.getGoal().setLayoutX(xmarg);
		gv.getGoal().setLayoutY(ymarg);
	}

	protected void updateTheme() {
		MWTheme theme = mode.getTheme();
		super.updateTheme(theme);
	}
	
	@Override
	public void update(String msg) {
		super.update(msg);
		if (msg.equals("maze_idx"))
			updateButtons();
		else if (msg.equals("report"))
			updateReport();
		else if (msg.equals("score"))
			updateScore();
	}


	private void updateButtons() {
		boolean perms[] = mode.getPermissions();
		boolean left = perms[0];
		boolean right = perms[1];

		backButton.setImage(left? Images.greennextbutton: Images.disablednextbutton);
		nextButton.setImage(right? Images.greennextbutton: Images.disablednextbutton);
		backButton.setDisable(!left);
		nextButton.setDisable(!right); 
	}
	
	public void updatePlayers() {
		for (Node n: pvs.getChildren())
			n.setVisible(false);
		
		pvs.getChildren().clear();		
		
		for (int i = 0; i < mode.getPlayers().size(); i++)
			addPlayer(i);
	}
	
	protected void updateMV() {	
		mv.changeMaze(mode.getMaze());		
		super.help_updateMV(); //resizes and adjusts
	
		gv.getGlow().setLayoutX(mode.getXmarg());
		gv.getGlow().setLayoutY(mode.getYmarg());
		gv.getBacknet().setLayoutX(mode.getXmarg());
		gv.getBacknet().setLayoutY(mode.getYmarg());
		gv.getGoal().setLayoutX(mode.getXmarg());
		gv.getGoal().setLayoutY(mode.getYmarg());
		
		mazeround.setText("LEVEL "+mode.getMazeIdx());
		updateButtons();
	}
	
	private void updateReport() {
		report.setText(mode.getReport());
	}
	
	private void updateScore() {
		score.setText("SCORE: "+mode.getScore());
	}
	
	protected void updateCelebration() {
		
		confetti.setVisible(mode.getCelebrate());
	}


	@Override
	protected void addPlayer(int index) {
		MWPlayerView pv = new MWPlayerView(mode.getPlayers().get(index));
		pvs.getChildren().add(index*2, pv.getBall());
		pvs.getChildren().add(index*2+1, pv.getParachute());
//		pvs.getChildren().addAll(pv.getBall(), pv.getParachute()); //ISSUE: TODO: not adding player to their corresponding index
		//could override getChildren(), and implement it as a hashmap?
	}
	
	@Override
	public void removePlayer(int index) { 
		int removeIdx = index*2; 
	//	pvs.getChildren().get(removeIdx).setVisible(false); 
	//	pvs.getChildren().get(removeIdx+1).setVisible(false);
		pvs.getChildren().remove(removeIdx);
		pvs.getChildren().remove(removeIdx);	
	}
}
