package engine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import objects.definitions.GameObjectDef;

public class Game {
    
	private String name = "";
	private Map<String, Level> levels = new LinkedHashMap<String, Level>();
	private Map<String, GameObjectDef> objects = new ConcurrentHashMap<String, GameObjectDef>();
	
	private String absolutePath = null;
	
	public Game(String n) {
		name = n;
	}
	
	public GameObjectDef getObject(String name) {
		return objects.get(name);
	}
	
	public void addObject(GameObjectDef unit) {
		objects.put(unit.name, unit);
	}
	
	public Map<String, Level> getLevels() {
		return levels;
	}
	
	public void addLevel(String name, Level l) {
		levels.put(name, l);
	}

    public String getName () {
        return name;
    }
    
    public void setAbsolutePath(String p) {
    	absolutePath = p;
    }
    
    public String getFilePath() {
    	return absolutePath;
    }
}
