package display.objectSelector;

import objects.definitions.AIUnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Processes updating the display or creating a new
 * object based on user information.  Does some common
 * work itself, and then delegates to the head of the
 * chain of responsibility so that the event actually
 * gets handled.
 * @author Teddy
 *
 */
public class SelectorProcessor {

	/**
	 * The start of the chain of responsibility.
	 */
	private ObjectSelectorHandler myHead;

	/**
	 * The end of the chain of responsibility.
	 */
	private ObjectSelectorHandler myPreviousHandler;

	public SelectorProcessor (ObjectSelectorHandler head) {
		myHead = head;
		myPreviousHandler = head;
	}

	/**
	 * Adds a new handler to the end of the chain.
	 * @param handler the handler to add
	 */
	public void addHandler(ObjectSelectorHandler handler){
		if(myPreviousHandler != null) {
			myPreviousHandler.setSuccessor(handler);
		}
		myPreviousHandler = handler;
	}

	/**
	 * Delegates to the chain of responsibility to
	 * update the layout of the ObjectSelectorView based
	 * on the type of object the user has chosen to create.
	 * @param mainSelected the type of object the user wants to place
	 * @param currentObject the currently selected object, if there is one
	 * @param mainSelector the section of the view that lets the user choose
	 * a type of object to place
	 * @param objectDefinePanel the panel that the user defines all the
	 * properties of the object in
	 * @param strategyEdit the panel that the user defines an AI unit's
	 * strategy in.
	 * @param unit a unit containing information to be passed along
	 * @param actionSelector a list that lets the user choose which
	 * actions an item object can perform
	 * @param itemSelector a list of items the user can choose to give
	 * to any newly created Unit
	 * @param weaponSelector a list of weapons the user can choose to place
	 */
	public void createLayout(final String mainSelected, GameObject currentObject,
			ImageListView mainSelector, Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector){
		objectDefinePanel.clearPanel();
		strategyEdit.clearPanel();
		myHead.handleLayout(mainSelected, currentObject, mainSelector, objectDefinePanel, 
				strategyEdit, unit, actionSelector, itemSelector, weaponSelector);
	}

	/**
	 * Extracts information that
	 * the user provided that is common to all object types.
	 * Then, delegates to the chain of responsibility for creation
	 * of a new instance of the object based on the information
	 * provided by the user.
	 *
	 * @param mainSelected the type of object being created.
	 * @param objectDefinePanel the panel that the user defined all the
	 * properties of the object in
	 * @param itemSelector a list of items the user might have used
	 * to choose which to give to a new Unit
	 * @param strategyEdit a panel the user might have used to determine
	 * an AIUnit's strategy
	 * @param actionSelector a list the user might have used to choose
	 * which actions an item object could perform
	 * @param weaponSelector a list of weapons the user might have used
	 * to choose which to place
	 * @param controller the controller for the game being played
	 */
	public void createObject(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, ViewController controller) {

		String name = (String)objectDefinePanel.getCurrValue(Constant.objectNameTag);
		String imageName = (String)objectDefinePanel.getCurrValue(Constant.imageChooserTag);
		int collisionId = Constant.nameToCollisionID.get(imageName);
		int team = Constant.nameToTeamID.get(imageName);
		String graphic = (String) objectDefinePanel.getCurrValue(Constant.imageSelectedPathTag);
		//Constant.LOG(this.getClass(), imageName + " " + graphic);

		AIUnitDef info = new AIUnitDef(collisionId, name, graphic, team, 0, 0, 0, 0, null, null, mainSelected, null, null, null, null, imageName, null);

		myHead.handleCreation(mainSelected, objectDefinePanel, itemSelector, strategyEdit, actionSelector, weaponSelector, info, controller);
	}

}
