package resources;

import java.net.URL;
import javafx.animation.Animation;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class Music {

	public static MediaPlayer menu_music, mw_music, pw_music, war_music;
	
	//public static MediaPlayer prin_far, prin_close; //music if princess is closer, more tense
	public static MediaPlayer explosion;
	
	
	static {
		menu_music = getMedia("Halloween.mp3");
		mw_music = getMedia("Bonus-Mystic.mp3");
		pw_music = getMedia("Instrumental-Blinding Lights.mp3");
		war_music = getMedia("Music-Universe.mp3");
		
		menu_music.setCycleCount(Animation.INDEFINITE);
		mw_music.setCycleCount(Animation.INDEFINITE);
		pw_music.setCycleCount(Animation.INDEFINITE);
		war_music.setCycleCount(Animation.INDEFINITE);
		
		//explosion = getMedia("explosion.mp4");
		
		//explosion.setCycleCount(1);
	}


	private static URL url;
	public static MediaPlayer getMedia(String name) {
		url = Music.class.getResource("music/"+name);
		if (url == null)  {
			System.out.println("music "+name+" not found");
			url = Music.class.getResource("music/Silent.wav"); //FIXME
		}
		return new MediaPlayer(new Media(url.toString()));
	}
	
}