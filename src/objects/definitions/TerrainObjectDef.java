package objects.definitions;

import java.io.Serializable;

public class TerrainObjectDef extends GameObjectDef implements Serializable {

	private static final long serialVersionUID = -1329206825935730383L;

	public TerrainObjectDef(final int collisionID, 
			final String name,
			final String graphicsName, 
			final int x, 
			final int y, 
			final int row,
			final int col,
			final String originalPath,
			final boolean isMovable,
			final String objName) {
		super(collisionID, name, graphicsName, x, y, row, col, "Terrain", originalPath, isMovable, false, objName);
	}

}
