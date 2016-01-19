package display.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import display.Constant;
import engine.ViewController;

@SuppressWarnings("serial")
public class ListView extends Panel {
	
	protected String label;
	protected JLabel info;
	protected JButton button;
	protected JList listbox;
	protected DefaultListModel listModel;
	protected JListSelectionToggleModel selectionModel = null;
	
	protected Set<Object> selected = null;
	protected List<MouseAdapter> mouseActions = null;
	
	protected Boolean isToggle = false;
	protected Boolean isMultiSelection = false;
	protected Boolean hasButton = false;
	
	protected boolean selectedValid = false;
	
	public ListView(String _label, EmptyBorder border, Dimension dim, ViewController controller, boolean enableMultiSelection, boolean enableToggle, boolean hasButton) {
		
		label = _label;
		isToggle = enableToggle;
		isMultiSelection = enableMultiSelection;
		this.hasButton = hasButton;

		masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		masterPanel.setBorder(border);
		masterPanel.setPreferredSize(dim);
		
		listModel = new DefaultListModel();
		
		info = new JLabel(label);
		info.setFont(Constant.titleFont);
		
		if (hasButton) {
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(info, BorderLayout.WEST);
			
			button = new JButton(Constant.exitTypeTag);
			p.add(button, BorderLayout.EAST);
			
			masterPanel.add(p, BorderLayout.NORTH);
		} else
			masterPanel.add(info, BorderLayout.NORTH);

		listbox = new JList(listModel);
		listbox.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		listbox.setFont(Constant.contentFont);

		DefaultListCellRenderer renderer = (DefaultListCellRenderer) listbox.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (isMultiSelection || isToggle) {
			selectionModel = new JListSelectionToggleModel();
			listbox.setSelectionModel(selectionModel);
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(listbox);
		
		masterPanel.add(scrollPane);
		selected = new CopyOnWriteArraySet<Object>();
		mouseActions =  new CopyOnWriteArrayList<MouseAdapter>();
	}
	
	public void setCellRenderer(ListCellRenderer renderer) {
		listbox.setCellRenderer(renderer);
	}
	
	public void setLabel(String l) {
		label = l;
		info.setText(l);
	}
	
	public void setMouseClickListener(MouseAdapter listener) {
		mouseActions.add(listener);
		listbox.addMouseListener(listener);
	}
	
	public void setListSelectionListener(ListSelectionListener listener) {
		listbox.addListSelectionListener(listener);
	}
	
	public Object getSelectionContent() {
		return listbox.getSelectedValue();
	}
	
	public void addItem(Object item) {
		listModel.addElement(item);	
		listbox.add(new JSeparator());
	}
	
	public void addItems(Object ... items) {
		for (Object i : items)
			addItem(i);
	}
	
	public void clearAll() {
		clearContents();
		
		for (MouseListener ml : mouseActions)
			listbox.removeMouseListener(ml);
		mouseActions.clear();
	}
	
	public void clearContents() {
		clearSelection();
		listModel.clear();
		selected.clear();
	}
	
	public DefaultListModel getListModel() {
		return listModel;
	}
	
	public void setModel(DefaultListModel model) {
		listModel = model;
		listbox.setModel(listModel);
	}
	
	public void setSelected(Object item, Boolean e) {
		int index = listModel.indexOf(item);
		if (e) {
			selected.add(item);
			listbox.addSelectionInterval(index, index);
		} else {
			selected.remove(item);
			listbox.removeSelectionInterval(index, index);
		}
	}
	
	public void clearSelection() {
		if (selectionModel != null) 
			selectionModel.clearSelection();
		else
			listbox.clearSelection();
		selected.clear();
	}
	
	public void addSelections(Object[] names) {
		for (Object name : names)
			setSelected(name, true);
	}
	
	public JList getJList() {
		return listbox;
	}
	
	public void setEnabled(boolean b) {
		masterPanel.setEnabled(b);
		clearSelection();
	}
	
	public boolean isEnabled() {
		return masterPanel.isEnabled();
	}
	
	public Object getItemWithPosition(MouseEvent e) {
		int index = listbox.locationToIndex(e.getPoint());
    	return listModel.getElementAt(index);
	}
	
	public void setButtonActionListener(ActionListener l) {
		if (button != null)
			button.addActionListener(l);
	}
	
	public void setListSelectionValid(boolean b) {
		selectedValid = b;
	}
	
	public boolean getListSelectionValid() {
		return selectedValid;
	}

	private class JListSelectionToggleModel extends DefaultListSelectionModel {

	    boolean gestureStarted = false;

	    @Override
	    public void setSelectionInterval(int index0, int index1) {
	    	if (isToggle)
	    		if (!selected.contains(listModel.getElementAt(index0))) {
	    			if (!isMultiSelection) {
	    				super.clearSelection();
	    				selected.clear();
	    			}
	    			super.addSelectionInterval(index0, index1);
	    			selected.add(listModel.getElementAt(index0));
	    		} else if (selected.contains(listModel.getElementAt(index0))) {
	    			if (!isMultiSelection) {
	    				super.clearSelection();
	    			}
	    			super.removeSelectionInterval(index0, index1);
	    			selected.remove(listModel.getElementAt(index0));
	    		}
	    	
	        if(isMultiSelection && !gestureStarted) {
	            if (!selected.contains(listModel.getElementAt(index0)))
	                super.removeSelectionInterval(index0, index1);
	            else
	                super.addSelectionInterval(index0, index1);

	            gestureStarted = true;
	        }
	    }

	    @Override
	    public void setValueIsAdjusting(boolean isAdjusting) {
	    	if (isToggle) {
	    		super.setValueIsAdjusting(isAdjusting);
	    		return;
	    	}
	    	if (isMultiSelection && isAdjusting == false)
	            gestureStarted = false;
	    }
	}
}
