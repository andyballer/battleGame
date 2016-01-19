package saveLoad;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import display.Constant;
import engine.Game;
import engine.Level;
import engine.ViewController;

public class GameGsonTypeAdapter implements JsonSerializer<Game>, JsonDeserializer<Game> {
	
	private ViewController controller;
	
	public GameGsonTypeAdapter(ViewController c) {
		controller = c;
	}

	@Override
	public Game deserialize(JsonElement json, Type arg1, JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		String[] names = context.deserialize(obj.get(Constant.levelNamesTag), String[].class);
		for (String name : names)
			context.deserialize(obj.get(name), Level.class);
		return controller.getLevelManager().getCurrGame();
	}

	@Override
	public JsonElement serialize(Game game, Type type, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		Map<String, Level> g = game.getLevels();
		obj.add(Constant.levelNamesTag, context.serialize(g.keySet().toArray(), String[].class));
		for (String name : g.keySet())
			obj.add(name, context.serialize(g.get(name), Level.class));
		
		return obj;
	}

}
