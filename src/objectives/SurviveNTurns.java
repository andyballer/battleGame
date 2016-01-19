package objectives;

import java.util.List;

import engine.ViewController;

/**
 * Subclass of Objective that defines surviving a certain number of turns.
 * @author Grace
 */
public class SurviveNTurns extends Objective {
	/**
	 * Constructor for SurviveNTurns.
	 * @param level
	 * @param args - first (only) element should be the number of turns the user 
	 * wants to survive for
	 */
	public SurviveNTurns(final String l, List<Integer> args, ViewController c) {
		super(l, args,c);
	}

	@Override
	public boolean isAchieved() {
		return (myController.getStateManager().getTurns() >= myTurnLimit);
	}

	@Override
	public boolean isFailed(int currentTurnCount){
		return false;
	}

	@Override
	public void remove() {

	}
}
