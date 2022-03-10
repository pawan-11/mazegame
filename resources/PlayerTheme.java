package resources;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

abstract public class PlayerTheme {

	private Image ball;
	
	public PlayerTheme(Image ball) {
		this.ball = ball;
	}
	
	public Image getBall() {
		return ball;
	}
	
	public static class MWPlayerTheme extends PlayerTheme {

		private Image parachute, propellor;
		
		public MWPlayerTheme(Image ball, Image parachute, Image propellor) {
			super(ball);
			this.parachute = parachute;
			this.propellor = propellor;
		}
		
		public Image getParachute() {
			return parachute;
		}
		
		public Image getPropellor() {
			return propellor;
		}
	}
	
	public static class WarPlayerTheme extends PlayerTheme {
		
		public WarPlayerTheme(Image ball) {
			super(ball);
		}
	}
	
	public static class PWPlayerTheme extends PlayerTheme {
		
		public PWPlayerTheme(Image ball) {
			super(ball);
		}
	}

	public static class PrincessTheme extends PlayerTheme {
		
		public PrincessTheme(Image princess) {
			super(princess);
		}	
	}
	
	public static class BulletTheme extends PlayerTheme {
		
		public BulletTheme(Image bullet) {
			super(bullet);
		}
	}
}
