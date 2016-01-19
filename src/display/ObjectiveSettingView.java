package display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import objectives.Objective;

import org.junit.Assert;

import display.util.Layout;
import engine.ViewController;

@SuppressWarnings("serial")
public class ObjectiveSettingView extends JFrame {
	private ViewController controller;
	private Layout layout;
	private Object[][] currGoal = null;
	
	private int width;
	private int height;

	public ObjectiveSettingView(String s, int windowLength, int windowHeight, ViewController c){
		controller = c;
		width = windowLength;
		height = windowHeight;
		setTitle(s);
		updateSize();
		setVisible(false);

		layout = new Layout();
		setContentPane(layout.getPanel());
		layout.addButton(Constant.okTag, new OKAction());
		layout.addButton(Constant.cancelTag, new CancelAction());
	}

	private void initLayout(Object[][] goal, Objective obj) {
		currGoal = goal;
		layout.clearPanel();
		
		Assert.assertTrue(goal.length > 0);
		layout.addSetting((String)goal[0][0], (Class<?>)goal[0][1], "", (Object)goal[0][2], (Boolean)goal[0][3]);
		
		layout.addSetting(Constant.enableTag, Boolean.class, "", obj == null ? false : true, true);
		
		List<Integer> args = null;
		if (obj != null) {
			args = obj.getAllArguments();
			Assert.assertEquals(goal.length - 1, args.size());
		}
		for (int i = 1; i < goal.length; i++) {
			Assert.assertEquals(4, goal[i].length);
			layout.addSetting((String)goal[i][0], (Class<?>)goal[i][1], "", obj == null ? (Object)goal[i][2] : args.get(i-1), (Boolean)goal[i][3]);	
		}
	}
	
	public void setVisible(String label, Objective obj) {
		initLayout(controller.getObjectiveLayout(label), obj);
		updateSize();
		super.setVisible(true);
	}
	
	private void updateSize() {
		setSize(width,height);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(HIDE_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		pack();
	}
	
	private void focusBackToMainView() {
		setVisible(false);
		layout.clean();
		controller.focusBackToMainView();
	}
	
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		layout.setEnabled(b);
		pack();
	}
	
	private class OKAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isEnabled()) {
				focusBackToMainView();
				return;
			}
			Boolean enabled = (Boolean)layout.getCurrValue(Constant.enableTag);
			if (enabled) {
				if (!layout.checkValidInput()) {
					JOptionPane.showMessageDialog(ObjectiveSettingView.this, Constant.invalidTag);
					return;
				}
				List<Integer> args = new Vector<Integer>();
				for (int i = 1; i < currGoal.length; i++) {
					args.add((Integer)layout.getCurrValue((String)currGoal[i][0]));
				}
				controller.setObjective((String)currGoal[0][2], args);
			} else {
				controller.removeObjective((String)currGoal[0][2]);
			}
			focusBackToMainView();
		}
	}
	
	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			focusBackToMainView();
		}
	}
}
