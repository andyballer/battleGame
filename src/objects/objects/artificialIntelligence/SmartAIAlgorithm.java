package objects.objects.artificialIntelligence;

import java.util.List;
import java.util.Map;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;
import objectives.CapturePoint;
import objectives.KillBoss;
import objectives.LeaveNEnemies;
import objectives.LeaveNObjects;
import objectives.SurviveNTurns;
import objects.definitions.GameObjectDef;
import objects.definitions.UnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;

public class SmartAIAlgorithm extends AIAlgorithm {

	public SmartAIAlgorithm(ViewController controller) {
		super(controller);
	}
	
	@Override
	public double evaluateAttack(JGPoint point, AIUnit unit) {
		return evaluateSmartAttack(point, unit);
	}

	@Override
	public double evaluateDefense(JGPoint point, AIUnit unit) {
		double value = 0;

		value += evaluateDefenseBasedOnItems(point);
		
		Map<JGPoint, GameObjectDef> gameMap = myController.getCurrLevel().getMap();
		for (JGPoint p: gameMap.keySet()){
			GameObjectDef obj = gameMap.get(p);
			if (obj instanceof UnitDef && ((UnitDef) obj).type.equals(Constant.playerTypeTag)){
				double pathLength = PathFinder.findOptimalPath(point, p, myController.getCurrLevel()).size() + 1;
				value += Math.sqrt(pathLength) * Constant.AIFactor;
			}
		}

		return value;
	}

	@Override
	public double evaluateMoney(JGPoint point, AIUnit unit) {
		return evaluateSmartMoney(point, unit);
	}

	@Override
	public double evaluateObjective(JGPoint point, AIUnit unit) {
		Object[] objectives = myController.getCurrLevel().getAllObjectives();
		int value = 0;

		for (Object objective : objectives){
			if (objective instanceof LeaveNEnemies)
				value += evaluateDefense(point, unit)/Constant.AIFactor;
			if(objective instanceof SurviveNTurns)
				value += evaluateAttack(point, unit)/Constant.AIFactor;
			
			else if (objective instanceof KillBoss){
				List<JGPoint> bosses = findBosses();
				for (JGPoint p: bosses){
					double bossPath = PathFinder.findOptimalPath(unit.getPosition(), p, myController.getCurrLevel()).size() + 1;
					value += 1/bossPath * Constant.AIFactor;
				}
			}	
			else if (objective instanceof LeaveNObjects)
				value += evaluateMoney(point, unit)/Constant.AIFactor;
			else if (objective instanceof CapturePoint){
				double capturePath = PathFinder.findOptimalPath(point, ((CapturePoint) objective).getCapturePoint(), 
						myController.getCurrLevel()).size() + 1;
				value += 1/capturePath * Constant.AIFactor;
			}
				
		}
		return value;
	}

	/**
	 * Determines the reward for attacking the given square.
	 * @param point the square
	 * @return the reward value
	 */
	@Override
	public double evaluateHit(JGPoint point, AIUnit unit) {
		double value = 0;

		GameObject object = myController.getObject(point.x, point.y);

		Unit toAttack = null;
		if(object instanceof Unit && ((Unit) object).getType().equals(Constant.playerTypeTag)) {
			toAttack = (Unit) object;
		} else {
			return Double.MIN_VALUE;
		}

		value = Constant.AIFactor / toAttack.getStats().getCurrentHealth();
		value *= (unit.getStats().getPower() - toAttack.getStats().getDefense());
		value *= Constant.AIFactor/toAttack.getRange(true);

		return value;
	}

}
