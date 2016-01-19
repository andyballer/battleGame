package engine;

import gameplayer.highscore.HighScoreManager;
import gameplayer.mode.ActionMode;
import gameplayer.mode.AllOverMode;
import gameplayer.mode.EnemyMode;
import gameplayer.mode.GameOverMode;
import gameplayer.mode.LevelCompleteMode;
import gameplayer.mode.SourceSelectionCompleteMode;
import gameplayer.mode.SourceSelectionMode;
import gameplayer.mode.StateManager;
import gameplayer.mode.TargetSelectionCompleteMode;
import gameplayer.mode.TargetSelectionMode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import jgame.JGObject;
import jgame.JGPoint;
import objectives.Objective;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.objects.GameObject;
import objects.objects.StatSheet;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.Strategy;

import org.junit.Assert;

import saveLoad.JsonProcessor;
import display.Constant;
import display.FinalResultsView;
import display.LevelSettingView;
import display.MainView;
import display.ObjectiveSettingView;
import display.PreferenceView;
import display.UnitStatsView;
import display.jgame.GameEnv;
import display.objectSelector.ObjectSelectorView;


public class Controller implements ViewController {
	
	final private Preferences userPref;
	final private LevelManager levelManager;
	final private LevelSettingView settings;
	final private ObjectiveSettingView objSet;
	final private ObjectSelectorView listSelector;
	final private UnitStatsView statView;
	final private FinalResultsView resultsView;
	final private HighScoreManager scoreManager;

	private StateManager stateManager;
	private MainView mainView = null;
	private GameEnv gameEnv = null;
	private PreferenceView prefView;

	private Map<String, Object[][]> objectives;

	public Controller() {
		levelManager = new LevelManager();
		userPref = Preferences.userRoot().node(Constant.prefDirectory);
		settings = new LevelSettingView(Constant.settingName, Constant.prefWindowWidth, Constant.prefWindowHeight, this);
		objSet = new ObjectiveSettingView("Objective Setting", Constant.prefWindowWidth, Constant.prefWindowHeight, this);
		statView = new UnitStatsView("", Constant.prefWindowWidth, Constant.prefWindowHeight);
		resultsView = new FinalResultsView("Level Results", Constant.prefWindowWidth, Constant.prefWindowHeight, this);
		scoreManager = new HighScoreManager();
		
		objectives = new HashMap<String, Object[][]>();
		listSelector = new ObjectSelectorView("ListView", Constant.prefWindowWidth, Constant.prefWindowHeight, this);
		listSelector.addImages(Constant.listShowMainTag, Constant.mainTypeTag);
		listSelector.addImages(Constant.listShowMainTag, Constant.exitTypeTag);
		listSelector.setMainSelectorListener(new SelectItemAction());
	}
	
	public void addSelectables(Set<JGPoint> points, boolean exclusive) {
		gameEnv.addSelectables(points, exclusive);
	}
	
	public boolean isDemo() {
		return mainView.isDemo();
	}
	
	public void setMainView(MainView mv) {
		mainView = mv;
		gameEnv = mv.getGameEnv();
		stateManager = new StateManager(this, gameEnv);
		initStates();
	}
	
	public void moveObject(GameObject toMove, int row, int col) {
		gameEnv.moveObject(toMove, row, col);
		stateManager.moveObject(toMove, row, col);
	}
	
	public void pushObject(GameObject toMove, int oldRow, int oldCol, int row, int col) {
		gameEnv.pushObject(toMove, row, col);
		stateManager.pushObject(toMove, oldRow, oldCol, row, col);
	}
	
	public void setStatsVisible(boolean b, Unit u) {
		statView.setVisible(b, u);
	}
	
	public boolean checkLevelWithObjectives() {
		return levelManager.checkLevelWithObjectives();
	}
	
	private void initStates() {
		Assert.assertNotNull(stateManager);
		stateManager.addState(new ActionMode(Constant.actionMode, this, stateManager));
		
		stateManager.addState(new SourceSelectionMode(Constant.sourceSelectionMode, this, stateManager));
		stateManager.addState(new TargetSelectionMode(Constant.targetSelectionMode, this, stateManager));
		stateManager.addState(new SourceSelectionCompleteMode(Constant.sourceSelectionCompleteMode, this, stateManager));
		stateManager.addState(new TargetSelectionCompleteMode(Constant.targetSelectionCompleteMode, this, stateManager));
		
		stateManager.addState(new EnemyMode(Constant.enemyMode, this, stateManager));
		
		stateManager.addState(new LevelCompleteMode(Constant.levelCompleteMode, this, stateManager));
		stateManager.addState(new GameOverMode(Constant.exitMode, this, stateManager));
		stateManager.addState(new AllOverMode(Constant.allOverMode, this, stateManager));
	}
	
	public Preferences getUserDefaults() {
		return userPref;
	}
	
	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void makePrefVisible() {
		Assert.assertNotNull(prefView);
		prefView.setVisible(true);
	}

	public void setPref(PreferenceView p) {
		prefView = p;
	}

	public void newLevel() {
		Assert.assertTrue(!isPlaying());
		settings.setVisible(true);
		settings.setAlwaysOnTop(true);
		mainView.setEnabled(false);
	}
	
	public boolean isPlaying() {
		return gameEnv.isPlaying();
	}
	
	public boolean checkGameObjectives() {
		if (getCurrLevel() == null) return false;
		List<String> achieved = getCurrLevel().getCompleteObjectives();
		mainView.setGoalAchieved(achieved);
		Constant.LOG(this.getClass(), "Objectives: " + mainView.getAchievedObjectiveNumber() + " " + achieved + " " + getCurrLevel().getObjectiveSize());
		return (mainView.getAchievedObjectiveNumber() == getCurrLevel().getObjectiveSize());
	}
	
	public boolean checkFailure(int currentTurnCount) {
		return getCurrLevel().isFailed(currentTurnCount);
	}
	
	public void preparePlayMode() {
		mainView.startPlayMode();
	}
	
	public void startPlayMode(String levelName) {
		switchLevel(getCurrGameName(), levelName);
		startPlayMode();
	}
	
	public void startPlayMode(int level) {
		switchLevel(level);
		startPlayMode();
	}
	
	private void startPlayMode() {
		displayStatus(getCurrGameName() + " " + getCurrLevelName());
		boolean res = stateManager.initLevel(getCurrLevel().isPlayerFirst(), 
											levelManager.getCurrGameLevelNumber(),
											getCurrLevel().getPlayers(), 
											getCurrLevel().getEnemies());
		if (!res) {
			return;
		}
		Thread t = new Thread() {
			public void run() {
				stateManager.start();
		    }
		};
		t.start();
	}
	
	/** 
	 * used to display words on jgame
	 */
	public void displayStatus(String s) {
		gameEnv.displayStatus(s);
	}
	
	public void displayImportantStatus(String s) {
		gameEnv.displayImportantStatus(s);
	}

	public void setPlaying(boolean b) {
		gameEnv.setPlaying(b);
		objSet.setEnabled(!b);
		stateManager.resetLevelNumber();
	}
	
	public void startEditMode() {
		mainView.startEditMode();
		gameEnv.setStatShowVisible(false);
	}

	public String getCurrGameName() {
		return levelManager.getCurrGameName();
	}
	
	public String getCurrLevelName() {
		return levelManager.getCurrLevelName();
	}
	
	public void addObjectiveBinding(String name, Object[][] s) {
		objectives.put(name, s);
	}
	
	public Object[][] getObjectiveLayout(String name) {
		return objectives.get(name);
	}
	
	public Level getCurrLevel() {
		return levelManager.getCurrLevel();
	}
	
	public StateManager getStateManager() {
		return stateManager;
	}
	
	public void focusBackToMainView() {
		mainView.setEnabled(true);
		settings.setAlwaysOnTop(false);
		prefView.setAlwaysOnTop(false);
		objSet.setAlwaysOnTop(false);
		listSelector.setAlwaysOnTop(false);
		resultsView.setAlwaysOnTop(false);
	}

	public GameObject makeObject(GameObjectDef def) {
		return gameEnv.makeObject(def, def.x, def.y, def.row, def.col);
	}
	
	public GameObject makeObject(GameObjectDef def, int row, int col) {
		JGPoint pixel = getTileCoordinates()[row][col];
		return gameEnv.makeObject(def, pixel.x, pixel.y, row, col);
	}

	public Level newLevel(String gameName, 
						 String levelName, 
						 Integer rows, 
						 Integer cols,
						 Integer tileSize,
						 String difficulty) {
		final Level level = levelManager.newLevel(gameName, levelName, rows, cols, tileSize, difficulty);
		levelManager.deactivateCurrLevel();
		mainView.clearAll();
		levelManager.activateLevel(level);
		Assert.assertNotNull(levelManager.getCurrLevel());
		gameEnv.makeGrid(rows, cols, tileSize); 
		mainView.setLevel(levelManager.getCurrGameNameAll(), levelManager.getCurrLevelNameAll(), levelManager.getCurrLevel());
		return level;
	}
	
	public void newObject(String name, int collisionId, int team, String graphic, StatSheet stats, StatSheet updates, 
			String type, Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, List<String> actions, String objName, Strategy strategy) {
		gameEnv.makeObject(name, collisionId, team, graphic, stats, updates, type, inventory, weapons, actions, objName, strategy);
	}
	
	public void deleteObject() {
		gameEnv.deleteObject();
	}
	
	public void deleteObject(GameObjectDef def) {
		if (isPlaying())
			stateManager.deleteObject(def.row, def.col, def.type);
		getCurrLevel().deleteObject(def.row, def.col);	
	}
	
	public void setJGameEngineSelectable(boolean b) {
		gameEnv.setSelectable(b);
		gameEnv.setStatShowVisible(b);
	}
	
	public void setObjectSelectorPanelEnabled(boolean b) {
		mainView.setObjectSelectorPanelEnabled(b);
	}
	
	public void switchLevel(int i) {
		String levelName = levelManager.getLevelGameInCurrGame(i);
		if (levelName == null) return;
		switchLevel(getCurrGameName(), levelName);
	}
	
	public void switchGame(String gameName) {
		Level l = levelManager.getLevel(gameName);
		scoreManager.initGame(gameName);
		Constant.LOG(this.getClass(), "switch game: " + gameName + " " + l.getGameName() + " " + l.getLevelName());
		if (!l.getGameName().equals(levelManager.getCurrGameName())) switchLevel(l);
	}

	public void switchLevel(String gameName, String levelName) {
		if (gameName.equals(getCurrGameName()) && levelName.equals(getCurrLevelName())) return;
		Level l = levelManager.getLevel(gameName, levelName);
		Constant.LOG(this.getClass(), "switch level: " + gameName + " " + levelName + " " + l.getGameName() + " " + l.getLevelName());
		switchLevel(l);
	}
	
	protected void switchLevel(final Level l) {
		levelManager.deactivateCurrLevel();
		mainView.clearAll();
		levelManager.activateLevel(l);
		Assert.assertNotNull(levelManager.getCurrLevel());
		mainView.restoreAll(l);
		mainView.setLevel(levelManager.getCurrLevelNameAll(), levelManager.getCurrLevel());
	}
	
	public void deleteGame(final String gameName, boolean prompt) {
		final Level prevLevel = levelManager.getPrevGame();
		
		if (prevLevel == null) {
			levelManager.clearAll();
			if (prompt) newLevel();
		} else {
			switchLevel(prevLevel);
			levelManager.removeGame(gameName);
			mainView.setLevel(levelManager.getCurrGameNameAll(), levelManager.getCurrLevelNameAll(), levelManager.getCurrLevel());
		}
	}
	
	public void deleteLevel(final String gameName, final String levelName) {
		final Level currLevel = levelManager.getLevel(gameName, levelName);
		Assert.assertNotNull(currLevel);
		final Level prevLevel = levelManager.getPrevLevel();

		if (prevLevel == null) {
			levelManager.clearAll();
			newLevel();
		} else {
			switchLevel(prevLevel);
			levelManager.removeLevel(currLevel);
			mainView.setLevel(levelManager.getCurrGameNameAll(), levelManager.getCurrLevelNameAll(), levelManager.getCurrLevel());
		}
	}
	
	public GameObject getObject(int row, int col) {
		return getCurrLevel().getObject(row, col);
	}
	
	public boolean isSelectable(int row, int col) {
		return gameEnv.isSelectable(row, col);
	}
	
	public void showListSelectorView() {
		Assert.assertTrue(!isPlaying());
		listSelector.setVisible(true);
		listSelector.setAlwaysOnTop(true);
		mainView.setEnabled(false);
	}
	
	public void showListSelectorViewWithPrevData(GameObject unit) {
		Assert.assertTrue(!isPlaying());
		listSelector.restore(unit);
		listSelector.setVisible(true);
		listSelector.setAlwaysOnTop(true);
		mainView.setEnabled(false);
	}
	
	public void showResultsView(boolean playerWin, boolean allOver) {
		resultsView.setVisible(true, playerWin, allOver, getCurrLevel().getPlayersInfo(), getCurrScore(), scoreManager.getHighscoreData(), mainView.isEditable());
		scoreManager.resetScore();
		resultsView.setAlwaysOnTop(true);
		mainView.setEnabled(false);
		gameEnv.setStatShowVisible(false);
	}
	
	public void showObjectiveSettingView(String name) {
		objSet.setVisible(name, levelManager.getCurrLevel().getObjective(name));
		objSet.setAlwaysOnTop(true);
		if (!isPlaying()) mainView.setEnabled(false);
	}
	
	public void setObjective(String obj, List<Integer> args) {
		mainView.setObjective(obj, true);
		Objective o = levelManager.getCurrLevel().getObjective(obj);
		if (o != null) o.remove();
		levelManager.getCurrLevel().addObjective(obj, args, this);
	}
	
	public void removeObjective(String obj) {
		mainView.setObjective(obj, false);
		levelManager.getCurrLevel().removeObjective(obj);
	}
	
	public boolean checkDuplicateName(String gameName, String levelName) {
		return levelManager.checkDuplicateName(gameName, levelName);
	}
	
	public void setJGameEngineEnabled(Boolean b) {
		mainView.setJGameEngineEnabled(b);
	}
	
	private class SelectItemAction extends MouseAdapter {
	    public void mouseClicked(MouseEvent e) {
	        if (e.getClickCount() == 1) {
	        	String name = listSelector.getMainCategorySelection(); // type
	        	Map<String, String> picsToPaths = ResourceLoader.load(Constant.exitTypeTag, Constant.imagePropertyFile);
	    		Assert.assertTrue(picsToPaths.size() == 1);
	        	if (name.equals(picsToPaths.keySet().toArray(new String[0])[0])) {
	        		listSelector.focusBackToMainView();
	        		return;
	        	}
	        	
	    		Map<String, String> namesToPaths = ResourceLoader.load(name, Constant.imagePropertyFile);
	        	listSelector.addImages(Constant.listShowItemTag, Constant.itemTypeTag);
	        	listSelector.addImages(Constant.listShowWeaponTag, Constant.weaponTypeTag);
	        	listSelector.updateImageSelectorPanel(name, namesToPaths);
	         }
	    }
	}

	
	public void playActionSound(String description) {
		gameEnv.playActionSound(description);
	}

	public JGPoint[][] getTileCoordinates() {
		return gameEnv.getTileCoordinates();
	}

    @Override
    public void saveGame (String filepath) {
        JsonProcessor processor = new JsonProcessor(this);
        Game game = levelManager.getCurrGame();
        processor.saveGameToFilepath(game, filepath);
    }

    @Override
    public void loadGame (String filepath) {
        JsonProcessor processor = new JsonProcessor(this);
        processor.loadGameFromFilepath(filepath);
        switchLevel(0);
    }
    
    public void saveScore(String name, int score){
    	scoreManager.addScore(name, score);
    }

	@Override
	public int getCurrScore() {
		//TODO:  Create field in which to store game score
		return scoreManager.getCurrScore();
	}

	@Override
	public HighScoreManager getScoreManager() {
		return scoreManager;
	}

	/**
	 * Function for awarding points within current game
	 * @param points Number of points to award.
	 */
	public void awardPoints(int points) {
		scoreManager.awardPoints(points);		
	}
	
	public String scaleImage(String name, String tileName, int collisionid, int x, int y) {
		return gameEnv.defineImageWithScale(name, tileName, collisionid, x, y);
	}
	
	public void displayAnimation(String name, double x, double y) {
		JGObject obj = new JGObject(name, true, 0, 0, 0, name);
		Map<String, String> animations = ResourceLoader.load(Constant.animationTag, Constant.commonPropertyFile);
		try {
			Class.forName(animations.get(name)).getConstructor(String.class, int.class, int.class, JGObject.class)
											.newInstance(name, (int)(x + gameEnv.getTileSize()/2), (int)(y - gameEnv.getTileSize()/2), obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateStatsTable() {
		mainView.updatePlayerStats();
	}
}
