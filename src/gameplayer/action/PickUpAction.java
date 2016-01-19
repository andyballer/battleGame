package gameplayer.action;

import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import objects.objects.GameObject;
import objects.objects.PickupObject;
import objects.objects.Unit;

public class PickUpAction extends UnitAction {

	public PickUpAction(String l, ViewController c) {
		super(l, c);
	}

	@Override
	public void applyAction(Object obj, int row, int col) {
		Assert.assertTrue(obj instanceof Unit);
		Unit source = (Unit)obj;
		PickupObject pickUpItem = (PickupObject)myController.getObject(row, col);
		source.pickUp(pickUpItem);
		myController.playActionSound(Constant.pickUpTag);
	}

	@Override
	public boolean checkValid(int row, int col) {
		GameObject obj = myController.getObject(row, col);
		if (obj == null) return false;
		if (obj instanceof PickupObject) return true;
		else return false;
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
