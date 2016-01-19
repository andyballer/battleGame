package display.util;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.junit.Assert;

import display.Constant;

public class Layout extends Panel {

	private JLabel label;
	private JPanel basic;
	private ButtonPanel buttons;
	private Map<String, JComponent> panels = new ConcurrentHashMap<String, JComponent>();
	private Map<String, SelectorView>  selectors = new HashMap<String, SelectorView>();
	private Map<String, Object> results = new ConcurrentHashMap<String, Object>();
	
	private int checkItemNumber = 0;
	private boolean hasTag = true;
	
	public Layout() {
		this(false, true, true);
	}

	public Layout(boolean hasLabel, boolean hasTag, boolean hasButton) {
		this.hasTag = hasTag;
		masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		
		if (hasLabel) {
			JPanel labelp = new JPanel(); 
			label = new JLabel();
			labelp.add(label);
			masterPanel.add(labelp);
		}
		
		basic = new JPanel(new SpringLayout());
		masterPanel.add(basic);
		
		if (hasButton) {
			buttons = new ButtonPanel(new EmptyBorder(new Insets(10, 10, 10, 10)), 3);
			masterPanel.add(buttons.getPanel());
		}
		
	}
	
	public void setEnabled(boolean b) {
		masterPanel.setEnabled(b);
		buttons.setVisible(b);
	}
	
	public void enableButton(String buttonName, boolean b) {
		buttons.enableButton(buttonName, b);
	}
	
	public void setLabel(String name) {
		label.setText(name);
		label.setFont(Constant.titleFont);
	}
	
	/**
	 * add a new panel into this layout
	 * @param myLabel The name of this new panel
	 * @param cls The class of the information in this panel
	 * @param hint The information that will appear in the textfield (if it's a textfield, otherwise it's an empty string)
	 * @param defaultValue The default value in that panel
	 * @param editable Indicate whether this panel is editable
	 */
	public void addSetting(final String tag, final Class<?> cls, final String hint, final Object defaultValue, boolean editable) {
		JLabel l = new JLabel(tag, JLabel.TRAILING);
		JPanel p = null;
		if (panels.containsKey(tag)) p = (JPanel)panels.get(tag);
		else {
			p = new JPanel(new FlowLayout());
			if (hasTag) basic.add(l);
			basic.add(p);
			panels.put(tag, p);
		}
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		
		if (cls.equals(boolean.class) || cls.equals(Boolean.class)) {
			final JCheckBox check = new JCheckBox();
			
			check.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					results.put(tag, new Boolean(check.isSelected()));
				}
			});
			l.setLabelFor(check);
			if (defaultValue instanceof Boolean)
				check.setSelected((Boolean)defaultValue);
			
			p.add(check);
		} else {
			final JTextField textField = new JTextField(13);
			textField.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent arg0) { change(); }

				@Override
				public void insertUpdate(DocumentEvent arg0) { change(); }

				@Override
				public void removeUpdate(DocumentEvent arg0) { change(); }
				
				private void change() {
					try {
						if (cls.equals(Integer.class)) {
							results.put(tag + hint, Integer.parseInt(textField.getText()));
						} else if (cls.equals(Double.class)) {
							results.put(tag + hint, Double.parseDouble(textField.getText()));
						} else if (cls.equals(Long.class)) {
							results.put(tag + hint, Long.parseLong(textField.getText()));
						} else if (cls.equals(Byte.class)) {
							results.put(tag + hint, Byte.parseByte(textField.getText()));
						} else if (cls.equals(Short.class)) {
							results.put(tag + hint, Short.parseShort(textField.getText()));
						} else {
							results.put(tag + hint, textField.getText());
						}
					} catch (NumberFormatException e) {
						results.remove(tag + hint);
					}
				  }
			});
		    l.setLabelFor(textField);
		    if (hint.equals("")) {
		    	textField.setText(defaultValue.toString());
		    } else {
		    	if (editable)
		    		textField.addFocusListener(new FocusListener() {
					    public void focusGained(FocusEvent e) {
					    	textField.setText("");
					    }

					    public void focusLost(FocusEvent e) {
					        // nothing
					    }
					});
		    	textField.setText(hint + defaultValue.toString());
		    }
		    textField.setHorizontalAlignment(JTextField.CENTER);
		    textField.setEditable(editable);
		    
			p.add(textField);
		}
		
		results.put(tag + hint, defaultValue);
		checkItemNumber++;
		updateGrid();
	}
	
	/**
	 * add a new panel into this layout
	 * @param myLabel The name of this new panel
	 * @param c The component to be added into this layout
	 * @param defaultValue The default value for this component
	 */
	public void addSetting(String tag, JComponent c, Object defaultValue) {
		JLabel l = new JLabel(tag, JLabel.TRAILING);
		if (hasTag) basic.add(l);
		
		l.setLabelFor(c);
		basic.add(c);
		
		panels.put(tag, c);
		results.put(tag, defaultValue);
		checkItemNumber++;
		updateGrid();
	}
	
	public final static ImageIcon createImageIcon(String path) {
		Constant.LOG(Layout.class, path);
        java.net.URL imgURL = Layout.class.getResource(path);
        Assert.assertNotNull(imgURL);
        Constant.LOG(Layout.class, imgURL.toExternalForm());
        return new ImageIcon(imgURL);
    }
	
	/**
	 * a Combo box that users can select one of them, all items in that combo box are images
	 * @param myLabel The name of this component
	 * @param namesToPaths Names and file paths to all images
	 * @param defaultValue The default one to be selected
	 */
	public void addImageSelector(final String tag, Map<String, String> namesToPaths, final Object defaultValue) {
		final String[] names = namesToPaths.keySet().toArray(new String[0]);
		final String[] paths = namesToPaths.values().toArray(new String[0]);

		Assert.assertEquals(paths.length, names.length);
		ImageIcon[] icons = new ImageIcon[paths.length];
		Integer[] ints = new Integer[paths.length];
		int selected = -1;
		
		for (int i = 0; i < paths.length; i++) {
			icons[i] = createImageIcon(paths[i]);
			ints[i] = new Integer(i);
			if (paths[i].equals((String)defaultValue)) {
				selected = i;
			}
		}
		
		if (!selectors.containsKey(tag)) {
			final ImageSelectorView combobox = new ImageSelectorView(
					BorderFactory.createEmptyBorder(0, Constant.leftBorder, 0, Constant.rightBorder), null);
			combobox.addActionListener(new ActionListener() {
						
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int index = (Integer) combobox.getSelectedItem();
					if (defaultValue == null || (defaultValue != null && !paths[index].equals((String)defaultValue))) {
						results.put(tag, names[index]);
						results.put(Constant.imageSelectedPathTag, paths[index]);
					}
				}
			});
			
			JLabel l = new JLabel(tag, JLabel.TRAILING);
			if (hasTag) basic.add(l);
			l.setLabelFor(combobox.getPanel());
			basic.add(combobox.getPanel());
			selectors.put(tag, combobox);
		}
		ImageSelectorView combobox = (ImageSelectorView)selectors.get(tag);
        combobox.updateContents(ints, names, icons);
		checkItemNumber += 2;
		if (selected >= 0) {
			results.put(tag, names[selected]);
			results.put(Constant.imageSelectedPathTag, paths[selected]);
			combobox.setSelected(selected);
		}
        
		if (!results.containsKey(tag)) {
			results.put(tag, names[0]);
			results.put(Constant.imageSelectedPathTag, paths[0]);
		}
		
		updateGrid();
	}
	
	public void setImageSelectorSelectionAction(String selectorName, ActionListener action) {
		Constant.LOG(this.getClass(), selectors.toString() + selectorName);
		if (selectors.containsKey(selectorName))
			selectors.get(selectorName).addActionListener(action);
	}
	
	public void addViewItem(String label, String path) {
		
		JPanel images = null;
		if (!panels.containsKey(label)) {
			images = new JPanel();
			
			JLabel l = new JLabel(label, JLabel.TRAILING);
			basic.add(l);
			l.setLabelFor(images);
			basic.add(images);
			panels.put(label, images);
		}
		images = (JPanel)panels.get(label);
		ImageIcon icon = createImageIcon(path);
		JLabel jlabel = new JLabel(icon);
		images.add(jlabel);
		
		updateGrid();
	}
	
	public void addButton(String name, ActionListener action) {
		buttons.addButton(name, action);
	}
	
	public int getSettingSize() {
		return panels.size() + selectors.size();
	}
	
	/**
	 * only clear the results object, keep all panels
	 */
	public void clean() {
		results.clear();
		checkItemNumber = 0;
	}
	
	public Object getCurrValue(String tag) {
		return results.get(tag);
	}
	
	/**
	 * clear all things in this panel, including all panels and results
	 */
	public void clearPanel() {
		basic.removeAll();
		panels.clear();
		selectors.clear();
		clean();
	}
	
	private void updateGrid() {
		SpringUtilities.makeCompactGrid(basic,
                panels.size() + selectors.size(), hasTag ? 2 : 1, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
	}
	
	public void remove(String name) {
		Constant.LOG(this.getClass(), "trying to remove all images " + panels.containsKey(name));
		if (panels.containsKey(name)) {
			Assert.assertTrue(panels.get(name) instanceof JComponent);
			JComponent c = (JComponent)panels.get(name);
			c.removeAll();	
		} else if (selectors.containsKey(name)) {
			Assert.assertTrue(selectors.get(name) instanceof ImageSelectorView);
			ImageSelectorView c = (ImageSelectorView)selectors.get(name);
			c.clearAll();
		}
	}
	
	public boolean checkValidInput() {
		return checkItemNumber == results.size();
	}
		
}
