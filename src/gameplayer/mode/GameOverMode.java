package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;

public class GameOverMode extends State {

	public GameOverMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				controller.setJGameEngineSelectable(false);
				controller.setObjectSelectorPanelEnabled(false);
				action();
			}
		});
	}

	@Override
	public void action() {
		controller.getScoreManager().awardPoints(controller.getCurrLevel().getObjectiveSize() * Constant.defaultObjectiveCompleteScore);
		controller.showResultsView(false, false);
	}
	
}
