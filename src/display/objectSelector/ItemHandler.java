package display.objectSelector;

import java.awt.Dimension;
import java.util.List;

import objects.definitions.AIUnitDef;
import objects.objects.GameObject;
import objects.objects.PickupObject;
import objects.objects.StatSheet;
import objects.objects.Unit;
import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Handles updating the display for item creation
 * or actually creating the new item.
 * @author Teddy
 *
 */
public class ItemHandler extends ObjectSelectorHandler {

	public void handleLayout(String mainSelected, GameObject currentObject,
			ImageListView mainSelector, final Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, final ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector) {
		if (mainSelected.equals(Constant.weaponTypeTag) || mainSelected.equals(Constant.itemTypeTag)) {
			PickupObject pickup = null;
			if (currentObject != null && currentObject.getType().equals(mainSelected))
				pickup = (PickupObject)currentObject;	
			
			mainSelector.getPanel().setPreferredSize(new Dimension(Constant.listSelectorMainWidthSmaller, Constant.listSelectorMainHeightLarger));
			objectDefinePanel.addSetting(Constant.objectNameTag, String.class, "", 
				pickup == null ? "" : pickup.getUnitName(), true);
			
			objectDefinePanel.addSetting(Constant.initialHealthTag, Double.class, Constant.boostPercentageTag, 
				pickup == null ? Constant.defaultHealthLevelUp : 
											pickup.getBuffs().getMaxHealth(), true);
			
			objectDefinePanel.addSetting(Constant.currentHealthTag, Double.class, Constant.boostPercentageTag, 
					pickup == null ? Constant.defaultHealthLevelUp : 
											pickup.getBuffs().getCurrentHealth(), true);
				
			objectDefinePanel.addSetting(Constant.initialPowerTag, Double.class, Constant.boostPercentageTag, 
				pickup == null ? Constant.defaultPowerLevelUp : 
											pickup.getBuffs().getPower(), true);
			
			objectDefinePanel.addSetting(Constant.initialDefenseTag, Double.class, Constant.boostPercentageTag, 
				pickup == null ? Constant.defaultDefenseLevelUp : 
											pickup.getBuffs().getDefense(), true);
			
			objectDefinePanel.addSetting(Constant.initialSpeedTag, Double.class, Constant.boostPercentageTag, 
				pickup == null ? Constant.defaultSpeedLevelUp : 
											pickup.getBuffs().getSpeed(), true);
			
			objectDefinePanel.addSetting(Constant.initialRangeTag, Double.class, Constant.boostPercentageTag, 
				pickup == null ? Constant.defaultRangeLevelUp : 
											pickup.getBuffs().getRange(), true);
			
			
			actionSelector.addImages(Constant.actionSelectionTag);
			
			if (pickup != null) 
				actionSelector.setSelections(pickup.getActions(), true);
			
			if (!actionSelector.getPanel().isVisible()) actionSelector.getPanel().setVisible(true);
		}
		else {
			mySuccessor.handleLayout(mainSelected, currentObject, mainSelector, 
					objectDefinePanel, strategyEdit, unit, actionSelector,
					itemSelector, weaponSelector);
		}
		
	}

	public void handleCreation(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, AIUnitDef info, 
			ViewController controller) {
		
		if(mainSelected.equals(Constant.itemTypeTag) || mainSelected.equals(Constant.weaponTypeTag)) {
			Double h = (Double)objectDefinePanel.getCurrValue(Constant.initialHealthTag + Constant.boostPercentageTag);
			Double c = (Double)objectDefinePanel.getCurrValue(Constant.currentHealthTag + Constant.boostPercentageTag);
			Double d = (Double)objectDefinePanel.getCurrValue(Constant.initialDefenseTag + Constant.boostPercentageTag);
			Double p = (Double)objectDefinePanel.getCurrValue(Constant.initialPowerTag + Constant.boostPercentageTag);
			Double s = (Double)objectDefinePanel.getCurrValue(Constant.initialSpeedTag + Constant.boostPercentageTag);
			Double r = (Double)objectDefinePanel.getCurrValue(Constant.initialRangeTag + Constant.boostPercentageTag);
			
			StatSheet updates = new StatSheet(h, c, p, d, s, r);
			
			List<String> actions = actionSelector.getSelections();
			
			controller.newObject(info.name, info.collisionID, info.team, 
					info.jGameGraphicPath, info.stats, updates, mainSelected, 
					null, null, actions, info.objName, null);
		} else {
			mySuccessor.handleCreation(mainSelected, objectDefinePanel,
					itemSelector, strategyEdit, actionSelector, weaponSelector, 
					info, controller);
		}
	}

}
