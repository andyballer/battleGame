package display.util;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import display.Constant;
import engine.ViewController;

/**
 * A combination of JList and JCheckBox
 * Each JList entry will have a JCheckBox to indicate if it has been selected
 * @author Xin
 *
 */

public class CheckListView extends ListView {
	
	private CheckListRenderer renderer = null;

	public CheckListView(String _label, EmptyBorder border, Dimension dim,
			ViewController controller, boolean enableMultiSelection,
			boolean enableToggle, boolean hasButton) {
		super(_label, border, dim, controller, enableMultiSelection, enableToggle, hasButton);
		renderer = new CheckListRenderer();
		setCellRenderer(renderer);
	}
	
	public List<String> getSelections() {
		List<String> selects = new ArrayList<String>();
		for (Object i : selected) {
			CheckListItem item = (CheckListItem)i;
			if (item.isSelected()) selects.add(item.toString());
			Constant.LOG(this.getClass(), item.toString() + " " + item.isSelected());
		}
		return selects;
	}
	
	@Override
	public void addItem(Object item) {
		CheckListItem i = new CheckListItem((String)item);
		listModel.addElement(i);	
		listbox.add(new JSeparator());
	}
	
	@Override
	public void clearSelection() {
		for (Object i : selected) {
			CheckListItem item = (CheckListItem)i;
			item.setSelected(false);
		}
		selected.clear();
	}
	
	/**
	 * set {@code item} to be selected if {@code e} is true or unselected if false
	 */
	@Override
	public void setSelected(Object item, Boolean e) {
		if (item instanceof CheckListItem) {
			CheckListItem check = (CheckListItem)item;
			check.setSelected(e);
			selected.add(check);
		} else if (item instanceof String) {
			for (int i = 0; i < listModel.getSize(); i++) {
				CheckListItem p = (CheckListItem) listModel.getElementAt(i);
				if (p.toString().equals((String)item)) {
					p.setSelected(e);
					selected.add(p);
				}
			}
		} else {
			Constant.LOG(this.getClass(), "Unknown Type: " + item.toString());
		}
	}
	
	private class CheckListRenderer extends JCheckBox implements ListCellRenderer {

		private static final long serialVersionUID = 2438448647042782829L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			CheckListItem p = (CheckListItem)value;
			setEnabled(list.isEnabled());
			setSelected(p.isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
		}
	}
	
	public class CheckListItem {

		private String label;
		private boolean isSelected = false;

		public CheckListItem(String label) {
			this.label = label;
		}

		public boolean isSelected()	{
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public String toString() {
			return label;
		}
	}

}
