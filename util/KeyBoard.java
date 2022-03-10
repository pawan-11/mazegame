package util;

import javafx.scene.input.KeyCode;

public class KeyBoard extends Observable {

	//TODO: make some keyboards static here like mouse and constructor private
	private KeyCode codes[];
	public boolean codes_status[];
	
	public KeyBoard(KeyCode codes[]) {
		this.codes = codes;
		this.codes_status = new boolean[codes.length];
	}
	
	public boolean listensTo(KeyCode[] codes) {
		if (codes.length != this.codes.length)
			return false;
		for (int i = 0; i < codes.length; i++)
			if (codes[i] != this.codes[i])
				return false;
		return true;
	}

	public void keyPressed(KeyCode code_pressed) {
		for (int i = 0; i < codes.length; i++)
			if (code_pressed == codes[i]) {
				codes_status[i] = true;
				changed(i+"");
				break;
			}
	}
	
	public void keyReleased(KeyCode code_released) {
		for (int i = 0; i < codes.length; i++)
			if (code_released == codes[i]) {
				codes_status[i] = false;
				break;
			}
	}

	@Override
	public void create_lists() {
		super.create_list("strat", Integer.MAX_VALUE); //key strategy players
		super.create_list("mazewar", 1);
	}
}
