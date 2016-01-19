package gameplayer.action;

import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import objects.definitions.UnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;

public class HitAction extends UnitAction {

	public HitAction(String l, ViewController c) {
		super(l, c);
	}

	@Override
	public void applyAction(Object o, int row, int col) {
		Assert.assertTrue(o instanceof Unit);
		Unit unit = (Unit)o;
		GameObject obj = myController.getObject(row, col);
		myController.displayStatus(unit.getUnitName() + " is attacking " + obj.getUnitName());
		myController.displayAnimation(Constant.explodeAnimationTag, obj.x, obj.y);
		Assert.assertTrue(obj instanceof Unit);
		Unit target = (Unit)obj;
		UnitDef def = (UnitDef)target.getDefinition();
		boolean res = unit.attack(target);
		if (res)
			myController.deleteObject(def);
		myController.playActionSound(Constant.hitTag);
	}

	@Override
	public boolean checkValid(int row, int col) {
		GameObject obj = myController.getObject(row, col);
		if (obj != null && obj instanceof Unit) return true;
		return false;
	}

	@Override
	public boolean requireTarget() {
		return true;
	}

	@Override
	public Set<JGPoint> getTargetRange(Unit source) {
		return myController.getCurrLevel().getRange(source, true);
	}

}
