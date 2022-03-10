package vc;

import player.PWPlayer;
import player.WarPlayer;
import resources.Images;
import gamemode.MazeWar;
import gamemode.PrincessWorld;
import javafx.scene.image.Image;
import strategy.PWStrategy;
import strategy.WarStrategy;
import util.Util;


public class WarSettings extends Settings<WarStrategy, MazeWar> {

	private boolean all_new_players = false;
	
	public WarSettings(MazeWar war) {
		super(war);

		addContent();
		addEvents();
		fixLayout();
	}

	public void addContent() {
		super.initContent();

		this.getChildren().addAll(players, addlayer, controllayer, ballbox, addbutton);		
	}

	public void addEvents() {
		
		players.setOnMouseClicked(m->{
			if (m != null)
				m.consume(); //so this click doesnt shoot bullet
			if (addhuman.isVisible()) {
				mode.resume();			
			}
			else {
				mode.pause();
				//mode.setReport("Let's add more players");
			}		
			all_new_players = false; //mode.getPlayers().size() == 0; 
			onPlayers();
		});
		addhuman.setOnMouseClicked(m->{
			onAddHuman();
		});
		addbot.setOnMouseClicked(m->{
			onAddBot();
		});
		//add down arrow below players
		clear.setOnMouseClicked(m->{
			mode.cleanUp();
			onClear();
		});
		addbutton.setOnMouseClicked(m->{
			WarPlayer p = new WarPlayer(type, getStrategy(strat_id.getVal()), theme_id.getVal());
			mode.addPlayer(p); //all_new_players
			//hide();
			//pw.resume();
		});
	}
	
	public void fixLayout() {
		super.initLayout();
	}
	
	public void resize(int width, int height, int k) {
		k = (int)(k*1.2);
		super.resize_help(width, height, k);
	}
	
	protected void makeControlBox() {
		super.makeControlBox(new Image[]{Images.mouse, Images.lurdm, Images.awdsq});
	}
	
	protected void makeBallBox() {		
		super.makeBallBox(mode.getTheme().getPlayerThemes());
	}
	
	public void updateTheme() {
		int i = this.getChildren().indexOf(ballbox);
		this.getChildren().remove(i);
		super.makeBallBox(mode.getTheme().getPlayerThemes());
		this.getChildren().add(i, ballbox);
		ballbox.setVisible(false);	
	}
	
	protected WarStrategy getStrategy(int code) {
		if (code == 0)
			return new WarStrategy.WarMouseStrategy(mode);
		else if (0 <= code && code < 5)
			return new WarStrategy.WarKeyStrategy(mode, code-1);
		//else if (code == 5)
		return new WarStrategy.WarBfsStrategy(mode);
	}
	
}
