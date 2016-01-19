package display.jgame;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import org.junit.Assert;

import display.Constant;
import display.util.FileChooserView;
import objects.definitions.AIUnitDef;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.definitions.TerrainObjectDef;
import objects.definitions.UnitDef;
import objects.objects.GameObject;
import objects.objects.PickupObject;
import objects.objects.StatSheet;
import objects.objects.TerrainObject;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.AIUnit;
import objects.objects.artificialIntelligence.PathFinder;
import objects.objects.artificialIntelligence.Strategy;
import engine.ViewController;
import jgame.JGColor;
import jgame.JGPoint;
import jgame.JGTimer;
import jgame.platform.AppConfig;
import jgame.platform.JGEngine;
import engine.ResourceLoader;


@SuppressWarnings("serial")
public class GameEnv extends JGEngine {	

	private ViewController controller = null;
	private List<GridLine> myGridLines = new ArrayList<GridLine>();
	private GridSquare[][] myGridSquares;
	private List<JGPoint> mySelectable;
	
	private int myRows, myCols, tileSize;
	
	private JGPoint[][] tileCoordinates;
	private JGPoint currentTile;
	private JGPoint currentRowCol;
	private JGPoint prevRowCol;

	private GameCursor myCursor;
	private Map<String, String> soundsToPath = new ConcurrentHashMap<String, String>();


	private Map<String, GameObjectDef> defaultSettings = new ConcurrentHashMap<String, GameObjectDef>();
	private GameObjectDef instantDrawObject = null;
	
	private List<String> display = new CopyOnWriteArrayList<String>();
	private List<String> importantDisplay = new CopyOnWriteArrayList<String>();	
	
	private boolean TOGGLE_BOOL = true;
	private boolean isSelectable = true;
	private boolean showStat = true;

	// The following fields must be public
	public AppConfig appconfig = null;
	public int key_left = KeyLeft, 
			key_right = KeyRight,
			key_up = KeyUp,    
			key_down = KeyDown,
			default_cursor = DEFAULT_CURSOR;
	public int key_toggleGrid = 'T';
	public int key_showPreferenceWindow = 'J';
	
	private boolean isPlaying = false;
	private boolean isShowingStats = false;
	
	@Override
	public void initCanvas() {
		setCanvasSettings(1, // width of the canvas in tiles
				1, // height of the canvas in tiles
				displayWidth(), // width of one tile
				displayHeight(), // height of one tile
				JGColor.black,// foreground color -> use default color white
				JGColor.white,// background color -> use default color black
				null); // standard font -> use default font 
	}

	@Override
	public void initGame() {
		defineMedia("/resources/animations.tbl");
		setFrameRate(Constant.jgameRate, 2);
		setBGColor(Constant.jgameBG);

		appconfig = new AppConfig(getClass().getName().substring(getClass().getName().indexOf('.') + 1) + " settings", 
				this, 
				getConfigPath(getClass().getName() + ".cfg"));
		appconfig.defineFields("key_","","","","key");
		appconfig.loadFromFile();
		appconfig.saveToObject();

		if (appconfig != null) {
			appconfig.setListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					start();
					requestGameFocus();
				}
			});
		}
		mySelectable = new CopyOnWriteArrayList<JGPoint>();
		controller.getUserDefaults().putInt(String.valueOf(Constant.jgameWindowWidth), pfWidth()/2);
	}

	public GameEnv(Dimension dim, ViewController c){
		initEngineComponent(dim.width, dim.height);
		controller = c;
	}

	@Override
	public void doFrame() {

		checkTileClickEvent();
		moveObjects();

		if (getKey(key_toggleGrid)) {
			clearKey(key_toggleGrid);
			toggleGrid();
		} else if (getKey(key_showPreferenceWindow)) {
			clearKey(key_showPreferenceWindow);
			showPrefWindow();
		}
		
		if (inGameState(Constant.sourceSelectionCompleteMode))
			setSelectable(false);
		else if (inGameState(Constant.sourceSelectionMode) || inGameState(Constant.targetSelectionMode))
			setSelectable(true);
		
		// Display the status panel when playing
		if (isPlaying && showStat) {	
			JGPoint tile = getTileCoordinates(getMouseX(), getMouseY());
			if (tile != null && currentRowCol.x != -1 && currentRowCol.y != -1) {
				GameObject obj = controller.getObject(currentRowCol.x, currentRowCol.y);
				if (obj != null && obj instanceof Unit && (isShowingStats == false || !prevRowCol.equals(currentRowCol)))
					setStatsVisible(true, (Unit)obj);
				if ((obj == null || (obj != null && !(obj instanceof Unit))) && isShowingStats == true)
					setStatsVisible(false, null);
			}
		}
		else
			setStatsVisible(false, null);
		prevRowCol = currentRowCol;
		
	}
	
	private void setStatsVisible(boolean b, Unit u) {
		controller.setStatsVisible(b, u);
		isShowingStats = b;
	}
	
	public void paintFrame() {
		setColor(JGColor.white);
		
		if (Constant.DEBUG && Constant.DEBUG_JGAME)
			for (int i = 0 ; i < display.size(); i++) drawString(display.get(i), pfWidth()/2, 110 + i * 20, 0);
		
		for (int i = 0 ; i < importantDisplay.size(); i++) 
			drawString(importantDisplay.get(i), pfWidth()/2, 110 + i * 20, 0, Constant.displayFont, JGColor.red);
	}
	
	public void displayStatus(final String w) {
		display.add(w);
		Constant.LOG(this.getClass(), w);
		new JGTimer(500, true) {
			
			@Override
			public void alarm() {
				display.remove(w);
			}
		};
	}
	
	/**
	 * FOR DEMO
	 * TODO: take this out.
	 */
	public void displayImportantStatus(final String w) {
		importantDisplay.add(w);
		Constant.LOG(this.getClass(), w);
		new JGTimer(Constant.displayTime, true) {
			
			@Override
			public void alarm() {
				importantDisplay.remove(w);
			}
		};
	}

	public void showPrefWindow() {
		if (appconfig != null) {
			appconfig.openGui();
			stop();
		}
	}
	
	/**
	 * Clears highlighting of tiles from cursor
	 */
	private void clearCursor(){
		if (myCursor != null) {
			myCursor.remove();
			myCursor = null;
		}
	}	
	
	private void checkTileClickEvent() {
		if(isPlaying && getKey(KeyEnter)){
			currentTile = getTileCoordinates((int)myCursor.x, (int)myCursor.y);
			clearKey(KeyEnter);
			if (currentTile == null)
				return;
			handleGameplaySelection();
		}
		
		else if (getKey(KeyMouse1)) {
			currentTile = getTileCoordinates(getMousePos().x, getMousePos().y);
			clearKey(KeyMouse1);
			
			if (currentTile == null)
				return;
			if (!isPlaying) {
				GameObject unit = controller.getObject(currentRowCol.x, currentRowCol.y);
				if (unit == null) {
					if (instantDrawObject != null) {
						try {
							GameObjectDef copy = (GameObjectDef) defaultSettings.get(instantDrawObject.objName).clone();
							Constant.LOG(this.getClass(), "instant " + copy);
							makeObject(copy, currentTile.x, currentTile.y, currentRowCol.x, currentRowCol.y);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						return;
					}
					controller.showListSelectorView();
				} else {
					Constant.LOG(this.getClass(), "not null: " + unit.getDefinition().toString());
					controller.showListSelectorViewWithPrevData(unit);
				}
				stop();
			} else {
				handleGameplaySelection();
			}
        }		
	}
	
	private void handleGameplaySelection(){
		if (isSelectable && mySelectable.contains(currentRowCol)) {
			displayStatus("SELECTED: " + currentRowCol);
			if (inGameState(Constant.sourceSelectionMode)) {
				goToPlayerSelectionCompleteMode();
			} else if (inGameState(Constant.targetSelectionMode)) {
				if (controller.getStateManager().checkValidSelection(currentRowCol.x, currentRowCol.y))
					goToEnemySelectionCompleteMode();
			}
		}
	}
	
	private void goToPlayerSelectionCompleteMode() {
		controller.getStateManager().setState(Constant.sourceSelectionCompleteMode, currentRowCol, true);
		setSelectable(false);
	}
	
	private void goToEnemySelectionCompleteMode() {
		if (isSelectable) {
			setSelectable(false);
			displayStatus("ENEMY SELECTION COMPLETE");
			displayStatus("CLEAR ALL SELECTED");
			controller.getStateManager().setState(Constant.targetSelectionCompleteMode, currentRowCol, false);
		}
		controller.setObjectSelectorPanelEnabled(false);
	}

	 /**
	 * 
	 * @param x mouse click coordinate x
	 * @param y mouse click coordinate y
	 * @return top left JGPoint of corresponding tile
	 */
	public JGPoint getTileCoordinates(int x, int y) {
		if (tileCoordinates == null || tileCoordinates[0] == null) return null;
		JGPoint tile = null;
		if (x < tileCoordinates[0][0].x || x > tileCoordinates[0][tileCoordinates[0].length-1].x + tileSize 
				|| y < tileCoordinates[0][0].y || y > tileCoordinates[tileCoordinates.length-1][0].y + tileSize){
			return null;
		}
		currentRowCol = new JGPoint(-1,  -1);
		for (int i = 0; i < tileCoordinates.length; i++){
			for (int j = 0; j < tileCoordinates[0].length; j++){
				int gridX = tileCoordinates[i][j].x;
				int gridY = tileCoordinates[i][j].y;
				if (((gridX < x) && (x < (gridX + tileSize))) && ((gridY < y) && (y < (gridY + tileSize)))){
					tile = tileCoordinates[i][j];
					currentRowCol = new JGPoint(i, j);
				}
			}
		}
		return tile;
	}

	/**
	 * default method: makes a centered, optimized grid based on only rows and cols
	 * @param rows
	 * @param cols
	 */
	public void makeGrid(int rows, int cols, int tSize){
		myRows = rows;
		myCols = cols;
		if (tSize == 0)
			tileSize = calculateTileSize(rows, cols);
		else
			tileSize = tSize;
		myGridSquares = new GridSquare[myRows][myCols];
		int[] xTileCoord = new int[myCols];
		int[] yTileCoord = new int[myRows];

		if (tileSize == 0)
			tileSize = calculateTileSize(rows, cols);
		
		int offsetX = (pfWidth() - cols*tileSize)/2;
		int offsetY = (pfHeight() - rows*tileSize)/2;

		if (tileSize*tileSize*rows*cols > pfWidth()*pfHeight()){
			return;
		}

		//vertical lines
		for (int i = 0; i < cols+1; i++){
			myGridLines.add(new GridLine("gridline", 0, i*tileSize + offsetX, offsetY, 
					i*tileSize + offsetX, rows*tileSize + offsetY, this));
			if (i < xTileCoord.length){
				xTileCoord[i] = i*tileSize+offsetX;
			}

		}

		//horizontal lines
		for (int i = 0; i < rows+1; i++){
			myGridLines.add(new GridLine("gridline", 0, offsetX, i*tileSize + offsetY, 
					cols*tileSize + offsetX, i*tileSize + offsetY, this));
			if (i < yTileCoord.length){
				yTileCoord[i] = i*tileSize+offsetY;
			}
		}

		createTileCoordMap(xTileCoord, yTileCoord);
		clearCursor();
		myCursor = new GameCursor(tileCoordinates[0][0].x, tileCoordinates[0][0].y, 0, 0, tileSize, this);
	}
	
	public JGPoint[][] getTileCoordinates(){
		return tileCoordinates;
	}

	private void createTileCoordMap(int[] xTileCoord, int[] yTileCoord) {
		tileCoordinates = new JGPoint[yTileCoord.length][xTileCoord.length];
		for (int y=0; y < yTileCoord.length; y++){
			for (int x=0; x < xTileCoord.length; x++){
				tileCoordinates[y][x] = new JGPoint(xTileCoord[x], yTileCoord[y]);
			}
		}
	}

	/**
	 * erases grid and grid list
	 */
	public void clearGrid(){
		removeObjects("gridline",0);
		myGridLines.clear();
	}

	public void toggleGrid(){

		if (TOGGLE_BOOL){
			clearGrid();
		}
		else
			makeGrid(myRows,myCols, tileSize);

		TOGGLE_BOOL ^= true;
	}
	
	/**
	 * 
	 * @param row starting at 0
	 * @param col starting at 0
	 * @param color
	 */
	private void colorGridSquare(int row, int col, JGColor color) {
		if (myGridSquares[row][col] != null && myGridSquares[row][col].getColor().equals(color)) return;
		deleteGridSquare(row, col);
		
		JGPoint tileToColor = tileCoordinates[row][col];
		myGridSquares[row][col] = new GridSquare("gridsquare",tileToColor.x, 
				tileToColor.y, 0, tileSize, color, this);
	}
	
	/**
	 * 
	 * @param row starting at 0...
	 * @param col starting at 0...
	 */
	private void deleteGridSquare(int row, int col){
		if (myGridSquares[row][col] == null){
			return;
		}
		String squareName = myGridSquares[row][col].getName();
		removeObjects(squareName, 0);
		myGridSquares[row][col] = null;
	}
	
	/**
	 * 
	 * @param name
	 * @param collisionId
	 * @param graphic
	 */
	public void makeObject(String name, int collisionId, int team, String graphic, StatSheet stats, StatSheet updates, String type, 
			Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, List<String> actions, String objName, Strategy strategy){
		makeObject(name, collisionId, team, stats, updates, type, 
				currentTile.x, currentTile.y, currentRowCol.x, currentRowCol.y, graphic, inventory, weapons, actions, objName, strategy);
	}
	
	public void pushObject(final GameObject object, final int row, final int col) {
		if(controller.getObject(row, col) == null) {
			moveObject(object, row, col);
		} else {
			controller.displayImportantStatus("Object cannot be pushed!");
			controller.getStateManager().setActionFinished(true);
		}
	}

	public void moveObject(final GameObject object, final int row, final int col){		
		//find path and move step by step
		new Thread() {
			public void run() {
				LinkedList<JGPoint> path = PathFinder.findOptimalPath(object.getPosition(), new JGPoint(row, col), controller.getCurrLevel());
				controller.setJGameEngineEnabled(true);
				while (path.peekLast() != null) {
					JGPoint temp = path.pollLast();
					final JGPoint next = tileCoordinates[temp.x][temp.y];
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							controller.getCurrLevel().moveObject(object, next.x, next.y, row, col);	
						}
					});
					displayStatus("NOW: " + next);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				controller.getStateManager().setActionFinished(true);
			}
		}.start();
		
	}
	
	public void setInstantDrawObject(String name, int collisionID, int team, String graphic, StatSheet stats, StatSheet updates, String type, 
			Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, boolean movable, List<String> actions, String objName, Strategy strategy) {
		instantDrawObject = makeObjectDef(name, collisionID, team, graphic, stats, updates, type, inventory, weapons, movable, actions, objName, strategy);
		defaultSettings.put(instantDrawObject.objName, instantDrawObject);
	}
	
	private GameObjectDef makeObjectDef(String name, int collisionID, int team, String graphic, StatSheet stats, StatSheet updates, String type, 
			Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, boolean movable, List<String> actions, String objName, Strategy strategy) {
		GameObjectDef def = null;
		String imageName = this.defineImageWithScale(name, "#", Constant.imageDefaultCollisionID, graphic, tileSize, tileSize);
		if (type.equals(Constant.playerTypeTag))
			def = new UnitDef(collisionID, name, imageName, team, -1, -1, -1, -1, stats, updates, type, graphic, inventory, weapons, null, objName);
		else if(type.equals(Constant.enemyTypeTag))
			def = new AIUnitDef(collisionID, name, imageName, team, -1, -1, -1, -1, stats, updates, type, graphic, inventory, weapons, null,
					objName, new Strategy(Constant.defaultAIIntelligence, Constant.defaultAIAttack, Constant.defaultAIDefense, Constant.defaultAIMoney, Constant.defaultAIObjective));
		else if (type.equals(Constant.terrainTypeTag))
			def = new TerrainObjectDef(collisionID, name, imageName, -1, -1, -1, -1, graphic, movable, objName); //TODO: false?
		else if (type.equals(Constant.itemTypeTag) || type.equals(Constant.weaponTypeTag))
			def = new PickupObjectDef(collisionID, name, imageName, -1, -1, -1, -1, type, updates, graphic, movable, actions, objName);
		defaultSettings.put(def.objName, def);
		return def;
	}
	
	/**
	 * Create an object and adds it to the level
	 * @param name The name of this object
	 * @param collisionId Collision ID
	 * @param team Team number
	 * @param graphic Graphic name inside of jgame
	 * @param stats 
	 * @param updates
	 * @param type Object type
	 * @param x 
	 * @param y
	 * @param origPath Image path in local file system
	 */
	private void makeObject(String name, int collisionId, int team, StatSheet stats, StatSheet updates, String type, 
			int x, int y, int row, int col, String origPath, Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, List<String> actions, String objName, Strategy strategy) {
		String graphic = this.defineImageWithScale(name, "#", Constant.imageDefaultCollisionID, origPath, tileSize, tileSize);
		if(type.equals(Constant.playerTypeTag) || type.equals(Constant.enemyTypeTag)){
			Unit u = null;
			if (type.equals(Constant.playerTypeTag))
				u = new Unit(new UnitDef(collisionId, name, graphic, team, x, y, row, col, stats, updates, type, origPath, inventory, weapons, null, objName));
			else if (type.equals(Constant.enemyTypeTag))
				u = new AIUnit(new AIUnitDef(collisionId, name, graphic, team, x, y, row, col, stats, updates, type, origPath, inventory, weapons, null, objName, strategy), controller);
			controller.getCurrLevel().placeUnit(u, x, y, row, col);
			defaultSettings.put(u.getObjName(), u.getDefinition());
		} else {
			GameObject newObject = null;
			if(type.equals(Constant.terrainTypeTag)){
				newObject = new TerrainObject(new TerrainObjectDef(collisionId, name, graphic, x, y, row, col, origPath, true, objName));
			}
			if(type.equals(Constant.itemTypeTag) || type.equals(Constant.weaponTypeTag)){
				newObject = new PickupObject(new PickupObjectDef(collisionId, name, graphic, x, y, row, col, type, updates, origPath, true, actions, objName));
			}
			Assert.assertNotNull(newObject);
			controller.getCurrLevel().placeObject(newObject, x, y, row, col);
			defaultSettings.put(newObject.getObjName(), (GameObjectDef)newObject.getDefinition());
		}
	}
	
	public GameObject makeObject(GameObjectDef def, int x, int y, int row, int col) {
		String jGameGraphicPath = existsImage(def.jGameGraphicPath) ? def.jGameGraphicPath :
			this.defineImageWithScale(def.name, "#", Constant.imageDefaultCollisionID, def.originalGraphicPath, tileSize, tileSize);

			
		if (def.type.equals(Constant.playerTypeTag)) {
			UnitDef temp = (UnitDef)def;
			Unit u = new Unit(new UnitDef(temp.collisionID, temp.name, jGameGraphicPath, temp.team, temp.x, temp.y, temp.row, temp.col, 
					temp.stats, temp.growths, temp.type, temp.originalGraphicPath, temp.level, temp.experience, temp.inventory, 
					temp.weapons, temp.actions, temp.objName));
			return controller.getCurrLevel().placeUnit(u, x, y, row, col);
		} else if (def.type.equals(Constant.enemyTypeTag)) {
			AIUnitDef temp = (AIUnitDef)def;
			AIUnit u = new AIUnit(new AIUnitDef(temp.collisionID, temp.name, jGameGraphicPath, temp.team, temp.x, temp.y, temp.row, temp.col, 
					temp.stats, temp.growths, temp.type, temp.originalGraphicPath, temp.level, temp.experience, temp.inventory, temp.weapons, 
					temp.actions, temp.objName, temp.strategy), controller);
			return controller.getCurrLevel().placeUnit(u, x, y, row, col); //TODO: remove repeated code
		} else {
			GameObject newObject = null;
			if (def.type.equals(Constant.terrainTypeTag)) {
				TerrainObjectDef temp = (TerrainObjectDef)def;
				newObject = new TerrainObject(new TerrainObjectDef(temp.collisionID, temp.name, jGameGraphicPath, temp.x, temp.y, temp.row, temp.col, 
						temp.originalGraphicPath, temp.isMovable, temp.objName));
			}
			if (def.type.equals(Constant.itemTypeTag) || def.type.equals(Constant.weaponTypeTag)) {
				PickupObjectDef temp = (PickupObjectDef)def;
				newObject = new PickupObject(new PickupObjectDef(temp.collisionID, temp.name, jGameGraphicPath, temp.x, temp.y, temp.row, temp.col, 
						temp.type, temp.buffs, temp.originalGraphicPath, temp.isMovable, temp.actions, temp.objName));
			}
			Assert.assertNotNull(newObject);
			return controller.getCurrLevel().placeObject(newObject, x, y, row, col);
		}
	}
	
	public void deleteObject() {
		controller.getCurrLevel().deleteObject(currentRowCol.x, currentRowCol.y);
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setPlaying(boolean b) {
		isPlaying = b;
		instantDrawObject = null;
		if(!b) clearCursor();
	}
	
	public void setSelectable(boolean b) {
		isSelectable = b;
		if (b && myCursor != null) myCursor.unlock();
		else {
			if (myCursor != null) myCursor.lock();
			clearSelectables();
		}
	}
	
	public void addSelectables(Set<JGPoint> points, boolean exclusive) {
		if (points == null) return;
		if (exclusive)
			clearSelectables();
		for (JGPoint p : points) {
			mySelectable.add(p);
			colorGridSquare(p.x, p.y, JGColor.red);
		}
	}
	
	private void clearSelectables() {
		for (JGPoint p : mySelectable) {
			if (myGridSquares[p.x][p.y] != null) {
				myGridSquares[p.x][p.y].remove();
				myGridSquares[p.x][p.y] = null;
			}
		}
		mySelectable.clear();
	}
	
	public JGPoint getCurrSelected() {
		return new JGPoint(currentRowCol.x, currentRowCol.y);
	}
	
	public JGPoint getDimensions(){
		return new JGPoint(myRows, myCols);
	}
	
	public boolean isSelectable(int row, int col) {
		return mySelectable.contains(new JGPoint(row, col));
	}
	
	public void setStatShowVisible(boolean b) {
		showStat = b;
	}
	
	/**
	 * color a bunch of grids
	 * @param rangeMap each boolean indicates whether this grid needs coloring. true for coloring
	 * @param color
	 */
	public void colorGrids(Boolean[][] rangeMap, JGColor color) {
		Assert.assertTrue(rangeMap != null && rangeMap[0] != null);
		Assert.assertEquals(myGridSquares.length, rangeMap.length);
		Assert.assertEquals(myGridSquares[0].length, rangeMap[0].length);
		for (int i = 0; i < rangeMap.length; i++)
			for (int j = 0; j < rangeMap[0].length; j++)
				colorGridSquare(i, j, color);
	}
	
	public String selectImage(boolean isTileImage, String dir){
			return getImageSelection(dir);
	}

	//opens file chooser to get either background or new image
	private String getImageSelection(String dir) {
		FileChooserView fileChooser = new FileChooserView();
		fileChooser.setFileFilter(Constant.myImageFilter, null);
		fileChooser.setDefaultDirectory(new File(dir));
		return fileChooser.loadFile();
	}
	
	public int calculateTileSize(int rows, int cols){

		if(pfHeight()/rows > pfWidth()/cols){
			tileSize = (pfWidth() - 120)/cols;
		}
		else{
			tileSize = (pfHeight() - 100)/rows;
		}
		
		return tileSize;

	}
	
	public int getTileSize(){
		return tileSize;
	}
	
	public GameCursor getGameCursor(){
		return myCursor;
	}
	
	public void removeInstantDrawObject() {
		instantDrawObject = null;
	}
	
	/**
	 * Play a sound after a given action
	 * @param name that is a description of the action
	 * @param fileName directory of audio file
	 */
	public void playActionSound(String name){
		if (!soundsToPath.containsKey(name)){
			loadAudioMedia(name);
		}
		displayStatus("play sound " + name);
		defineAudioClip(name, soundsToPath.get(name));
		playAudio(name);
	}
	
	private void loadAudioMedia(String tag){
		soundsToPath = ResourceLoader.load(tag, Constant.soundPropertyFile);
	}
	
}
