package vc;

import resources.Images;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Window extends Application {	
	
    
    private Stage stage;
    private GameMenu menu;
    
	public void start(Stage stage) {
		this.stage = stage;
		this.menu = new GameMenu();
		
		Scene scene = new Scene(menu, 900, 600);
		scene.getStylesheets().add("util/style.css");
		
		stage.setMaximized(false);
		stage.setScene(scene);
		stage.setTitle("SOCCER MAZE");
		stage.centerOnScreen();
		//stage.getScene().setFill(Color.YELLOW);
		stage.setResizable(true);
		stage.getIcons().add(Images.icon);
		stage.show();
		//stage.setMaximized(true);
		menu.requestFocus();
		addEvents();
		
		resize();
	}	
	
	
	public void addEvents() {
		Timeline t = new Timeline(new KeyFrame(Duration.millis(500), e->{
			resize();
		}));
		t.setCycleCount(1);
		stage.widthProperty().addListener((c, old, ne)->{
			t.play();
		});
		stage.heightProperty().addListener((c, old, ne)->{
			t.play();
		});
		
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, k->{
			if (k.getCode() == KeyCode.ESCAPE) {
				stage.setFullScreen(false);		
			}
			else if (k.getCode() == KeyCode.F) {
				stage.setFullScreen(true);		
			}
			//else if (k.getCode() == KeyCode.M && k.getCode() == KeyCode.COMMAND)
			//	stage.setIconified(true);
		});
		
		stage.setOnCloseRequest(we->{   
			menu.save();
			Platform.exit();
		});
	}
	
	
	private void resize() {
		int w = (int)stage.getScene().getWidth(), h = (int)stage.getScene().getHeight();
		w = w < 400?400:w;
		h = h < 300?300:h;
		menu.resize(w, h);
	//	System.out.println("after "+stage.getScene().widthProperty().intValue());
	}
	
	public static void main(String[] args) {
		launch();
	}

}
