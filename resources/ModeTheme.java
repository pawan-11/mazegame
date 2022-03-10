package resources;

import resources.MazeTheme.MWMazeTheme;
import resources.MazeTheme.PWMazeTheme;
import resources.MazeTheme.WarMazeTheme;
import resources.PlayerTheme.MWPlayerTheme;
import resources.PlayerTheme.PWPlayerTheme;
import resources.PlayerTheme.PrincessTheme;
import resources.PlayerTheme.WarPlayerTheme;
import javafx.scene.image.Image;

abstract public class ModeTheme<MT extends MazeTheme<?>, PT extends PlayerTheme> {

	protected Image bg, border;
	protected String bg_color, border_color;
	protected MT maze_theme;
	protected PT[] player_themes;
	
	public ModeTheme(PT[] player_themes, MT maze_theme, Image bg, Image border, String bg_color, String border_color) {
		this.player_themes = player_themes;
		this.maze_theme = maze_theme;
		this.bg = bg;
		this.border = border;
		this.bg_color = bg_color;
		this.border_color = border_color;
	}
	
	public Image getBg() {
		return bg;
	}

	public String getBgColor() {
		return bg_color;
	}
	
	
	public Image getBorder() {
		return border;
	}
	
	public String getBorderColor() {
		return border_color;
	}
	
	public MT getMazeTheme() {
		return maze_theme;
	}
	
	public PT[] getPlayerThemes() {
		return player_themes;
	}
	
	public static class MWTheme extends ModeTheme<MWMazeTheme, MWPlayerTheme> {
		
		public MWTheme(MWPlayerTheme[] player_themes, MWMazeTheme maze_theme, Image bg, Image border, String bg_color, String border_color) {
			super(player_themes, maze_theme, bg, border, bg_color, border_color);
		}
	}
	
	public static class WarTheme extends ModeTheme<WarMazeTheme, WarPlayerTheme> {
		
		public WarTheme(WarPlayerTheme[] player_themes, WarMazeTheme maze_theme, 
				Image bg, Image border, String bg_color, String border_color) {
			super(player_themes, maze_theme, bg, border, bg_color, border_color);
		}
	}
	
	public static class PWTheme extends ModeTheme<PWMazeTheme, PWPlayerTheme> {
		
		private PrincessTheme princess_theme;
		
		public PWTheme(PWPlayerTheme[] player_themes, PrincessTheme princess_theme, PWMazeTheme maze_theme, Image bg, Image border, String bg_color, String border_color) {
			super(player_themes, maze_theme, bg, border, bg_color, border_color);
			this.princess_theme = princess_theme;
		}
		
		public PrincessTheme getPrincessTheme() {
			return princess_theme;
		}
	}

}

