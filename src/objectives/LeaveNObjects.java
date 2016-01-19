package objectives;

import java.util.List;

import engine.ViewController;

/**
 * Subclass of Objective that defines leaving N collectible objects on the grid
 * (or collecting (total # - N) objects).
 * @author Grace
 */
public class LeaveNObjects extends Objective {

	private int myN;
	
	/**
	 * Constructor for LeaveNObjects
	 * @param level
	 * @param args - first (only) element should be the number of objects to be left (N)
	 */
	public LeaveNObjects(final String l, List<Integer> args, ViewController c) {
		super(l, args,c);
		populateFlaggedCollisionIDs(3); //using 3 for testing purposes; to be replaced with user input later
		myN = args.get(0);
	}

	@Override
	public boolean isAchieved() {
		return getNumberFlaggedObjects(myCollisionIDIndicator) <= myN;
	}

	@Override
	public void remove() {

	}

}
