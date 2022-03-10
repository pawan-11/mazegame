package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

abstract public class Observable {

	protected HashMap<String, ArrayList<Observer>> observers;
	protected HashMap<String, Integer> lists;
	
	//TODO:, map string type to list of observers, so observable can remove which type of observers to remove, needed to garbage collect dead observers
	public Observable() {
		observers = new HashMap<String, ArrayList<Observer>>();
		lists = new HashMap<String, Integer>();
		create_lists();
	}
	
	abstract public void create_lists();
	
	protected void create_list(String type, int max_observers) {
		observers.put(type, new ArrayList<Observer>());
		lists.put(type, max_observers);
	}
	
	public void addObserver(String type, Observer o) {
	
		if (observers.containsKey(type)) {
			if (lists.get(type).intValue() == observers.get(type).size()) {
			//	Util.print("overloaded "+type);
				observers.get(type).remove(0);
			}
			observers.get(type).add(o);
		//	Util.print("added observer "+type+" allowed: "+lists.get(type).intValue()+"\n");
		}
	}
	
	public void clearObservers() {
		Set<String> types = observers.keySet();
		for (String type: types)
			observers.get(type).clear();
	}
	
	public void clearObservers(String type) {
		observers.get(type).clear();
	}
	
	public void removeObserver(Observer o) {
		//TODO: mouse, keyboard should remove the players strategies that get removed
	}
	
	public void changed(String msg) {
		Set<String> types = observers.keySet();
		for (String type: types)
			for (Observer o: observers.get(type))
				o.update(msg);
	}
	
	public void print() {
		Util.print("observable's observers report");
		Set<String> types = observers.keySet();
		for (String type: types)
			Util.print(type, observers.get(type).size());
		Util.print("\n");
	}
}
