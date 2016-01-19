package display.animation;

import jgame.JGObject;

/*
 * Code adapted from Jiaran Hao who took this course last semester
 */

public class OneTimeAnimation extends Animation {

	public OneTimeAnimation(String name, int x, int y, JGObject obj) {
		super(name, x, y, obj);
	}
	
	public void move() {
		if (myObject.getImageName().equals(label + "End")) {
			myObject.remove();
			this.remove();
		}
	}

}
