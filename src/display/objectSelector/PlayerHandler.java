package display.objectSelector;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import objects.definitions.AIUnitDef;
import objects.definitions.PickupObjectDef;
import objects.objects.GameObject;
import objects.objects.StatSheet;
import objects.objects.Unit;
import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ResourceLoader;
import engine.ViewController;

/**
 * Handles updating the display for player unit creation
 * or actually creating the new player units.
 * @author Teddy
 *
 */
public class PlayerHandler extends ObjectSelectorHandler {

	public void handleLayout(String mainSelected, GameObject currentObject,
			ImageListView mainSelector, Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector) {
		
		if(mainSelected.equals(Constant.playerTypeTag) || mainSelected.equals(Constant.enemyTypeTag)) {
			unit = null;
			if (currentObject != null && currentObject.getType().equals(mainSelected))
				unit = (Unit) currentObject;
			
			mainSelector.getPanel().setPreferredSize(new Dimension(Constant.listSelectorMainWidthSmaller, Constant.listSelectorMainHeightLarger));
			objectDefinePanel.addSetting(Constant.objectNameTag, String.class, "", 
				unit == null ? "" : unit.getUnitName(), true);
			
			objectDefinePanel.addSetting(Constant.initialHealthTag, Integer.class, Constant.initialValueTag, 
				unit == null ? Constant.defaultHealth : unit.getStats().getMaxHealth().intValue(), true);
			objectDefinePanel.addSetting(Constant.initialHealthTag, Double.class, Constant.levelUpPercentageTag, 
				unit == null ? Constant.defaultHealthLevelUp : unit.getGrowths().getMaxHealth(), true);
			
			objectDefinePanel.addSetting(Constant.initialPowerTag, Integer.class, Constant.initialValueTag, 
				unit == null ? Constant.defaultPower : unit.getStats().getPower().intValue(), true);
			objectDefinePanel.addSetting(Constant.initialPowerTag, Double.class, Constant.levelUpPercentageTag, 
				unit == null ? Constant.defaultPowerLevelUp : unit.getGrowths().getPower(), true);
			
			objectDefinePanel.addSetting(Constant.initialDefenseTag, Integer.class, Constant.initialValueTag, 
				unit == null ? Constant.defaultDefense : unit.getStats().getDefense().intValue(), true);
			objectDefinePanel.addSetting(Constant.initialDefenseTag, Double.class, Constant.levelUpPercentageTag, 
				unit == null ? Constant.defaultDefenseLevelUp : unit.getGrowths().getDefense(), true);
			
			objectDefinePanel.addSetting(Constant.initialSpeedTag, Integer.class, Constant.initialValueTag, 
				unit == null ? Constant.defaultSpeed : unit.getStats().getSpeed().intValue(), true);
			objectDefinePanel.addSetting(Constant.initialSpeedTag, Double.class, Constant.levelUpPercentageTag, 
				unit == null ? Constant.defaultSpeedLevelUp : unit.getGrowths().getSpeed(), true);
			
			objectDefinePanel.addSetting(Constant.initialRangeTag, Integer.class, Constant.initialValueTag, 
				unit == null ? Constant.defaultRange : unit.getStats().getRange().intValue(), true);
			objectDefinePanel.addSetting(Constant.initialRangeTag, Double.class, Constant.levelUpPercentageTag, 
				unit == null ? Constant.defaultRangeLevelUp : unit.getGrowths().getRange(), true);
			objectDefinePanel.addSetting(Constant.listShowItemTag, itemSelector.getPanel(), ""); 
			objectDefinePanel.addSetting(Constant.listShowWeaponTag, weaponSelector.getPanel(), "");
		
			
			if (unit != null) {
				Map<PickupObjectDef, Integer> inv = unit.getInventory();
				Constant.LOG(this.getClass(), "inv here: " + inv.toString());
				Map<String, Integer> temp = new HashMap<String, Integer>();
				for (PickupObjectDef p : inv.keySet())
					temp.put(p.objName, inv.get(p));
				Constant.LOG(this.getClass(), "here: " + temp.toString());
				itemSelector.setSelections(temp);
				
				List<String> names = new ArrayList<String>();
				for (PickupObjectDef p : unit.getWeapons())
					names.add(p.objName);
				
				weaponSelector.setSelections(names, true);
			}
		}
		if(!mainSelected.equals(Constant.playerTypeTag) || mainSelected.equals(Constant.enemyTypeTag)) {
			mySuccessor.handleLayout(mainSelected, currentObject, mainSelector, 
					objectDefinePanel, strategyEdit, unit, actionSelector,
					itemSelector, weaponSelector);
		}
	}

	/**
	 * Tries to create a player Unit.
	 */
	public void handleCreation(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, AIUnitDef info, 
			ViewController controller) {
		if(mainSelected.equals(Constant.playerTypeTag) || mainSelected.equals(Constant.enemyTypeTag)) {
			StatSheet stats = new StatSheet((Integer)objectDefinePanel.getCurrValue(Constant.initialHealthTag + Constant.initialValueTag), 
					  (Integer)objectDefinePanel.getCurrValue(Constant.initialHealthTag + Constant.initialValueTag), 
					  (Integer)objectDefinePanel.getCurrValue(Constant.initialPowerTag + Constant.initialValueTag), 
					  (Integer)objectDefinePanel.getCurrValue(Constant.initialDefenseTag + Constant.initialValueTag), 
					  (Integer)objectDefinePanel.getCurrValue(Constant.initialSpeedTag + Constant.initialValueTag), 
					  (Integer)objectDefinePanel.getCurrValue(Constant.initialRangeTag + Constant.initialValueTag));

			Double h = (Double)objectDefinePanel.getCurrValue(Constant.initialHealthTag + Constant.levelUpPercentageTag);
			Double d = (Double)objectDefinePanel.getCurrValue(Constant.initialDefenseTag + Constant.levelUpPercentageTag);
			Double p = (Double)objectDefinePanel.getCurrValue(Constant.initialPowerTag + Constant.levelUpPercentageTag);
			Double s = (Double)objectDefinePanel.getCurrValue(Constant.initialSpeedTag + Constant.levelUpPercentageTag);
			Double r = (Double)objectDefinePanel.getCurrValue(Constant.initialRangeTag + Constant.levelUpPercentageTag);
			
			StatSheet updates = new StatSheet(h, h, p, d, s, r);
			
			Map<String,String> nameToActions = ResourceLoader.load(Constant.defaultItemActionTag, Constant.commonPropertyFile);
			// inventoryNames store the objName -> Number pairs
			// need convert them into PickUpObjectDef -> Number pairs
			Map<String, Integer> inventoryNames = itemSelector.getSelections();
			Constant.LOG(this.getClass(), inventoryNames.toString());
			Map<PickupObjectDef, Integer> inventory = new HashMap<PickupObjectDef, Integer>();
			for (String obj : inventoryNames.keySet()) {
				if (nameToActions.containsKey(obj)) {
					List<String> a = new ArrayList<String>();
					a.add(nameToActions.get(obj));
					inventory.put(new PickupObjectDef(Constant.envCollisionID, obj, "", -1, -1, -1, -1, Constant.itemTypeTag, 
						new StatSheet(0.0, 0.2, 0.1, 0.1, 0.1, 0.1), "", false, a, obj), inventoryNames.get(obj));
				}
			}
			
			List<String> weaponNames = weaponSelector.getSelections();
			Set<PickupObjectDef> weapons = new HashSet<PickupObjectDef>();
			for (String obj : weaponNames) {
				if (nameToActions.containsKey(obj)) {
					List<String> a = new ArrayList<String>();
					a.add(nameToActions.get(obj));
					weapons.add(new PickupObjectDef(Constant.envCollisionID, obj, "", -1, -1, -1, -1, Constant.weaponTypeTag, 
						new StatSheet(0.0, 0.0, 0.2, 0.2, 0.2, 0.2), "", false, a, obj));
				}
			}

			info = new AIUnitDef(info.collisionID, info.name, info.jGameGraphicPath, info.team, 
					0,0,0,0, stats, updates, mainSelected, null, 
					inventory, weapons, null, info.objName, null);

			if(!mainSelected.equals(Constant.enemyTypeTag)) {
				controller.newObject(info.name, info.collisionID, info.team, 
						info.jGameGraphicPath, stats, updates, mainSelected, 
						inventory, weapons, null, info.objName, null);
			}
		}
		if(!mainSelected.equals(Constant.playerTypeTag) || mainSelected.equals(Constant.enemyTypeTag)) {
			mySuccessor.handleCreation(mainSelected, objectDefinePanel,
					itemSelector, strategyEdit, actionSelector, weaponSelector,
					info, controller);
		}
	}
}
