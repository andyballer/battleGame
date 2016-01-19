package objects.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.Assert;

import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.definitions.UnitDef;
import display.Constant;
import engine.DeepCloner;
import engine.LevelManager;
import engine.ResourceLoader;
import engine.ViewController;
import gameplayer.action.UnitAction;


/**
 * Defines a character in the game.
 *This character can be controlled by either the computer or the player.
 * Units have several actions: they can move, attack things, push things,
 *  or just wait.  As they pick up items, they might get more abilities
 *  (ie: an axe might let one cut down trees).  These actions are all
 *  defined in gameplayers action package (see below), and future
 *  programmers could certainly add new, custom actions.
 *  The Unit has stats that determine how far it can move in a turn,
 *  how far away it can attack, how powerful it is, etc., and these
 *  can all increase when the Unit accomplishes enough things
 *  (ie: kills enough enemies) to level up.
 */
public class Unit extends GameObject {

	protected StatSheet myStats;
	protected StatSheet myGrowths;
	
	protected double myTurnCount = 0;
	protected double myMoveCount = 0;  //number of times unit is moved
	protected double myAttackCount = 0; //numer of times unit attacks
	
	protected int myExperience;
	protected int myLevel;
	protected int myNextLevelExperienceRequirement;
	
	protected int myTeam;
	// we only need def classes here
	protected Map<PickupObjectDef, Integer> myInventory;
	protected Set<PickupObjectDef> myWeapons;
	protected Set<String> myActions;
	
	public Unit (UnitDef def) {
	    super(def);
	    myStats = def.stats;
	    myGrowths = def.growths;
	    myTeam = def.team;

	    myExperience = def.experience;
	    myLevel = def.level;
	    
	    myInventory = def.inventory;
	    if (myInventory == null) myInventory = new ConcurrentHashMap<PickupObjectDef, Integer>();
	    
	    myWeapons = def.weapons;
	    if (myWeapons == null) myWeapons = new CopyOnWriteArraySet<PickupObjectDef>();
	    
	    myActions = new CopyOnWriteArraySet<String>();
	    if (myActions == null) myActions = new CopyOnWriteArraySet<String>();
	    
	    myActions.add(Constant.pickUpTag);
	    Constant.LOG(this.getClass(), myInventory + " " + myWeapons + " " + myActions);
	}
	
	public int getRange(boolean isAttacking) {
		int range = -1;
		
		if(isAttacking)
			range = myStats.getRange().intValue();
		else
			range = myStats.getSpeed().intValue();
		return range;
	}
	
	/**
	 * Same as above, but takes a statsheet as a parameter
	 * @param boosts the statsheet containing the amount to boost things by
	 * @return the new statsheet
	 */
	public void boostStats(StatSheet boosts){
		boostStats(boosts.getStats());
	}
	
	private void boostStats(List<Double> updates){
		myStats.boostStats(updates);
	}

	public void move(int x, int y){
		while(this.x != x){
			this.x+=(x-this.x)/Math.abs(x-this.x);
		}
		while(this.y != y)
			this.y += (y-this.y)/Math.abs(y-this.y);
		
	}
	
	/**
	 * Attacks another unit.
	 * @param other the unit to attack
	 * @return indicate whether the tart is dead after the attack
	 */
	public boolean attack(Unit other){
		int power = myStats.getPower().intValue();
		int otherDefense = other.getStats().getDefense().intValue();
		int baseDamage = power - otherDefense;
		boolean res = false;
		if (baseDamage > 0) {
			res = other.damage(baseDamage);
			int levelDifference = other.getLevel() - myLevel;
			if (levelDifference < 0) levelDifference = -1;
			gainExperience(10 + (levelDifference)*5); //placeholder exp calculating algorithm
		}
		myAttackCount++;
		return res;
	}
	
	public void pickUp(PickupObject pickup) {
		Assert.assertNotNull(pickup);
		if (pickup.getType().equals(Constant.itemTypeTag)) {
			PickupObjectDef def = (PickupObjectDef)pickup.getDefinition();
			for (String a : def.actions) myActions.add(a);
			if (myInventory.containsKey(def))
				myInventory.put(def, myInventory.get(def) + 1);
			else
				myInventory.put(def, 1);
		} else if (pickup.getType().equals(Constant.weaponTypeTag)) {
			addWeapon((PickupObjectDef)pickup.getDefinition());
		}
		pickup.remove();
	}
	
	/**
	 * Allows player to decide if he wants the item when he moves to it
	 * @param wantItem boolean
	 * @return boolean: whether or not the player wants the item
	 */
	public boolean doesWantItem(boolean wantItem){
		return wantItem;
	}
	
	public int getLevel() {
		return myLevel;
	}

	/**
	 * Takes damage
	 * @param damage the amount of damage to take
	 */
	private boolean damage(int damage){
		double newHealth = setCurrentHealth(myStats.getCurrentHealth() - damage);
		myStats.setCurrentHealth(newHealth);
		if (newHealth <= 0)
			return true;
		return false;
	}
	
	/**
	 * sets current health to the given amount
	 * @param newHealth amount to set health.
	 * @return the new health of this unit.
	 * caps health at the max health
	 */
	public double setCurrentHealth(double newHealth){
		double max = myStats.getMaxHealth();
		if(newHealth > max)
			return max;
		if(newHealth <= 0)
			die();
		return newHealth;
	}
	
	public void initLevel(LevelManager manager, String gameName) {
		GameObjectDef gdef = manager.getUnitDef(gameName, myName);
		if (gdef != null && gdef instanceof UnitDef) {
			UnitDef def = (UnitDef)gdef;
			myLevel = def.level;
			myExperience = def.experience;
			myStats = def.stats;
			myGrowths = def.growths;
			myInventory = def.inventory;
			myWeapons = def.weapons;
			myActions = def.actions;
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
	
	/**
	 * Kills the unit by calling JGObject's remove method.
	 * Ability to add more code if we want to get all fancy with death.
	 */
	public void die(){
		remove();
	}
	
	public int gainExperience(int toGain){
		myExperience += toGain;
		while (myExperience >= 100 * Math.log(myLevel+2))
			levelUp();
		return myExperience;
	}
	
	public int getNextLevelExperienceRequirement() {
		myNextLevelExperienceRequirement = (int)(100 * Math.log(myLevel + 2) - myExperience);
		return myNextLevelExperienceRequirement;
	}
	
	/**
	 * Goes through the process that occurs at a level up
	 * Most of the code is for boosting the stats according to their growth rates
	 */
	public void levelUp(){
		myLevel++;
		myExperience = 0;
		getNextLevelExperienceRequirement();
		boostStats(myGrowths);
		myStats.setCurrentHealth(myStats.getMaxHealth());
	}
	
	/**
	 * Creates a copy of the statsheet
	 * @return a copy of the statsheet
	 */
	public StatSheet getStats(){
		try {
			return (StatSheet) DeepCloner.deepCopy(myStats);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	
	public int getTeam(){
		return myTeam;
	}
	
	public int getExperience() {
		return myExperience;
	}
	
	public int getUnitLevel() {
		return myLevel;
	}
	
	public Set<String> getActions() {
		return myActions;
	}
	
	/**
	 * Gives a set of different things that the unit has the ability to destroy.
	 * @return
	 */
	public Set<String> getDestroyableIDs() {
		Set<String> myDestroyables = new HashSet<String>();
		for(String action : myActions){
			if(action.startsWith(Constant.destroyTag)) {
				myDestroyables.add(action.substring(Constant.destroyTag.length()));
			}
		}
		return myDestroyables;
	}
	
	@Override
	public GameObjectDef getDefinition() {
		return new UnitDef(myCollisionID, myName, myJGameGraphicPath, myTeam, (int)x, (int)y, myRow, myCol, myStats, 
				myGrowths, myType, myOriginalGraphicPath, myLevel, myExperience, myInventory, myWeapons, myActions, myObjName);
	}
	
	public void addWeapon(PickupObjectDef weaponDef) {
		this.boostStats(weaponDef.buffs);
		weaponDef.actions.remove(Constant.boostTag);
		for (String action : weaponDef.actions) {
			myActions.add(action);
		}
		for (PickupObjectDef i : myWeapons)
			if (i.objName.equals(weaponDef.objName))
				return;
		myWeapons.add(weaponDef);
	}
	
	public void removeItem(String name) {
		for (PickupObjectDef def :myInventory.keySet()) {
			if (def.name.equals(name)) {
				int number = myInventory.get(def);
				if (number > 1)
					myInventory.put(def, number - 1);
				else
					myInventory.remove(def);
				return;
			}
		}
	}
	
	/**
	 * Creates a copy of the statsheet
	 * @return a copy of the statsheet
	 */
	public StatSheet getGrowths(){
		try {
			return (StatSheet) DeepCloner.deepCopy(myGrowths);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Set<PickupObjectDef> getWeapons() {
		try {
			return (Set<PickupObjectDef>) DeepCloner.deepCopy(myWeapons);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	/**
	 * get usable items in this unit: items and weapons
	 * basically, this method excludes those items/weapons that don't have an action
	 * @return a map the key is the unit name, the value is the image path
	 */
	public Map<String, String> getUsableInventory() {
		Map<String, String> picsToPaths = ResourceLoader.load(Constant.itemTypeTag, Constant.imagePropertyFile);
		Map<String, String> items = new HashMap<String, String>();
		
		for (PickupObjectDef pickUp : myInventory.keySet()) {
			if (pickUp.actions.size() > 0) items.put(pickUp.name, picsToPaths.get(pickUp.objName));
		}
		Constant.LOG(this.getClass(), "All inventory: " + items.toString());
		return items;
	}
	
	public Map<String, Integer> getUsableInventoryNumber() {
		Map<String, Integer> items = new HashMap<String, Integer>();
		
		for (PickupObjectDef pickUp : myInventory.keySet())
			if (pickUp.actions.size() > 0) items.put(pickUp.name, myInventory.get(pickUp));
		return items;
	}
	
	public PickupObjectDef getItem(String name) {
		for (PickupObjectDef pickUp : myInventory.keySet())
			if (pickUp.name.equals(name)) return pickUp;
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<PickupObjectDef, Integer> getInventory() {
		Constant.LOG(this.getClass(), "Inv"  + myInventory);
		try {
			return (Map<PickupObjectDef, Integer>) DeepCloner.deepCopy(myInventory);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}

	public UnitAction checkAvailableAction(int row, int col, ViewController controller) {
		for (String a : myActions) {
			try {
				Map<String, String> namesToPaths = ResourceLoader.load(Constant.actionTypeTag, Constant.commonPropertyFile);
				UnitAction action = (UnitAction) Class.forName(namesToPaths.get(a)).getConstructor(String.class, ViewController.class).newInstance(a, controller);
				if (action.checkValid(row, col)) return action;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void incrementTurnCount() {
		myTurnCount++;
	}
	
	public void decreaseTurnCount() {
		myTurnCount--;
	}

	public String toString() {
		return myName + " " + myCollisionID + " " + myTeam + " " + myType + " " + myRow + " " + myCol + " " + x + " " + y + " " 
				+ myStats.toString() + " " + myGrowths.toString() + " " + myInventory.toString();
	}
	
	public void incrementMoveCount(){
		myMoveCount++;
	}
	
	public double getMoveRate(){
		if (myTurnCount != 0)
			return myMoveCount / myTurnCount;
		return 0;
	}
	
	public double getAttackRate(){
		if (myTurnCount != 0)
			return myAttackCount / myTurnCount;
		return 0;
	}
}
