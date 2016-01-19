package gameplayer.action;

import java.util.Set;

import jgame.JGPoint;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import objects.objects.GameObject;
import objects.objects.Unit;

public class MoveAction extends UnitAction {
	
	private UnitAction includedAction = null;

	public MoveAction(String l, ViewController c) {
		super(l, c);
	}
	
	/**
	 * move unit to grid with {@code row} and {@code col}
	 * @param unit The unit to be moved
	 * @param row The row of the destination
	 * @param col The column of the destination
	 */
	public void applyAction(Object obj, int row, int col) {
		myController.getStateManager().setActionFinished(false);
		Assert.assertTrue(obj instanceof Unit);
		Unit unit = (Unit)obj;
		unit.incrementMoveCount();
		if (includedAction != null) {
			includedAction.applyAction(obj, row, col);
			includedAction = null;
		}
		myController.moveObject(unit, row, col);
		while (!myController.getStateManager().isActionFinished())
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		myController.playActionSound(Constant.moveTag);
	}
	
	/**
	 * check whether the destination with {@code row} and {@code col} is valid to take this action
	 * @param row The row of the destination
	 * @param col The column of the destination
	 * @return whether the destination is valid to take this action
	 */
	public boolean checkValid(int row, int col) {
		GameObject obj = myController.getObject(row, col);
		if (obj == null) return true;
		UnitAction action = myController.getStateManager().getCurrPlayingUnit().checkAvailableAction(row, col, myController);
		if (action != null) {
			includedAction = action;
			return true;
		}
		return false;
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
