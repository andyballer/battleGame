package display.animation;

import jgame.JGObject;
import jgame.JGPoint;

/*
 * Code adapted from Jiaran Hao who took this course last semester
 */

public class Animation extends JGObject {
	
	protected JGObject myObject;
	protected String label;
	
	public Animation(String animationName, int x, int y, JGObject obj) {
		super(animationName, true, 0, 0, 0, null);
		label = animationName;
		myObject = obj;
		JGPoint p = eng.getImageSize(obj.getImageName());
        myObject.x = x - p.x / 2;
        myObject.y = y - p.y / 2;
        this.x = myObject.x;
        this.y = myObject.y;
	}

}
