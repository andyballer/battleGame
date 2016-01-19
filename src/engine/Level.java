package engine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;

import display.Constant;
import objectives.Objective;
import objectives.ObjectiveFactory;
import objects.definitions.GameObjectDef;
import objects.definitions.UnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.AIUnit;
import objects.objects.artificialIntelligence.PathFinder;
import jgame.JGPoint;

/**
 * Deals with running the game on the backend
 * by keeping track of where characters are and
 * dealing with their interactions.
 *
 * A Level object contains the game name, the level name,
 * a Map of the objects on the grid, a list of objectives,
 * and the tile size, a list of enemies, a list of players,
 * a stack of previous actions taken by a game author, etc.
 *
 * **NOTE: The 2D arrays of the grid (also referred to as maps) are
 * structured assuming that each cell in the grid is identified with
 * an x and y coordinate, with the top left corner being (0, 0). Cells
 * are not identified by raw Cartesian coordinates that correspond to
 * pixels, what a mess that would be.
 * 
 * @author Teddy, Andy, Grace
 */
public class Level {

	private final String myGameName;
	private final String myLevelName;
	// Don't use tile size in the Level, use row and column to find the object you want
	private int myTileSize;

	// transient marks objects so they will NOT be serialized with default behavior
	private transient Map<JGPoint, GameObject> myMap; 

	private int myRows;
	private int myCols;

	private transient Map<String, Objective> myObjectives;

	private transient List<AIUnit> myEnemies;
	private transient List<Unit> myPlayers;

	private String myDifficulty;
	//private JGColor gridColor = null;
	private boolean playerfirst = true;
	
	// do not need to recover after serialization
	private transient Stack<GameObjectDef> placementHistory;
	private transient Stack<GameObjectDef> undoDefHistory;
	private transient Stack<JGPoint> undoPosHistory;
	
	private transient ObjectiveFactory myObjectiveFactory = null;

	/**
	 * Constructor for class level.
	 * @param mapWidth the width of the map grid
	 * @param mapHeight the height of the map
	 */
	public Level(final String gameName, 
			final String levelName, 
			final int mapWidth, 
			final int mapHeight, 
			final int tileSize, final String diff) {
		myGameName = gameName;
		myLevelName = levelName;

		myMap = new ConcurrentHashMap<JGPoint, GameObject>();

		myTileSize = tileSize;
		myObjectives = new ConcurrentHashMap<String, Objective>();
		myDifficulty = diff;

		myEnemies = new CopyOnWriteArrayList<AIUnit>();
		myPlayers = new CopyOnWriteArrayList<Unit>();

		myRows = mapWidth;
		myCols = mapHeight;
		
		placementHistory = new Stack<GameObjectDef>();
		undoDefHistory = new Stack<GameObjectDef>();
		undoPosHistory = new Stack<JGPoint>();
		
		myObjectiveFactory = new ObjectiveFactory(Constant.objectivePropertyFile);
	}
	
	public void remove() {
		for (JGPoint p : myMap.keySet())
			myMap.get(p).remove();
		myPlayers.clear();
		myEnemies.clear();
	}

	/**
	 * places an already existing object on the playfield.
	 * @param toPlace the object to place
	 * @param x final x location of this object
	 * @param y final y location of this object
	 * @param row row to place this object in
	 * @param col column to place this object in
	 * @return the object that was placed
	 */
	public final GameObject placeObject(final GameObject toPlace, final int x, final int y, final int row, final int col) {
		if (toPlace.getUnitName().equals(Constant.defaultObjectName))
			toPlace.setUnitName(Constant.defaultObjectName + "-" + myMap.size());
		
		try {
			JGPoint p = new JGPoint(row, col);
			if (myMap.containsKey(p)) {
				if (toPlace.getType().equals(Constant.playerTypeTag))
					myPlayers.remove(myMap.get(p));
				else if (toPlace.getType().equals(Constant.enemyTypeTag))
					myEnemies.remove(myMap.get(p));
				myMap.get(p).remove();
			}
			toPlace.x = x;
			toPlace.y = y;
			toPlace.setPosition(row, col);
			myMap.put(p, toPlace); //grid coordinates
			placementHistory.add((GameObjectDef)toPlace.getDefinition());
			return toPlace;
		} catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
	}

	public final GameObject moveObject(final GameObject toMove, final int x, final int y, final int row, final int col){
		int oldRow = toMove.getRow();
		int oldCol=  toMove.getCol();

		if (toMove.getUnitName().equals(Constant.defaultObjectName))
			toMove.setUnitName(Constant.defaultObjectName + "-" + myMap.size());

		try{
			JGPoint newLocation = new JGPoint(row, col);
			myMap.remove(new JGPoint(oldRow, oldCol));
			return tryMove(toMove, x, y, newLocation.x, newLocation.y);
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}    
	}
	
	/**
	 * places an already existing unit on the playfield.
	 * @param toPlace the unit to place
	 * @param x initial x location of this object
	 * @param y initial y location of this object
	 * @return the unit that was placed
	 */
	public final GameObject placeUnit(final Unit toPlace, final int x, final int y, final int row, final int col){
		if (toPlace.getUnitName().equals(Constant.defaultObjectName))
			toPlace.setUnitName(Constant.defaultObjectName + "-" + myMap.size());
		
		if (toPlace.getTeam() == Constant.playerTeam)
			myPlayers.add(toPlace);
		else
			myEnemies.add((AIUnit)toPlace);
		return placeObject(toPlace, x, y, row, col);    
	}

	/**
	 * Helper method for trying to move an object
	 * on the grid to a given x,y,row,and col.
	 * @param toMove object to move
	 * @param newRow row to move to
	 * @param newCol column to move to
	 * @param x x position to move to
	 * @param y y position to move to
	 * @return the gameobject after the move
	 */
	private GameObject tryMove(final GameObject toMove,
			final int x, final int y,
			final int newRow, final int newCol) {
		try {
			toMove.setPosition(newRow, newCol);
			toMove.x = x;
			toMove.y = y;
			myMap.put(new JGPoint(newRow, newCol), toMove);
			//System.out.println(toMove.toString() + "\n" + myMap.get(new JGPoint(newRow,newCol)));
			return toMove;
		} catch (ArrayIndexOutOfBoundsException e) {
			return toMove;
		}
	}

	/**
	 * Allows one to add a new objective to the game
	 * @param objectiveName the type of objective to add
	 * @param args a list of specific values that need to be achieved
	 * ie: number of coins to pick up, location of capture point, etc.
	 * I will write up a list of the order that these arguments need
	 * to be in for each objective soon
	 */
	public final Objective addObjective(String objectiveType, List<Integer> args, ViewController c){
		// Is there a reason objectiveFactory was an instance variable?                
		Objective objective = myObjectiveFactory.create(objectiveType, args, c); 
		objective.setLevel(this);
		myObjectives.put(objectiveType, objective);
		return objective;   
	}

	public void deleteObject(int row, int col) {
		JGPoint p = new JGPoint(row, col);
		if (myMap.containsKey(p)) {
			GameObject g = myMap.get(p);
			if(g instanceof AIUnit){
				myEnemies.remove(g);
			} else if(g instanceof Unit){
				myPlayers.remove(g);
			}
			g.remove();
			myMap.remove(p);
		}
	}
	
	public void undoPlacement() {
		if(placementHistory.peek() != null) {
			GameObjectDef obj = placementHistory.pop();
			undoDefHistory.push(obj);
			undoPosHistory.push(new JGPoint(obj.row, obj.col));
			deleteObject(obj.row, obj.col);
		}
	}
	
	public GameObjectDef redoPlacement() {
		if(undoDefHistory.peek() != null) {
			return undoDefHistory.pop();
		}
		return null;
	}
	
	public JGPoint getRedoPosition() {
		if(undoPosHistory.peek() != null) {
			return undoPosHistory.pop();
		}
		return null;
	}

	public final Objective getObjective(String name) {
		return myObjectives.get(name);
	}

	public final void removeObjective(String obj) {
		myObjectives.remove(obj);
	}

	public final Object[] getAllObjectivesName() {
		return myObjectives.keySet().toArray();
	}
	
	public final Object[] getAllObjectives() {
		return myObjectives.values().toArray();
	}

	public final GameObject getObject(int row, int col) {
		JGPoint p = new JGPoint(row, col);
		return myMap.get(p);
	}

	public final GameObject getObject(JGPoint p) {
		return myMap.get(p);
	}

	/**
	 * If any of the objectives in the list for this level are complete, return true.
	 * @return whether any of the objectives are complete.
	 */
	public final List<String> getCompleteObjectives() {
		List<String> completeObjectives = new CopyOnWriteArrayList<String>();
		for (String obj : myObjectives.keySet())
			if (myObjectives.get(obj).isAchieved())
				completeObjectives.add(obj);
		return completeObjectives;    
	}

	/**
	 * Checks which squares a unit can move to or attack.
	 * @param unit the unit to check the movement/attack range of.
	 * @param isAttacking whether or not the unit is attacking right now
	 * ie: true if checking attack range, false if checking movement range
	 * @return a set of grid squares within range
	 */
	public Set<JGPoint> getRange(final Unit unit, final boolean isAttacking) {
		Set<JGPoint> rangeMap = new HashSet<JGPoint>();
		//Assert.assertTrue(range >= 0);
		return rangeRecurse(unit.getRow(), unit.getCol(), myRows, myCols, rangeMap, unit.getRange(isAttacking), unit, !isAttacking);
	}
	
	public final Set<JGPoint> getAttackRangeFromPoint(final JGPoint point, final int range) {
		Set<JGPoint> rangeMap = new HashSet<JGPoint>();
		return rangeRecurse(point.x, point.y, myRows, myCols, rangeMap, range, null, false);
	}
	
	/**
	 * Returns a set with the four jgpoints surrounding
	 * the given one on the map.
	 * @param p
	 * @return
	 */
	public final Set<JGPoint> getSurroundings(final JGPoint p) {
		Set<JGPoint> rangeMap = new HashSet<JGPoint>();
		List<JGPoint> rangeList = PathFinder.getSuccessors(p, this);
		for (JGPoint point : rangeList) {
			rangeMap.add(point);
		}
		return rangeMap;
	}

	/**
	 * Helper method for getting a range.
	 * @param x x location
	 * @param y y location
	 * @param rangeMap true/false map of whether or not the unit
	 * being checked can go to each square
	 * @param movesLeft number of squares the unit can still move
	 * @return true/false map of whether or not the unit
	 * being checked can go to each square
	 */
	private Set<JGPoint> rangeRecurse(int x, int y, int rows, int cols, Set<JGPoint> rangeMap, int movesLeft, Unit unit, boolean isMoving) {
		GameObject object = getObject(x,y);
		if(movesLeft < 0 || ((x >= getCols() || x < 0 || y >= getRows() || y < 0 || (object != null && object instanceof Unit && isMoving))&& object != unit)
				|| (object != null && object.getType().equals(Constant.terrainTypeTag)) && isMoving && !unit.getDestroyableIDs().contains(object.getObjName()))
			return rangeMap;
		else if((object != null && object.getType().equals(Constant.terrainTypeTag)) && isMoving && unit.getDestroyableIDs().contains(object.getObjName())) {
			rangeMap.add(new JGPoint(x, y));
		}
		else {
			rangeMap.add(new JGPoint(x, y));
			movesLeft--;
			rangeRecurse(x+1, y, rows, cols, rangeMap, movesLeft, unit, isMoving);
			rangeRecurse(x, y+1, rows, cols, rangeMap, movesLeft, unit, isMoving);
			rangeRecurse(x-1, y, rows, cols, rangeMap, movesLeft, unit, isMoving);
			rangeRecurse(x, y-1, rows, cols, rangeMap, movesLeft, unit, isMoving);
		}        
		return rangeMap;
	}

	public int getObjectiveSize() {
		return myObjectives.size();
	}

	/**
	 * @return The name of this level
	 */
	public final String getName(){
		return myLevelName;
	}

	public String getGameName() {
		return myGameName;
	}

	public String getLevelName() {
		return myLevelName;
	}

	public final Map<JGPoint, GameObjectDef> getMap() {
		Map<JGPoint, GameObjectDef> mapCopy = new ConcurrentHashMap<JGPoint, GameObjectDef>();
		for (JGPoint p : myMap.keySet())
			mapCopy.put(p, (GameObjectDef)myMap.get(p).getDefinition());
		return mapCopy;
	}

	public int getRows() {
		return myRows;
	}

	public int getCols() {
		return myCols;
	}

	public int getTileSize() {
		return myTileSize;
	}

	public String getDifficulty() {
		return myDifficulty;
	}

	public boolean isPlayerFirst() {
		return playerfirst;
	}

	public Map<JGPoint, Unit> getPlayers() {
		int size = myPlayers.size();
		if (size == 0) return null;
		Map<JGPoint, Unit> points = new ConcurrentHashMap<JGPoint, Unit>();
		for (int i = 0; i < size; i++)
			points.put(new JGPoint(myPlayers.get(i).getPosition()), myPlayers.get(i));
		return points;
	}

	public Map<JGPoint, AIUnit> getEnemies() {
		int size = myEnemies.size();
		if (size == 0) return null;
		Map<JGPoint, AIUnit> points = new ConcurrentHashMap<JGPoint, AIUnit>();
		for (int i = 0; i < size; i++)
			points.put(new JGPoint(myEnemies.get(i).getPosition()), myEnemies.get(i));
		return points;
	}
	
	public List<UnitDef> getPlayersInfo() {
		List<UnitDef> infos = new CopyOnWriteArrayList<UnitDef>();
		for (Unit u : myPlayers)
			infos.add((UnitDef)u.getDefinition());
		return infos;
	}

	public void hideAll() {
		Assert.assertNotNull(myMap);
		for (GameObject obj : myMap.values())
			if (obj != null) obj.hide();
	}

	public void showAll() {
		Assert.assertNotNull(myMap);
		for (GameObject obj : myMap.values())
			if (obj != null) obj.show();
	}

	public boolean isFailed(int currentTurnCount) {
		for(Objective objective : myObjectives.values()) {
			if(objective.isFailed(currentTurnCount))
				return true;
		}
		return false;
	}
}
