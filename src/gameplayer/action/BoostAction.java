package gameplayer.action;

import java.util.HashSet;
import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import objects.definitions.PickupObjectDef;
import objects.objects.GameObject;
import objects.objects.Unit;

public class BoostAction extends UnitAction {

	public BoostAction(String l, ViewController c) {
		super(l, c);
	}

	/**
	 * 
	 * @param obj The pickUp object that can boost a unit
	 * @param row The row where has the unit
	 * @param col The col where has the unit
	 */
	@Override
	public void applyAction(Object obj, int row, int col) {
		Assert.assertNotNull(obj);
		Assert.assertTrue(obj instanceof PickupObjectDef);
		GameObject target = myController.getObject(row, col);
		if (target instanceof Unit) {
			Unit unit = (Unit)target;
			PickupObjectDef pick = (PickupObjectDef)obj;
			myController.displayAnimation(Constant.healAnimationTag, unit.x, unit.y);
			unit.boostStats(pick.buffs);
		}
		
	}
	/**
	 * boost action is a passive action, so no need to call checkValid actually
	 * the caller can call applyAction directly but needs to guarantee that all arguments are valid
	 */
	@Override
	public boolean checkValid(int row, int col) {
		return false;
	}

	@Override
	public boolean requireTarget() {
		return false;
	}

	@Override
	public Set<JGPoint> getTargetRange(Unit source) {
		Set<JGPoint> points = new HashSet<JGPoint>();
		points.add(myController.getStateManager().getCurrPlayingUnit().getPosition());
		return points;
	}

}
