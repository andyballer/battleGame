package saveLoad;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgame.JGPoint;
import objects.definitions.AIUnitDef;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.definitions.TerrainObjectDef;
import objects.definitions.UnitDef;
import objects.objects.PickupObject;
import objects.objects.StatSheet;
import objects.objects.TerrainObject;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.AIUnit;
import objects.objects.artificialIntelligence.Strategy;

import org.junit.Test;

import display.Constant;
import display.MainView;
import engine.Controller;
import engine.Game;
import engine.Level;
import engine.ViewController;


public class TestGson {
	
	private ViewController controller = null;
	private MainView view = null;
	
	private void initTest() {
		controller = new Controller();
    	view = new MainView(Constant.progName, Constant.mainWindowWidth, Constant.mainWindowHeight, controller, true);
    	controller.setMainView(view);
	}
	
	@SuppressWarnings("unused")
    private class SimpleObject {
        private int myInt;
        private String myString;
		private Map<JGPoint, String> myIntStringMap;

        public SimpleObject (int i, String str, Map<JGPoint, String> map) {
            myInt = i;
            myString = str;
            myIntStringMap = map;
        }
    }

    private void testGsonSerializableToFile(Object obj, String filename) {
        Class<?> objClass = obj.getClass();
        JsonProcessor writer = new JsonProcessor(controller); 
        File file;
        try {
        	String oldJson = writer.toJson(obj);
            file = writer.toJSonFile(obj, filename);
            controller.getLevelManager().clearAll();
            //System.out.printf("Object %s written to file %s\n", obj.toString(), file.getAbsolutePath());
            Object newObj = writer.fromJsonFilePath(file.getAbsolutePath(), objClass);
            String newJson = writer.toJson(newObj);
            
            String assertMessage =
                    String.format("Object %s was unable to be serialized\nOriginal Json:\t%s\nNew Json:\t%s",
                                  obj.getClass().getName(),
                                  oldJson, newJson);
            assertEquals(assertMessage, oldJson, newJson);
        }
        catch (IOException e) {
            e.printStackTrace();
            fail("IOexception");
        }
    }
    
    private void testGsonSerializable (Object obj) {

        Class<?> objClass = obj.getClass();
        JsonProcessor writer = new JsonProcessor(controller); 
        String json = writer.toJson(obj);

        // FromJson Method needs to save class name
        Object newObj = writer.fromJson(json, objClass);
        controller.getLevelManager().clearAll();
        String newJson = writer.toJson(newObj);

        String assertMessage =
                String.format("Object %s was unable to be serialized\nOriginal Json:\t%s\nNew Json:\t%s",
                              obj.getClass().getName(),
                              json, newJson);
        assertEquals(assertMessage, json, newJson);
    }
    
    @Test
    public void testGsonMap () {
    	initTest();
        Map<JGPoint, String> map = new HashMap<JGPoint, String>();
        map.put(new JGPoint(1, 2), "one");
        map.put(new JGPoint(3, 4), "two");
        SimpleObject obj = new SimpleObject(1, "Hello World!", map);
        testGsonSerializable(obj);
        testGsonSerializableToFile(obj, "simpleObject.json");
    }

    @Test
    public void testGSonGameObjectDef () {
    	initTest();
        GameObjectDef def =
                new GameObjectDef(1, "name", "gfxname", 2, 3, 4, 5, "type", "originalPath", true,
                                  true, "obj");
        testGsonSerializable(def);
        testGsonSerializableToFile(def, "GameObjectDef.json");
    }

    @Test
    public void testGsonUnitDef () {
    	initTest();
        UnitDef def = new UnitDef(1, "name", "gfxname", 1, 2, 3, 4, 5, "type", "originalPath", null, null, null, "obj");
        testGsonSerializable(def);
        testGsonSerializableToFile(def, "UnitDef.json");
    }

    @Test
    public void testStatSheet () {
    	initTest();
        StatSheet stats = new StatSheet(5.0, 5.0, 5.0, 5.0, 5.0, 5.0);
        testGsonSerializable(stats);
        testGsonSerializableToFile(stats, "StatSheet.json");
    }

    @Test
    public void testGsonLevel () {
    	initTest();
        Level lev = createTestLevel("gamename", "levelname", 5, 5, 64, Constant.moderateTag);
        testGsonSerializableToFile(lev, "Level.json");
        testGsonSerializable(lev);
    }

    @Test
    public void testGsonGame () {
    	initTest();
        Game game = new Game("Game");
        game.addLevel("level 1", createTestLevel("game 1", "level 1", 5, 5, 1, "difficulty"));
        game.addLevel("level 2", createTestLevel("game 1", "level 2", 5, 5, 1, "difficulty"));
        testGsonSerializableToFile(game, "Game.json");
        testGsonSerializable(game);
    }

    private Level createTestLevel (String gameName,
                                   String levelName,
                                   int row,
                                   int col,
                                   int tile,
                                   String difficulty) {
        Level lev = new Level(gameName, levelName, row, col, tile, difficulty);
        UnitDef udef = new UnitDef(Constant.playerCollisionID, Constant.defaultObjectName, "gfxname", Constant.playerTeam, 100, 200, 2, 3, 
        		new StatSheet(200, 100, 100, 50, 4, 3), new StatSheet(0.1,0.1,0.1,0.1,0.1,0.1), Constant.playerTypeTag, "gfxname", 
        		1, 0, null, null, null, Constant.playerTag);
        Unit u = new Unit(udef);
        lev.placeUnit(u, 100, 200, 2, 3);
        
        AIUnitDef aidef = new AIUnitDef(Constant.enemyCollisionID, Constant.defaultObjectName, "gfxname", Constant.enemyTeam, 120, 220, 2, 4, 
        		new StatSheet(200, 100, 100, 150, 4, 5), new StatSheet(0.1,0.2,0.1,0.1,0.1,0.1), Constant.enemyTypeTag, "gfxname", 
        		1, 0, null, null, null, Constant.enemyTag, new Strategy(10, 100, 0, 100, 20));
        AIUnit ai = new AIUnit(aidef, controller);
        lev.placeUnit(ai, 100, 200, 2, 4);
        
        PickupObjectDef pdef = new PickupObjectDef(Constant.envCollisionID, Constant.defaultObjectName, "gfxname", 50, 100, 1, 1, 
        		Constant.itemTypeTag, new StatSheet(0.1,0.1,0.1,0.1,0.1,0.1), "fsfsdfs.png", true, null, Constant.potionTag);
        PickupObject pick = new PickupObject(pdef);
        lev.placeObject(pick, 50, 100, 1, 1);
        
        PickupObjectDef wdef = new PickupObjectDef(Constant.envCollisionID, Constant.defaultObjectName, "gfxname", 50, 150, 1, 2, 
        		Constant.weaponTypeTag, new StatSheet(0.1,0.1,0.5,0.1,0.1,0.1), "fsfsdfsfff.png", true, null, Constant.axeTag);
        PickupObject weapon = new PickupObject(wdef);
        lev.placeObject(weapon, 50, 150, 1, 2);
        
        TerrainObjectDef tdef = new TerrainObjectDef(Constant.envCollisionID, Constant.defaultObjectName, "gfxname", 250, 300, 3, 3, 
        		"sfsdfiweo.png", false, Constant.treeTag);
        TerrainObject tree = new TerrainObject(tdef);
        lev.placeObject(tree, 250, 300, 3, 3);
        
        List<Integer> args = new ArrayList<Integer>();
        args.add(2);
        args.add(2);
        lev.addObjective(Constant.goalCapturePointTag, args, controller);
        
        return lev;
    }
}
