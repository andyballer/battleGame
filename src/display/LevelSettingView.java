package display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import display.util.Layout;
import engine.ViewController;

@SuppressWarnings("serial")
public class LevelSettingView extends JFrame {
	private ViewController controller;
	private Layout layout;
	
	private boolean first = true;

	public LevelSettingView(String s, int windowLength, int windowHeight, ViewController c){
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
		layout = new Layout() {
			
			@Override
			public boolean checkValidInput() {
				if (!super.checkValidInput()) return false;
				String gameName = (String)layout.getCurrValue(Constant.gameNameTag);
				String levelName = (String)layout.getCurrValue(Constant.levelNameTag);
				boolean invalid = false;
				if (gameName.equals("") || gameName.equals(" ")){
					invalid = true;
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidGameNameTag);
				} else if (controller.checkDuplicateName(gameName, levelName) || (levelName != null && (levelName.equals("") || levelName.equals(" ")))) {
					invalid = true;
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidLevelNameTag);
				} else if ((Integer) layout.getCurrValue(Constant.numrowTag) < Constant.minRows || 
						   (Integer) layout.getCurrValue(Constant.numcolTag) < Constant.minRows) {
					invalid = true;
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidRowColTag);
				} else if ((((Integer) layout.getCurrValue(Constant.numrowTag)) * ((Integer) layout.getCurrValue(Constant.tileSizeTag))) > Constant.jgameWindowHeight ||
						  (((Integer) layout.getCurrValue(Constant.numcolTag)) *  ((Integer) layout.getCurrValue(Constant.tileSizeTag))) > Constant.jgameWindowWidth) {
					invalid = true;
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidGridTag);
				} else if (((((Integer) layout.getCurrValue(Constant.tileSizeTag)) < Constant.minTileSize) &&
						(((Integer) layout.getCurrValue(Constant.tileSizeTag)) != 0))){
					invalid = true;			
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidTileSizeTag);
				}else if ((((Integer) layout.getCurrValue(Constant.numrowTag)) * Constant.minTileSize) > Constant.jgameWindowHeight ||
						  (((Integer) layout.getCurrValue(Constant.numcolTag)) *  Constant.minTileSize) > Constant.jgameWindowWidth){
					invalid = true;
					JOptionPane.showMessageDialog(LevelSettingView.this, Constant.invalidGridTag);
				}else
					invalid = false;
				return !invalid;
			}
		};
		
		this.setContentPane(layout.getPanel());
		layout.addSetting(Constant.gameNameTag, String.class, "", Constant.gameSelectorInit, true);
		layout.addSetting(Constant.levelNameTag, String.class, "", Constant.levelSelectorInit, true);
		layout.addSetting(Constant.numrowTag, Integer.class, "", Constant.defaultNumberOfRows, true);
		layout.addSetting(Constant.numcolTag, Integer.class, "", Constant.defaultNumberOfCols, true);
		layout.addSetting(Constant.tileSizeTag, Integer.class, "", Constant.defaultTileSize, true);

		layout.addButton(Constant.okTag, new OKAction());
		layout.addButton(Constant.cancelTag, new CancelAction());
		if (first) {
			setUndecorated(true);
			layout.enableButton(Constant.cancelTag, false);
			pack();
		}
	}

	private class OKAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (layout.checkValidInput()) {
				controller.newLevel((String)layout.getCurrValue(Constant.gameNameTag),
									(String)layout.getCurrValue(Constant.levelNameTag),
									(Integer)layout.getCurrValue(Constant.numrowTag),
									(Integer)layout.getCurrValue(Constant.numcolTag),
									(Integer)layout.getCurrValue(Constant.tileSizeTag),
									(String)layout.getCurrValue(Constant.hardnessTag));
				setVisible(false);
				controller.focusBackToMainView();
				if (first) {
					dispose();
					setUndecorated(false);
					layout.enableButton(Constant.cancelTag, true);
					pack();
					first = !first;
				}
			} else {
				return;
			}
		}
	}

	private class CancelAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			controller.focusBackToMainView();
		}
	}

}
