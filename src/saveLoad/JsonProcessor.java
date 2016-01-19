package saveLoad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import objects.definitions.AIUnitDef;
import objects.definitions.PickupObjectDef;
import objects.definitions.UnitDef;

import com.google.gson.*;

import engine.Game;
import engine.Level;
import engine.ViewController;


/**
 * <p>
 * This is the first version of the Json Processor to save our game & game states. Saving and
 * loading of more objects will be implemented as the class is built on and extended. However the
 * API will remain the same. Right now it can only save instances of the following classes:
 * </p>
 * 
 * <ul>
 * <li>Java primitives & classes with only Java primitives</li>
 * <li>Collections of Java Primitives</li>
 * <li>StatSheet</li>
 * <li>JGObjectDef -- to save play state of the game eventually</li>
 * <li>Games (containing only the GameObject subclasses listed below)</li>
 * <li>GameObjects</li>
 * <li>Units</li>
 * </ul>
 * 
 * <p>
 * The program must have knowledge of the original class of the object. Eventually, this 
 * will be extended and the parser will save & load the class names from the JSON file.  
 * </p>
 * 
 * <pre>
 * public static void main (String[] args) {
 *      JsonProcessor processor = new JsonProcessor;
 *      Game myGame = new Game();
 *      Level lev = new Level(gameName, levelName, row, col, tile, difficulty);
 *      for (int i = 0; i < row-1; i++) {
 *          GameObjectDef def =
 *                  new GameObjectDef(1, Constant.defaultObjectName, "gfxname", 2, 3, 4, 5, "type", "originalPath",
 *                                    true,
 *                                    true);
 *          GameObject obj = new GameObject(def);
 *          lev.placeObject(obj, i, 0, i, 0);
 *      }
 *      game.addLevel("level1", lev);
 *      
 *      // Save to string
 *      String jsonStringRepresentationOfGame = processor.toJson(myGame);
 *      
 *      // Save to file
 *      File file = toJSonFile (myGame, "myGame.json");
 *      
 *      // Retrieve from string
 *      Game retrievedGame = (Game) processor.fromJson(jsonStringRepresentationOfGame, Game.class);
 *      
 *      // Retrieve from file
 *      Game retrievedGame = (Game) processor.fromJsonFilePath("myGame.json", Game.class);
 * } 
 * </pre>
 * 
 * @author Thanh-Ha Nguyen
 * 
 */
public class JsonProcessor {
    private Gson myGson;
    private ViewController controller;

    public JsonProcessor (ViewController c) {
    	controller = c;
        myGson = new GsonBuilder()
        		.registerTypeAdapter(PickupObjectDef.class, new PickupObjectDefGsonTypeAdapter())
        		.registerTypeAdapter(UnitDef.class, new UnitDefGsonTypeAdapter())
        		.registerTypeAdapter(AIUnitDef.class, new AIUnitDefGsonTypeAdapter())
        		.registerTypeAdapter(Game.class, new GameGsonTypeAdapter(controller))
                .registerTypeAdapter(Level.class, new LevelGsonTypeAdapter(controller))
                .enableComplexMapKeySerialization()
                .setVersion(1.0)
                .setPrettyPrinting()
                .serializeNulls()
                .create();  
    }

    public String toJson (Object obj) {
        return myGson.toJson(obj);
    }

    public Object fromJson (String json, Class<?> classOfT) {
        return myGson.fromJson(json, classOfT);
    }

    public File toJSonFile (Object obj, String filepath) throws IOException {
    	PrintWriter fileWriter = null;
        File file = null;
        try {
            file = new File(filepath);
            fileWriter = new PrintWriter(file);
            fileWriter.write(toJson(obj));
        }
        catch (IOException ex) {
            System.out.printf("JSonWriter failed writing object to file:\n");
            System.out.printf("\tObject:\t%s\n", obj.toString());
            System.out.printf("\tFilepath:\t%s\n", filepath);
            System.out.printf("Error:\t%s\n", ex);
            throw (ex);
        }
        finally {
            fileWriter.close();
        }
        return file;
    }

    public Object fromJsonFilePath (String filepath, Class<?> classOfT) throws IOException {
        String json = readFile(filepath);
        return fromJson(json, classOfT);
    }

    // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    private String readFile (String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public Game loadGameFromFilepath (String filepath) {
        try {
			return ((Game)fromJsonFilePath(filepath, Game.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public String saveGameToFilepath(Game game, String filepath) {
        try {
        	game.setAbsolutePath(filepath);
			toJSonFile(game, filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return filepath;
    }
}
