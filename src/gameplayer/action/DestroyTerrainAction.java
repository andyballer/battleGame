package gameplayer.action;

import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import objects.objects.GameObject;
import objects.objects.TerrainObject;
import objects.objects.Unit;
import engine.ViewController;

/**
 * Combines smashing rocks and cutting down trees into one action class.
 */
public class DestroyTerrainAction extends UnitAction {

	protected String myTag;
	protected String myDestroyTag;

	public DestroyTerrainAction(String l, ViewController c) {
		super(l, c);
	}

	@Override
	public void applyAction(Object obj, int row, int col) {
		GameObject target = myController.getObject(row, col);
		Assert.assertTrue(obj != null && obj instanceof Unit && 
				target instanceof TerrainObject && target.getObjName().equals(myTag));
		target.remove();
		myController.playActionSound(myDestroyTag);
	}

	@Override
	public boolean checkValid(int row, int col) {
		GameObject obj = myController.getObject(row, col);
		if (obj == null) return false;
		return (obj instanceof TerrainObject && obj.getObjName().equals(myTag));
	}

	@Override
	public boolean requireTarget() {
		return true;
	}

	@Override
	public Set<JGPoint> getTargetRange(Unit source) {
		return myController.getCurrLevel().getRange(source, false);
	}

}
