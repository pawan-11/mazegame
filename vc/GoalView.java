package vc;

import util.Point;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import util.Goal;
import util.Observer;
import resources.Images;

public class GoalView implements Observer { //extends Group

	
	private ImageView goalv, backnet;
	private Circle circ;
	private Goal goal = new Goal();
	private Rectangle event = new Rectangle(); //or set style of background
	
	public GoalView() {
		addContent();
		fixlayout();
	}
	
	public GoalView(Goal goal) {
		this();
		changeGoal(goal);
	}
	
	public void changeGoal(Goal goal) {
		this.goal = goal;
		goal.addObserver("view", this);
		Adjust();
	}
	
	private void addContent() {
		goalv = new ImageView(Images.soccergoal);
		backnet = new ImageView(Images.backnet);

		circ = new Circle();
	}

	private void fixlayout() {
		goalv.setOpacity(1); //0.8
		event.setOpacity(0);
		
		DropShadow s = new DropShadow();
		s.colorProperty().set(Color.WHITE);
		s.setBlurType(BlurType.GAUSSIAN);
		
		circ.setFill(Color.GOLD);
		circ.setOpacity(0.25);
		circ.setEffect(s);		
		circ.setMouseTransparent(true);
	}
	
	
	private void Adjust() {        
		Point pos = goal.get_pos();
		Point size = goal.getSize();
		float x = pos.getX();
		float y = pos.getY();
		float width = (float)size.getX();
		float height =  (float)size.getY();
		int degrees = goal.getDegrees();
		
		event.setTranslateX(x);
		event.setTranslateY(y); 
		event.setWidth(width);
		event.setHeight(height);
		
		circ.setTranslateX(x + width / 2);
		circ.setTranslateY(y + height / 2);	
		goalv.setTranslateX(x);
		goalv.setTranslateY(y); 
		backnet.setTranslateX(x);
		backnet.setTranslateY(y); 
		
		circ.setRadius(width);
		
		goalv.getTransforms().clear();
		goalv.setRotate(0);
		backnet.getTransforms().clear();
		backnet.setRotate(0);
		
		if (degrees == 180) {
			goalv.setRotationAxis(Rotate.Y_AXIS);
			goalv.setRotate(degrees);
			backnet.setRotationAxis(Rotate.Y_AXIS);
			backnet.setRotate(degrees);
		} 
		else if (degrees == -90) {
			goalv.getTransforms().add(Rotate.rotate(degrees, 0, 0));
			backnet.getTransforms().add(Rotate.rotate(degrees, 0, 0));

			float tmp = width;
			width = height;
			height = tmp;
			goalv.getTransforms().add(Transform.translate(-width, 0));		
			backnet.getTransforms().add(Transform.translate(-width, 0));		

		}
		else if (degrees == 90) {
			goalv.getTransforms().add(Rotate.rotate(degrees, 0, 0));
			backnet.getTransforms().add(Rotate.rotate(degrees, 0, 0));

			float tmp = width;
			width = height;
			height = tmp;
			
			goalv.getTransforms().add(Transform.translate(0, -height));			
			backnet.getTransforms().add(Transform.translate(0, -height));			
		}
		
		goalv.setFitWidth(width);
		goalv.setFitHeight(height);
		backnet.setFitWidth(width);
		backnet.setFitHeight(height);
	}
	
	@Override
	public void update(String msg) {
		Adjust();
	}
	
	public Node getGoal() {
		return goalv;
	}
	
	public Node getBacknet() {
		return backnet;
	}
	
	public Node getGlow() {
		return circ;
	}
	
	public Node getEvent() { //used to drag goalview in maze maker
		return event;
 	}
}
