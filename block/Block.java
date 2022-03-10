package block;

import java.io.FileWriter;
import resources.BlockTheme;
import util.Observable;
import util.Util;

abstract public class Block<BT extends BlockTheme> extends Observable {

	protected boolean left, right, top, bottom;
	protected BT theme;
	protected String fixed_type, type;
	
	public Block() { //ideas: have static blocks allocated, to reuse same blocks in each maze
		this(false, false, false, false);
	}

	public Block(boolean left, boolean top, boolean right, boolean bottom) {
	//	super("block");
		set(left, top, right, bottom);
	}
	
	public void setTheme(BT theme) {		
		this.theme = theme;
		changed("theme");
	}
	
	public BT getTheme() {
		return theme;
	}
	
	public void fix_type(String type) {
		this.fixed_type = type;
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getFixType() {
		return this.fixed_type;
	}
	
	public void next() {
		if (!left) left = true;
		else if (!top) top = true;
		else if (!right) right = true;
		else if (!bottom) bottom = true;
		else {
			setSquare(false);
		}
		changed("walls");
	}

	public void opposite() {
		if (left) right = true;
		else if (top) bottom = true;
		else if (right) left = true;
		else if (bottom) top = true;
		else
			next();
		changed("walls");
	}

	public void set(boolean left, boolean top, boolean right, boolean bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		changed("walls");
	}
	
	public void set(int idx, boolean bool) {
		if (idx == 0)
			this.left = bool;
		else if (idx == 1)
			this.top = bool;
		else if (idx == 2)
			this.right = bool;
		else
			this.bottom = bool;
		changed("walls");
	}
	
	public void setSquare(boolean sq) {
		set(sq, sq, sq, sq);
	}
	
	public void flipSquare() {
		set(!left, left, left, left);
	}
	
	
	public void rotate() {
		set(bottom, left, top, right);
		changed("walls");
	}
	
	public boolean isLeft() {
		return left;
	}
	
	public boolean isTop() {
		return top;
	}
	
	public boolean isRight() {
		return right;
	}
	
	public boolean isBottom() {
		return bottom;
	}
	
	public boolean isSquare() {
		return bottom && left && right && top;
	}
	
	public String toString() {
		return "("+left+","+top+","+right+","+bottom+")";
	}
	
	@Override
	public void create_lists() {
		super.create_list("view", 1);
	}
	
	abstract public Block<?> duplicate();
	abstract public void write(FileWriter writer);
	
}