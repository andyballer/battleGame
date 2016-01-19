package display.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.junit.Assert;

import display.Constant;

@SuppressWarnings("serial")
public class ImageSelectorView extends SelectorView {

	public ImageSelectorView(Border border, ActionListener chooserAction) {
		super(border, chooserAction);
	}
	
	public void updateContents(Integer[] ids, String[] labels, ImageIcon[] icons) {
		Assert.assertTrue(ids.length == labels.length && labels.length == icons.length);
		setRenderer(new ComboBoxRenderer(icons, labels));
		addItems(ids);
	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		private Font uhOhFont;
		
		private ImageIcon[] images = null;
		private String[] names = null;

		public ComboBoxRenderer(ImageIcon[] i, String[] n) {
			images= i;
			names = n;
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		/*
		 * This method finds the image and text corresponding
		 * to the selected value and returns the myLabel, set up
		 * to display the text and image.
		 */
		public Component getListCellRendererComponent(JList list,
                    								  Object value,
                    								  int index,
                    								  boolean isSelected,
                    								  boolean cellHasFocus) {
			//Get the selected index. (The index param isn't
			//always valid, so just use the value.)
			int selectedIndex = ((Integer)value).intValue();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			//Set the icon and text.  If icon was null, say so.
			ImageIcon icon = images[selectedIndex];
			String pet = names[selectedIndex];
			setIcon(icon);
			if (icon != null) {
				setText(pet);
				setFont(Constant.contentFont);
			} else {
				setUhOhText(pet + " (no image available)", list.getFont());
			}

			return this;
		}

		//Set the font and text when no image was found.
		protected void setUhOhText(String uhOhText, Font normalFont) {
			if (uhOhFont == null) { //lazily create this font
				uhOhFont = Constant.contentFont;
			}
			setFont(uhOhFont);
			setText(uhOhText);
		}
	}

}
