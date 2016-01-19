package display.jgame;

import jgame.JGColor;

public class GridLine extends GridObject{
	private double myX1;
	private double myY1;
	private double myX2;
	private double myY2;
	private GameEnv authorEnv;

	public GridLine(String name, int collisionid, double x1, double y1, double x2, double y2, GameEnv authorEnv){
		super(name, x1, y1, collisionid);
		this.authorEnv = authorEnv;
		myX1 = x1;
		myX2 = x2;
		myY1 = y1;
		myY2 = y2;
	}

	@Override
	public void paint() {
		authorEnv.drawLine(myX1, myY1, myX2, myY2, 1, JGColor.white);	
	}

}

