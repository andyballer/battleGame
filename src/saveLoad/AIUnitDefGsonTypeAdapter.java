package saveLoad;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import objects.definitions.AIUnitDef;
import objects.definitions.PickupObjectDef;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import display.Constant;

public class AIUnitDefGsonTypeAdapter  implements JsonSerializer<AIUnitDef>, JsonDeserializer<AIUnitDef> {

	@Override
	public AIUnitDef deserialize(JsonElement json, Type arg1, JsonDeserializationContext context) throws JsonParseException {
		Gson gson = new GsonBuilder().create();
		JsonObject obj = json.getAsJsonObject();

		// Retrieve everything except non-primitive types
		JsonElement params = obj.get(Constant.levelNormalParamsTag);
		AIUnitDef temp = gson.fromJson(params, AIUnitDef.class);

		Map<PickupObjectDef, Integer> inv = new ConcurrentHashMap<PickupObjectDef, Integer>();
		PickupObjectDef[] picks = gson.fromJson(obj.get(Constant.inventoryTag), PickupObjectDef[].class);
		for (PickupObjectDef pick : picks) {
			Integer number = gson.fromJson(obj.get(Constant.inventoryTag + "-" + pick.name + "-" + pick.objName), Integer.class);
			inv.put(pick, number);	
		}
		
		Type weaponType = new TypeToken<CopyOnWriteArraySet<PickupObjectDef>>(){}.getType();  
		Set<PickupObjectDef> weapons = context.deserialize(obj.get(Constant.weaponTypeTag), weaponType);
		Constant.LOG(this.getClass(), weapons.toString());
		
		Type actionType = new TypeToken<CopyOnWriteArraySet<String>>(){}.getType();  
		Set<String> actions = context.deserialize(obj.get(Constant.actionTypeTag), actionType);
		Constant.LOG(this.getClass(), actions.toString());
		Constant.LOG(this.getClass(), "inv: " + inv.toString());
		return new AIUnitDef(temp.collisionID, temp.name, temp.jGameGraphicPath, temp.team, temp.x, temp.y, 
				temp.row, temp.col, temp.stats, temp.growths, temp.type, temp.originalGraphicPath, inv, weapons, actions, temp.objName, temp.strategy);
	}

	@Override
	public JsonElement serialize(AIUnitDef udef, Type type, JsonSerializationContext context) {
		Gson gson = new Gson();
		JsonObject jsonObj = new JsonObject();
		
		// add primitive types
		jsonObj.add(Constant.levelNormalParamsTag, gson.toJsonTree(udef, AIUnitDef.class));
		
		// add non-primitive types
		jsonObj.add(Constant.inventoryTag, gson.toJsonTree(udef.inventory.keySet().toArray(), PickupObjectDef[].class));
		for (PickupObjectDef def : udef.inventory.keySet()) {
			jsonObj.add(Constant.inventoryTag + "-" + def.name + "-" + def.objName, gson.toJsonTree(udef.inventory.get(def), Integer.class));
		}
		
		Type weaponType = new TypeToken<Set<PickupObjectDef>>(){}.getType();  
		jsonObj.add(Constant.weaponTypeTag, context.serialize(udef.weapons, weaponType));
		Type actionType = new TypeToken<Set<String>>(){}.getType();
		jsonObj.add(Constant.actionTypeTag, context.serialize(udef.actions, actionType));
		return jsonObj;
	}

}
