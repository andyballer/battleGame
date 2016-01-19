package objectives;

import java.util.List;

import display.Constant;
import engine.ViewController;

/**
 * The subclass of Objective that defines killing the boss.
 * @author Grace
 */
public class KillBoss extends Objective {

	/**
	 * Constructor for the objective of killing all bosses.
	 * Calls the populateTeamIDs method, which adds the team IDs of all objects of interest. 
	 * For KillBoss, the team ID of the boss, retrieved from Constant.java, will be used.
	 * All this hardcoding is temporary.
	 * @param level
	 * @param args - should be empty
	 */
	public KillBoss(final String l, List<Integer> args,ViewController c) {
		super(l, args, c);
		populateFlaggedTeamIDs(Constant.bossTeam);
	}
	
	public boolean isAchieved(){
		return (getNumberFlaggedObjects(myTeamIDIndicator) <= 0);
	}

	@Override
	public void remove() {
	}

}
