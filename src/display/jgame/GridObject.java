package display.jgame;

import jgame.JGObject;

public abstract class GridObject extends JGObject{

	public GridObject(String name, double x1, double y1, int collisionid) {
		super(name, true, x1, y1, collisionid, null);
	}
	
	public abstract void paint();

}
