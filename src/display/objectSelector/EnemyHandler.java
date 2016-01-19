package display.objectSelector;

import javax.swing.JLabel;

import org.junit.Assert;

import objects.definitions.AIUnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.AIUnit;
import objects.objects.artificialIntelligence.Strategy;
import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Handles updating the display for enemy AIunit creation
 * or actually creating the new enemy AIunits.
 * @author Teddy
 *
 */
public class EnemyHandler extends ObjectSelectorHandler {

	public void handleLayout(String mainSelected, GameObject currentObject,
			ImageListView mainSelector, Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector) {

		if(mainSelected.equals(Constant.enemyTypeTag)) {
			AIUnit ai = null;
			if (unit != null) {
				Assert.assertTrue(unit instanceof AIUnit);
				ai = (AIUnit) unit;
			}
			strategyEdit.setVisible(true);
			strategyEdit.clearPanel();

			strategyEdit.addSetting("", new JLabel(Constant.AIintelligenceHint), "");
			strategyEdit.addSetting(Constant.AIintelligenceTag, Integer.class, "", 
					ai == null ? Constant.defaultAIIntelligence : ai.getStrategy().getIntelligence(), true);
			strategyEdit.addSetting(Constant.AIAttackTag, Integer.class, "",
					ai == null ? Constant.defaultAIAttack : ai.getStrategy().getAttackValue(), true);
			strategyEdit.addSetting(Constant.AIDefenseTag, Integer.class, "",
					ai == null ? Constant.defaultAIDefense : ai.getStrategy().getDefenseValue(), true);
			strategyEdit.addSetting(Constant.AIMoneyTag, Integer.class, "",
					ai == null ? Constant.defaultAIMoney : ai.getStrategy().getMoneyValue(), true);
			strategyEdit.addSetting(Constant.AIObjectiveTag, Integer.class, "",
					ai == null ? Constant.defaultAIObjective : ai.getStrategy().getObjectiveValue(), true);
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
		if (mainSelected.equals(Constant.enemyTypeTag)) {
			Strategy strategy = new Strategy((Integer)strategyEdit.getCurrValue(Constant.AIintelligenceTag), 
									(Integer)strategyEdit.getCurrValue(Constant.AIAttackTag),
									(Integer)strategyEdit.getCurrValue(Constant.AIDefenseTag),
									(Integer)strategyEdit.getCurrValue(Constant.AIMoneyTag),
									(Integer)strategyEdit.getCurrValue(Constant.AIObjectiveTag));

			controller.newObject(info.name, info.collisionID, info.team, 
					info.jGameGraphicPath, info.stats, info.growths, mainSelected, 
					info.inventory, info.weapons, null, info.objName, strategy);
		} else {
			mySuccessor.handleCreation(mainSelected, objectDefinePanel,
					itemSelector, strategyEdit, actionSelector, weaponSelector, 
					info, controller);
		}
	}
}
