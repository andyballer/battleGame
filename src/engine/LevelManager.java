package engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import objects.definitions.GameObjectDef;

import org.junit.Assert;

public class LevelManager {
	
	private Level activeLevel;

	private List<Level> levels;
	
	private Map<String, Game> games; // game name -> a list of levels
	private Stack<Level> trace;
	
	public LevelManager() {
		levels = new CopyOnWriteArrayList<Level>();
		trace = new Stack<Level>();
		games = new ConcurrentHashMap<String, Game>();
	}
	
	public Level newLevel(String gameName, String levelName, int rows, int cols, int tileSize, String difficulty) {
		Level l = new Level(gameName, levelName, rows, cols, tileSize, difficulty);
		levels.add(l);
		if (!games.containsKey(gameName))
			games.put(gameName, new Game(gameName));
	
		games.get(gameName).getLevels().put(levelName, l);
		trace.add(l);
		return l;
	}
	
	public GameObjectDef getUnitDef(String gameName, String objName) {
		if (games.containsKey(gameName))
			return games.get(gameName).getObject(objName);
		return null;
	}
	
	public void setObject(String gameName, GameObjectDef def) {
		if (games.containsKey(gameName))
			games.get(gameName).addObject(def);
	}
	
	public void deactivateCurrLevel() {
		if (activeLevel == null) return;
		activeLevel.hideAll();
	}
	
	public void activateLevel(Level l) {
		Assert.assertNotNull(l);
		if (l == activeLevel || l == null) return;
		if (trace.peek() != l) trace.add(l);
		activeLevel = l;
		activeLevel.showAll();
	}
	
	public void clearAll() {
		if (activeLevel != null) activeLevel.remove();
		activeLevel = null;
		games.clear();
		levels.clear();
		trace.clear();
	}
	
	public String getCurrLevelName() {
		if (activeLevel == null) return "";
		return activeLevel.getLevelName();
	}
	
	public String getCurrGameName() {
		if (activeLevel == null) return "";
		return activeLevel.getGameName();
	}
	
	public String getLevelGameInCurrGame(int i) {
		if (games.get(activeLevel.getGameName()).getLevels().size() <= i) return null;
		return (String) games.get(activeLevel.getGameName()).getLevels().keySet().toArray()[i];
	}
	
	public int getCurrGameLevelNumber() {
		return games.get(activeLevel.getGameName()).getLevels().size();
	}
	
	public Collection<String> getCurrLevelNameAll() {
		Assert.assertNotNull(activeLevel);
		return games.get(activeLevel.getGameName()).getLevels().keySet();
	}
	
	/**
	 * What does this do? Get the name of all games? If so, it's a misnomer.
	 * @return
	 */
	public Collection<String> getCurrGameNameAll() {
		return games.keySet();
	}
	
	public Level getLevel(String gameName, String levelName) {
		return games.get(gameName).getLevels().get(levelName);
	}
	
	public Level getLevel(String gameName) {
		Stack<Level> temp = new Stack<Level>();
		while (!trace.isEmpty() && !trace.peek().getGameName().equals(gameName)) {
			temp.add(trace.peek());
			trace.pop();
		}
		Assert.assertNotSame(0, trace.size());
		Level l = trace.peek();
		trace.pop();
		while (!temp.isEmpty()) {
			trace.add(temp.peek());
			temp.pop();
		}
		return l;
	}
	
	public Level getCurrLevel() {
		return activeLevel;
	}
	
	public Level getPrevLevel() {
		trace.pop();
		while (!trace.isEmpty() && levels.indexOf(trace.peek()) == -1) {
			trace.pop();
		}
		if (trace.isEmpty()) return null;
		return trace.peek();
	}
	
	public Level getPrevGame() {
		trace.pop();
		while (!trace.isEmpty() && (levels.indexOf(trace.peek()) == -1 || trace.peek().getGameName().equals(activeLevel.getGameName()))) {
			trace.pop();
		}
		if (trace.isEmpty()) return null;
		return trace.peek();
	}
	
	public void removeLevel(Level l) {
		levels.remove(l);
		Assert.assertNotNull(games.get(l.getGameName()));
		games.get(l.getGameName()).getLevels().remove(l.getLevelName());
		if (games.get(l.getGameName()).getLevels().isEmpty())
			games.remove(l.getGameName());
	}
	
	public void removeGame(String gameName) {
		for (Level l : games.get(gameName).getLevels().values())
			levels.remove(l);
		
		games.remove(gameName);
	}
	
	public boolean checkDuplicateName(String gameName, String levelName) {
		if (!games.containsKey(gameName)) return false;
		return games.get(gameName).getLevels().containsKey(levelName);
	}
	
	public boolean checkLevelWithObjectives() {
		for (Level l : games.get(activeLevel.getGameName()).getLevels().values())
			if (l.getObjectiveSize() == 0)
				return false;
		return true;
	}
	
	public Game getCurrGame() {
		return games.get(activeLevel.getGameName());
	}
	
}
