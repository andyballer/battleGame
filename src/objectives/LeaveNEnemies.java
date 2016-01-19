package objectives;

import java.util.List;

import display.Constant;
import engine.ViewController;

/**
 * The subclass of Objectives that defines killing a certain
 * number of enemy objects, excluding the boss.
 * @author Grace
 */
public class LeaveNEnemies extends Objective {
	
	private int myN;

	/**
	 * Constructor for the LeaveNEnemies objective. Calls the populateTeamIDs method,
	 * which adds the team IDs of all objects of interest to a list of flagged team IDs. 
	 * 
	 * Perhaps the parameter we should ask for from the user is the number of enemies they
	 * want to eliminate, rather than leave alive? That would require jumping through some 
	 * arithmetic hoops.
	 * 
	 * For LeaveNEnemies, the team ID of enemies in general, retrieved from 
	 * Constant.java, will be used. Whether a boss is still alive or not is not considered.
	 * @param level
	 * @param args - first (only) element should be the number of enemies to be left (N)
	 */
	public LeaveNEnemies(final String l, List<Integer> args, ViewController c) {
		super(l, args,c);
		populateFlaggedTeamIDs(Constant.enemyTeam);
		myN = args.get(0);
	}

	@Override
	public boolean isAchieved() {
		int numFlagged = getNumberFlaggedObjects(myTeamIDIndicator);
		return (numFlagged <= myN);
	}

	@Override
	public void remove() {
	
	}
	
}
