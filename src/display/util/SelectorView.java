package display.util;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class SelectorView extends Panel {

	protected JComboBox selector;
	protected DefaultComboBoxModel model;
	protected List<Object> elements;

	public SelectorView(Border border, ActionListener chooserAction){
		masterPanel = makeGridButton(border);
		model = new DefaultComboBoxModel();
		selector = new JComboBox(model);
		((JLabel)selector.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		selector.addActionListener(chooserAction);
		
		masterPanel.add(selector);
		elements = new CopyOnWriteArrayList<Object>();
	}

	private JPanel makeGridButton(Border border){
		JPanel panel = new JPanel();
		if (border != null) panel.setBorder(border);
		panel.setLayout(new GridLayout(1, 2, 15, 15));
		return panel;
	}
	
	public void addItem(Object str) {
		if (!elements.contains(str)) {
			elements.add(str);
			model.addElement(str);
		}
	}
	
	public void addItems(Object[] strs) {
		for (Object s : strs)
			addItem(s);
	}
	
	public void setSelected(Object name) {
		addItem(name);
		selector.setSelectedItem(name);
	}
	
	public Object getSelectedItem() {
		return selector.getSelectedItem();
	}
	
	public void removeItem(Object name) {
		elements.remove(name);
		model.removeElement(name);
	}
	
	public void updateAndSetSelected(Object[] items, Object item) {
		if (!elements.contains(item)) model.addElement(item); 
		else elements.remove(item);
		setSelected(item);
		for(Object e : elements)
			model.removeElement(e);
		elements.clear();
		elements.add(item);
		addItems(items);
	}
	
	public Integer getItem(Object name) {
		ComboBoxModel model = selector.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if ((model.getElementAt(i)).equals(name))
				return i;
		}
		return -1;
	}
	
	public boolean checkDuplicateName(Object name) {
		ComboBoxModel model = selector.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if ((model.getElementAt(i)).equals(name))
				return true;
		}
		return false;
	}
	
	public void clearAll() {
		model.removeAllElements();
		elements.clear();
	}
	
	public void setEnabled(boolean b) {
		selector.setEnabled(b);
	}
	
	public void setRenderer(ListCellRenderer r) {
		selector.setRenderer(r);
	}
	
	public void addActionListener(ActionListener action) {
		if (selector != null) {
			ActionListener[] actions = selector.getActionListeners();
			for (ActionListener a : actions)
				selector.removeActionListener(a);
			selector.addActionListener(action);
			for (ActionListener a : actions)
				selector.addActionListener(a);
		}
	}

}
