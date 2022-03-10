package util;

import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class ImageButton extends StackPane { //change from stackpane
	
	protected int width, height;
	protected Rectangle border;
	protected ImageView iv;
	
	public ImageButton() {		
		iv = new ImageView();
		
		border = new Rectangle();
		border.setFill(null);
		border.setStroke(Color.WHITE);
	//	border.setStrokeWidth(2);
		border.setVisible(false);
		
		border.setCache(true);
		border.setCacheHint(CacheHint.SCALE);
		
		
		this.getChildren().addAll(iv, border);
	}
	
	public ImageButton(Image i) {
		this();
		setImage(i);
	}
	
	public void updateEffect() {	
		int x = width<40?1:2; // (int)(0.03*width);
		int y = height<40?1:2; // (int)(0.03*height);
		
		iv.setOnMouseEntered(m->{	
			highlight(true);
			iv.setFitWidth(width-x);
			iv.setFitHeight(height-y);
		});
		iv.setOnMouseExited(m->{
			highlight(false);
			iv.setFitWidth(width+x);
			iv.setFitHeight(height+y);
		});
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		
		iv.setFitWidth(width);
		iv.setFitHeight(height);
		
		border.setWidth(width+0.05*width);
		border.setHeight(height+0.05*height);
		border.setStrokeWidth(Math.ceil(0.01*height));
		
		updateEffect();
	}
	
	public void highlight(boolean hl) {
		border.setVisible(hl);
	}
	
	public void setImage(Image i) {
		iv.setImage(i);
	}
	
	
	public static class TextImageButton extends ImageButton {
		
		private Text text;
		
		public TextImageButton(Image i, String t) {
			super(i);
			
			text = new Text();
			text.setText(t);
			this.getChildren().addAll(text);
		}
	}
	
	public static class LockImageButton extends ImageButton implements Observer {
		
		private boolean lock = false;
		private int id = -1;
		
		public LockImageButton(Image ball) {
			super(ball);
		}

		public void updateEffect() {
			int x = width<40?1:2; // (int)(0.03*width);
			int y = height<40?1:2; 
			iv.setOnMouseExited(m->{
				highlight(lock);
				iv.setFitWidth(width+x);
				iv.setFitHeight(height+y);
			});
		}
		
		public void lock(boolean lock) {
			this.lock = lock;
			highlight(lock);
		}
		
		public void observeInteger(int id, ObsInteger oi) {	
			this.id = id;
			oi.addObserver("button", this);
			lock(oi.getVal() == id);
		}
		
		@Override
		public void update(String msg) { //observes integer
			try {
				int i = Integer.parseInt(msg);		
				lock(i == id);
			}
			catch (NumberFormatException e) {
				System.out.println("bad id given by ObsInteger");
			}
		}
		
		public int getid() {
			return id;	
		}
		
	}
	
}
