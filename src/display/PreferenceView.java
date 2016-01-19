package display;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

import display.util.Layout;
import engine.ViewController;

@SuppressWarnings("serial")
public class PreferenceView extends JFrame{

	private ViewController controller;
	private Layout layout;

	public PreferenceView(String s, int windowLength, int windowHeight, ViewController c){
		controller = c;
		setTitle(s);
		initLayout();
		setSize(windowLength,windowHeight);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(HIDE_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		pack();
		setVisible(false);
	}
	
	private void initLayout() {
		layout = new Layout();
		setContentPane(layout.getPanel());
		
		JButton colorButton = new JButton("Color");
		colorButton.addActionListener(new ColorChooser(Constant.colorAvailableMoveTag, controller));
		layout.addSetting(Constant.colorAvailableMoveTag, colorButton, (Object)Color.red);
		layout.addSetting(Constant.playerFirstTag, Boolean.class, "", true, true);
	}
	
	
	private class ColorChooser implements ActionListener {
		
		String prefName;
		ViewController controller;
		
		private ColorChooser(String n, ViewController c) {
			prefName = n;
			controller = c;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Color color = JColorChooser.showDialog(PreferenceView.this, "Choose Color", Color.black);
			if (color != null) {
				controller.getUserDefaults().putInt(prefName, color.getRGB());
			}
		}
	}

}

