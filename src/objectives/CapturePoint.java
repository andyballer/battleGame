package objectives;

import java.util.ArrayList;
import java.util.List;

import jgame.JGPoint;
import display.Constant;
import engine.ResourceLoader;
import engine.ViewController;
import objects.definitions.PickupObjectDef;
import objects.objects.GameObject;
import objects.objects.StatSheet;
import objects.objects.Unit;

/**
 * In this objective, the character with the given ID needs 
 * to be on the specified x and y coordinates to successfully
 * accomplish the goal
 * @author Teddy
 */
public class CapturePoint extends Objective {

	private int myX;
	private int myY;
	
	private GameObject highlight = null;
	
	/**
	 * Constructor for CapturePoint.
	 * @param level Level this is an objective for
	 * @param args : first (0th index) is y coordinate of capture point,
	 * 				second (1st index) is x coordinate of capture point
	 */
	public CapturePoint(final String l, final List<Integer> args, ViewController c){
		super(l, args, c);
		myX = args.get(0);
		myY = args.get(1);
		
		//creating flag image
		String flagName = Constant.flagTag;
		String flagImagePath = ResourceLoader.load(flagName, Constant.imagePropertyFile).get("");
		
		highlight = c.makeObject(new PickupObjectDef(Constant.envCollisionID, "", flagName, 0, 0, myY, myX, 
				Constant.itemTypeTag, new StatSheet(0,0,0,0,0,0), flagImagePath, false, new ArrayList<String>(), flagName), myY, myX);
	}
	
	public void remove() {
		if (highlight != null) {
			myController.deleteObject((PickupObjectDef)highlight.getDefinition());
			highlight = null;
		}
	}
	
	/**
	 * Checks if there is a player-controlled unit on the checkpoint
	 */
	@Override
	public boolean isAchieved(){
		GameObject g = level.getObject(myY, myX);
		if (g instanceof Unit)
			return (((Unit)g).getTeam() == Constant.playerTeam);
		return false;
	}
	
	public JGPoint getCapturePoint(){
		return new JGPoint(myX, myY);
	}
	
}
