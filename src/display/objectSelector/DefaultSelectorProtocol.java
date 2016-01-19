package display.objectSelector;

import objects.objects.GameObject;
import objects.objects.Unit;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import engine.ViewController;

/**
 * Defines the order in which the Handlers should attempt
 * to handle updating the layout of the ObjectSelectorView
 * and creating a new instance of an object.
 *
 * In this case, information is passed in the following order:
 * PlayerHandler>EnemyHandler>ItemHandler>OtherHandler
 *
 * (the display checks if the user is creating a player,
 * and if not, checks if the user is creating an enemy,
 * and if not, checks if the user is creating an item,
 * and if not, the OtherHandler figures stuff out)
 *
 * In theory, one could define a new order
 * without changing any of the processor/handler code
 * by writing a new protocol class.
 *
 * @author Teddy
 *
 */
public class DefaultSelectorProtocol {

	/**
	 * The processor that stores all of the
	 * different handlers.
	 */
	private SelectorProcessor processor;

	public DefaultSelectorProtocol() {
		createProcessor();
	}

	/**
	 * Creates the processor and defines the order of handlers.
	 */
	private void createProcessor() {
		processor = new SelectorProcessor(new PlayerHandler());
		processor.addHandler(new EnemyHandler());
		processor.addHandler(new ItemHandler());
		processor.addHandler(new OtherHandler());
	}

	public void createLayout(String mainSelected, GameObject currentObject, 
			ImageListView mainSelector, Layout objectDefinePanel, 
			Layout strategyEdit, Unit unit, ImageListView actionSelector, 
			ImageTableView itemSelector, ImageListView weaponSelector) {
		processor.createLayout(mainSelected, currentObject, mainSelector, objectDefinePanel, 
			strategyEdit, unit, actionSelector, itemSelector, weaponSelector);
	}

	public void createObject(String mainSelected, Layout objectDefinePanel,
			ImageTableView itemSelector, Layout strategyEdit,
			ImageListView actionSelector, ImageListView weaponSelector, ViewController controller){
		processor.createObject(mainSelected, objectDefinePanel, itemSelector, 
				strategyEdit, actionSelector, weaponSelector, controller);
	}
}
