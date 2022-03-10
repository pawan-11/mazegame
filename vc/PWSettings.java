package vc;

import resources.Images;
import javafx.scene.image.Image;
import player.PWPlayer;
import gamemode.PrincessWorld;
import strategy.PWStrategy;

public class PWSettings extends Settings<PWStrategy, PrincessWorld> {

	private boolean all_new_players = false;
	
	public PWSettings(PrincessWorld pw) {
		super(pw);

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
			if (addhuman.isVisible()) {
				mode.resume();
			}
			else {
				mode.pause();
				mode.setReport("Let's add more friends");
			}		
			all_new_players = false; //mode.getPlayers().size() == 0; 
			onPlayers();
		});
		addhuman.setOnMouseClicked(m->{
			onAddHuman();
		//	mode.getMaze().setup(mode.getPlayers());
		});
		addbot.setOnMouseClicked(m->{
			onAddBot();
		});
		//add down arrow below players
		clear.setOnMouseClicked(m->{
			mode.cleanUp();
			onClear();
			all_new_players = true;
		});
		addbutton.setOnMouseClicked(m->{
			PWPlayer p = new PWPlayer(type, getStrategy(strat_id.getVal()), theme_id.getVal());
			mode.addPlayer(p, all_new_players);
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
		super.makeControlBox(new Image[]{Images.mouse, Images.lr, Images.ad, Images.gj});
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
	
	protected PWStrategy getStrategy(int code) { 
		if (code == 0)
			return new PWStrategy.PWMouseStrategy(mode);
		else if (0 <= code && code < 5)
			return new PWStrategy.PWKeyStrategy(mode, code-1);
		//else if (code == 5)
		return new PWStrategy.PWRandomStrategy(mode);
	}
	
}
