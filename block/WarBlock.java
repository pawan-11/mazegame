package block;

import java.io.FileWriter;
import java.io.IOException;
import resources.Theme;
import resources.BlockTheme.WarBlockTheme;

public class WarBlock extends Block<WarBlockTheme> {

	
	public WarBlock(String type, boolean left, boolean top, boolean right, boolean bottom) {
		super(left, top, right, bottom);
		this.fix_type(type);
	//	super.setTheme(Theme.warblocktheme);	
	}
	
	public WarBlock(String type) {
		super();
//		super.setTheme(Theme.warblocktheme);
		fix_type(type);
	}
	
	public WarBlock() {
		this("ground");
	}

	public void fix_type(String type) {
		super.fix_type(type);
		changed("walls");
	}
	
	public WarBlock duplicate() {
		return new WarBlock(fixed_type, left, top, right, bottom);
	}
	
	public void write(FileWriter writer) {
		try {		
			writer.write(fixed_type+","+(left?1:0)+""+(top?1:0)+""+(right?1:0)+""+(bottom?1:0)+"\n");
		}
		catch (IOException i) {
			System.out.println("error writing a war block");
		}
	}
}
