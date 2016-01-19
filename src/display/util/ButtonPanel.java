package display.util;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.junit.Assert;

/*
 * manage all buttons
 * use addButton directly, no need to care about how they are displayed
 */

public class ButtonPanel extends Panel {
	public Map<String,JButton> buttonMap;
	public Map<String, ActionListener> buttonAction;
	
	public int buttonsPerCol = 2;

	public ButtonPanel(EmptyBorder border, int cols){
		buttonMap = new HashMap<String, JButton>();
		buttonAction = new HashMap<String, ActionListener>();
		masterPanel = makeGridButton(border);
		buttonsPerCol = cols;
	}

	private JPanel makeGridButton(EmptyBorder border){
		JPanel panel = new JPanel();
		panel.setBorder(border);
		return panel;
	}
	
	/**
	 * add button
	 * @param name button name
	 * @param action action when pressing this menu bar item
	 */
	public void addButton(String name, ActionListener action) {
		JButton temp = new JButton(name);
		buttonMap.put(name, temp);
		int rows = buttonMap.size() % buttonsPerCol == 0 ? buttonMap.size() / buttonsPerCol : buttonMap.size() / buttonsPerCol + 1;
		masterPanel.setLayout(new GridLayout(rows, buttonsPerCol > buttonMap.size() ? buttonMap.size() : buttonsPerCol, 15, 15));
		masterPanel.add(temp);
		temp.addActionListener(action);
		buttonAction.put(name, action);
	}
	
	public void enableButton(String name, boolean b) {
		Assert.assertTrue(buttonMap.containsKey(name));
		buttonMap.get(name).setEnabled(b);
	}
	
	public void changeName(String oldName, String newName, ActionListener action) {
		Assert.assertTrue(buttonMap.containsKey(oldName));
		Assert.assertTrue(buttonAction.containsKey(oldName));
		
		JButton button = buttonMap.get(oldName);
		button.setText(newName);
		button.removeActionListener(buttonAction.get(oldName));
		button.addActionListener(action);
		
		buttonMap.remove(oldName);
		buttonAction.remove(oldName);
		buttonMap.put(newName, button);
		buttonAction.put(newName, action);
	}

}
