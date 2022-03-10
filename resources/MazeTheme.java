package resources;

import resources.BlockTheme.MWBlockTheme;
import resources.BlockTheme.PWBlockTheme;
import resources.BlockTheme.WarBlockTheme;
import javafx.scene.image.Image;

public class MazeTheme<BT extends BlockTheme> {

	protected BT block_theme;
	
	public MazeTheme(BT block_theme) {
		this.block_theme = block_theme;
	}
	
	public BT getBlockTheme() {
		return block_theme;
	}
	
	public static class MWMazeTheme extends MazeTheme<MWBlockTheme> {
		
		MWMazeTheme(MWBlockTheme block_theme) {
			super(block_theme);
		}
	}
	
	
	public static class WarMazeTheme extends MazeTheme<WarBlockTheme> {		
		
		private Image maze_bg;
		private String maze_bg_color;
	
		public WarMazeTheme(WarBlockTheme block_theme, Image maze_bg, String maze_bg_color) {
			super(block_theme);
			this.maze_bg = maze_bg;
			this.maze_bg_color = maze_bg_color;
		}
		
		public Image getMazeBg() {
			return maze_bg;
		}

		public String getMazeBgColor() {
			return maze_bg_color;
		}
	}
	
	
	public static class PWMazeTheme extends MazeTheme<PWBlockTheme> {
		
		private Image maze_bg;
		private String maze_bg_color;
		
		public PWMazeTheme(PWBlockTheme block_theme, Image maze_bg, String maze_bg_color) {
			super(block_theme);
			this.maze_bg = maze_bg;
			this.maze_bg_color = maze_bg_color;
		}

		public Image getMazeBg() {
			return maze_bg;
		}

		public String getMazeBgColor() {
			return maze_bg_color;
		}
	}

	
	
}
