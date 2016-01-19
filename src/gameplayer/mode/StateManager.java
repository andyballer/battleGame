package gameplayer.mode;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.SwingUtilities;

import objects.definitions.GameObjectDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.AIUnit;

import org.junit.Assert;

import jgame.JGPoint;
import display.Constant;
import display.animation.WordAnimation;
import display.jgame.GameEnv;
import engine.ViewController;
import gameplayer.mode.ActionMode;
import gameplayer.action.UnitAction;

/*
 * basically manages various states 
 */

public class StateManager {

	private ViewController controller = null;
	private GameEnv gameEnv = null;

	private Map<String, State> states;
	private Deque<State> currentStates;
	private State currActionState; // store the current Action state Hit/Move/Inventory
	private UnitAction currAction = null;
	private Unit currUnit = null; // store the player that plays in current time of current turn
	private JGPoint targetUnit = null;
	private Set<JGPoint> potentialSpots;
	
	private Map<JGPoint, Unit> players; // won't change during one play
	private Map<JGPoint, AIUnit> enemies; // won't change during one play
	
	private Set<JGPoint> willPlay = null; // store all players will play in current turn
	
	private boolean playerTurn = true; // store the current team
	private int maxLevel = 0;
	private int currLevel = 1;
	
	private boolean playerWin = false;
	private boolean actionFinished = false;
	
	private boolean moved = false;
	
	private int myTurnCount = 0;
	
	private WordAnimation currAnimation = null;

	public StateManager(ViewController c, GameEnv g) {
		controller = c;
		gameEnv = g;
		states = new ConcurrentHashMap<String, State>();
		currentStates = new LinkedBlockingDeque<State>();
		willPlay = null;
	}
	
	public void resetAll() {
		currAction = null;
		targetUnit = null;
		currActionState = null;
		potentialSpots = null;
		currentStates.clear();
		if (players != null) players.clear();
		if (enemies != null) enemies.clear();
		if (willPlay != null) willPlay.clear();
		currUnit = null;
		myTurnCount = 0;
		moved = false;
	}
	
	public void resetLevelNumber() {
		currLevel = 1;
	}

	public void addState(State state) {
		states.put(state.getStateName(), state);
	}
	
	public void setState(final String name, final Set<JGPoint> selectables, final boolean exclusive, UnitAction action) {
		Assert.assertTrue(selectables != null && action != null);
		currAction = action;
		setStateHelper(name, exclusive);
		potentialSpots = selectables;
		currentStates.getLast().start(selectables);
	}

	public void setState(final String name, final Set<JGPoint> selectables, final boolean exclusive) {
		setStateHelper(name, exclusive);
		currentStates.getLast().start(selectables);
	}
	
	public void setState(final String name, final JGPoint selectable, final boolean exclusive) {
		setStateHelper(name, exclusive);
		Set<JGPoint> temp = new HashSet<JGPoint>();
		temp.add(selectable);
		currentStates.getLast().start(temp);
	}
	
	public void setState(final String name, final boolean exclusive) {
		setStateHelper(name, exclusive);
		currentStates.getLast().start(null);
	}
	/**
	 * set the state in StateManager and JGame
	 * @param name The name of state
	 * @param exclusive Clear all previous states in queue if true
	 */
	private void setStateHelper(final String name, final boolean exclusive) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				if (exclusive)
					gameEnv.setGameState(name);
				else
					gameEnv.addGameState(name);
				controller.displayStatus("ENTER STATE: " + name);
			}
		});
		if (exclusive) currentStates.clear();
		State curr = states.get(name);
		currentStates.add(curr);
		Assert.assertNotNull(currentStates);
		if (curr.getClass().equals(ActionMode.class)) {
			currActionState = curr;
		}
	}
	
	public State getCurrActionState() {
		return currActionState;
	}
	
	public UnitAction getCurrAction() {
		return currAction;
	}
	
	public JGPoint getCurrTarget() {
		if (currAction.requireTarget())
			return targetUnit;
		else
			return currUnit.getPosition();
	}
	
	public boolean initLevel(boolean playerFirst, int max, Map<JGPoint, Unit> p, Map<JGPoint, AIUnit> e) {
		resetAll();
		playerTurn = playerFirst;
		maxLevel = max;
		players = p;
		enemies = e;
		if (players != null) {
			for (Unit u : p.values()) u.initLevel(controller.getLevelManager(), controller.getCurrGameName());
			if (playerTurn) willPlay = deepCopySet(players.keySet());
		}
		if (enemies != null) {
			for (AIUnit u : e.values()) u.initLevel(controller.getLevelManager(), controller.getCurrGameName());
			if (!playerTurn) willPlay = deepCopySet(enemies.keySet());
		}
		
		return true;
	}
	
	public int getTurns() {
		return myTurnCount;
	}
	
	public void start() {
		if (playerTurn) setState(Constant.sourceSelectionMode, willPlay, true);
		else setState(Constant.enemyMode, willPlay, true);
		controller.updateStatsTable();
	}
	
	public void changeTurn() {
		playerTurn = !playerTurn;
		if (playerTurn) {
			controller.setJGameEngineEnabled(true);
			displayState("PLAYER TURN");
			do {
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (animationIsAlive());
			willPlay = deepCopySet(players.keySet());
		}
		else {
			if (enemies == null || enemies.isEmpty()) {
				changeTurn();
				return;
			}
			willPlay = deepCopySet(enemies.keySet());
		}
		Constant.LOG(this.getClass(), "State: " + myTurnCount + " " + currentStates.toString());
		start();
		
	}
	
	private Set<JGPoint> deepCopySet(Set<JGPoint> set) {
		Set<JGPoint> copy = new CopyOnWriteArraySet<JGPoint>();
		for (JGPoint p : set) 
			copy.add(new JGPoint(p.x, p.y));
		return copy;
	}
	
	public void nextPlayer() {
		currActionState = null;
		currAction = null;
		targetUnit = null;
		currentStates.clear();
		currUnit = null;
		potentialSpots = null;
		moved = false;

		Set<JGPoint> restPlayers = deepCopySet(players.keySet());

		if (checkObjectives()) {
			return;
		}

		if (restPlayers.isEmpty()) {
			setState(Constant.exitMode, true);
			return;
		}

		if (willPlay.isEmpty()) {
			if (playerTurn)
				myTurnCount++;
			changeTurn();
			return;
		}
		start();
	}

	public boolean checkObjectives(){
		if (controller.checkFailure(myTurnCount)) {
			setState(Constant.exitMode, true);
		}
		if (controller.checkGameObjectives()) {
			Constant.LOG(this.getClass(), currLevel + " " + maxLevel);
			if (currLevel == maxLevel)
				setState(Constant.allOverMode, true);
			else 
				setState(Constant.levelCompleteMode, true);
			return true;
		}
		return false;
	}
	
	public void nextLevel() {
		// store current objects' stats
		if (controller.getCurrLevel().getPlayers() != null)
			for (Unit player : controller.getCurrLevel().getPlayers().values())
				controller.getLevelManager().setObject(controller.getCurrGameName(), (GameObjectDef)player.getDefinition());
		if (controller.getCurrLevel().getEnemies() != null)
			for (AIUnit enemy : controller.getCurrLevel().getEnemies().values())
				controller.getLevelManager().setObject(controller.getCurrGameName(), (GameObjectDef)enemy.getDefinition());
		
		controller.displayStatus("NEXTLEVEL " + currLevel + " " + maxLevel);
		currLevel++;
		if (currLevel > maxLevel){
			setState(Constant.allOverMode, true);
			return;
		}
		controller.startPlayMode(currLevel-1);
	}
	
	public void addPlaying(JGPoint p, Unit unit) {
		willPlay.remove(p);
		currUnit = unit;
	}
	
	public void deleteObject(int row, int col, String type) {
		if (type.equals(Constant.playerTypeTag))
			players.remove(new JGPoint(row, col));
		else if (type.equals(Constant.enemyTypeTag))
			enemies.remove(new JGPoint(row, col));
		else
			Constant.LOG(this.getClass(), "Unknown Type");
	}
	
	public Unit getCurrPlayingUnit() {
		return currUnit;
	}
	/**
	 * move unit and update location info
	 * For player, unit has the old positions and need to be moved to new position at {@code row} and {@code col}
	 * For enemy, unit has the new positions and need update the {@code enemies} to new one 
	 * and delete the old position at {@code row} and {@code col}
	 * @param object
	 * @param row
	 * @param col
	 */
	public void moveObject(GameObject object, int row, int col) {
		int oldRow = object.getRow();
		int oldCol = object.getCol();
		if (object.getType().equals(Constant.playerTypeTag)) {
			players.remove(new JGPoint(oldRow, oldCol));
			players.put(new JGPoint(row, col), (Unit) object);
		} else if (object.getType().equals(Constant.enemyTypeTag)) {
			enemies.remove(new JGPoint(row, col));
			enemies.put(new JGPoint(oldRow, oldCol), (AIUnit) object);
		} else
			Constant.LOG(this.getClass(), "Unknown Type");
	}
	
	public void pushObject(GameObject object, int oldRow, int oldCol, int row, int col){
		if(object.getType().equals(Constant.playerTypeTag)){
			JGPoint oldLocation = new JGPoint(oldRow, oldCol);
			players.remove(oldLocation);
			willPlay.remove(oldLocation);
			
			JGPoint newLocation = new JGPoint(row, col);
			players.put(newLocation, (Unit) object);
			willPlay.add(newLocation);
		}
		else if(object.getType().equals(Constant.enemyTypeTag)) {
			enemies.remove(new JGPoint(oldRow, oldCol));
			enemies.put(new JGPoint(row, col), (AIUnit) object);
		}
	}
	
	public boolean getLevelResult() {
		return playerWin;
	}
	
	public void setActionFinished(boolean b) {
		actionFinished = b;
	}
	
	public boolean checkValidSelection(int row, int col) {
		if (currAction != null) {
			targetUnit = new JGPoint(row, col);
			return currAction.checkValid(row, col);
		}
		return false;
	}
	
	public boolean isActionFinished() {
		return actionFinished;
	}
	
	public boolean hasNextEnemy() {
		Iterator<JGPoint> it = willPlay.iterator();
		return it.hasNext();
	}
	
	public JGPoint getNextEnemy() {
		Iterator<JGPoint> it = willPlay.iterator();
		if (it.hasNext()) return it.next();
		else return null;
	}
	
	public Set<JGPoint> getPotentialSpots() {
		return deepCopySet(potentialSpots);
	}
	
	public void setMoved(boolean b) {
		moved = b;
	}
	
	public boolean isMoved() {
		return moved;
	}
	
	public boolean animationIsAlive() {
		if (currAnimation != null)
			if (currAnimation.isAlive()) return true;
			else removeCurrAnimation();
		return false;
	}
	
	public void displayState(final String name) {
		while (animationIsAlive())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
		currAnimation = new WordAnimation(controller.getUserDefaults().getInt(String.valueOf(Constant.jgameWindowWidth), 
				  Constant.jgameWindowWidth/2), 
				  Constant.defaultWordHeight, 
				  name, 
				  controller);
	}
	
	public void displayState(final String name, final int interval, final double duration) {
		while (animationIsAlive())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
		currAnimation = new WordAnimation(controller.getUserDefaults().getInt(String.valueOf(Constant.jgameWindowWidth), 
				  Constant.jgameWindowWidth/2), 
				  Constant.defaultWordHeight, 
				  name, 
				  interval,
				  duration,
				  controller);
	}
	
	public void removeCurrAnimation() {
		if (currAnimation != null) {
			currAnimation.remove();
			currAnimation = null;			
		}
	}

}
