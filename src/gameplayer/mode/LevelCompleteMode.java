package gameplayer.mode;

import java.util.Set;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;

public class LevelCompleteMode extends State {

	public LevelCompleteMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		action();
	}

	@Override
	public void action() {
		controller.getScoreManager().awardPoints(Constant.defaultLevelCompleteScore + 
				controller.getCurrLevel().getObjectiveSize() * Constant.defaultObjectiveCompleteScore);
		controller.showResultsView(true, false);
	}

}
