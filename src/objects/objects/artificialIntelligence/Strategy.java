package objects.objects.artificialIntelligence;

import java.io.Serializable;

public class Strategy implements Serializable {
	
	private static final long serialVersionUID = 4728009407168575088L;
	private int myIntelligence;
	private int myAttackValue;
	private int myDefenseValue;
	private int myMoneyValue;
	private int myObjectiveValue;
	
	public Strategy(int intelligence,
			int attackValue, int defenseValue,
			int moneyValue, int objectiveValue){
		myIntelligence = intelligence;
		myAttackValue = attackValue;
		myDefenseValue = defenseValue;
		myMoneyValue = moneyValue;
		myObjectiveValue = objectiveValue;
	}
	
	/**
	 * @return the Intelligence
	 */
	public final int getIntelligence() {
		return myIntelligence;
	}

	/**
	 * @return the AttackValue
	 */
	public final int getAttackValue() {
		return myAttackValue;
	}

	/**
	 * @return the DefenseValue
	 */
	public final int getDefenseValue() {
		return myDefenseValue;
	}

	/**
	 * @return the MoneyValue
	 */
	public final int getMoneyValue() {
		return myMoneyValue;
	}

	/**
	 * @return the ObjectiveValue
	 */
	public final int getObjectiveValue() {
		return myObjectiveValue;
	}
	
}
