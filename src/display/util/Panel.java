package display.util;

import javax.swing.JPanel;

public class Panel {
	protected JPanel masterPanel;
	
	public JPanel getPanel(){
		return masterPanel;
	}
	
	public void setVisible(boolean b) {
		masterPanel.setVisible(b);
	}

}
