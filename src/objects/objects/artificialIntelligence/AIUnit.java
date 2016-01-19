package objects.objects.artificialIntelligence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import display.Constant;
import jgame.JGPoint;
import engine.LevelManager;
import engine.ViewController;
import gameplayer.action.HitAction;
import gameplayer.action.MoveAction;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.objects.Unit;
import objects.definitions.AIUnitDef;

public class AIUnit extends Unit {

	private ViewController myController;
	private Strategy myStrategy;
	private AIAlgorithm myAlgorithm;

	public AIUnit(AIUnitDef def, ViewController controller) {
		super(def);
		myStrategy = def.strategy;
		myController = controller;
		

		AlgorithmFactory algorithmFactory = new AlgorithmFactory(Constant.algorithmPropertyFile, myController, Constant.maxAIIntelligence);
		myAlgorithm = algorithmFactory.selectAlgorithm(myStrategy.getIntelligence());
	}

	/**
	 * Moves this AIUnit based on its strategy
	 * @return this AIUnit.
	 */
	public AIUnit moveEnemy(){
		JGPoint finalPosition = this.getPosition();

		finalPosition = findActionPosition(false);
		//finalPosition = new JGPoint(0,2);

		MoveAction moveAction = new MoveAction(this.getName(), myController);
		if(moveAction.checkValid(finalPosition.x, finalPosition.y)) {
			moveAction.applyAction(this, finalPosition.x, finalPosition.y);
		}

		JGPoint hitPosition = findActionPosition(true);
		if (hitPosition != null) {
			HitAction hitAction = new HitAction(this.getName(), myController);
			if(hitAction.checkValid(hitPosition.x, hitPosition.y)) {
				hitAction.applyAction(this, hitPosition.x, hitPosition.y);
			}
		}

		return this;
	}
	
	private JGPoint findActionPosition(final boolean isAttacking) {
		Set<JGPoint> points = myController.getCurrLevel().getRange(this, isAttacking);
		HashMap<Double,JGPoint> pointValues = new HashMap<Double, JGPoint>();

		for (JGPoint point : points) {
			if (isAttacking) {
				pointValues.put(evaluateHit(point), point);
			} else {
				pointValues.put(evaluateMove(point), point);
			}
		}
		double maxValue = findMax(pointValues);
		if(maxValue == Double.MIN_VALUE)
			return null;
		return pointValues.get(maxValue);
	}
	
	private double evaluateMove(JGPoint point) {
		double moveValue = 0;
		moveValue += myAlgorithm.evaluateAttack(point, this) * myStrategy.getAttackValue();
		moveValue += myAlgorithm.evaluateDefense(point, this) * myStrategy.getDefenseValue();
		moveValue += myAlgorithm.evaluateMoney(point, this) * myStrategy.getMoneyValue();
		moveValue += myAlgorithm.evaluateObjective(point, this) * myStrategy.getObjectiveValue();
		return moveValue;
	}
	
	private double evaluateHit(JGPoint point) {
		return myAlgorithm.evaluateHit(point, this);
	}
	
	private double findMax(Map<Double, JGPoint> map) {
		double max = 0;
		for(double value : map.keySet()){
			if(value > max){
				max = value;
			}
		}
		return max;
	}
	
	@Override
	public void initLevel(LevelManager manager, String gameName) {
		GameObjectDef gdef = manager.getUnitDef(gameName, myName);
		if (gdef != null && gdef instanceof AIUnitDef) {
			AIUnitDef def = (AIUnitDef)gdef;
			myLevel = def.level;
			myExperience = def.experience;
			myStats = def.stats;
			myGrowths = def.growths;
			myInventory = def.inventory;
			myWeapons = def.weapons;
			myActions = def.actions;
			myStrategy = def.strategy;
		} else {
			double h = myStats.getMaxHealth();
			myStats.setCurrentHealth(h);
			myLevel = 1;
			myExperience = 0;
		}
		
		if (myWeapons != null)
			for (PickupObjectDef p : myWeapons) {
		    	this.boostStats(p.buffs);
		    	p.actions.remove(Constant.boostTag);
		    }
		
		getNextLevelExperienceRequirement();
	}

	public Strategy getStrategy() {
		return myStrategy;
	}
	
	@Override
	public GameObjectDef getDefinition() {
		return new AIUnitDef(myCollisionID, myName, myJGameGraphicPath, myTeam, (int)x, (int)y, myRow, myCol, myStats, 
				myGrowths, myType, myOriginalGraphicPath, myLevel, myExperience, myInventory, myWeapons, myActions, myObjName, myStrategy);
	}
}
