package util;

public class ObsInteger extends Observable {

	private int i;
	
	public ObsInteger(int i) {
		setVal(i);
	}
	
	public int getVal() {
		return i;
	}
	
	public void setVal(int i) {
		this.i = i;
		changed(i+"");
	}

	@Override
	public void create_lists() {
		super.create_list("button", Integer.MAX_VALUE);
	}
	
}
