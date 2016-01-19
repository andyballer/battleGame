package display;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import display.util.Layout;
import display.util.TableView;
import objects.definitions.UnitDef;
import engine.ViewController;

/**
 * 
 * @author Xin
 * @author Evan
 *
 */

public class FinalResultsView extends JFrame {
	
	private static final long serialVersionUID = 2603844851514128653L;
	private ViewController controller;
	private Layout layout;
	private int score = 0;
	
	private boolean isEditable = true;

	public FinalResultsView(String s, int windowLength, int windowHeight, ViewController c){
		controller = c;
		setTitle(s);
		setSize(windowLength,windowHeight);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(HIDE_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		setUndecorated(true);
		setVisible(false);
		
		layout = new Layout(true, true, true);
		setContentPane(layout.getPanel());
		layout.addButton(Constant.nextLevelTag, new NextLevelAction());
		layout.addButton(Constant.retryTag, new RetryAction());
		layout.addButton(Constant.backToEditTag, new BackToEditAction());
		layout.addButton(Constant.okTag, new OKAction());
	}
	
	private void updateLayout(boolean playerWin, boolean allOver, List<UnitDef> players, int s, String[][] scoreData, boolean isEditable) {
		System.out.println(playerWin + " " + allOver + " " + controller.isDemo());
		score = s;
		layout.clearPanel();
		if (playerWin) {
			if (allOver) layout.setLabel("Game over, you win!");
			else layout.setLabel("You Win!");
		} else 
			layout.setLabel("Game Over");
		
		for (UnitDef u : players) {
			layout.addSetting(u.name, Integer.class, Constant.experienceTag, u.experience, false);
			layout.addSetting(u.name, Integer.class, Constant.currentLevelTag, u.level, false);
		}
		if (allOver || !playerWin) {
			Insets border = new Insets(Constant.upperBorder, Constant.leftBorder, Constant.bottomBorder, Constant.rightBorder);
			TableView table = new TableView("", new EmptyBorder(border), new Dimension(Constant.statusWidth, Constant.statusHeight), false, false, false);
			table.setTableModel(new DefaultTableModel(scoreData, Constant.highScoreColumns));
			layout.addSetting(Constant.leaderboardTag, table.getPanel(), "");
			layout.addSetting(controller.getCurrGameName(), Integer.class, Constant.scoreTag, score, false);
			layout.addSetting(controller.getCurrGameName(), String.class, Constant.userNameTag, "", true);
		}

		if (allOver || !playerWin || controller.isDemo()) layout.enableButton(Constant.nextLevelTag, false);
		if (!allOver) layout.enableButton(Constant.okTag, false);
		if (!isEditable) layout.enableButton(Constant.backToEditTag, false);
		pack();
	}
	
	public void setVisible(boolean b, boolean playerWin, boolean allOver, List<UnitDef> players, int score, String[][] scoreData, boolean isEditable) {
		this.isEditable = isEditable;
		if (b) {
			updateLayout(playerWin, allOver, players, score, scoreData, isEditable);
		}
		setVisible(b);
	}
	
	private class NextLevelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			controller.focusBackToMainView();
			controller.getStateManager().nextLevel();
		}
	}
	
	private class RetryAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			controller.focusBackToMainView();
			String currLevelName = controller.getCurrLevelName();
			controller.loadGame(Constant.defaultSaveFilePath);
			controller.startPlayMode(currLevelName);
		}
	}
	
	private class BackToEditAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String name = (String) layout.getCurrValue(controller.getCurrGameName() + Constant.userNameTag);
			controller.saveScore(name, score);
			setVisible(false);
			controller.focusBackToMainView();
			if (isEditable) controller.startEditMode();
		}
	}
	
	private class OKAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String name = (String) layout.getCurrValue(controller.getCurrGameName() + Constant.userNameTag);
			controller.saveScore(name, score);
			//System.out.println("Score Saved!:  " + name + ", " + score);
			setVisible(false);
			controller.focusBackToMainView();
			if (isEditable) controller.startEditMode();
		}
		
	}

}
