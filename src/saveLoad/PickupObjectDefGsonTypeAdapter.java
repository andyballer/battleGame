package saveLoad;

import java.lang.reflect.Type;

import objects.definitions.PickupObjectDef;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import display.Constant;

public class PickupObjectDefGsonTypeAdapter implements JsonSerializer<PickupObjectDef>, JsonDeserializer<PickupObjectDef> {

	@Override
	public PickupObjectDef deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		Gson gson = new Gson();
		JsonObject obj = json.getAsJsonObject();
		return gson.fromJson(obj.get(Constant.pickUpTag), PickupObjectDef.class);
	}

	@Override
	public JsonElement serialize(PickupObjectDef obj, Type type,
			JsonSerializationContext context) {
		Gson gson = new Gson();
		JsonObject jsonObj = new JsonObject();
		jsonObj.add(Constant.pickUpTag, gson.toJsonTree(obj, PickupObjectDef.class));
		return jsonObj;
	}

}
