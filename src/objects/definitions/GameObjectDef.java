package objects.definitions;

import java.io.Serializable;

import engine.DeepCloner;
import saveLoad.Definition;

public class GameObjectDef extends Definition implements Serializable {

	private static final long serialVersionUID = 4862610181700766923L;
	public final String jGameGraphicPath;
	public final String originalGraphicPath;
	public final int collisionID;
	public final String name;

	public final int x;
	public final int y;

	public final int row;
	public final int col;

	public final boolean isMovable;
	public final boolean isCollectible;
	public final String type;
	public final String objName;

	/**
	 * GameObject constructor. No team ID for game objects (non-unit objects)
	 * because putting trees in teams makes no sense.
	 * @param collisionID - collision ID of object
	 * @param name - name of object
	 * @param JGameGraphicsName - name of image associated with object
	 * @param x - x-coordinate of object
	 * @param y - y-coordinate of object
	 * @param tileSize - number of pixels along one edge of a tile
	 * @param type - player/enemy/terrain/item (tags in Constant)
	 * @param originalPath - original graphics path
	 * @param isMovable - whether or not the GameObject is movable
	 * @param isCollectible - whether or not the GameObject is collectible
	 */
	public GameObjectDef(final int collisionID, 
			final String name,
			final String JGameGraphicsName, 
			final int x, 
			final int y, 
			final int row,
			final int col,
			final String type, 
			final String originalPath,
			final boolean isMovable,
			final boolean isCollectible,
			final String objName) {

		this.jGameGraphicPath = JGameGraphicsName;
		this.originalGraphicPath = originalPath;
		this.collisionID = collisionID;
		this.name = name;
		this.type = type;
		this.x = x;
		this.y = y;
		this.row = row;
		this.col = col;
		this.isMovable = isMovable;
		this.isCollectible = isCollectible;
		this.objName = objName;
	}
	
	public GameObjectDef clone() {
    	try {
    		return (GameObjectDef) DeepCloner.deepCopy(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof GameObjectDef)) return false;
		GameObjectDef obj = (GameObjectDef)o;
		return this.name.equals(obj.name) && this.objName.equals(obj.objName) && this.type.equals(obj.type);
	}
	
	public String toString() {
		return name + " " + type + " " + collisionID + " " + jGameGraphicPath + " " + originalGraphicPath +
				" " + objName + " " + x + " " + y + " " + row + " " + col + " " + isMovable + " " + isCollectible;
	}

}
