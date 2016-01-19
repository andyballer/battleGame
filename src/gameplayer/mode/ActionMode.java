package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import objects.objects.Unit;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import gameplayer.action.UnitAction;
import jgame.JGPoint;

public class ActionMode extends State {

	public ActionMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}
	
	@Override
	public void start(final Set<JGPoint> selectables) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				if (!stateManager.getCurrAction().requireTarget())
					controller.getStateManager().setState(Constant.targetSelectionCompleteMode, false);
				else
					controller.getStateManager().setState(Constant.targetSelectionMode, 
							selectables == null ? stateManager.getPotentialSpots() : selectables, false);	
			}
		});
	}

	@Override
	public void action() {
		Unit actionFrom = stateManager.getCurrPlayingUnit();
		UnitAction action = stateManager.getCurrAction();
		JGPoint target = stateManager.getCurrTarget();
		Assert.assertNotNull(actionFrom);
		Assert.assertNotNull(action);
		Assert.assertNotNull(target);
		action.applyAction(actionFrom, target.x, target.y);
	}
	
}
