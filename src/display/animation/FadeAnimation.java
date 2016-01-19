package display.animation;

import jgame.JGObject;

public class FadeAnimation extends Animation {
	
	private double fade = 5;
	
	public FadeAnimation (String effectName, int x, int y, JGObject o, double duration) {
		super(effectName, x, y, o);
		fade = duration;
	}

	public void move () {
		fade -= 0.025;
		if(fade <= 0) {
			myObject.remove();
			remove();
		}
	}
	
	public void remove() {
		if (myObject != null) {
			myObject.remove();
			super.remove();
		}
	}
}
