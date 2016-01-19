package engine;

import gameplayer.highscore.HighScoreManager;
import gameplayer.mode.StateManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import jgame.JGPoint;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.objects.GameObject;
import objects.objects.StatSheet;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.Strategy;
import display.MainView;
import display.PreferenceView;

/**
 * All public methods needed by other classes in Game is declared here
 * so only those methods are available outside, no need to pass Game to everywhere
 */
public interface ViewController {
	// register basic components
	public void setPref(PreferenceView p);
	public void setMainView(MainView mv);
	
	// control the visibility of windows and panels
	public void makePrefVisible();
	public void showObjectiveSettingView(String name);
	public void showListSelectorView();
	public void showListSelectorViewWithPrevData(GameObject unit);
	public void setStatsVisible(boolean b, Unit u);
	public void focusBackToMainView();
	public void showResultsView(boolean playerWin, boolean allOver);
	public void setObjectSelectorPanelEnabled(boolean b);
	public void updateStatsTable();
	
	// levels and games switching/deleting
	public void newLevel();
	public Level newLevel(String gameName, String levelName, Integer rows, Integer cols, Integer tileSize, String difficulty);
	public String getCurrGameName();
	public String getCurrLevelName();
	public Level getCurrLevel();
	public void switchGame(String gameName);
	public void switchLevel(String gameName, String levelName);
	public void deleteGame(final String gameName, boolean prompt);
	public void deleteLevel(final String gameName, final String levelName);
	public boolean isDemo();
	
	// objectives related APIs: add/set/delete/check
	public void addObjectiveBinding(String name, Object[][] s);
	public void setObjective(String obj, List<Integer> args);
	public void removeObjective(String obj);
	public Object[][] getObjectiveLayout(String name);
	public boolean checkLevelWithObjectives();
	public boolean checkGameObjectives();
	public boolean checkFailure(int currentTurnCount);
	
	// objects related APIs: create/move/delete
	public GameObject getObject(int row, int col);
	public void moveObject(GameObject object, int row, int col);
	public void newObject(String name, int collisionId, int team, String graphic, StatSheet stats, StatSheet updates, 
			String type, Map<PickupObjectDef, Integer> inventory, Set<PickupObjectDef> weapons, List<String> actions, 
			String objName, Strategy strategy);
	public GameObject makeObject(GameObjectDef def);
	public GameObject makeObject(GameObjectDef def, int row, int col);
	public void deleteObject();
	public void deleteObject(GameObjectDef def);
	public void pushObject(GameObject toPush, int row, int col, int i, int j);
	
	// jgame states related APIs: check/switch
	public boolean isPlaying();
	public void preparePlayMode();
	public void startPlayMode(int level);
	public void startPlayMode(String levelName);
	public void startEditMode();
	public void setPlaying(boolean b);
	public boolean isSelectable(int row, int col);
	public void addSelectables(Set<JGPoint> points, boolean exclusive);
	public void setJGameEngineEnabled(Boolean b);
	public void setJGameEngineSelectable(boolean b);
	public void playActionSound(String description);
	public boolean checkDuplicateName(String gameName, String levelName);
	public JGPoint[][] getTileCoordinates();
	public void displayAnimation(String name, double x, double y);
	public String scaleImage(String name, String tileName, int collisionid, int x, int y);
	
	// getters of basic components
	public Preferences getUserDefaults();
	public StateManager getStateManager();
	public LevelManager getLevelManager();
	public HighScoreManager getScoreManager();

	// display related APIs: mainly for debugging
	public void displayStatus(String s);
	public void displayImportantStatus(String s);
        
	// saving & loading game
	public void saveGame(String filepath);
	public void loadGame(String filepath);
	
	//saving and showing scores
	public void saveScore(String name, int score);
	public int getCurrScore();
	public void awardPoints(int points);
}
