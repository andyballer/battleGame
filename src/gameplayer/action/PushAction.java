package gameplayer.action;

import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import objects.objects.GameObject;
import objects.objects.Unit;
import engine.ViewController;

public class PushAction extends UnitAction {
	
	public PushAction(String l, ViewController c) {
		super(l,c);
	}

	@Override
	public void applyAction(Object obj, int row, int col) {
		Assert.assertTrue(obj instanceof Unit);
		Unit pusher = (Unit) obj;
		GameObject toPush = myController.getObject(row, col);
		if(toPush.isMovable()) {
			int xChange = toPush.getRow() - pusher.getRow();
			int yChange = toPush.getCol() - pusher.getCol();
			myController.pushObject(toPush, row, col, row+xChange, col+yChange);
			myController.playActionSound(Constant.pushTag);
		}
	}

	@Override
	public boolean checkValid(int row, int col) {
		if (myController.isSelectable(row, col)) {
			GameObject obj = myController.getObject(row, col);
			if (obj != null && obj.isMovable()) return true;
		}
		return false;
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
