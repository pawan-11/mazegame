package gamemode;

import resources.Images;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import maze.MWMazeView;
import maze.WarMazeView;
import player.BulletView;
import player.MWPlayerView;
import player.WarPlayer;
import player.WarPlayerView;
import util.ImageButton;
import util.KeyBoard;
import util.MouseInfo;
import util.Point;
import util.Util;
import vc.ClockView;
import vc.GameMenu;
import vc.PWSettings;
import vc.WarSettings;

public class MazeWarVC extends GameModeVC<WarMazeView, WarPlayerView, MazeWar> {

	//private ImageView explosion;
	protected ImageButton nextButton, backButton;
	private Text mazeround;
	private HBox scoreboard;
	private ClockView cv;
	private Pane bullet_views; 
//	private
	private Canvas draw;
	private GraphicsContext draw_g;
	private Pane explosions;
	
	public MazeWarVC(MazeWar war) {
		super(war);
		
		addContent();
		addEvents();
		addLayout();
		
		updateMV();
		updatePlayers();
	//	updateBullets();
		updateExplosion();
		updateScore();
		updateExplosion();
		updateButtons();
		updateTheme();
		updateCountdown();
	}
	
	
	protected void addContent() {
		super.initContent();
		nextButton = new ImageButton();  
		backButton = new ImageButton();
			
		mazeround = new Text("");
		
		scoreboard = new HBox();
		bullet_views = new Pane();
		settings = new WarSettings(mode);
		
		mv = new WarMazeView();
		mv.changeMaze(mode.getMaze());
		
		cv = new ClockView(mode.getClock());
		
		draw = new Canvas();
		draw_g = draw.getGraphicsContext2D();
															//draw
		this.getChildren().addAll(gamebg, colorborder, colorbg, mv.getColorBg(), mv.getImageBg(), draw, mv.getTypeV(), mv.getWallV());
		this.getChildren().addAll(bullet_views, pvs, border, cv, countdown, scoreboard, mazeround, settings);
		this.getChildren().addAll(backButton, nextButton, homeButton);
	}
	
	
	//private int k = 0;
	protected void addEvents() {
		super.initEvents();
		backButton.setOnMouseClicked(e->{
			e.consume();
			mode.prevMaze();
		});
		nextButton.setOnMouseClicked((e)->{		
			e.consume();
			mode.nextMaze();
		});

		this.setOnMousePressed(m->{
		//for (MouseInfo mi: mode.getMouses())
			MouseInfo.mouse_info.mousePressed();
		});
		this.setOnMouseReleased(m->{
			//for (MouseInfo mi: mode.getMouses())
			MouseInfo.mouse_info.mouseReleased();
		});
	
	}
	
	
	public void addLayout() { //screen dimensions changed
		super.initLayout();
		gamebg.setOpacity(0.7);
		colorbg.setOpacity(0.99);
//		mv.getColorBg().setOpacity(0); //FIXME 0.9
//		mv.getImageBg().setOpacity(0); 0.9
		
		mazeround.setEffect(new DropShadow(3, Color.BLACK));
		mazeround.setTextAlignment(TextAlignment.CENTER);
		mazeround.setFill(Color.WHITE);
		
		scoreboard.setEffect(new DropShadow(1, Color.BLACK));	
		scoreboard.setStyle("-fx-background-color: black;");
		
		colorbg.setStrokeType(StrokeType.OUTSIDE);

		this.setStyle("-fx-background-color: #ffffff;");	 //EF8128
	}
	
	
	protected void resize() {
		int width = mode.getWidth();
		int height = mode.getHeight();
		super.resize_help(width, height);
		
		int k = getK(width, height);
		settings.resize(width, height, k);
		
		cv.resize(k);
		
		for (Node n: scoreboard.getChildren()) {
			Text score = (Text)n;
			score.setFont(Font.font(GameMenu.game_font, k/2));
		}
		
		mazeround.setWrappingWidth(k*5);
		mazeround.setFont(Font.font(GameMenu.game_font, 0.8*k));	
		
		nextButton.resize(k, k);
		backButton.resize(k,k);
		
		clearDrawing();
		draw.setWidth(width);
		draw.setHeight(height);
		
		this.layout(); //for getwidth to be used
	
		nextButton.setLayoutX(width-nextButton.getLayoutBounds().getWidth()-k/2); ///
		nextButton.setLayoutY(5);	
		backButton.setRotate(180);
		backButton.setLayoutX(nextButton.getLayoutX()-backButton.getLayoutBounds().getWidth()-k/4); ///
		backButton.setLayoutY(5);
		
		scoreboard.setLayoutX(width/2-scoreboard.getLayoutBounds().getWidth()/2);
		scoreboard.setLayoutY(height-k*0.5);			
		mazeround.setLayoutX(width/2-mazeround.getWrappingWidth()/2);
		mazeround.setLayoutY(k);	

		//settings.setLayoutX(backButton.getLayoutX()-settings.getWidth()/2-k*2);
		//settings.setLayoutY(backButton.getLayoutY()-1);//-k);
		
		cv.setLayoutX(width/2-cv.getWidth()/2);
		cv.setLayoutY(k*1.3);
		
		settings.setLayoutX(backButton.getLayoutX()-settings.getWidth()/2-k*2);
		settings.setLayoutY(backButton.getLayoutY()-1);//-k);
		
		
		float xmarg = mode.getXmarg();
		float ymarg = mode.getYmarg();
		
		mv.setLayout(xmarg, ymarg);
		pvs.setLayoutX(xmarg);
		pvs.setLayoutY(ymarg);
		bullet_views.setLayoutX(xmarg);
		bullet_views.setLayoutY(ymarg);
	}

	protected void updateTheme() {
		super.updateTheme(mode.getTheme());

		gamebg.setVisible(false); //FIXME
		border.setVisible(false);
		colorbg.setVisible(true);
		
		mv.getColorBg().setVisible(false); //GameMenu.classic
		mv.getImageBg().setVisible(false); //!GameMenu.classic
	}
	
	@Override
	public void update(String msg) {
		super.update(msg);
		if (msg.equals("draw"))
			updateDrawing();
		else if (msg.equals("added bullet"))
			addBullet(mode.getBullets().size()-1);
		else if (msg.contains("removed bullet")) 
			removeBullet(Integer.parseInt(msg.split(" ")[2]+"")); 
		else if (msg.equals("bullets"))
			updateBullets();
		else if (msg.equals("maze_idx") || msg.equals("mazes")) //changed maze idx or mazes size
			updateButtons();
		else if (msg.equals("explosion"))
			updateExplosion();
		else if (msg.equals("score"))
			updateScore();
		else if (msg.equals("teleported players"))
			clearDrawing();
		
	}


	private Color random_color = Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
	private void updateDrawing() {
		draw_g.setFill(random_color);
		WarPlayer p = mode.getLastMoved();
		draw_g.fillOval(p.get_pos().getX()-2+mode.getXmarg(), p.get_pos().getY()-2+mode.getYmarg(), 4, 4);
	}
	
	private void clearDrawing() {
		draw_g.clearRect(0, 0, draw.getWidth(), draw.getHeight());
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
	
	protected void addPlayer(int index) {
	//	Util.print("adding p view "+index);
		WarPlayerView pv = new WarPlayerView(mode.getPlayers().get(index));
		pvs.getChildren().add(index*2, pv.getBall());
		pvs.getChildren().add(index*2+1, pv.getStats());
	}
	
	public void removePlayer(int index) {
	//	Util.print("removing p view "+index);
		int removeIdx = index*2; //pvs.getChildren().size()-1-index*2
		pvs.getChildren().get(removeIdx).setVisible(false); 
		pvs.getChildren().get(removeIdx+1).setVisible(false);
		pvs.getChildren().remove(removeIdx);
		pvs.getChildren().remove(removeIdx);	
	}
	
	public void updatePlayers() {
		if (mode.getPlayers().size() == 0)
			clearDrawing();
		
		pvs.getChildren().clear();
		scoreboard.getChildren().clear();
		
		for (int i = 0; i < mode.getPlayers().size(); i++) { //used to add backwards so human stays up front
			addPlayer(i);
			
			/*Text score = new Text();
			score.setFill(Color.WHITE);	
			score.setTextAlignment(TextAlignment.CENTER);
			scoreboard.getChildren().add(score);*/
		}
	}
	
	public void addBullet(int index) { //TODO: FIX, indexes might be bad. indexes can change due to shifting
		BulletView bv = new BulletView(mode.getBullets().get(index)); 
		bullet_views.getChildren().add(index, bv.getBall());  //FIXME: LAGG, FOLLOWUP: THIS NOT THE LAGG. I REPEAT
	//	Util.print("added bulletview "+index+" size:"+bullet_views.getChildren().size()+"\n");

	//	bullet_views.getChildren().add(bv.getBall());
	}
	
	public void removeBullet(int index) { //bullets not disappearing right away cmon man TODO: FIX
		bullet_views.getChildren().get(index).setVisible(false); //FIXME: LAGG
		bullet_views.getChildren().remove(index);
	//	Util.print("removed bulletview "+index+" size:"+bullet_views.getChildren().size()+"\n");
		//TODO: add explosion effect
	}
	
	private void updateBullets() {
		bullet_views.getChildren().clear();
		
		for (int x = 0; x < mode.getBullets().size(); x++)
			addBullet(x);
	}
	
	protected void updateMV() {	
		mv.changeMaze(mode.getMaze());		
		super.help_updateMV();
		
		mazeround.setText("LEVEL "+mode.getMazeIdx());
		updateButtons();
	}
	
	
	private void updateScore() {
//		score.setText("SCORE: "+mode.getScore());
	}
	
	private void updateExplosion() { //explosion
		
		//confetti.setVisible(mode.getCelebrate());
	}
	
	protected void updateCelebration() {
		
	}
}
