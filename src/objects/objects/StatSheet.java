package objects.objects;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;

import display.Constant;
import engine.DeepCloner;

/**
 * Holds a list of stats for a character
 * but can be reused if, for example, a user
 * wants to create many similar characters
 * with the same stats but perhaps different
 * names.
 */

public class StatSheet implements Serializable {

	private static final long serialVersionUID = -872939209740509456L;
	private List<Double> myStats; // in the order of maxlife currlife power defense speed range
	
	/**
	 * Constructor for class StatSheet.
	 * @param maxLife maximum hit points
	 * @param currentLife current number of hit points
	 * @param power strength stat for this character
	 * @param defense defense stat for this character
	 * @param speed speed stat for this character
	 * @param range attack range stat for this character
	 */
	public StatSheet(final Double maxLife, Double currentLife,
			final Double power, final Double defense,
			final Double speed, final Double range) {
		myStats = new CopyOnWriteArrayList<Double>();
		myStats.add(maxLife);
		myStats.add(currentLife);
		myStats.add(power);
		myStats.add(defense);
		myStats.add(speed);
		myStats.add(range);
	}
	
	public StatSheet() {
		myStats = new CopyOnWriteArrayList<Double>(new Double[]{Constant.defaultHealth.doubleValue(), 
												   Constant.defaultHealth.doubleValue(),
												   Constant.defaultPower.doubleValue(),
												   Constant.defaultDefense.doubleValue(),
												   Constant.defaultSpeed.doubleValue(),
												   Constant.defaultRange.doubleValue()});
	}
	
	public StatSheet(Integer maxLife, Integer currentLife, Integer power,
			Integer defense, Integer speed, Integer range) {
		myStats = new CopyOnWriteArrayList<Double>(new Double[]{maxLife.doubleValue(), 
				   												currentLife.doubleValue(),
				   												power.doubleValue(),
				   												defense.doubleValue(),
				   												speed.doubleValue(),
				   												range.doubleValue()});
	}

	public Double getMaxHealth() {
		return myStats.get(0);
	}
	
	public Double getCurrentHealth() {
		return myStats.get(1);
	}
	
	public Double getPower() {
		return myStats.get(2);
	}
	
	public Double getDefense() {
		return myStats.get(3);
	}
	
	public Double getSpeed() {
		return myStats.get(4);
	}
	
	public Double getRange() {
		return myStats.get(5);
	}
	
	public void setMaxHealth(Double v) {
		myStats.set(0, v);
	}
	public void setCurrentHealth(Double v) {
		myStats.set(1, v);
	}
	public void setPower(Double v) {
		myStats.set(2, v);
	}
	public void setDefense(Double v) {
		myStats.set(3, v);
	}
	public void setSpeed(Double v) {
		myStats.set(4, v);
	}
	public void setRange(Double v) {
		myStats.set(5, v);
	}
	
	@SuppressWarnings("unchecked")
	public List<Double> getStats() {
		try {
			return (List<Double>) DeepCloner.deepCopy(myStats);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void boostStats(List<Double> boosts) {
		Assert.assertNotNull(myStats);
		Assert.assertTrue(boosts.size() == myStats.size());
		
		for (int i = 0; i < myStats.size(); i++) {
			myStats.set(i, myStats.get(i) * (1 + boosts.get(i)));
		}
		if (getCurrentHealth() > getMaxHealth()) myStats.set(1, myStats.get(0));
	}
	
	public String toString() {
		return myStats.toString();
	}

	public boolean equals(Object obj) {
		if (obj == null  || ! obj.getClass().equals(this.getClass())) {
			return false;
		}
		StatSheet s = (StatSheet) obj;
		List<Double> objStatMap = s.getStats();
		for (int i = 0; i < myStats.size(); i++) {
			if (myStats.get(i) != objStatMap.get(i)) return false;
		}
		return true;
	}
	
	public int hashCode() {
		return (int)(myStats.get(0) * 2 + 
				myStats.get(1) * 3 + 
				myStats.get(2) * 5 + 
				myStats.get(3) * 7 +
				myStats.get(4) * 11 +
				myStats.get(5) * 13);
	}
}
