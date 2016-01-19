package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;

public class AllOverMode extends State {

	public AllOverMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		action();
	}

	@Override
	public void action() {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				controller.getScoreManager().awardPoints(Constant.defaultLevelCompleteScore + 
						controller.getCurrLevel().getObjectiveSize() * Constant.defaultObjectiveCompleteScore);
				controller.showResultsView(true, true);
			}
		});
	}
}
