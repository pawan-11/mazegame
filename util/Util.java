package util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javafx.scene.paint.Color;

public class Util {

	private Util() {}

	public static void print(String[] arr) {
		for (String s: arr) {
			System.out.print(s+", ");
		}
		System.out.println();
	}
	
	public static void print(String s) {
		System.out.println(s);
	}
	
	public static void print(Object... o) {
		System.out.println(Arrays.toString(o));
	}
	
	public static void print(java.util.Collection<?> c) {
	//	Iterator<?> i = c.iterator();
		Util.print(c.toArray());
	//	while (i.hasNext()) {
	//		i.next();
	//	}
	}
	
	public static String getRandomColor() {
		String hex = "";
		for (int i = 0; i < 6; i++) {
			hex += toHex((int)(Math.random()*16));
		}
		return hex;
	}
	
	private static String toHex(int x) { //0 <= x <= 15		
		return x < 10?x+"":(char)(x-10+(int)'a')+"";
	}
}
