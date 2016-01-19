package gameplayer.mode;

import java.util.Set;

import jgame.JGPoint;
import engine.ViewController;

public abstract class State {
	
	protected ViewController controller = null;
	protected StateManager stateManager = null;
	protected final String label;
	
	public State(String l, ViewController c, StateManager manager) {
		controller = c;
		stateManager = manager;
		label = l;
	}
	
	public String getStateName() { // gets the name of the state 
		return label;
	}
	
	abstract public void start(Set<JGPoint> selectables); // initializes the state given the user input
	abstract public void action();

}
