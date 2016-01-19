package jgame;

/** Minimal replacement of java.awt.Point. */
public class JGPoint {

	public int x=0,y=0;

	public JGPoint () {}

	public JGPoint (JGPoint p) {
		x = p.x;
		y = p.y;
	}

	public JGPoint (int x,int y) {
		this.x=x;
		this.y=y;
	}
	public String toString() {
		return "JGPoint("+x+","+y+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JGPoint) {
			JGPoint p = (JGPoint)obj;
			if (p.x == x && p.y == y) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// cannot guarantee the hash code will always be different for different (x,y)
		String str = x + "!@#$%*****^&*()" + y;
		return str.hashCode();
	}
}
