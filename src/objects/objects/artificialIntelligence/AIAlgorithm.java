package objects.objects.artificialIntelligence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import display.Constant;

import engine.ViewController;
import objectives.CapturePoint;
import objects.definitions.GameObjectDef;
import objects.definitions.UnitDef;
import objects.objects.GameObject;
import objects.objects.PickupObject;
import objects.objects.Unit;
import jgame.JGPoint;

/**
 * Allows one to write new algorithms that the AI will use
 * to determine where to move
 * 
 * The choice of algorithm would presumably be done by the AI's
 * intelligence value
 * @author Teddy and Matt
 */
public abstract class AIAlgorithm {

	protected ViewController myController;

	public AIAlgorithm(ViewController controller) {
		myController = controller;
	}

	protected abstract double evaluateAttack(JGPoint point, AIUnit unit);

	protected abstract double evaluateDefense(JGPoint point, AIUnit unit);

	protected abstract double evaluateMoney(JGPoint point, AIUnit unit);

	protected abstract double evaluateObjective(JGPoint point, AIUnit unit);

	/**
	 * Determines the reward for the given AI unit attacking the given square.
	 * @param point the square
	 * @return the reward value
	 */
	protected abstract double evaluateHit(JGPoint point, AIUnit unit);

	protected List<JGPoint> findBosses(){
		List<JGPoint> bosses = new ArrayList<JGPoint>();
		Map<JGPoint, GameObjectDef> gameMap = myController.getCurrLevel().getMap();
		for (JGPoint p: gameMap.keySet()){
			GameObjectDef obj = gameMap.get(p);
			if (obj instanceof UnitDef && ((UnitDef) obj).team == 2) {
				bosses.add(p);
			}
		}
		return bosses;
	}
	
	/**
	 * Finds the weakest unit in a set of points
	 * 
	 * @param points the set of points to search
	 * @param friendlyTeam the team we don't want to attack
	 * @return the weakest unit adjacent to that point
	 */
	protected Unit findWeakestInSet(Set<JGPoint> points, final int friendlyTeam) {
		Unit myWeakest = null;
		double lowestHealth = Double.MAX_VALUE;

		for (JGPoint adj : points) {
			GameObject object = myController.getObject(adj.x, adj.y);
			if (object instanceof Unit && ((Unit) object).getTeam() != friendlyTeam) {
				Unit other = (Unit) object;
				double unitHealth = other.getStats().getCurrentHealth();
				if (unitHealth < lowestHealth) {
					lowestHealth = unitHealth;
					myWeakest = other;
				}
			}
		}

		return myWeakest;
	}
	
	/**
	 * Checks to see if the capture point is in a set of adj points (not the game map)
	 * 
	 * @param points the set of points to search
	 * @param the cp 
	 * @return boolean of whether or not capture point is in range
	 */
	protected boolean checkCapturePointInRange(Set<JGPoint> points, JGPoint cp){
		for (JGPoint adj: points){
			if (adj.equals(cp)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Find to bosses in a set of adj points (not the game map)
	 * 
	 * @param points the set of points to search
	 * @return list of bosses in the range
	 */
	protected List<JGPoint> findBossesInRange(Set<JGPoint> points){
		List<JGPoint> bosses = findBosses();
		List<JGPoint> bossesInRange = new ArrayList<JGPoint>();
		for (JGPoint adj: points){
			if (bosses.contains(adj)){
				bossesInRange.add(adj);
			}
		}
		return bossesInRange;
	}
	
	
	/**
	 * Find to players in a set of adj points (not the game map)
	 * 
	 * @param points the set of points to search
	 * @return list of players in the range
	 */
	protected List<JGPoint> findPlayersInRange(Set<JGPoint> points){
		List<JGPoint> players = new ArrayList<JGPoint>();
		for (JGPoint adj: points){
			Object object = myController.getObject(adj.x, adj.y);
			if (object instanceof Unit && ((Unit) object).getType().equals(Constant.playerTypeTag)){
				players.add(adj);
			}
		}
		return players;
	}
	
	
	/**
	 * Evaluates value of tile based on attack
	 * 
	 * @param point (tile) that will be assigned a value
	 * @param enemy unit
	 * @return calculated value
	 */
	protected double evaluateSmartAttack(JGPoint point, AIUnit unit){
		double value = 0;
		GameObject object = myController.getObject(point.x, point.y);

		Set<JGPoint> attackRange = myController.getCurrLevel().getAttackRangeFromPoint(point, unit.getRange(true));
		
		
		Unit weakestInRange = findWeakestInSet(attackRange, unit.getTeam());
		
		if (weakestInRange != null) {
			double pathLength = PathFinder.findOptimalPath(weakestInRange.getPosition(), point, myController.getCurrLevel()).size() + 1;
			value += 1/(pathLength * weakestInRange.getStats().getCurrentHealth()) * (unit.getStats().getPower() - weakestInRange.getStats().getDefense()) * Math.pow(Constant.AIFactor,3);
		} else if (object instanceof PickupObject) {
			value += Constant.AIFactor * 1/2 * ((PickupObject) object).getBuffs().getPower();
		}
		else {
			Map<JGPoint, GameObjectDef> gameMap = myController.getCurrLevel().getMap();
			for (JGPoint p: gameMap.keySet()){
				GameObjectDef obj = gameMap.get(p);
				if (obj instanceof UnitDef && ((UnitDef) obj).team != unit.getTeam()) {
					double outsidePath = (double) PathFinder.findOptimalPath(point, new JGPoint(p.y,p.x), myController.getCurrLevel()).size() + 1;
					value += 1/outsidePath * Constant.AIFactor;
				}
			}
		}
		return value;
	}
	
	
	/**
	 * Evaluates value of tile based on defense with regards to other players
	 * 
	 * @param enemy unit
	 * @param point (tile) that will be assigned a value
	 * @return calculated value
	 */
	protected double evaluateDefenseBasedOnPlayers(AIUnit unit, JGPoint point){
		double value = 0;
		List<JGPoint> players = findPlayersInRange(myController.getCurrLevel().getAttackRangeFromPoint(unit.getPosition(), (int) (double) (unit.getStats().getRange()+unit.getStats().getSpeed())));
		for (JGPoint p: players){
			double playerPath = PathFinder.findOptimalPath(point, p, myController.getCurrLevel()).size() + 1;
			if (playerPath > ((Unit) myController.getObject(p.x, p.y)).getRange(true)){
				value += playerPath * Constant.AIFactor;
			}
		}
		return value;
	}
	
	/**
	 * Evaluates value of tile based on defense with regards to other items
	 * 
	 * @param point (tile) that will be assigned a value
	 * @return calculated value
	 */
	protected double evaluateDefenseBasedOnItems(JGPoint point){
		double value = 0;
		
		Object object = myController.getObject(point.x, point.y);

		if (object instanceof PickupObject){
			value += Constant.AIFactor * 1/2 * ((PickupObject) object).getBuffs().getCurrentHealth(); 
			value += Constant.AIFactor * 1/2 * ((PickupObject) object).getBuffs().getDefense();
		}
		
		return value;
	}
	
	
	/**
	 * Evaluates value of tile based on pickup objects
	 * 
	 * @param point (tile) that will be assigned a value
	 * @param enemy unit
	 * @return calculated value
	 */
	protected double evaluateSmartMoney(JGPoint point, AIUnit unit){
		GameObject object = myController.getObject(point.x, point.y);
		if (object instanceof PickupObject) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Evaluates value of tile based on proximity to a capture point if 
	 * that objective is being used
	 * 
	 * @param objective (should be a capture point objective)
	 * @param enemy unit
	 * * @param point (tile) that will be assigned a value
	 * @return calculated value
	 */
	protected double evaluateCapturePoint(Object objective, AIUnit unit, JGPoint point){
		double value = 0;
		
		if (checkCapturePointInRange(myController.getCurrLevel().getRange(unit, false), 
				((CapturePoint) objective).getCapturePoint())){
			double capturePath = PathFinder.findOptimalPath(point, 
					((CapturePoint) objective).getCapturePoint(), myController.getCurrLevel()).size() + 1;
			value += 1/capturePath * Constant.AIFactor;
		}
		
		return value;
	}
	
}
