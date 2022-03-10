package fileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import maze.MWMaze;
import util.Point;
import block.MWBlock;
import gamemode.MazeWorld;
import javafx.geometry.Point2D;

public class MWCreator implements GMCreator {

	//mazeworld progress header: max_maze_idx:score
	public static final Pattern header1 = Pattern.compile("^(-?\\d+),(-?\\d+)$");
	//maze header,  blocks:rows,cols,start_row,start_col,goal_row,goal_col
	private static Pattern header2 = Pattern.compile("^blocks:(\\d{1,3}),(\\d{1,3}),(-?\\d{1,3}),(-?\\d{1,3}),(-?\\d{1,3}),(-?\\d{1,3})$");
	//block pattern: left,right,top,bottom
	public static final Pattern block = Pattern.compile("^(\\d)(\\d)(\\d)(\\d)$");

	public static String path = "";
	private boolean editable = true;
	private MazeWorld mw;
	private int max_maze_idx=0;
                
	public MWCreator(MazeWorld mw) {
		this.mw = mw;
	}
	
	public void parse() {
	//	System.out.println("now parsing maze world");
		MWBlock blocks[][] = null;
		int row = 0, col = 0, rows = 1, cols = 1;
		Point start_pos = null;
		Point goal_pos = null;
		int line = 0;
		int score = 0;
		String curr_line="";

		try {	
			path = new File(MWCreator.class.getProtectionDomain().getCodeSource().
					getLocation().toURI()).getPath();
			path = GMCreator.trim_last_dir(path);
			path+="/"+mw.getFileName();
			//System.out.println("path: "+path);
			BufferedReader reader;
			Matcher m;
			
			if (!editable) {
			//	InputStream stream = MWCreator.class.getResourceAsStream(mw.getFileName());
				reader = new BufferedReader(new InputStreamReader(MWCreator.class.getResourceAsStream(mw.getFileName())));
			}
			else
				reader = new BufferedReader(new FileReader(path));
			
	//		System.out.println("parsing mwworld file: "+path);
			int casee = -1;

			while ((curr_line = reader.readLine()) != null) {
				line++;
				switch (casee) {
				case -1:
					m = header1.matcher(curr_line);
					if (m.matches()) {
						max_maze_idx = Integer.parseInt(m.group(1));
						score = Integer.parseInt(m.group(2));
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
						start_pos = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
						goal_pos = new Point(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
						
						blocks = new MWBlock[rows][cols];
						
						casee = 1;
					}
					else {
						System.out.println("blocks:[row],[col],[ball_row],[ball_col],[goal_row],[goal_col]"
								+ " expected at line "+line+", but found "+ curr_line);
						tryagain();
						return;
					}
					break;
				case 1:
					m = block.matcher(curr_line);
					if (m.matches()) {
						blocks[row][col] = new MWBlock(m.group(1).equals("1"), m.group(2).equals("1"),
								m.group(3).equals("1"), m.group(4).equals("1"));
						
						row += Math.floorDiv(col+1, cols);
						col = (col+1)%cols;
						break;
					}
					else {
						casee = 2;
					}		
				case 2:
					if (end.matcher(curr_line).matches()) {
						mw.addMaze(new MWMaze(blocks, start_pos, goal_pos),
								mw.get_mazes_size(), false);
						casee = 0;
					}
					else {
						System.out.println("true,false,true,false or end expected at line "+line);
						tryagain();
						return;
					}
					break;
				}
			}
			
			if (mw.get_mazes_size() == 0) {
				tryagain();
			}
			reader.close();
		} 
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("MazeWorld file read error at line "+line+" : "+curr_line);
			tryagain();
		}

		mw.loadProgress(max_maze_idx, score);
		return;
	}

	public FileWriter getWriter() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(path, false);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("maze world file open error");
		}	
		return writer;
	}
	

	public void tryagain() {
		if (editable) {
			editable = false;
			parse();
		}
	}
	


	
}
