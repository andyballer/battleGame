package objectives;

import java.util.ArrayList;
import java.util.List;

import objects.objects.Unit;
import engine.Level;
import engine.ViewController;

/**
 * superclass for any task that the user should accomplish to complete the level.  
 * Examples include: leave no enemies alive (LeaveNEnemies), kill the boss (KillBoss), 
 * reach a designated square (CapturePoint), or survive 20 turns (Survive).
 * 
 * The purpose of all subclasses is basically to determine which team or collision 
 * IDs should be searched for on the grid to determine whether the objective has
 * been fulfilled or not, and to carry out those searches and return boolean values
 * (whether or not the objective has been fulfilled).
 */
public abstract class Objective {
    
	private String label;
    protected transient Level level;
    protected ViewController myController;
	private List<Integer> myFlaggedTeamIDs;
	private List<Integer> myFlaggedCollisionIDs;
	private List<Integer> myArguments;
	
	protected int myTurnLimit;
	
	protected final String myTeamIDIndicator = "team";
	protected final String myCollisionIDIndicator = "collision";
	
	/**
	 * Constructor for an objective.
	 * @param args specific values that need to be achieved. 
	 * ie: number of coins to pick up, location of capture point, etc.
	 * There will be a specific order of inputs, as determined by the objectives; 
	 * see each objective class for details on its requirements.
	 * ie: number of coins to pick up, location of capture point, etc.
	 */
	public Objective(final String l, final List<Integer> args, ViewController c) {
		label = l;
		myArguments = args;
		myController = c;
		
		myTurnLimit = myArguments.get(myArguments.size() - 1);
	}
	
	public String getName() {
		return label;
	}
	
	public void setLevel(Level l) {
		level = l;
	}
	
	/**
	 * This method takes in a list of team IDs that should be checked for
	 * in checking whether the objective has been fulfilled, and populates
	 * the list of flagged team IDs in the Objective superclass with the IDs
	 * found in the input list.
	 * @param teamID - a list of team IDs to be watched for when checking
	 * the grid to see whether the objective has been met or not. Any number
	 * of IDs may be entered. No brackets required, just delimit with commas.
	 */
	public void populateFlaggedTeamIDs(int ... teamIDs) {
		myFlaggedTeamIDs = new ArrayList<Integer>();
		for(int ID: teamIDs) {
			myFlaggedTeamIDs.add(ID);
		}
	}
	
	/**
	 * This method takes in a list of collision IDs that should be checked for
	 * in checking whether the objective has been fulfilled, and populates
	 * the list of flagged collision IDs in the Objective superclass with the IDs
	 * found in the input list.
	 * @param collisionID - a list of collision IDs to be watched for when checking
	 * the grid to see whether the objective has been met or not. Any number
	 * of IDs may be entered. No brackets required, just delimit with commas.
	 */
	public void populateFlaggedCollisionIDs(int ... collisionIDs) {
		myFlaggedCollisionIDs = new ArrayList<Integer>();
		for(int ID: collisionIDs) {
			myFlaggedCollisionIDs.add(ID);
		}
	}
	
	/**
	 * Traverses the Grid and gives the number of enemies left
	 * @return the number of enemies left
	 */
	protected int getNumberFlaggedObjects(String indicator){
		int count = 0;
		for(Integer ID : (indicator.toLowerCase().trim().equals(myTeamIDIndicator))? 
				myFlaggedTeamIDs : myFlaggedCollisionIDs) {
			try {
				for(int row = 0; row < level.getRows(); row++) {
					for(int col = 0; col < level.getCols(); col++) {
						try {
							Unit unit = (Unit) level.getObject(row, col);
							if((ID == ((indicator.equals(myTeamIDIndicator))? 
									unit.getTeam() : unit.getCollisionID())) && unit.isAlive()) {
								count++;
							}
						} catch (Exception e) {
							//System.out.println("No unit or object found at " + row + ", " + col);
							continue;
						}
					}
				}
			} catch (NullPointerException e) {
				System.out.println("The map has not been instantiated.");
			}
		}
		return count;
	}
	
	public List<Integer> getAllArguments() {
		return myArguments;
	}
	
	/**
	 * Returns the list of flagged team IDs.
	 */
	public List<Integer> getFlaggedTeamIDs() {
		return myFlaggedTeamIDs;
	}
	
	/**
	 * Returns the list of flagged collision IDs.
	 */
	public List<Integer> getFlaggedCollisionIDs() {
		return myFlaggedCollisionIDs;
	}
	
	/**
	 * This method will check to see if the objective in question has been
	 * achieved or not. 
	 * @return boolean - true if objective has been achieved, false if not.
	 */
	public abstract boolean isAchieved();
	
	public abstract void remove();
	
	public boolean isFailed(int currentTurn){
		return (currentTurn >= myTurnLimit);
	}
}
