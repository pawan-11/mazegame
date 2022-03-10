package block;

import java.io.FileWriter;
import java.io.IOException;
import resources.Theme;
import resources.BlockTheme.PWBlockTheme;

public class PWBlock extends Block<PWBlockTheme> {

	
	public PWBlock(String type, boolean left, boolean top, boolean right, boolean bottom) {
		super(left, top, right, bottom);
		this.fix_type(type);
		//super.setTheme(Theme.pwblocktheme);
	}
	
	public PWBlock(String type) {
		super();
	//	super.setTheme(Theme.pwblocktheme);
		fix_type(type);
	}
	
	public PWBlock() {
		this("closed");
	}
	
	public void openDoor() {
		if (this.type.equals("closed")) {
			this.type = "open";
			changed("type");
		}
	}
	
	public void closeDoor() {
		if (this.type.equals("open") && !this.fixed_type.equals("open")) {
			this.type = "closed";
			changed("type");
		}
	}
	
	public void fix_type(String type) {
		super.fix_type(type);
		
		changed("walls");
	}
	
	public PWBlock duplicate() {
		return new PWBlock(fixed_type, left, top, right, bottom);
	}
	
	public void write(FileWriter writer) {
		try {
			
			int l = left?1:0;
			int t = top?1:0;
			int r = right?1:0;
			int b = bottom?1:0;
			writer.write(fixed_type+","+l+""+t+""+r+""+b+"\n");
		}
		catch (IOException i) {
			System.out.println("error writing a pw block");
		}
	}
}
