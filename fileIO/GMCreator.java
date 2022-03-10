package fileIO;

import java.io.FileWriter;
import java.util.regex.Pattern;

public interface GMCreator { //GameMode creator

	public static Pattern end = Pattern.compile("^end$");
	
	public void parse();
	public void tryagain();
	public FileWriter getWriter();
	
	public static String trim_last_dir(String path) {
		String new_path = path;
		int idx = path.length()-2;
		while (idx >= 0 && path.charAt(idx) != '/')
			idx -= 1;
		
		new_path = new_path.substring(0, idx);
		//System.out.println("old: "+path+" new: "+new_path+" idx: "+idx+" length: "+path.length());
		return new_path;
	}

}
