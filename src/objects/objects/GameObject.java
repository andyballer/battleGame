package objects.objects;

import objects.definitions.GameObjectDef;
import saveLoad.Definable;
import saveLoad.Definition;
import jgame.JGObject;
import jgame.JGPoint;


/**
 * Superclass of all game objects that can be placed
 * on the game grid. Includes both units (characters) and objects.
 * 
 * @author Grace
 */
public class GameObject extends JGObject implements Definable {
	
    protected String myJGameGraphicPath;
    protected String myOriginalGraphicPath;
    protected int myCollisionID;
    protected String myName;

    protected int myRow;
    protected int myCol;

    protected boolean myMovable;
    protected boolean myCollectible;
    protected String myType;
    protected String myObjName;

    /**
     * GameObject constructor. No team ID for game objects (non-unit objects)
     * because putting trees in teams makes no sense.
     * 
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
    public GameObject (GameObjectDef def) {
        super(def.name, true, def.x, def.y, def.collisionID, def.jGameGraphicPath);

        myJGameGraphicPath = def.jGameGraphicPath;
        myOriginalGraphicPath = def.originalGraphicPath;
        myCollisionID = def.collisionID;
        myName = def.name;
        myType = def.type;
        myRow = def.row;
        myCol = def.col;
        myMovable = def.isMovable;
        myCollectible = def.isCollectible;
        myObjName = def.objName;
    }

    public String getGraphicPath() {
        return myOriginalGraphicPath;
    }

    public int getCollisionID(){
        return myCollisionID;
    }

    /**
     * Returns the type tag of the object
     * @return String type (player/enemy/terrain/weapon/stock)
     */
    public String getType() {
        return myType;
    }

    public String getUnitName() {
        return myName;
    }

    public void setUnitName(String s) {
        myName = s;
    }

    public JGPoint getPixelLocation() {
        return new JGPoint((int)this.x, (int)this.y);
    }

    public void hide() {
        setImage(null);
    }

    public boolean isMovable() {
        return myMovable;
    }

    public boolean isCollectible() {
        return myCollectible;
    }

    public void show() {
        setImage(myJGameGraphicPath);
    }

    public int getRow() {
        return myRow;
    }

    public int getCol() {
        return myCol;
    }
    
    public String getObjName() {
    	return myObjName;
    }

    public void setPosition(int r, int c) {
        myRow = r;
        myCol = c;
    }

    public JGPoint getPosition() {
        return new JGPoint(myRow, myCol);
    }

    @Override
    public Definition getDefinition () {
        return new GameObjectDef(myCollisionID, myName, myJGameGraphicPath, (int) x, (int) y, myRow, myCol,
                                 myType, myOriginalGraphicPath, myMovable, myCollectible, myObjName);
    }
}
