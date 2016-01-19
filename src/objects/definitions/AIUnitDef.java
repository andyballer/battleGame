package objects.definitions;

import java.util.Map;
import java.util.Set;

import engine.DeepCloner;
import objects.objects.StatSheet;
import objects.objects.artificialIntelligence.Strategy;

public class AIUnitDef extends UnitDef {

	private static final long serialVersionUID = -8520459907286948226L;
	public final Strategy strategy;
	
	public AIUnitDef (final int collisionID,
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
			final String objName,
			final Strategy strategy) {
		super(collisionID, name, graphicsName, team, x, y, row, col, stats, growths, type, origPath, 1, 0, inventory, weapons, actions, objName);
		this.strategy = strategy;
	}

	public AIUnitDef (final int collisionID,
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
			final String objName,
			final Strategy strategy) {
		super(collisionID, name, graphicsName, team, x, y, row, col, stats, growths, type, origPath, level, experience, inventory, weapons, actions, objName);
		this.strategy = strategy;
	}
	
	@SuppressWarnings("unchecked")
	public AIUnitDef clone() {
    	try {
			AIUnitDef def = (AIUnitDef) DeepCloner.deepCopy(this);
			Map<PickupObjectDef, Integer> inv = (Map<PickupObjectDef, Integer>) DeepCloner.deepCopy(inventory);
	    	Set<PickupObjectDef> w = (Set<PickupObjectDef>) DeepCloner.deepCopy(weapons);
	        Set<String> a = (Set<String>) DeepCloner.deepCopy(actions);
	        return new AIUnitDef(def.collisionID, def.name, def.jGameGraphicPath, def.team, def.x, def.y, def.row, def.col, 
	        		def.stats, def.growths, def.type, def.originalGraphicPath, def.level, def.experience, inv, w, a, def.objName, def.strategy);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }

}
