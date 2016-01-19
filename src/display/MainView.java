package display;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jgame.JGPoint;
import objects.definitions.GameObjectDef;
import objects.definitions.PickupObjectDef;
import objects.objects.StatSheet;
import objects.objects.Unit;
import objects.objects.artificialIntelligence.Strategy;

import org.junit.Assert;

import saveLoad.GameImageSaver;
import display.jgame.GameEnv;
import display.util.ButtonPanel;
import display.util.CheckListView;
import display.util.FileChooserView;
import display.util.ImageListView;
import display.util.ImageTableView;
import display.util.ListView;
import display.util.MenuBar;
import display.util.SelectorView;
import display.util.CheckListView.CheckListItem;
import engine.Level;
import engine.ResourceLoader;
import engine.ViewController;
import gameplayer.action.BoostAction;
import gameplayer.action.UnitAction;


public class MainView extends JFrame {	

	private static final long serialVersionUID = 8778300297678418463L;

	private ViewController controller;
	
	private ListView goalSelector;
	private CheckListView goalViewer;
	
	private SelectorView levelSelector;
	private SelectorView gameSelector;

	private ButtonPanel buttons;
	private ImageListView objectSelector;
	private GameEnv authorEnv;
	
	private ImageTableView inventorySelector;
	private ImageListView actionSelector;
	
	private PlayerStatsTableView playerStatsView;
	
	private FileChooserView fileChooser;
	
	private MenuBar menu;
	
	private Map<String, KeyStroke> shortcuts;
	
	private boolean isEditable = true;
	private boolean demoMode = false;

	public MainView(String s, int x, int y, ViewController _controller, boolean isEditable) {
		controller = _controller;
		this.isEditable = isEditable;
		setShortcutBasedOnOS();
		customMenu();
		manageLayout(isEditable);
		setTitle(s);		// title
		setSize(x,y);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(EXIT_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		pack();
	}

	private void setShortcutBasedOnOS() {
		shortcuts = new HashMap<String, KeyStroke>();
		String vers = System.getProperty("os.name").toLowerCase();
		
		for (int i = 0; i < Constant.shortcuts.length; i++)
			if (vers.indexOf("windows") != -1 || vers.indexOf("linux") != -1)
				shortcuts.put(Constant.shortcuts[i][0], KeyStroke.getKeyStroke(Constant.shortcuts[i][1].toCharArray()[0], Event.CTRL_MASK));
			else
				shortcuts.put(Constant.shortcuts[i][0], KeyStroke.getKeyStroke(Constant.shortcuts[i][1].toCharArray()[0], 
																			   Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	protected void manageLayout(boolean isEditable) {

		Insets border = new Insets(Constant.upperBorder, Constant.leftBorder, Constant.bottomBorder, Constant.rightBorder);

		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.X_AXIS));
		add(basic);

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

		// Add jgame
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(Constant.jgameWindowWidth, Constant.jgameWindowHeight));
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(border));

		authorEnv = new GameEnv(new Dimension(Constant.jgameWindowWidth, Constant.jgameWindowHeight), controller);
		authorEnv.setFocusable(true);
		panel.add(authorEnv);
		left.add(panel);

		// level selector
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		JPanel selectors = new JPanel();
		selectors.setLayout(new BoxLayout(selectors, BoxLayout.X_AXIS));
		
		gameSelector = new SelectorView(new EmptyBorder(border), new GameChooserAction());
		selectors.add(gameSelector.getPanel());
		
		levelSelector = new SelectorView(new EmptyBorder(border), new LevelChooserAction());
		selectors.add(levelSelector.getPanel());

		right.add(selectors);

		goalSelector = new ListView("Objectives", new EmptyBorder(border), new Dimension(Constant.goalWidth, Constant.goalHeight), controller, true, false, false);
		goalSelector.setMouseClickListener(new ModifyObjectiveSettingAction());
		initObjectives();

		right.add(goalSelector.getPanel());

		goalViewer = new CheckListView("Achievements", new EmptyBorder(border), new Dimension(Constant.goalWidth, Constant.goalHeight), controller, true, false, false);
		goalViewer.setMouseClickListener(new ViewObjectiveSettingAction());
		goalViewer.setVisible(false);
		right.add(goalViewer.getPanel());
		
		objectSelector = new ImageListView(Constant.objectSelectName, new EmptyBorder(border), new Dimension(Constant.statusWidth, Constant.statusHeight), 
				controller, false, true, false);
		right.add(objectSelector.getPanel());
		
		inventorySelector = new ImageTableView(Constant.inventorySelectName, new EmptyBorder(border), new Dimension(Constant.statusWidth, Constant.statusHeight), 
				false, true, true);
		inventorySelector.setButtonActionListener(new InventoryBackAction());
		inventorySelector.setListSelectionListener(new UseInventoryAction());
		inventorySelector.setVisible(false);
		right.add(inventorySelector.getPanel());
		
		actionSelector = new ImageListView(Constant.actionSelectName, new EmptyBorder(border), new Dimension(Constant.statusWidth, Constant.statusHeight), 
				controller, false, true, false);
		actionSelector.setListSelectionListener(new ChooseItemActionAction());
		actionSelector.setVisible(false);
		right.add(actionSelector.getPanel());

		// Add the buttons panel
		buttons = new ButtonPanel(new EmptyBorder(border), Constant.buttonsPerCol);
		buttons.addButton("New Game/Level", new NewGameAction());
		buttons.addButton("Play", new PlayAction());
		buttons.addButton("Delete Game", new DeleteGameAction());
		buttons.addButton("Delete Level", new DeleteLevelAction());
		
		//buttons.addButton("Preference", new PreferenceAction());
		buttons.addButton("Save", new SaveFileAction());
		buttons.addButton("Load", new LoadFileAction());
		

		if(isEditable)
			right.add(buttons.getPanel());
		else {
			playerStatsView = new PlayerStatsTableView("Player Statistics", new EmptyBorder(border), new Dimension(Constant.statsWidth, Constant.statsHeight), 
					false, false, false);
			right.add(playerStatsView.getPanel());
		}
			
		basic.add(left);
		basic.add(right);
		
		fileChooser = new FileChooserView();
		try {
			File currDir = new File(new File(".").getCanonicalPath() + Constant.defaultSavePathPrefix);
			fileChooser.setDefaultDirectory(currDir);
			fileChooser.setFileFilter(Constant.jsonFilter, Constant.defaultJsonExtension);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void customMenu(){
		menu = new MenuBar();
		menu.addMenu("File");
		menu.addMenuItem("File", "New Game", new NewGameAction(), shortcuts.get(Constant.newLevelKeyTag));
		menu.addMenuItem("File", "New Level", new NewLevelAction(), shortcuts.get(Constant.newLevelKeyTag));
		menu.addMenuItem("File", "Load File", new LoadFileAction(), shortcuts.get(Constant.loadFileKeyTag));
		menu.addMenuItem("File", "Save File", new SaveFileAction(), shortcuts.get(Constant.saveFileKeyTag));
		menu.addSeparator("File");
		menu.addMenuItem("File", "Global Preference", new PreferenceAction(), shortcuts.get(Constant.prefKeyTag));
		menu.addMenuItem("File", "Customize Controls", new ShowJGamePrefAction(), null);
		menu.addSeparator("File");
		menu.addMenuItem("File", "Exit", new ExitProgramAction(), shortcuts.get(Constant.exitKeyTag));
		menu.addMenu("Edit");
		menu.addMenuItem("Edit", "Undo", new UndoAction(), shortcuts.get(Constant.undoKeyTag));
		menu.addMenuItem("Edit", "Redo", new RedoAction(), shortcuts.get(Constant.redoKeyTag));
		menu.addMenu("Play");
		menu.addMenuItem("Play", "Play All Levels", new PlayAction(), shortcuts.get(Constant.playAllKeyTag));
		menu.addMenuItem("Play", "Play Current Level", new PlayCurrentLevelAction(), shortcuts.get(Constant.playCurrKeyTag));
		menu.addMenu("Help");
		menu.addMenuItem("Help", "How to Play", new GameHelpAction(), shortcuts.get(Constant.helpKeyTag));

		menu.paintMenu(this);
	}
	
	private void initObjectives() {		
		goalSelector.addItem(Constant.goalKillAllTag);
		controller.addObjectiveBinding(Constant.goalKillAllTag, Constant.goalKillAll);
		
		goalSelector.addItem(Constant.goalKillBossTag);
		controller.addObjectiveBinding(Constant.goalKillBossTag, Constant.goalKillBoss);
		
		goalSelector.addItem(Constant.goalCapturePointTag);
		controller.addObjectiveBinding(Constant.goalCapturePointTag, Constant.goalCapturePoint);
		
		goalSelector.addItem(Constant.goalObjectsTag);
		controller.addObjectiveBinding(Constant.goalObjectsTag, Constant.goalObjects);
		
		goalSelector.addItem(Constant.goalSurviveTag);
		controller.addObjectiveBinding(Constant.goalSurviveTag, Constant.goalSurvive);
	}
	
	public void startPlayMode() {
		goalSelector.setVisible(false);
		goalViewer.setVisible(true);
		goalViewer.clearContents();
		Constant.LOG(this.getClass(), "Array: " + controller.getCurrLevel().getAllObjectivesName());
		goalViewer.addItems(controller.getCurrLevel().getAllObjectivesName());
		
		gameSelector.setEnabled(false);
		levelSelector.setEnabled(false);
		
		buttons.enableButton("New Game/Level", false);
		buttons.enableButton("Delete Game", false);
		buttons.enableButton("Delete Level", false);
		buttons.enableButton("Save", false);
		buttons.enableButton("Load", false);
		
		buttons.changeName("Play", "Edit", new EditAction());
		
		menu.setEnabled("Edit", false);
		menu.setEnabled("File", false);
		menu.setEnabled("Play", false);
		
		controller.saveGame(Constant.defaultSaveFilePath);
		controller.getScoreManager().initGame(controller.getCurrGameName());
		controller.setPlaying(true);
		
		objectSelector.setLabel(Constant.actionSelectName);
		objectSelector.clearAll();
		objectSelector.addImages(Constant.actionTypeTag);
		objectSelector.setMouseClickListener(new TakeActionAction());
		
		if (playerStatsView != null) playerStatsView.setPlayerStats(controller.getCurrLevel());
		
		authorEnv.displayStatus("Start Playing");
	}
	
	public boolean isEditable() {
		return isEditable;
	}
	
	public void updatePlayerStats() {
		if (playerStatsView != null) playerStatsView.updateStats();
	}
	
	public void clearAll() {
		// TODO clear jgame, obj, status
		authorEnv.clearGrid();
		goalSelector.clearSelection();
		goalViewer.clearSelection();
	}
	
	public int getAchievedObjectiveNumber() {
		return goalViewer.getSelections().size();
	}
	
	public void setLevel(Collection<String> levelNames, Level l) {
		gameSelector.setSelected(l.getGameName());
		levelSelector.updateAndSetSelected(levelNames.toArray(), l.getLevelName());
	}
	
	public void setLevel(Collection<String> gameNames, Collection<String> levelNames, Level l) {
		gameSelector.updateAndSetSelected(gameNames.toArray(), l.getGameName());
		levelSelector.updateAndSetSelected(levelNames.toArray(), l.getLevelName());
	}
	
	public GameEnv getGameEnv() {
		return authorEnv;
	}
	
	public void setGoalAchieved(List<String> achieved) {
		for (String name : achieved)
			goalViewer.setSelected(name, true);
	}
	
	public void restoreAll(Level l) {
		authorEnv.makeGrid(l.getRows(), l.getCols(), l.getTileSize());
		goalSelector.addSelections(l.getAllObjectivesName());
		goalViewer.clearContents();
		goalViewer.addItems(controller.getCurrLevel().getAllObjectivesName());
		
		if (playerStatsView != null) playerStatsView.setPlayerStats(controller.getCurrLevel());
	}
	
	public void removeGame(String gameName) {
		gameSelector.removeItem(gameName);
	}
	
	public void setObjective(String obj, Boolean e) {
		goalSelector.setSelected(obj, e);
	}
	
	public boolean isDemo() {
		return demoMode;
	}
	
	public void setJGameEngineEnabled(Boolean b) {
		authorEnv.start();
		authorEnv.requestGameFocus();
	}
	
	public void setObjectSelectorPanelEnabled(boolean b) {
		if (!b) objectSelector.clearSelection();
		objectSelector.setEnabled(b);
	}
	
	public void startEditMode() {
		if (controller.isPlaying()) {
			controller.setJGameEngineSelectable(false);
			goalSelector.setVisible(true);
			goalViewer.setVisible(false);
			objectSelector.setVisible(true);
			inventorySelector.setVisible(false);
			
			gameSelector.setEnabled(true);
			levelSelector.setEnabled(true);
			
			buttons.enableButton("New Game/Level", true);
			buttons.enableButton("Delete Game", true);
			buttons.enableButton("Delete Level", true);
			buttons.enableButton("Save", true);
			buttons.enableButton("Load", true);
			
			buttons.changeName("Edit", "Play", new PlayAction());
			
			menu.setEnabled("Edit", true);
			menu.setEnabled("File", true);
			menu.setEnabled("Play", true);
			
			setJGameEngineEnabled(true);
			setObjectSelectorPanelEnabled(true);
			controller.setPlaying(false);
			
			controller.deleteGame(controller.getCurrGameName(), false);
			controller.loadGame(Constant.defaultSaveFilePath);
		}
		
		objectSelector.clearAll();
		objectSelector.addImages(Constant.playerTypeTag); //player
		objectSelector.addImages(Constant.enemyTypeTag); //enemy
		objectSelector.addImages(Constant.terrainTypeTag); //terrain
		objectSelector.addImages(Constant.itemTypeTag); //item
		objectSelector.addImages(Constant.weaponTypeTag); // weapon
		objectSelector.setMouseClickListener(new AddObjectsAction());
	}
	
	private void showActionList() {
		inventorySelector.setListSelectionValid(false);
		actionSelector.setListSelectionValid(false);
		objectSelector.clearContents();
		objectSelector.addImages(Constant.actionTypeTag);
		inventorySelector.setVisible(false);
		actionSelector.setVisible(false);
		objectSelector.setVisible(true);
	}
	
	private void showInventoryList() {
		inventorySelector.clearAll();
		inventorySelector.addImages(controller.getStateManager().getCurrPlayingUnit().getUsableInventory());
		inventorySelector.setSelections(controller.getStateManager().getCurrPlayingUnit().getUsableInventoryNumber());
		inventorySelector.clearSelections();
		inventorySelector.setListSelectionValid(true);
		inventorySelector.setVisible(true);
		actionSelector.setVisible(false);
		objectSelector.setVisible(false);
	}
	
	private void showItemActionList(PickupObjectDef pick) {
		inventorySelector.setVisible(false);
		objectSelector.setVisible(false);
		actionSelector.clearAll();
		actionSelector.addImages(pick.getActions());
		actionSelector.clearSelection();
		actionSelector.setListSelectionValid(true);
		actionSelector.setVisible(true);
	}
	
	private void applyAction(String objName, Unit source) {
		Map<String, String> commons = ResourceLoader.load(Constant.actionTypeTag, Constant.commonPropertyFile);
    	UnitAction action = controller.getStateManager().getCurrAction();
    	Constant.LOG(this.getClass(), "ACTION: " + (action == null ? "null" : action.getName()) + " "  + objName);
    	if (action != null && action.getName().equals(objName)) {
    		controller.getStateManager().setState(Constant.actionMode, true);
    		authorEnv.setStatShowVisible(true);
    		return;
    	}
		try {
			action = (UnitAction) Class.forName(commons.get(objName)).getConstructor(String.class, ViewController.class)
					.newInstance(objName, controller);
			Set<JGPoint> points = action.getTargetRange(source);

			controller.getStateManager().setState(Constant.actionMode, points, true, action);
			authorEnv.setStatShowVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private class NewGameAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.newLevel();
		}
	}
	
	private class NewLevelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.newLevel();
		}
	}
	
	private class ShowJGamePrefAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			authorEnv.showPrefWindow();
		}
	}
	
	private class EditAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int opt = JOptionPane.showConfirmDialog (MainView.this, "WARNING: After clicking OK, all your previous progress will be discarded", "Start Editing", JOptionPane.OK_CANCEL_OPTION);

			if (opt != JOptionPane.YES_OPTION) 
				return;
			
			startEditMode();
		}
	}
	
	private class PlayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!controller.checkLevelWithObjectives()) {
				JOptionPane.showMessageDialog(MainView.this, "Not All Levels Have Objectives");
				return;
			}
			demoMode = false;
			startPlayMode();
			controller.startPlayMode(0);
		}
	}
	
	private class PlayCurrentLevelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!controller.checkLevelWithObjectives()) {
				JOptionPane.showMessageDialog(MainView.this, "Not All Levels Have Objectives");
				return;
			}
			demoMode = true;
			startPlayMode();
			controller.startPlayMode(controller.getCurrLevelName());
		}
	}
	
	private class SaveFileAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			final String path = fileChooser.saveFile();
			Constant.LOG(this.getClass(), path);
			if (path != null){
				GameImageSaver.saveGameImage(authorEnv, path);
				new Thread() {
					public void run() {
						controller.saveGame(path);
					}
				}.start();	
			}
		}
	}

	private class LoadFileAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			final String path = fileChooser.loadFile();
			Constant.LOG(this.getClass(), path);
			if (path != null) {
				new Thread() {
					public void run() {
						if (!controller.getStateManager().animationIsAlive())
							controller.getStateManager().displayState("LOADING", Constant.defaultWordInterval, 10);
						controller.loadGame(path);
						controller.getStateManager().removeCurrAnimation();
					}
				}.start();
			}
		}
	}
	
	private class GameHelpAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			File htmlFile = new File("oogaHelp.html");
			try {
				Desktop.getDesktop().browse(htmlFile.toURI());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class AddObjectsAction extends MouseAdapter {
	    public void mouseClicked(MouseEvent e) {
	    	if (e.getClickCount() == 1) {
	    		JPanel p = (JPanel) objectSelector.getSelectionContent();
				if (p == null) {
					authorEnv.removeInstantDrawObject();
					return;
				} else {
					String objName = p.getName(); // concrete object name, not type name
					String type = Constant.nameToType.get(objName);
					//System.out.println(type + " " + objName);
					String name = Constant.defaultObjectName;
					
					Map<String,String> nameToImages = ResourceLoader.load(type, Constant.imagePropertyFile);
					String imagePath = nameToImages.get(objName);
					Assert.assertNotNull(imagePath);
					
					int collisionId = Constant.nameToCollisionID.get(objName);
					int team = Constant.nameToTeamID.get(objName);
					StatSheet stats = null, updates = null;
					Map<PickupObjectDef, Integer> inventory = null;
					Set<PickupObjectDef> weapons = null;
					List<String> actions = null;
					Strategy strategy = null;
					Map<String,String> nameToActions = ResourceLoader.load(Constant.defaultItemActionTag, Constant.commonPropertyFile);
					if (type.equals(Constant.playerTypeTag) || type.equals(Constant.enemyTypeTag)) {
						stats = new StatSheet();
						stats.setMaxHealth(Constant.defaultHealth.doubleValue());
						stats.setDefense(Constant.defaultDefense.doubleValue());
						stats.setPower(Constant.defaultPower.doubleValue());
						stats.setRange(Constant.defaultRange.doubleValue());
						stats.setSpeed(Constant.defaultSpeed.doubleValue());
						
						inventory = new HashMap<PickupObjectDef, Integer>();
//						if (nameToActions.containsKey(Constant.potionTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.potionTag));
//							inventory.put(new PickupObjectDef(Constant.envCollisionID, "Small Potion", "", -1, -1, -1, -1, Constant.itemTypeTag, 
//								new StatSheet(0.0, 0.2, 0.1, 0.1, 0.1, 0.1), "", false, a, Constant.potionTag), 2);
//						}
//						if (nameToActions.containsKey(Constant.wateringCanTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.wateringCanTag));
//							inventory.put(new PickupObjectDef(Constant.envCollisionID, "Watering Can", "", -1, -1, -1, -1, Constant.itemTypeTag, 
//								new StatSheet(0, 0, 0, 0, 0, 0), "", false, a, Constant.wateringCanTag), 2);
//						}
//						if (nameToActions.containsKey(Constant.wheelbarrowTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.wheelbarrowTag));
//							inventory.put(new PickupObjectDef(Constant.envCollisionID, "Wheelbarrow", "", -1, -1, -1, -1, Constant.itemTypeTag, 
//								new StatSheet(0, 0, 0, 0, 0, 0), "", false, a, Constant.wheelbarrowTag), 2);
//						}
//						if (nameToActions.containsKey(Constant.gemTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.gemTag));
//							inventory.put(new PickupObjectDef(Constant.envCollisionID, "Small Gem", "", -1, -1, -1, -1, Constant.itemTypeTag, 
//								new StatSheet(0.2, 0.5, 0.2, 0.2, 0.2, 0.2), "", false, a, Constant.gemTag), 2);
//						}
//						if (nameToActions.containsKey(Constant.coinTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.coinTag));
//							inventory.put(new PickupObjectDef(Constant.envCollisionID, "Coin", "", -1, -1, -1, -1, Constant.itemTypeTag, 
//								new StatSheet(0.2, 0.5, 0.2, 0.2, 0.2, 0.2), "", false, a, Constant.coinTag), 2);
//						}
//						
//						weapons = new HashSet<PickupObjectDef>();
//						if (nameToActions.containsKey(Constant.shieldTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.shieldTag));
//							weapons.add(new PickupObjectDef(Constant.envCollisionID, "Basic Shield", "", -1, -1, -1, -1, Constant.weaponTypeTag, 
//									new StatSheet(0.0, 0.0, 0.0, 0.2, 0.0, 0.0), "", false, a, Constant.shieldTag));
//						}
//						if (nameToActions.containsKey(Constant.swordTag)) {
//							List<String> a = new ArrayList<String>();
//							a.add(nameToActions.get(Constant.swordTag));
//							weapons.add(new PickupObjectDef(Constant.envCollisionID, "Basic Sword", "", -1, -1, -1, -1, Constant.weaponTypeTag, 
//									new StatSheet(0.0, 0.0, 0.2, 0.0, 0.0, 0.0), "", false, a, Constant.swordTag));
//						}
						if (type.equals(Constant.enemyTypeTag))
							strategy = new Strategy(Constant.defaultAIIntelligence, 
													Constant.defaultAIAttack, 
													Constant.defaultAIDefense, 
													Constant.defaultAIMoney, 
													Constant.defaultAIObjective);
					} else if (type.equals(Constant.weaponTypeTag) || type.equals(Constant.itemTypeTag)) {
						actions = new ArrayList<String>();
						
						if (nameToActions.containsKey(objName))
							actions.add(nameToActions.get(objName));
						Constant.LOG(this.getClass(), objName + " " + actions.toString());
					}
					
					updates = new StatSheet();
					updates.setMaxHealth(Constant.defaultHealthLevelUp);
					updates.setCurrentHealth(Constant.defaultCurrHealthLevelUp);
					updates.setDefense(Constant.defaultDefenseLevelUp);
					updates.setPower(Constant.defaultPowerLevelUp);
					updates.setRange(Constant.defaultRangeLevelUp);
					updates.setSpeed(Constant.defaultSpeedLevelUp);
					
					boolean movable = false;
					if(objName.equals(Constant.rockTag))
						movable = true;
					
					authorEnv.setInstantDrawObject(name, collisionId, team, imagePath, stats, updates, type, 
							inventory, weapons, movable, actions, objName, strategy);
				}
			}
		}
	}
	
	private class InventoryBackAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			showActionList();
		}
	}
	
	private class ChooseItemActionAction implements ListSelectionListener {
	    
		public void valueChanged(ListSelectionEvent e) {
			if (!actionSelector.getListSelectionValid()) return;
			if (actionSelector.getSelections() == null || actionSelector.getSelections().isEmpty()) return;
			String objName = (String)actionSelector.getSelections().get(0);
			Unit unit = controller.getStateManager().getCurrPlayingUnit();
			if (unit != null) {
				applyAction(objName, unit);
			}
			showActionList();
			controller.setObjectSelectorPanelEnabled(false);
		}
	}
	
	private class UseInventoryAction implements ListSelectionListener {
	    
		public void valueChanged(ListSelectionEvent e) {
			if (!inventorySelector.getListSelectionValid()) return;
			if (inventorySelector.getSelections() == null || inventorySelector.getSelections().isEmpty()) return;
			String objName = (String)inventorySelector.getSelections().keySet().toArray()[0];
			Unit unit = controller.getStateManager().getCurrPlayingUnit();
			
			if (unit != null) {
				PickupObjectDef pick = unit.getItem(objName);
				if (pick == null) return;
				JGPoint pos = unit.getPosition();
				BoostAction boost = new BoostAction(Constant.boostTag, controller);
				boost.applyAction(pick, pos.x, pos.y);
				if (!pick.getActions().isEmpty())
					showItemActionList(pick);
				else {
					showActionList();
					controller.getStateManager().setState(Constant.targetSelectionCompleteMode, true);
				}
				unit.removeItem(pick.name);
				
			}
		}
	}
	
	private class TakeActionAction extends MouseAdapter {
		
		private String prevActionName;
		
	    public void mouseClicked(MouseEvent e) {
	    	if (!objectSelector.isEnabled()) return;
	        if (e.getClickCount() == 1) {
	        	authorEnv.setStatShowVisible(false);
	        	JPanel p = (JPanel) objectSelector.getSelectionContent();
				if (p == null) {
					// deselect an item, return to previous state
					if (prevActionName.equals(Constant.moveTag))
		        		controller.getStateManager().setMoved(false);
					controller.getStateManager().setState(Constant.sourceSelectionCompleteMode,  
							controller.getStateManager().getCurrPlayingUnit().getPosition(), true);
					return;
				} else {
					String objName = p.getName(); // action name  Hit/Move/Inventory
					prevActionName = objName;
					authorEnv.displayStatus("ACTION: " + objName);
					Unit unit = controller.getStateManager().getCurrPlayingUnit();
					Assert.assertNotNull(unit);
					
					if (controller.getStateManager().isMoved() && objName.equals(Constant.moveTag)) {
						objectSelector.clearSelection();
						return;
					}
					
					// choose inventory
					if (objName.equals(Constant.inventoryTag)) {
						if (authorEnv.inGameState(Constant.targetSelectionMode)) {
							controller.getStateManager().setState(Constant.sourceSelectionCompleteMode,  
									controller.getStateManager().getCurrPlayingUnit().getPosition(), true);
						}
						showInventoryList();
		        		return;
		        	}
					
					// choose action
					applyAction(objName, unit);
				}
	        }
	    }
	}

	private class ExitProgramAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	private class PreferenceAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			controller.makePrefVisible();
		}
	}

	private class ModifyObjectiveSettingAction extends MouseAdapter {
	    public void mouseClicked(MouseEvent e) {
	        if (e.getClickCount() == 2) {
	        	String item = (String)goalSelector.getItemWithPosition(e);
	        	controller.showObjectiveSettingView(item);
	         }
	    }
	}

	private class ViewObjectiveSettingAction extends MouseAdapter {
	    public void mouseClicked(MouseEvent e) {
	        if (e.getClickCount() == 2) {
	        	CheckListItem item = (CheckListItem)goalViewer.getItemWithPosition(e);
	        	controller.showObjectiveSettingView(item.toString());
	         }
	    }
	}
	
	private class GameChooserAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String tab = (String)gameSelector.getSelectedItem();
			if (controller.getCurrGameName() == null || !tab.equals(controller.getCurrGameName())) {
				Constant.LOG(this.getClass(), "trigger game chooser " + controller.getCurrGameName() + " " + tab);
				controller.switchGame(tab);
			}	
		}
	}
	
	private class LevelChooserAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String gameName = (String)gameSelector.getSelectedItem();
			String levelName = (String)levelSelector.getSelectedItem();
			if (controller.getCurrGameName() == null || 
				!gameName.equals(controller.getCurrGameName()) || 
				!levelName.equals(controller.getCurrLevelName())) {
				Constant.LOG(this.getClass(), "trigger level chooser " + controller.getCurrGameName() + " " + gameName + " " + levelName);
				controller.switchLevel(gameName, levelName);
			}		
		}
	}
	
	private class DeleteGameAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Constant.LOG(this.getClass(), "trigger game delete");
			controller.deleteGame((String)gameSelector.getSelectedItem(), true);
		}

	}
	
	private class DeleteLevelAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Constant.LOG(this.getClass(), "trigger level delete");
			controller.deleteLevel((String)gameSelector.getSelectedItem(), (String)levelSelector.getSelectedItem());
		}
	}
	
	private class UndoAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			controller.getCurrLevel().undoPlacement();
		}
	}
	
	private class RedoAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			GameObjectDef g = controller.getCurrLevel().redoPlacement();
			JGPoint p = controller.getCurrLevel().getRedoPosition();
			JGPoint pixel = controller.getTileCoordinates()[p.x][p.y];
			authorEnv.makeObject(g, pixel.x, pixel.y, p.x, p.y);
		}
	}
}
