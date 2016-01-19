package display.objectSelector;

import objects.definitions.AIUnitDef;
import objects.objects.GameObject;
import objects.objects.Unit;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Super class for all of the handlers.
 * Either handles updating the display in the ObjectSelectorView
 * or handles creating a new instance of an object.  If the specific
 * handler is right for the situation, it updates/creates the display/object.
 * If not, it passes all the necessary information onto its successor.
 *
 * @author Teddy
 */
public abstract class ObjectSelectorHandler {

	/**
	 * The next ObjectSelectorHandler in the chain of
	 * responsibility -- if this handler can't handle the request,
	 * it sends the request onto its successor.
	 */
	protected ObjectSelectorHandler mySuccessor;

	/**
	 * changes the successor.
	 * @param successor the new successor.
	 */
	public final void setSuccessor(final ObjectSelectorHandler successor) {
		mySuccessor = successor;
	}

	/**
	 * Updates the layout of the ObjectSelectorView.
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
	public abstract void handleLayout(String mainSelected, GameObject currentObject,
			ImageListView mainSelector, Layout objectDefinePanel,
			Layout strategyEdit, Unit unit, ImageListView actionSelector,
			ImageTableView itemSelector, ImageListView weaponSelector);

	/**
	 * Deals with creating a new instance of a unit after
	 * the user has entered all necessary information.
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
	 * @param info an AIUnitDef that stores info that is sometimes passed
	 * from one handler to the next so we don't have to recalculate anything
	 * @param controller the controller for the game being played
	 */
	public abstract void handleCreation(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, AIUnitDef info,
			ViewController controller);
}
