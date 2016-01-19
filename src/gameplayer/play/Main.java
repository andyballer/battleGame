package gameplayer.play;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import display.Constant;
import display.LoadGameView;
import display.MainView;
import display.PreferenceView;
import engine.Controller;
import engine.ViewController;

/**
 * Main class for running an uneditable
 * version of the program
 * @author Evan
 *
 */
public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				ViewController controller = new Controller();

				MainView view = new MainView(Constant.progName, Constant.mainWindowWidth, Constant.mainWindowHeight, controller, false);
				view.setVisible(true);
				controller.setMainView(view);
				
				PreferenceView pref = new PreferenceView(Constant.prefName, Constant.prefWindowWidth, Constant.prefWindowHeight, controller);
				controller.setPref(pref);
				
				view.setEnabled(false);
				try {
					File currDir = new File(new File(".").getCanonicalPath() + Constant.defaultSavePathPrefix);
					File imageDir = new File(new File(".").getCanonicalPath() + Constant.gameImageFilePath);
					new LoadGameView("Load Game", Constant.prefWindowWidth, Constant.prefWindowHeight, controller, 
							currDir, imageDir, Constant.defaultJsonExtension);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
}
