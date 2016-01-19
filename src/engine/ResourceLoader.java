package engine;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import display.Constant;

/**
 * Can load some desired information from a properties file.
 * This is normally used when we’re doing something with reflection.
 * For example, we use the resource loader to get all the different
 * images that can be used for enemies,
 * or the classpaths of the different algorithms for AIUnits.
 * Code heavily influenced by Jordan Ly's work in SLogo
 * 
 * @author Teddy
 *
 */
public class ResourceLoader {
	
	/**
	 * Loads all of the information from the given properties file 
	 * that starts with the given tag.  ie: if I wanted to get a 
	 * list of all the images that can be used for enemy units, 
	 * I would call load(“Enemy”, “resources.images”);
	 * This would scan the images properties file for any entries 
	 * that start with “Enemy” (ie: “EnemyGargoyle=medieval_enemy_64.png” 
	 * but not “PlayerStandard=player_standard_64.png”) 
	 * and give us a map linking the name (“Gargoyle”) 
	 * to the image path (“medieval_enemy.png”).
	 * @param tag the tag that identifies a certain type of resource
	 * @param fileName the properties file to load from
	 * @return a mapping of a name to some data
	 */
	public static Map<String, String> load(String tag, String fileName) {
		Map<String, String> loaded = new HashMap<String, String>();
		
		ResourceBundle bundle = ResourceBundle.getBundle(fileName);
		
		for(String key : bundle.keySet()){
			if(key.startsWith(tag)){
				String value = bundle.getString(key);
				int start = key.indexOf(tag) + tag.length();
				String newKey = key.substring(start);
				if(fileName.equals(Constant.imagePropertyFile)){
					value = Constant.imageDirectory + value;
					loaded.put(newKey, value);
				}
				
				else if(fileName.equals(Constant.soundPropertyFile)){
					value = Constant.soundDirectory + value;
					loaded.put(tag, value);
				}
				loaded.put(newKey, value);
			}
		}
		
		return loaded;
	}
}
