package gameplayer.action;

import java.util.Set;

import objects.objects.Unit;
import jgame.JGPoint;
import engine.ViewController;

/**
 * Abstract class for any type of action a unit may decide to perform
 */
public abstract class UnitAction {
	
	protected final String myLabel;
	protected final ViewController myController;
	
	public UnitAction(String l, ViewController c) {
		myLabel = l;
		myController = c;
	}
	
	public String getName() {
		return myLabel;
	}
	
	/**
	 * let unit take action and do something on grid with {@code row} and {@code col}
	 * @param unit
	 * @param row
	 * @param col
	 */
	abstract public void applyAction(Object obj, int row, int col);
	
	abstract public boolean checkValid(int row, int col);
	/**
	 * indicate whether this action requires targets other than the caller
	 * @return
	 */
	abstract public boolean requireTarget();
	
	abstract public Set<JGPoint> getTargetRange(Unit source);
 
}
