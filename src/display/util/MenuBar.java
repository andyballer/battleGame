package display.util;

/*
 * manage all menu bar items 
 * use those APIs below, no need to consider how those menu bars display
 */

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBar {
	public JMenuBar menubar;
	private Map<String, JMenu> menuNames;
	private Map<String, JMenuItem> menuItems;
	
	public MenuBar(){
		menubar = new JMenuBar();
		menuNames = new HashMap<String, JMenu>();
		menuItems = new HashMap<String, JMenuItem>();
	}

	/**
	 * add menu bar
	 * @param menu new menu bar list name
	 */
	public void addMenu(String menu){
		JMenu menuName = new JMenu(menu);
		menubar.add(menuName);
		menuNames.put(menu,menuName);
	}

	/**
	 * add menu bar items
	 * @param menuName main menu name
	 * @param subName submenu name
	 * @param action action when pressing this menu bar item
	 * @param keys shortcut for this menu bar item
	 */
	public void addMenuItem(String menuName, String subName, ActionListener action, KeyStroke keys){
		JMenu temp = null;
		for (String name : menuNames.keySet()){
			if (name.equals(menuName)) {
				temp = menuNames.get(name);
				break;
			}
		}
		if (temp == null) temp = new JMenu();
		JMenuItem subMenu = new JMenuItem(subName);
		subMenu.setAccelerator(keys);
		subMenu.addActionListener(action);
		temp.add(subMenu);
		menuItems.put(subName,subMenu);	
	}
	
	public void paintMenu(JFrame frame){
		frame.setJMenuBar(menubar);
	}
	
	/**
	 * add separator in menu bar
	 * @param menuName main menu name
	 */
	public void addSeparator(String menuName) {
		JMenu temp = null;
		for (String name : menuNames.keySet()){
			if (name.equals(menuName)) {
				temp = menuNames.get(name);
				break;
			}
		}
		if (temp == null) temp = new JMenu();
		temp.addSeparator();
	}
	
	public void setEnabled(String menuName, boolean b) {
		for (Component c : menuNames.get(menuName).getMenuComponents())
			c.setEnabled(b);
	}
}
