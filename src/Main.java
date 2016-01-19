import javax.swing.SwingUtilities;

import display.Constant;
import display.MainView;
import display.PreferenceView;
import engine.Controller;
import engine.ViewController;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				ViewController controller = new Controller();

				MainView view = new MainView(Constant.progName, Constant.mainWindowWidth, Constant.mainWindowHeight, controller, true);
				view.setVisible(true);
				controller.setMainView(view);

				PreferenceView pref = new PreferenceView(Constant.prefName, Constant.prefWindowWidth, Constant.prefWindowHeight, controller);
				controller.setPref(pref);

				controller.startEditMode();
				controller.newLevel();
			}
		});
	}
}
