package gameplayer.action;

import java.util.Map;
import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import objects.definitions.TerrainObjectDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import engine.ResourceLoader;
import engine.ViewController;

public class PlaceTerrainAction extends UnitAction{

	private String objName;
	private boolean isMovable;
	
	public PlaceTerrainAction(String l, ViewController c) {
		super(l, c);
		objName = l;
		if (objName.equals(Constant.treeTag))
			isMovable = false;
		else 
			isMovable = true;
	}

	@Override
	public void applyAction(Object obj, int row, int col) {
		Assert.assertTrue(obj != null && obj instanceof Unit && 
				myController.getObject(row,col) == null);
		String type = Constant.nameToType.get(objName);
		//System.out.println(type + " " + objName);
		String name = Constant.defaultObjectName;
		int x = myController.getTileCoordinates()[row][col].x;
		int y = myController.getTileCoordinates()[row][col].y;
		Map<String,String> nameToImages = ResourceLoader.load(type, Constant.imagePropertyFile);
		String imagePath = nameToImages.get(objName);
		Assert.assertNotNull(imagePath);
		int collisionId = Constant.nameToCollisionID.get(objName);
		myController.makeObject(new TerrainObjectDef(collisionId, name, objName, x, y, row, col,
				imagePath, isMovable, objName));
		myController.playActionSound(objName);
		
	}

	@Override
	public boolean checkValid(int row, int col) {
		GameObject obj = myController.getObject(row, col);
		return (obj == null);
	}

	@Override
	public boolean requireTarget() {
		return true;
	}

	@Override
	public Set<JGPoint> getTargetRange(Unit source) {
		return myController.getCurrLevel().getSurroundings(source.getPosition());
	}

}
