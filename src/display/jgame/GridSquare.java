package display.jgame;

import jgame.JGColor;

public class GridSquare extends GridObject{

	private GameEnv authorEnv;
	private double myX1, myY1, mySize;
	private JGColor myColor;
	
	public GridSquare(String name, int x1, int y1, int collisionid, int tileSize, JGColor color, GameEnv authorEnv){
		super(name, x1, y1, collisionid);
		this.authorEnv = authorEnv;
		myX1 = (double) x1 + 2;
		myY1 = (double) y1 + 2;
		mySize = (double) tileSize - 2;
		myColor = color;
	}
	
	@Override
	public void paint() {
		authorEnv.drawRect(myX1, myY1, mySize, mySize, false, false, 2, myColor);
	}
	
	public JGColor getColor() {
		return myColor;
	}

}
