package objects.objects;

import objects.definitions.GameObjectDef;
import objects.definitions.TerrainObjectDef;

/**
 * Could be an item that the character picks up 
 * or part of the terrain such as a mountain or
 * tree where the character cannot move. One can
 * tell the difference based on whether or not 
 * myCollectible is true or false.
 */
public class TerrainObject extends GameObject {

	public TerrainObject(TerrainObjectDef def) {
	    super(def);
	}
	
	@Override
	public GameObjectDef getDefinition() {
		return new TerrainObjectDef(myCollisionID, myName, myJGameGraphicPath, (int)x, (int)y, 
				myRow, myCol, myOriginalGraphicPath, myMovable, myObjName);
	}
}



