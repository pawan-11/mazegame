package gamemode;

import fileIO.MWCreator;
import resources.Images;
import resources.ModeTheme;
import util.ImageButton;
import util.KeyBoard;
import util.MouseInfo;
import util.Observer;
import util.Util;
import vc.GameMenu;
import vc.Settings;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import maze.MazeView;
import player.PlayerView;


public abstract class GameModeVC<MV extends MazeView<?>, PV extends PlayerView<?>, G extends GameMode<?,?,?>> extends Pane implements Observer {

	protected G mode;
    protected Settings<?,?> settings;
	protected ImageButton homeButton;
	protected ImageView gamebg, border;
	protected Rectangle colorbg, colorborder;
	protected Pane pvs; 
	protected MV mv;
	protected Text countdown, path;
	//boolean showpath = true;
	
	
	protected GameModeVC(G mode) {
		this.mode = mode;
		mode.addObserver("view", this);
	}
	
	protected void initContent() {
		homeButton = new ImageButton(Images.homebutton);
		gamebg = new ImageView();
		border = new ImageView();
		colorbg = new Rectangle();
		colorborder = new Rectangle();
		pvs = new Pane();

		countdown = new Text();
        path = new Text("path: "+MWCreator.path); //TODO: for debugging
	}
	
	//int k = 0;
	protected void initEvents() {
		this.setOnMouseMoved(m->{
			//if (k%5==0)
			for (MouseInfo mi: mode.getMouses()) {
				mi.mouseMoved((int)(m.getX()-mode.getXmarg()),(int)(m.getY()-mode.getYmarg()));
			}
		//	k=k%5+1;
		});
		this.setOnMouseDragged(m->{
			for (MouseInfo mi: mode.getMouses())
				mi.mouseMoved((int)(m.getX()-mode.getXmarg()), (int)(m.getY()-mode.getYmarg()));
		});
		
		this.setOnKeyPressed(k->{
			for (KeyBoard kb: mode.getKeyboards())
				kb.keyPressed(k.getCode());
			
		});
		
		this.setOnKeyReleased(k->{
			for (KeyBoard kb: mode.getKeyboards())
				kb.keyReleased(k.getCode());
		});
	}
	
	public void setBackScreen(GameMenu menu) {
		homeButton.setOnMouseClicked(m-> {
			mode.cleanUp(); //pauses as well
			
			getScene().setRoot(menu);
			menu.requestFocus();	
			menu.show();
		});
	}
	
	protected void initLayout() {	
        path.setFill(Color.WHITE);
        
		countdown.setFill(Color.WHITE);
		countdown.setText("4");
		countdown.setTextAlignment(TextAlignment.JUSTIFY);
		
		countdown.setStroke(Color.BLACK);
		countdown.setEffect(new DropShadow(2, Color.BLACK));
		
		colorborder.setFill(Color.TRANSPARENT); //to only show the border
		colorborder.setStrokeType(StrokeType.OUTSIDE);
	}
	
	protected void resize_help(int width, int height) {		
		int k = getK(width, height);
		homeButton.resize(k,k);
		countdown.setFont(Font.font(GameMenu.game_font, k*6));
		
		homeButton.setLayoutX(5);
		homeButton.setLayoutY(5);	
		
		int margin = mode.getBorder();
		
		colorbg.setWidth(width-margin*2);
		colorbg.setHeight(height-margin*2);	
		colorbg.setLayoutX(margin);
		colorbg.setLayoutY(margin);
		colorbg.setArcWidth(k);
		colorbg.setArcHeight(k);
		colorbg.setStrokeWidth(2);
		
		colorborder.setWidth(width-margin*2);
		colorborder.setHeight(height-margin*2);	
		colorborder.setLayoutX(margin);
		colorborder.setLayoutY(margin);
		colorborder.setArcWidth(k);
		colorborder.setArcHeight(k);

		colorborder.setStrokeWidth(margin*2);
		
		
		gamebg.setFitWidth(width-margin*2);
		gamebg.setFitHeight(height-margin*2);	
		gamebg.setLayoutX(margin); //border
		gamebg.setLayoutY(margin);
		//gamebg.setViewport(rect);
		
		border.setFitWidth(width);
		border.setFitHeight(height);	
		
		path.setLayoutX(width/2-6*k);
		path.setLayoutY(k);
		countdown.setLayoutX(width/2-countdown.getLayoutBounds().getWidth()/2);
		countdown.setLayoutY(height/2);//-countdown.getLayoutBounds().getHeight()/2);
		
		mv.setLayout(mode.getXmarg(), mode.getYmarg());
		pvs.setLayoutX(mode.getXmarg());
		pvs.setLayoutY(mode.getYmarg());	

		this.layout();
	}

	protected void updateTheme(ModeTheme<?,?> theme) {
		
		gamebg.setVisible(!GameMenu.classic);
		border.setVisible(!GameMenu.classic);
		colorbg.setVisible(GameMenu.classic);
	
		colorbg.setFill(Color.web(theme.getBgColor())); 
		gamebg.setImage(theme.getBg());
		border.setImage(theme.getBorder());
		
		colorborder.setStroke(Color.web(theme.getBorderColor()));
		settings.updateTheme();
		settings.resize(mode.width, mode.height, getK(mode.getWidth(), mode.getHeight()));
	}
	
	int getK(int w, int h) {
		if (h < w)
			return h/22+1;
		return w/22+1;
	}

	@Override
	public void update(String msg) {
		if (msg.contains("added player"))
			addPlayer(Integer.parseInt(msg.split(" ")[2]+""));
		else if (msg.contains("removed player"))
			removePlayer(Integer.parseInt(msg.split(" ")[2]+""));
		else if (msg.equals("resized"))
			resize();
		else if (msg.equals("celebration"))
			updateCelebration();
		else if (msg.equals("maze"))
			updateMV();
		else if (msg.equals("theme"))
			updateTheme();
		else if (msg.equals("countdown"))
			updateCountdown();
		else if (msg.equals("players"))
			updatePlayers();
	}
	
	protected void updateCountdown() {
		int number = mode.getCountdown();
		countdown.setVisible(number != 0);	
		countdown.setText(""+number);
	}
	
	public void help_updateMV() { //alternatively pass in mv, pvs?
		mv.setLayout(mode.getXmarg(), mode.getYmarg());
		pvs.setLayoutX(mode.getXmarg());
		pvs.setLayoutY(mode.getYmarg());
		
	}
	
	abstract protected void addContent();
	abstract protected void addEvents();
	abstract protected void addLayout();
	abstract protected void updatePlayers();
	abstract protected void updateCelebration();
	abstract protected void updateMV();
	abstract protected void updateTheme();
	abstract protected void resize();
	abstract protected void addPlayer(int index);
	abstract protected void removePlayer(int index);
	
	
}
