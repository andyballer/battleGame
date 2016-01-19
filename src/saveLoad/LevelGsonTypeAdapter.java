package saveLoad;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import jgame.JGPoint;
import objectives.Objective;
import objects.definitions.GameObjectDef;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import display.Constant;
import engine.Level;
import engine.ViewController;


public class LevelGsonTypeAdapter implements JsonSerializer<Level>, JsonDeserializer<Level> {
	
	private ViewController controller;
	
	public LevelGsonTypeAdapter(ViewController c) {
		controller = c;
	}

	@Override
	public JsonElement serialize (Level lev, Type src, JsonSerializationContext context) {
		Gson gson = new Gson();
		JsonObject jsonObj = new JsonObject();

		// Add non-JGObjects to JsonObject
		jsonObj.add(Constant.levelNormalParamsTag, gson.toJsonTree(lev));

		// Convert Level map & add GameObjectDefitionMap to JsonObject
		Map<JGPoint, GameObjectDef> map = lev.getMap();
		jsonObj.add(Constant.levelJGObjectParamsTag + "JGPoint", gson.toJsonTree(map.keySet().toArray(), JGPoint[].class));
		
		for (JGPoint p : map.keySet()) {
			GameObjectDef l = map.get(p);
			jsonObj.add(Constant.levelJGObjectParamsTag + p.toString() + "value", context.serialize(l, l.getClass()));
			jsonObj.add(Constant.levelJGObjectParamsTag + p.toString() + "class", gson.toJsonTree(l.getClass().getCanonicalName()));
		}
		
		// Store Objectives
		Object[] objs = lev.getAllObjectives();
		String[] names = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			Objective obj = (Objective)objs[i];
			names[i] = obj.getName();
			jsonObj.add(Constant.objectiveTag + names[i], gson.toJsonTree(obj.getAllArguments()));
		}
		jsonObj.add("Objective Name", gson.toJsonTree(names));
		
		return jsonObj;
	}

	@Override
	public Level deserialize (JsonElement json, Type srcType, JsonDeserializationContext context)
			throws JsonParseException {
		Gson gson = new Gson();
		JsonObject obj = json.getAsJsonObject();

		// Retrieve everything except objects
		JsonElement params = obj.get(Constant.levelNormalParamsTag);
		Level temp = gson.fromJson(params, Level.class);
		Assert.assertNotNull(temp);
		Assert.assertNotNull(controller);
		
		// recover grids
		Level level = controller.newLevel(temp.getGameName(), temp.getLevelName(), temp.getRows(), temp.getCols(), temp.getTileSize(), temp.getDifficulty());
		
		// Retrieve DefinitionJsonMap
		JGPoint[] points = gson.fromJson(obj.get(Constant.levelJGObjectParamsTag + "JGPoint"), JGPoint[].class);
		
		for (JGPoint p : points) {
			try {
				Class<?> type = Class.forName(gson.fromJson(obj.get(Constant.levelJGObjectParamsTag + p.toString() + "class"), String.class));
				Constant.LOG(this.getClass(), type.getCanonicalName());
				GameObjectDef def = (GameObjectDef) context.deserialize(obj.get(Constant.levelJGObjectParamsTag + p.toString() + "value"), type);
				controller.makeObject(def);
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
		}
		
		String[] names = gson.fromJson(obj.get("Objective Name"), String[].class);
		
		for (String objective : names) {
			Type listType = new TypeToken<List<Integer>>(){}.getType();  
			List<Integer> args = gson.fromJson(obj.get(Constant.objectiveTag + objective), listType);
			controller.setObjective(objective, args);
		}
		return level;
	}

}
