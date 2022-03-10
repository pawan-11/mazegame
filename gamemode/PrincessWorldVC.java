package gamemode;

import resources.Images;
import resources.ModeTheme.PWTheme;
import util.Point;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import maze.PWMazeView;
import player.MWPlayerView;
import player.PWPlayer;
import player.PWPlayerView;
import util.ImageButton;
import vc.GameMenu;
import vc.PWSettings;
import player.PrincessView;

public class PrincessWorldVC extends GameModeVC<PWMazeView, PWPlayerView, PrincessWorld>  { //add MV mazview paramter

	private ImageView confetti, prin_icon;
	protected ImageButton nextButton, backButton;
	private Text mazeround, report;
	private Rectangle roundbg, reportbg;
	private Region ground;
	//private PWMazeView pwmv;
	private ImageView prin_view;
	//private PWSettings settings;
	
	public PrincessWorldVC(PrincessWorld pw) {
		super(pw);
		
		addContent();
		addEvents();
		addLayout();
		
		updateButtons();
		updateReport();
		updateCelebration();
		updateMV();
		updateTheme();
		updatePlayers();
		updatePrincess();
	}

	
	@Override
	protected void addContent() {	
		super.initContent();
		
		nextButton = new ImageButton();  
		backButton = new ImageButton();
		confetti = new ImageView(Images.pw_confetti);
		prin_icon = new ImageView(Images.princess);
		mazeround = new Text("");
		report = new Text("");		
		roundbg = new Rectangle();
		reportbg = new Rectangle();
		settings = new PWSettings(mode);
		mv = new PWMazeView();
		prin_view = new PrincessView(mode.getPrincess()).getPrinV();
		
		ground = new Region();
		//prin_view should be before mazeview but after mazeview's bg,  put pwmv in mv,										
		this.getChildren().addAll(colorborder, colorbg, gamebg, ground, mv.getColorBg(), mv.getImageBg(), prin_view, 
				mv.getTypeV(), mv.getShoreV(), pvs, mv.getWallV(), border, countdown, roundbg, reportbg, 
				prin_icon, report, mazeround, confetti, settings);
	
		this.getChildren().addAll(backButton, nextButton, homeButton);
	}


	@Override
	protected void addEvents() {
		super.initEvents();
		backButton.setOnMouseClicked(e->{
			mode.prevMaze();
			//mode.removePlayers();
		});

		nextButton.setOnMouseClicked((e)->{		
			mode.nextMaze();
		});
	}
	
	@Override
	protected void addLayout() {
		super.initLayout();
	//	gamebg.setOpacity(0.6);
		confetti.setVisible(false);
		//border.setVisible(false);
        if (GameMenu.lowQ)
        	confetti.setSmooth(false);
		
		mazeround.setEffect(new DropShadow(3, Color.BLACK));
		mazeround.setTextAlignment(TextAlignment.CENTER);
		mazeround.setFill(Color.WHITE);
		
		roundbg.setOpacity(0.8);
		roundbg.setFill(Color.BLACK);

		report.setTextAlignment(TextAlignment.CENTER);
		report.setFill(Color.WHITE);
		report.setEffect(new DropShadow(2, Color.BLACK));
	
		reportbg.setOpacity(0.6);
		reportbg.setFill(Color.BLACK);
		
		ground.setVisible(false);  /////
		colorbg.setStrokeType(StrokeType.INSIDE);
		prin_icon.setPreserveRatio(true);
		// border-radius: 50%;");
		this.setStyle("-fx-background-color: #EF8128;");	
	}
	


	@Override
	protected void resize() {
		int width = mode.getWidth();
		int height = mode.getHeight();
		super.resize_help(width, height);
		int k = getK(width, height);		
		
		settings.resize(width, height, k);
		
		mazeround.setWrappingWidth(k*5);
		mazeround.setFont(Font.font(GameMenu.game_font, 0.8*k));	
		report.setWrappingWidth(k*6);
		report.setFont(Font.font(GameMenu.game_font, k*0.4));	
		confetti.setFitWidth(width/2);
		confetti.setFitHeight(height/2);
		nextButton.resize(k, k);
		backButton.resize(k,k);
		
		prin_icon.setFitHeight(k*1.5);
		
		this.layout(); //for getwidth to be used

		roundbg.setWidth(mazeround.getLayoutBounds().getWidth());
		roundbg.setHeight(k*1.5);
		reportbg.setWidth(report.getLayoutBounds().getWidth()+k*3);
		reportbg.setHeight(k*2);
		ground.setPrefWidth(mode.getMaze().getWidth());
		ground.setPrefHeight(mode.getMaze().getHeight());
		ground.setEffect(new BoxBlur(k*2, k*2, 2));
		
		nextButton.setLayoutX(width-nextButton.getLayoutBounds().getWidth()-k/2); ///
		nextButton.setLayoutY(5);	
		backButton.setRotate(180);
		backButton.setLayoutX(nextButton.getLayoutX()-backButton.getLayoutBounds().getWidth()-k/4); ///
		backButton.setLayoutY(5);
		
		mazeround.setLayoutX(width/2-mazeround.getWrappingWidth()/2);
		mazeround.setLayoutY(k);	
		report.setLayoutX(width-report.getLayoutBounds().getWidth()-k*2);
		report.setLayoutY(height-k*1.5);
		prin_icon.setLayoutX(report.getLayoutX()-k);
		prin_icon.setLayoutY(report.getLayoutY()-k/1.5);
		
		roundbg.setLayoutX(mazeround.getLayoutX()-(roundbg.getWidth()-mazeround.getLayoutBounds().getWidth())/2);
		roundbg.setLayoutY(mazeround.getLayoutY()-k/2);
		roundbg.setLayoutY(0);
		roundbg.setArcWidth(k/2);
		roundbg.setArcHeight(k/2);
		reportbg.setLayoutX(report.getLayoutX()-(reportbg.getWidth()-report.getLayoutBounds().getWidth())/2);
		reportbg.setLayoutY(report.getLayoutY()-k);
		reportbg.setArcWidth(k);
		reportbg.setArcHeight(k);
		ground.setLayoutX(mode.getXmarg());
		ground.setLayoutY(mode.getYmarg());
		
		settings.setLayoutX(backButton.getLayoutX()-settings.getWidth()/2-k*2);
		settings.setLayoutY(backButton.getLayoutY()-1);//-k);
		
		prin_view.setLayoutX(mode.getXmarg());
		prin_view.setLayoutY(mode.getYmarg());
		
	}
	
	public ImageView getPrinView() {
		return prin_view;
	}

	@Override
	public void update(String msg) {
		super.update(msg);
		if (msg.equals("permissions"))
			updateButtons();
		else if (msg.equals("celebration"))
			updateCelebration();
		else if (msg.equals("report"))
			updateReport();
		else if (msg.equals("hiding"))
			updatePrincess();
	}
	
	
	public void updatePlayers() {
		pvs.getChildren().clear();
		for (int i = mode.getPlayers().size()-1; i > -1; i--) {
			pvs.getChildren().add(new PWPlayerView(mode.getPlayers().get(i)).getBall());
		}
	}
	
	protected void updateTheme() {
		PWTheme theme = mode.getTheme();
		super.updateTheme(theme);
		
		
		ground.setStyle("-fx-background-color: "+theme.getBgColor()+";");
		//maze view theme is taken care of as it observes theme change in maze
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
	
	private void updatePrincess() {
		boolean hiding = mode.isPrinHiding();
		
	//	prin_view.setTranslateX(mode.getXmarg());
	// prin_view.setTranslateY(mode.getYmarg());
		
		this.getChildren().remove(prin_view);  //is there no better way?

		if (hiding)
			this.getChildren().add(this.getChildren().indexOf(mv.getImageBg())+1, prin_view);
		else {
			this.getChildren().add(this.getChildren().indexOf(mv.getTypeV())+1, prin_view);
			prin_view.toFront();
		}
	}
	
	protected void updateCelebration() {
		if (mode.getCelebrate()) {
			Point pos = mode.getWinner().get_pos();
			confetti.setLayoutX(pos.getX()-confetti.getFitWidth()/2+mode.getXmarg());
			confetti.setLayoutY(pos.getY()-confetti.getFitHeight()/2+mode.getYmarg());	
			confetti.setVisible(true);
		}
		else { 
			confetti.setVisible(false);
		}
	}
	
	protected void updateMV() {	
		mv.changeMaze(mode.getMaze());
		super.help_updateMV();
		prin_view.setLayoutX(mode.getXmarg());
		prin_view.setLayoutY(mode.getYmarg());
		
		mazeround.setText("CASTLE "+mode.getMazeIdx());
		updateButtons();
	}
	
	private void updateReport() {
		report.setText(mode.getReport());
	}
	
	@Override
	protected void addPlayer(int index) {
		PWPlayerView pv = new PWPlayerView(mode.getPlayers().get(index));
		pvs.getChildren().add(index, pv.getBall());
	}
	
	@Override
	public void removePlayer(int index) { //new player views are at index 0
		pvs.getChildren().get(index).setVisible(false); 
		pvs.getChildren().remove(index);	
	}
}
