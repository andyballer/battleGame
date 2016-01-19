package display.jgame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.ResourceBundle;

import display.Constant;
import jgame.JGPoint;

public class GameCursor extends GridObject {

	private GameEnv myGameEnv;
	private double mySize;
	private int myRow, myCol;
	private HashMap<Integer, String> myCommands;
	private String cursorImage;
	private boolean cursorLock = false;
	
	
	public GameCursor(int x1, int y1, int row, int col,
			int tileSize, GameEnv authorEnv) {
		super("Cursor", x1, y1, Constant.cursorCollisionID);
		x = (double) x1+1;
		y = (double) y1+1;
		mySize = (double) tileSize;
		myGameEnv = authorEnv;
		myRow = row;
		myCol = col;
		myCommands = new HashMap<Integer, String>();
		myCommands.put(myGameEnv.key_down, "Down");
		myCommands.put(myGameEnv.key_up, "Up");
		myCommands.put(myGameEnv.key_left, "Left");
		myCommands.put(myGameEnv.key_right, "Right");
		
		cursorImage = myGameEnv.defineImageWithScale("CursorImage", "Cursor", Constant.cursorCollisionID, 
				Constant.imageDirectory+ResourceBundle.getBundle(Constant.imagePropertyFile).getString("CursorImage"), 
				(int) mySize, (int) mySize);
	}

	public void paint(){
		myGameEnv.drawImage(x, y, cursorImage);
	}
	
	public void move(){
		JGPoint currentTile = myGameEnv.getTileCoordinates(myGameEnv.getMouseX(), myGameEnv.getMouseY());
		if(myGameEnv.getMouseInside() && currentTile != null && !cursorLock){
			setRowCol(currentTile.y/mySize, currentTile.x/mySize-1);
			setPos(currentTile.x, currentTile.y);
		} else if (!cursorLock){
			for(Integer key : myCommands.keySet()){
				if(myGameEnv.getKey(key)){
					myGameEnv.clearKey(key);
					Class<?> c = this.getClass();
					Method method = null;
					try {
						method = c.getDeclaredMethod (myCommands.get(key));
						method.invoke (this);
					} catch (NoSuchMethodException e) {e.printStackTrace();
					} catch (SecurityException e) {e.printStackTrace();
					} catch (IllegalArgumentException e) { e.printStackTrace();
					} catch (IllegalAccessException e) { e.printStackTrace();
					} catch (InvocationTargetException e) {e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void setRowCol(double row, double col){
		myRow = (int) row;
		myCol = (int) col;
	}
	
	private void Down(){
		if(myRow<myGameEnv.getDimensions().y-1){
			myRow++;		
			y+=mySize;
		}
	}
	
	private void Up(){
		if(myRow>0){
			myRow--;	
			y-=mySize;
		}
	}
	
	private void Left(){
		if(myCol>0){
			myCol--;	
			x-=mySize;
		}
	}
	
	private void Right(){
		if(myCol<myGameEnv.getDimensions().x-1){
			myCol++;
			x+=mySize;
		}
	}
	
	public void lock(){
		cursorLock = true;
	}
	
	public void unlock(){
		cursorLock = false;
	}
	
}
