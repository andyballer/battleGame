package objects.definitions;

import java.util.Map;
import java.util.Set;

import display.Constant;
import engine.DeepCloner;
import objects.objects.StatSheet;

public class UnitDef extends GameObjectDef {

	private static final long serialVersionUID = -647672225567884431L;
	public final int team;
    public final StatSheet stats;
    public final StatSheet growths;
    public final int level;
    public final int experience;

    public final transient Map<PickupObjectDef, Integer> inventory;
    public final transient Set<PickupObjectDef> weapons;
    public final transient Set<String> actions;

    /**
     * Normal Constructor for class UnitDef just assigns fields.
     * 
     * @param collisionID The ID this character uses to detect collisions
     * @param name The name of this character
     * @param graphicsName The name of the sprite for this character
     * @param x initial x location of this character
     * @param y initial y location of this character
     * @param team the team number of this character
     * @param stats The stat sheet for this character --
     *        contains max health, current health, power, defense, speed, and range
     * @param growths the percent chance each stat has to gain a rank on level up
     */
    public UnitDef (final int collisionID,
                    final String name,
                    final String graphicsName,
                    final int team,
                    final int x,
                    final int y,
                    final int row,
                    final int col,
                    final StatSheet stats,
                    final StatSheet growths,
                    final String type,
                    final String origPath,
                    final Map<PickupObjectDef, Integer> inventory,
                    final Set<PickupObjectDef> weapons,
                    final Set<String> actions,
                    final String objName) {
        this(collisionID, name, graphicsName, team, x, y, row, col, stats, growths, type, origPath,
             1, 0, inventory, weapons, actions, objName);
    }

    /**
     * Simpler constructor for Unit
     */
    public UnitDef (final int collisionID,
                    final String name,
                    final String graphicsName,
                    final int team,
                    final int x,
                    final int y,
                    final int row,
                    final int col,
                    final String type,
                    final String origPath,
                    final Map<PickupObjectDef, Integer> inventory,
                    final Set<PickupObjectDef> weapons,
                    final Set<String> actions,
                    final String objName) {
        this(collisionID, name, graphicsName, x, y, row, col, team, 
             new StatSheet(Constant.defaultHealth.doubleValue(), 
            		 	   Constant.defaultHealth.doubleValue(), 
            		 	   Constant.defaultPower.doubleValue(), 
            		 	   Constant.defaultDefense.doubleValue(), 
            		 	   Constant.defaultSpeed.doubleValue(), 
            		 	   Constant.defaultRange.doubleValue()), type, origPath, inventory, weapons, actions, objName);
    }

    /**
     * Simpler constructor for Unit
     */
    public UnitDef (final int collisionID,
                    final String name,
                    final String graphicsName,
                    final int team,
                    final int x,
                    final int y,
                    final int row,
                    final int col,
                    final StatSheet stats,
                    final String type,
                    final String origPath,
                    final Map<PickupObjectDef, Integer> inventory,
                    final Set<PickupObjectDef> weapons,
                    final Set<String> actions,
                    final String objName) {
        this(collisionID, name, graphicsName, team, x, y, row, col, stats, 
             new StatSheet(Constant.defaultHealthLevelUp, 
      		 	   Constant.defaultHealthLevelUp, 
      		 	   Constant.defaultPowerLevelUp, 
      		 	   Constant.defaultDefenseLevelUp, 
      		 	   Constant.defaultSpeedLevelUp, 
      		 	   Constant.defaultRangeLevelUp), type, origPath, inventory, weapons, actions, objName);
    }

    /**
     * This constructor is only called mid-game to save the state of the Unit 
     * 
     * @param collisionID
     * @param name
     * @param graphicsName
     * @param team
     * @param x
     * @param y
     * @param row
     * @param col
     * @param stats
     * @param growths
     * @param type
     * @param origPath
     * @param level
     * @param experience
     */
    public UnitDef (final int collisionID,
                    final String name,
                    final String graphicsName,
                    final int team,
                    final int x,
                    final int y,
                    final int row,
                    final int col,
                    final StatSheet stats,
                    final StatSheet growths,
                    final String type,
                    final String origPath,
                    final int level,
                    final int experience,
                    final Map<PickupObjectDef, Integer> inventory,
                    final Set<PickupObjectDef> weapons,
                    final Set<String> actions,
                    final String objName) {
        super(collisionID, name, graphicsName, x, y, row, col, type, origPath, true, false, objName);
        this.team = team;
        this.stats = stats;
        this.growths = growths;
        this.level = level;
        this.experience = experience;
        this.inventory = inventory;
        this.weapons = weapons;
        this.actions = actions;
    }
    
	@SuppressWarnings("unchecked")
	public UnitDef clone() {
    	try {
			UnitDef def = (UnitDef) DeepCloner.deepCopy(this);
			Map<PickupObjectDef, Integer> inv = (Map<PickupObjectDef, Integer>) DeepCloner.deepCopy(inventory);
	    	Set<PickupObjectDef> w = (Set<PickupObjectDef>) DeepCloner.deepCopy(weapons);
	        Set<String> a = (Set<String>) DeepCloner.deepCopy(actions);
	        return new UnitDef(def.collisionID, def.name, def.jGameGraphicPath, def.team, def.x, def.y, def.row, def.col, 
	        		def.stats, def.growths, def.type, def.originalGraphicPath, def.level, def.experience, inv, w, a, def.objName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	
    public String toString() {
        String statsStr = "";
        String growthsStr = "";
        String inventoryStr = "";
        String weaponsStr = "";
        if (stats != null)  {
            statsStr = stats.toString(); 
        }
        if (growths != null) {
            growthsStr = growths.toString();
        }
        if (inventory != null) {
            inventoryStr = inventory.toString();
        }
        if (weapons != null) {
            weaponsStr = weapons.toString();
        }
    	return super.toString() + " " + team + " " + statsStr + " " + growthsStr + " " +
    			level + " " + experience + " " + inventoryStr + " " + weaponsStr;
    }
    
}
