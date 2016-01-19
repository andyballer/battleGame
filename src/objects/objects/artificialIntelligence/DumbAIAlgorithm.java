package objects.objects.artificialIntelligence;

import engine.ViewController;
import jgame.JGPoint;

public class DumbAIAlgorithm extends AIAlgorithm {

	public DumbAIAlgorithm(ViewController controller) {
		super(controller);
	}

	@Override
	public double evaluateAttack(JGPoint point, AIUnit unit) {
		return Double.MIN_VALUE;
	}

	@Override
	public double evaluateDefense(JGPoint point, AIUnit unit) {
		return Double.MIN_VALUE;
	}

	@Override
	public double evaluateMoney(JGPoint point, AIUnit unit) {
		return Double.MIN_VALUE;
	}

	@Override
	public double evaluateObjective(JGPoint point, AIUnit unit) {
		return Double.MIN_VALUE;
	}

	@Override
	public double evaluateHit(JGPoint point, AIUnit unit) {
		return Double.MIN_VALUE;
	}

}
