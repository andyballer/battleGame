package display.objectSelector;

import java.awt.Dimension;

import objects.definitions.AIUnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Handles updating the display for object creation
 * or actually creating the new object when the selected
 * type isn't one of the ones earlier in the Chain of
 * Responsibility.  Essentially, I put this at the end
 * as a catch-all
 * @author Teddy
 *
 */
public class OtherHandler extends ObjectSelectorHandler {

	public void handleLayout(String mainSelected, GameObject currentObject,
			ImageListView mainSelector, Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector) {
		mainSelector.getPanel().setPreferredSize(new Dimension(Constant.listSelectorMainWidthSmaller, Constant.listSelectorMainHeight));
		objectDefinePanel.addSetting(Constant.objectNameTag, String.class, "", 
				currentObject == null ? "" : currentObject.getUnitName(), true);
	}

	public void handleCreation(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, AIUnitDef info, 
			ViewController controller) {
		controller.newObject(info.name, info.collisionID, info.team, 
				info.jGameGraphicPath, null, null, mainSelected, 
				null, null, null, info.objName, null);
	}

}
