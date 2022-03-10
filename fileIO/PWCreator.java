package fileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import maze.PWMaze;
import block.PWBlock;
import gamemode.PrincessWorld;


public class PWCreator implements GMCreator {
	
	//max_castle reached
	public static final Pattern header1 = Pattern.compile("^(-?\\d+)$");
	//maze header, blocks:rows,cols
	private static Pattern header2 = Pattern.compile("^blocks:(\\d{1,3}),(\\d{1,3})$");
	//pwblock pattern: open,left,top,right,bottom
	private static Pattern pwblock = Pattern.compile("^(\\w*),(\\d)(\\d)(\\d)(\\d)$");

	private PrincessWorld pw;
	private boolean editable = true;
	private String path = "";
    private int max_maze_idx=0;
	
	public PWCreator(PrincessWorld pw) {
		this.pw = pw;
	}
	
	@Override
	public void parse() {
	//	System.out.println("now parsing princess world");
		PWBlock blocks[][] = null;
		int row = 0, col = 0, rows = 1, cols = 1;
		int line = 0;
		
		try {	
			path = new File(MWCreator.class.getProtectionDomain().getCodeSource().
					getLocation().toURI()).getPath();
			path = GMCreator.trim_last_dir(path);
			path+="/"+pw.getFileName();
			
			BufferedReader reader;
			Matcher m;
			
			if (!editable) {
				InputStream stream = PWCreator.class.getResourceAsStream(pw.getFileName());
				reader = new BufferedReader(new InputStreamReader(stream));
			}
			else
				reader = new BufferedReader(new FileReader(path));
			
	//		System.out.println("parsing pwworld file: "+path);
			String curr_line;
			int casee = -1;

			while ((curr_line = reader.readLine()) != null) {
				line++;
				switch (casee) {
				case -1:
					m = header1.matcher(curr_line);
					if (m.matches()) {
						max_maze_idx = Integer.parseInt(m.group(1));
						
						casee = 0;
					}
					else {
						System.out.println("int,int expected at line "+line+", but found "+ curr_line);
						tryagain();
						return;
					}
					break;
				case 0:
					m = header2.matcher(curr_line);
					if (m.matches()) {
						row = 0; col = 0;
						rows = Integer.parseInt(m.group(1));
						cols = Integer.parseInt(m.group(2));						
						blocks = new PWBlock[rows][cols];
						
						casee = 1;
					}
					else {
						System.out.println("blocks:[row],[col]"
								+ " expected at line "+line+", but found "+ curr_line);
						tryagain();
						return;
					}
					break;
				case 1:
					m = pwblock.matcher(curr_line);
					if (m.matches()) {
					//	System.out.println(m1.group(1)+", line: "+curr_line);
						blocks[row][col] = new PWBlock(m.group(1), m.group(2).equals("1"), 
								m.group(3).equals("1"), m.group(4).equals("1"), m.group(5).equals("1"));
						

						row += Math.floorDiv(col+1, cols);
						col = (col+1)%cols;
						break;
					}
					else {
						casee = 2;
					}		
				case 2:
					m = end.matcher(curr_line);
					if (m.matches()) {
						pw.addMaze(new PWMaze(blocks), pw.get_mazes_size(), false);
						casee = 0;
					}
					else {
						System.out.println("types,0000 or end expected at line "+line+" but found "+curr_line);
						tryagain();
						return;
					}
					break;
				}
			}
			
			if (pw.get_mazes_size() == 0) {
				tryagain();
			}
			reader.close();
		} 
		catch (Exception e) {
			System.out.println("PrincessWorld file read error at line "+line);
			tryagain();
		//	e.printStackTrace();
		}

		pw.loadProgress(max_maze_idx);
		return;
		
		
	}

	@Override
	public void tryagain() {
		if (editable) {
			editable = false;
			parse();
		}
	}

	@Override
	public FileWriter getWriter() {
        //System.out.println("saving to "+path);
		FileWriter writer = null;
		try {
			writer = new FileWriter(path, false);
		}
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("PrincessWorld file with path "+ path+", open error");
		}	
		return writer;
	}
	
	//	path = new File(PWCreator.class.getProtectionDomain().getCodeSource().
	//			getLocation().toURI()).getPath()+"/"+pw.getFileName();
	
}
