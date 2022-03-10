package vc;

import player.MWPlayer;
import resources.Images;
import gamemode.MazeWorld;
import javafx.scene.image.Image;
import strategy.MWStrategy;
import util.Util;

public class MWSettings extends Settings<MWStrategy, MazeWorld> {
	
	//private MWStrategy new_strategy;	
	
	public MWSettings(MazeWorld mw) {
		super(mw);

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
			onPlayers();
		});
		addhuman.setOnMouseClicked(m->{
			onAddHuman();
		});
		addbot.setOnMouseClicked(m->{
			onAddBot();
		});
		clear.setOnMouseClicked(m->{
			onClear();
		});
		addbutton.setOnMouseClicked(m->{
			MWPlayer p = new MWPlayer(type, getStrategy(strat_id.getVal()), theme_id.getVal());
			mode.addPlayer(p);
		//	hide();
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
		super.makeControlBox(new Image[]{Images.mouse, Images.lur, Images.awd, Images.gyj});
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
	
	
	protected MWStrategy getStrategy(int code) {
		if (code == 0)
			return new MWStrategy.MWMouseStrategy(mode);
		else if (1<= code && code <= 4)
			return new MWStrategy.MWKeyStrategy(mode, code-1);
		//else if (code == 5)
		return new MWStrategy.MWBfsStrategy(mode);
	}

}
