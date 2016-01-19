package display;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import display.util.Layout;
import display.util.SelectorView;
import engine.ViewController;

/**
 * Class for creating view to load
 * game when starting program in
 * uneditable mode
 * @author Evan
 *
 */

public class LoadGameView extends JFrame {

	private static final long serialVersionUID = -7634172756271773660L;
	private ViewController controller;
	private SelectorView gameList;
	private Layout layout = null;
	
	private String path;
	private JLabel image;
	private File defaultPath = null;
	private File defaultImagePath = null;
	private String defaultExtension = null;
	
	public LoadGameView(String s, int windowLength, int windowHeight, ViewController c, 
			File path, File imagePath, String extension){
		controller = c;
		defaultPath = path;
		defaultImagePath = imagePath;
		defaultExtension = extension;
		
		
		setTitle(s);
		setSize(windowLength,windowHeight);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(EXIT_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		setUndecorated(true);
		
		layout = new Layout(false, false, true);
		this.setContentPane(layout.getPanel());
		
		Insets border = new Insets(Constant.upperBorder, Constant.leftBorder, Constant.bottomBorder, Constant.rightBorder);
		String[] names = getFiles();
		
		image = new JLabel();
		image.setBorder(new EmptyBorder(border));
        image.setFont(image.getFont().deriveFont(Font.ITALIC));
        image.setHorizontalAlignment(JLabel.CENTER);
		
		gameList = new SelectorView(new EmptyBorder(border), new GameSelectorAction());
		gameList.updateAndSetSelected(names, names[0]);
        updateImage();
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new OKActionListener());
	
		layout.addSetting("Select Game", gameList.getPanel(), names[0]);
		layout.addSetting("Game Image", image, names[0]);
		layout.addButton("Load", new OKActionListener());
		pack();
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	private void focusBackToMainView() {
		setAlwaysOnTop(false);
		setVisible(false);
		controller.focusBackToMainView();
	}

	private class GameSelectorAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String gameName = (String)gameList.getSelectedItem();
			Constant.LOG(this.getClass(), gameName);
			updateImage();
		}
	}
	
	private class OKActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e){
			path = defaultPath.getAbsolutePath() + "/" + (String)gameList.getSelectedItem() + defaultExtension;
			if (path != null) {
				focusBackToMainView();
				controller.loadGame(path);
				controller.preparePlayMode();
				controller.startPlayMode(controller.getCurrLevelName());
				dispose();
			}
		}
	}
	
	/**
	 * Create array of filenames
	 * from games folder, i.e. the
	 * list of games available to play
	 * @return string array of game names
	 */
	
	private String[] getFiles(){
		File dir = null;
		try {
			dir = new File(new File(".").getCanonicalPath() + Constant.defaultSavePathPrefix);
		} catch (IOException e) {
			e.printStackTrace();
		}
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(defaultExtension);
            }
        };
        String[] games = new String[dir.list(filter).length];
        for(int i = 0; i < games.length; i++) {
        	games[i] = dir.list(filter)[i].substring(0, dir.list(filter)[i].lastIndexOf("."));
        }
        return games;
	}
	
	/**
	 * Create an image to display
	 * in the view as a "preview"
	 * of the currently selected game
	 * @param game game currently selected in the ComboBox
	 */
	
	protected void updateImage(){
		ImageIcon icon = new ImageIcon(defaultImagePath.getAbsolutePath() + "/"+ (String)gameList.getSelectedItem() + Constant.defaultImageExtension);
		Constant.LOG(this.getClass(), defaultImagePath.getAbsolutePath() + "/"+ (String)gameList.getSelectedItem() + Constant.defaultImageExtension);
		image.setIcon(icon);
		image.setToolTipText("Screenshot of a level from " + (String)gameList.getSelectedItem());
        image.setText(null);
		
	}
}
