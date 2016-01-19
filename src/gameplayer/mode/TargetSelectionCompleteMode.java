package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import org.junit.Assert;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;

public class TargetSelectionCompleteMode extends State {

	public TargetSelectionCompleteMode(String l, ViewController c,
			StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		State state = controller.getStateManager().getCurrActionState();
		Assert.assertNotNull(state);
		state.action();
		controller.displayStatus("CURRENT PLAYER FINISHES");
		controller.setJGameEngineSelectable(false);
		controller.setObjectSelectorPanelEnabled(false);
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				action();
			}
		});

	}

	@Override
	public void action() {
		if (!stateManager.isMoved() && stateManager.getCurrAction().getName().equals(Constant.moveTag)) {
			controller.getStateManager().checkObjectives();
			controller.getStateManager().setMoved(true);
			controller.updateStatsTable();
			stateManager.getCurrPlayingUnit().decreaseTurnCount();
			controller.getStateManager().setState(Constant.sourceSelectionCompleteMode, 
					stateManager.getCurrPlayingUnit().getPosition(), true);
			return;
		}
		new Thread() {
			public void run() {
				controller.getStateManager().nextPlayer();
			}
		}.start();
	}
}
