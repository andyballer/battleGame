package display.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.junit.Assert;

import display.Constant;
import engine.ResourceLoader;
import engine.ViewController;

/**
 * used to specifically display a list of images with their names
 * @author Xin
 *
 */

public class ImageListView extends ListView {

	public ImageListView(String _label, EmptyBorder border, Dimension dim,
			ViewController controller, boolean enableMultiSelection,
			boolean enableToggle, boolean hasButton) {
		super(_label, border, dim, controller, enableMultiSelection, enableToggle, hasButton);
		setCellRenderer(new ImageListCellRenderer());
		listbox.setLayoutOrientation(JList.VERTICAL);
	}
	
	/** add multiple images into specific selector
	 * 
	 * @param category mainSelector/secondarySelector/skillSelector
	 * @param picNames a list of picture names
	 * @param fileNames a list of file paths
	 * @param action action for this selector
	 */
	public void addImages(String category, String[] picNames, String[] fileNames) {
		Assert.assertEquals(picNames.length, fileNames.length);
		
		for (int i = 0; i < picNames.length; i++) {
			Icon image = Layout.createImageIcon(fileNames[i]);
			JLabel label = new JLabel(picNames[i], image, JLabel.LEFT);
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(label);
			panel.setName(picNames[i]);
			addItem(panel);
		}
	}
	
	public void addImages(Map<String, String> images) {
		for (String name : images.keySet()) {
			Icon image = Layout.createImageIcon(images.get(name));
			JLabel label = new JLabel(name, image, JLabel.LEFT);
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(label);
			panel.setName(name);
			addItem(panel);
		}
	}
	
	public void addImages(String tag) {
		Map<String, String> picsToPaths = ResourceLoader.load(tag, Constant.imagePropertyFile);
		
		for (String picName : picsToPaths.keySet()) {
			Icon image = Layout.createImageIcon(picsToPaths.get(picName));
			JLabel label = new JLabel(picName, image, JLabel.LEFT);
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(label);
			panel.setName(picName);
			panel.setFont(Constant.contentFont);
			addItem(panel);
		}
		
//		Map<String, String> animationsToPaths = ResourceLoader.load(tag, Constant.animationFile);
//		for (String picName : animationsToPaths.keySet()) {
//			Icon image = Layout.createImageIcon(animationsToPaths.get(picName));
//			JLabel myLabel = new JLabel(picName, image, JLabel.LEFT);
//			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//			panel.add(myLabel);
//			panel.setName(picName);
//			panel.setFont(Constant.contentFont);
//			addItem(panel);
//		}
	}
	
	@Override
	public void setSelected(Object item, Boolean e) {
		Constant.LOG(this.getClass(), (String)item);
		int index = -1;
		
		for (int i = 0; i < listModel.getSize(); i++) {
			JPanel p = (JPanel) listModel.getElementAt(i);
			if (p.getName().equals((String)item))
				index = i;
		}
		Assert.assertTrue(index >= 0);
		if (e) {
			if (!isMultiSelection) {
				listbox.clearSelection();
				selected.clear();
			}
			selected.add(listModel.getElementAt(index));
			listbox.addSelectionInterval(index, index);
		} else {
			selected.remove(listModel.getElementAt(index));
			listbox.removeSelectionInterval(index, index);
		}
	}
	
	public void setSelections(List<String> names, boolean e) {
		for (String str : names)
			setSelected(str, e);
	}
	
	public List<String> getSelections() {
		List<String> res = new ArrayList<String>();
		for (Object s : selected) 
			res.add(((JPanel)s).getName());
		return res;
	}
	
	
	private class ImageListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 841023584089352801L;

		/**
		 * From http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html:
		 * 
		 * Return a component that has been configured to display the specified value. 
		 * That component's paint method is then called to "render" the cell. 
		 * If it is necessary to compute the dimensions of a list because the list cells do not have a fixed size, 
		 * this method is called to generate a component on which getPreferredSize can be invoked. 
		 * 
		 * jlist - the jlist we're painting
		 * value - the value returned by list.getModel().getElementAt(index).
		 * cellIndex - the cell index
		 * isSelected - true if the specified cell is currently selected
		 * cellHasFocus - true if the cell has focus
		 */
		public Component getListCellRendererComponent(JList jlist, 
		                                              Object value, 
		                                              int cellIndex, 
		                                              boolean isSelected, 
		                                              boolean cellHasFocus) {
			Assert.assertTrue(value instanceof JPanel);
		    Component component = (Component) value;
		    component.setForeground (Color.white);
		    component.setBackground (isSelected && ImageListView.this.isEnabled() ? UIManager.getColor("Table.focusCellForeground") : Color.white);
		    return component;
		}
	}

}
