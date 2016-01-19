package display.objectSelector;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import display.Constant;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.Layout;
import objects.objects.GameObject;
import engine.ResourceLoader;
import engine.ViewController;

/**
 * The object selector is how the user selects
 * the properties of any object when it is placed
 * on the map.  Try running main and clicking on
 * any of the grid squares after creating a blank
 * level -- you're looking at the object selector.
 *
 * This is the the swing/gui part of the object selector,
 * everything else in this package is backend.
 *
 * @author Xin
 *
 */
@SuppressWarnings("serial")
public class ObjectSelectorView extends JFrame {

	private ViewController controller = null;
	private ImageListView mainSelector = null;
	private Layout objectDefinePanel = null;
	private ImageTableView itemSelector = null;
	private ImageListView weaponSelector = null;
	private ImageListView actionSelector = null;
	private Layout strategyEdit = null;

	private GameObject currentObject = null;
	private String mainSelected = null;

	public ObjectSelectorView(String s, int x, int y, ViewController _controller) {
		controller = _controller;
		manageLayout();
		setTitle(s);		// title
		setSize(x,y);		// size
		setLocation(Constant.listSelectorLocationX, Constant.listSelectorLocationY);
		setDefaultCloseOperation(HIDE_ON_CLOSE); 		// close enabled
		setVisible(false);
		setUndecorated(true);
		pack();
	}

	protected void manageLayout() {
		Insets border = new Insets(Constant.upperBorder, Constant.leftBorder, Constant.bottomBorder, Constant.rightBorder);

		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.X_AXIS));
		add(basic);

		mainSelector = new ImageListView(Constant.listShowMainTag, new EmptyBorder(border), 
				new Dimension(Constant.listSelectorMainWidth, Constant.listSelectorMainHeight), controller, false, false, false);
		
		objectDefinePanel = new Layout(true, true, true) {
			
			@Override
			public boolean checkValidInput() {
				if (!super.checkValidInput()) return false;
				if (objectDefinePanel.getCurrValue(Constant.objectNameTag) != null) {
					String name = (String) objectDefinePanel.getCurrValue(Constant.objectNameTag);
					if (name.equals("") || name.equals(" ")) return false;
				}
				if (objectDefinePanel.getCurrValue(Constant.initialHealthTag) != null) {
					Integer val = (Integer)objectDefinePanel.getCurrValue(Constant.initialHealthTag);
					if (val < 0) return false;
				}
				if (objectDefinePanel.getCurrValue(Constant.initialDefenseTag) != null) {
					Integer val = (Integer)objectDefinePanel.getCurrValue(Constant.initialDefenseTag);
					if (val < 0) return false;
				}
				if (objectDefinePanel.getCurrValue(Constant.initialPowerTag) != null) {
					Integer val = (Integer)objectDefinePanel.getCurrValue(Constant.initialPowerTag);
					if (val < 0) return false;
				}
				if (objectDefinePanel.getCurrValue(Constant.initialRangeTag) != null) {
					Integer val = (Integer)objectDefinePanel.getCurrValue(Constant.initialRangeTag);
					if (val < 0) return false;
				}
				if (objectDefinePanel.getCurrValue(Constant.initialSpeedTag) != null) {
					Integer val = (Integer)objectDefinePanel.getCurrValue(Constant.initialSpeedTag);
					if (val < 0) return false;
				}
				if(strategyEdit.getCurrValue(Constant.AIintelligenceTag) != null) {
					Integer val = (Integer)strategyEdit.getCurrValue(Constant.AIintelligenceTag);
					if (val < 0 || val > Constant.maxAIIntelligence) return false;
				}
				return true;
			}
		};
		
		objectDefinePanel.addButton(Constant.okTag, new OKAction());
		objectDefinePanel.addButton(Constant.cancelTag, new CancelAction());
		objectDefinePanel.addButton(Constant.deleteTag, new DeleteAction());
		objectDefinePanel.getPanel().setVisible(false);
		
		itemSelector = new ImageTableView("", new EmptyBorder(border), 
				new Dimension(Constant.listSelectorWidth, Constant.listSelectorHeight), true, true, false);
		
		weaponSelector = new ImageListView("", new EmptyBorder(border), new Dimension(Constant.listSelectorWidth, Constant.listSelectorHeight), 
				controller, true, true, false);
		
		actionSelector = new ImageListView(Constant.actionTypeTag, new EmptyBorder(border), new Dimension(Constant.listSelectorWidth, Constant.listSelectorHeight), 
				controller, true, true, false);
		actionSelector.getPanel().setVisible(false);
	
		strategyEdit = new Layout(true, true, false);
		strategyEdit.setVisible(false);
		
		basic.add(mainSelector.getPanel());
		JPanel p = new JPanel(new FlowLayout());
		p.add(objectDefinePanel.getPanel());
		basic.add(p);
		basic.add(actionSelector.getPanel());
		p = new JPanel(new FlowLayout());
		p.add(strategyEdit.getPanel());
		basic.add(p);
	}
	
	/** add multiple images into specific selector
	 * 
	 * @param category mainSelector/secondarySelector/skillSelector
	 * @param picNames a list of picture names
	 * @param fileNames a list of file paths
	 * @param action action for this selector
	 */
	public void addImages(String category, String tag) {
		if (category.equals(Constant.listShowMainTag)) {
			mainSelector.addImages(tag);
		} else if (category.equals(Constant.listShowItemTag)) {
			itemSelector.clearAll();
			itemSelector.addImages(tag);
		} else if (category.equals(Constant.listShowWeaponTag)) {
			weaponSelector.clearAll();
			weaponSelector.addImages(tag);
		} else 
			Constant.LOG(this.getClass(), "Unknown Category");
	}
	
	public void setMainSelectorListener(MouseAdapter action) {
		mainSelector.setMouseClickListener(action);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		mainSelector.clearSelection();
	}

	/**
	 * update the contents of panel used to describe new object.
	 * @param type the type of object the user wants to place
	 * @param namesToPaths maps the names of objects to their image paths
	 */
	public void updateImageSelectorPanel(String type, Map<String, String> namesToPaths) {
		mainSelected = type;
		mainSelector.setSelected(mainSelected, true);
		objectDefinePanel.clearPanel();
		actionSelector.clearContents();
		actionSelector.getPanel().setVisible(false);
		strategyEdit.setVisible(false);
		
		DefaultSelectorProtocol selector = new DefaultSelectorProtocol();
		selector.createLayout(mainSelected, currentObject, 
				mainSelector, objectDefinePanel, strategyEdit, 
				null, actionSelector, itemSelector, weaponSelector);
		
		objectDefinePanel.remove(Constant.imageChooserTag);
		objectDefinePanel.addImageSelector(Constant.imageChooserTag, namesToPaths, currentObject == null ? null : currentObject.getGraphicPath());
		objectDefinePanel.setImageSelectorSelectionAction(Constant.imageChooserTag, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (currentObject != null && !currentObject.getObjName().equals(objectDefinePanel.getCurrValue(Constant.imageChooserTag))) 
					currentObject = null;
				updateActionSelections(currentObject);
			}
		});
		updateActionSelections(currentObject);
		if (!objectDefinePanel.getPanel().isVisible()) objectDefinePanel.getPanel().setVisible(true);
		pack();
	}
	
	private void updateActionSelections(GameObject obj) {
		if (obj != null) return;
		actionSelector.clearSelection();
		Map<String, String> defaultActions = ResourceLoader.load(Constant.defaultItemActionTag, Constant.commonPropertyFile);
		Constant.LOG(this.getClass(), "TRIGGERED: " + objectDefinePanel.getCurrValue(Constant.imageChooserTag));
		if (defaultActions.containsKey(objectDefinePanel.getCurrValue(Constant.imageChooserTag)))
			actionSelector.setSelected(defaultActions.get(objectDefinePanel.getCurrValue(Constant.imageChooserTag)), true);
	}
	
	/**
	 * get the selection on main category
	 * @return the name of the selected item in JList
	 */	
	public String getMainCategorySelection() {
		JPanel p = (JPanel) mainSelector.getSelectionContent();
		mainSelected = p.getName();
		Constant.LOG(this.getClass(), mainSelected);
		return mainSelected;
	}

	public void restore(GameObject obj) {
		currentObject = obj;
		mainSelected = currentObject.getType();
		Constant.LOG(this.getClass(), "Class: " + obj.getType() + " " + obj.getUnitName());
		Map<String, String> namesToPaths = ResourceLoader.load(obj.getType(), Constant.imagePropertyFile);
    	addImages(Constant.listShowItemTag, Constant.itemTypeTag);
    	addImages(Constant.listShowWeaponTag, Constant.weaponTypeTag);
    	updateImageSelectorPanel(currentObject.getType(), namesToPaths);
	}

	/**
	 * Exits the ObjectSelectorView and returns the focus
	 * to the main authoring environment view.
	 */
	public final void focusBackToMainView() {
		setVisible(false);
		mainSelected = null;
		currentObject = null;
		objectDefinePanel.setVisible(false);
		actionSelector.setVisible(false);
		strategyEdit.setVisible(false);
		objectDefinePanel.clean();
		controller.focusBackToMainView();
		controller.setJGameEngineEnabled(true);
	}

	/**
	 * Attempts to create an object when the user presses OK.
	 * Starts the Chain of Responsibility discussed in package-info.
	 */
	private class OKAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (!objectDefinePanel.checkValidInput()) {
				JOptionPane.showMessageDialog(ObjectSelectorView.this, Constant.invalidTag);
				return;
			}

			DefaultSelectorProtocol selector = new DefaultSelectorProtocol();
			selector.createObject(mainSelected, objectDefinePanel, itemSelector,
					strategyEdit, actionSelector, weaponSelector, controller);

			focusBackToMainView();
		}
	}

	/**
	 * Allows the user to cancel object placement.
	 */
	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			focusBackToMainView();
		}
	}

	/**
	 * Allows the user to delete a previously placed object.
	 */
	private class DeleteAction implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			controller.deleteObject();
			focusBackToMainView();
		}
	}
}
