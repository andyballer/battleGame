package objects.definitions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import display.Constant;
import engine.DeepCloner;
import engine.ResourceLoader;
import objects.objects.StatSheet;

public class PickupObjectDef extends GameObjectDef implements Serializable {

	private static final long serialVersionUID = 8850513770761121486L;
	public final StatSheet buffs;
    public final List<String> actions;

    /**
     * Constructor for defining an object that can be picked up.
     * @param collisionID
     * @param name
     * @param JGameGraphicsName
     * @param x
     * @param y
     * @param row
     * @param col
     * @param type
     * @param buffs
     * @param originalPath
     * @param isMovable
     * @param a
     * @param objName
     */
    public PickupObjectDef (final int collisionID,
                     final String name,
                     final String JGameGraphicsName,
                     final int x, final int y, final int row, final int col, final String type,
                     final StatSheet buffs,
                     final String originalPath,
                     final boolean isMovable,
                     final List<String> a,
                     final String objName) {
        super(collisionID, name, JGameGraphicsName, x, y, row, col, type, originalPath,
              isMovable, true, objName);
        this.buffs = buffs;
        this.actions = a;
        Assert.assertNotNull(this.buffs);
    }
    
    public String toString() {
    	String str = super.toString();
    	return str + " " + buffs.toString() + " " + actions.toString();
    }
       
	@SuppressWarnings("unchecked")
	public PickupObjectDef clone() {
    	try {
    		PickupObjectDef def = (PickupObjectDef) DeepCloner.deepCopy(this);
	    	List<String> a = (List<String>) DeepCloner.deepCopy(actions);
	        return new PickupObjectDef(def.collisionID, def.name, def.jGameGraphicPath, def.x, def.y, def.row, def.col, 
	        		def.type, def.buffs, def.originalGraphicPath, def.isMovable, a, def.objName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	
	public Map<String, String> getActions() {
		Map<String, String> picsToPaths = ResourceLoader.load(Constant.actionSelectionTag, Constant.imagePropertyFile);
		Map<String, String> items = new HashMap<String, String>();
		
		for (String a : actions) {
			if (a.equals(Constant.boostTag)) continue;
			items.put(a, picsToPaths.get(a));
		}
		Constant.LOG(this.getClass(), "All Actions: " + items.toString());
		return items;
	}
}
