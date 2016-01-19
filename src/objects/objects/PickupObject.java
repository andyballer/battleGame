package objects.objects;

import java.util.List;

import org.junit.Assert;

import engine.DeepCloner;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;

/**
 * Object that can be picked up (is collectible).
 */
public class PickupObject extends GameObject {
	
	private StatSheet myBuffs;
	private List<String> myActions;

	/**
	 * Constructor for a collectible object.
	 * @param def the definition class for this object
	 */
	public PickupObject(final PickupObjectDef def) {
	    super(def);
		myBuffs = def.buffs;
		myActions = def.actions;
		myCollectible = true;
	}

	/**
	 * Creates a new stat sheet for the pickup object, which
	 * contains the changes that should be made in the stats
	 * of whichever unit picks it up.
	 * @param maxHealth
	 * @param bonusHealth
	 * @param power
	 * @param defense
	 * @param speed
	 * @param range
	 */
	public void setStats(double maxHealth, double bonusHealth,
			double power, double defense,
			double speed, double range){
		myBuffs = new StatSheet(maxHealth, bonusHealth, power, defense, speed, range);
	}
	
	public StatSheet getBuffs() {
		Assert.assertNotNull(myBuffs);
		try {
			return (StatSheet) DeepCloner.deepCopy(myBuffs);
		} catch (Exception e) {
			System.out.println("Cloning of StatSheet failed"); //TODO: throw new deep clone exception
		}
		return null;
	}
	
	/**
	 * Dictates what happens when pickUpItem is called.
	 * @param unit
	 * @param maxLife
	 * @param bonusLife
	 * @param power
	 * @param defense
	 * @param speed
	 * @param range
	 */
	public void use(Unit unit){
		unit.boostStats(myBuffs);
	}
	
	public List<String> getActions() {
		return myActions;
	}
	
	public GameObjectDef getDefinition() {
		return new PickupObjectDef(myCollisionID, myName, myJGameGraphicPath, (int)x, (int)y, myRow, myCol, 
				myType, myBuffs, myOriginalGraphicPath, myMovable, myActions, myObjName);
	}
}
