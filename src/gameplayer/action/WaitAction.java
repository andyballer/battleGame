package gameplayer.action;

import java.util.HashSet;
import java.util.Set;

import jgame.JGPoint;
import objects.objects.Unit;
import display.Constant;
import engine.ViewController;

public class WaitAction extends UnitAction {

	public WaitAction(String l, ViewController c) {
		super(l, c);
	}

	@Override
	public void applyAction(Object obj, int row, int col) {
		myController.playActionSound(Constant.waitTag);
	}

	@Override
	public boolean checkValid(int row, int col) {
		return true;
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
