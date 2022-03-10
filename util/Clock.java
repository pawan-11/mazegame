package util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Clock extends Observable {

	private int seconds = 0;
	private Timeline timer, fast_timer;
	
	public Clock() {
		init_timers();
	}
	
	public void setTime(int seconds) {
		if (seconds < 0) {
			seconds = 0;
			timer.stop();
			fast_timer.stop();
		}
		this.seconds = seconds;
		changed("time");
	}

	private void init_timers() {
						
		timer = new Timeline();
		timer.setCycleCount(Animation.INDEFINITE);
		timer.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e-> {
			setTime(seconds-1);
		}));
		fast_timer = new Timeline();
		fast_timer.setCycleCount(Animation.INDEFINITE);
		fast_timer.getKeyFrames().add(new KeyFrame(Duration.millis(80), e-> {
			setTime(seconds-1);
		}));
	}
	
	public void fastplay() {
            timer.pause();
            fast_timer.play();
	}
	
	public void pause() {
            timer.pause();
            fast_timer.pause();
	}
	
	public void play() {
		fast_timer.pause();
		timer.play();
	}
	
	public int getSeconds() {
		return seconds;
	}

	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
	
}
