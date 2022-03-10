package block;

import java.io.FileWriter;
import java.io.IOException;
import resources.Theme;
import resources.BlockTheme.MWBlockTheme;

public class MWBlock extends Block<MWBlockTheme> {

	public MWBlock(boolean left, boolean top, boolean right, boolean bottom) {
		super(left, top, right, bottom);
	}
	
	public MWBlock() {
		super();
	}
	
	public void write(FileWriter writer) {
		try {
			int l = left?1:0;
			int t = top?1:0;
			int r = right?1:0;
			int b = bottom?1:0;

			writer.write(l+""+t+""+r+""+b+"\n");
		}
		catch (IOException i) {
			System.out.println("error writing a block");
		}
	}
	
	@Override
	public MWBlock duplicate() {
		return new MWBlock(left, top, right, bottom);
	}
}
