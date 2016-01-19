package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import engine.ViewController;
import jgame.JGPoint;

public class SourceSelectionMode extends State {

	public SourceSelectionMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(final Set<JGPoint> s) {
		if (s == null || s.size() == 0) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					controller.displayStatus("Player Lose, Game Over");
				}
			});
			stateManager.nextLevel();
			return;
		}
		
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				controller.setJGameEngineEnabled(true);
				controller.setJGameEngineSelectable(true);
				controller.addSelectables(s, true);
				controller.setObjectSelectorPanelEnabled(false);
			}
		});
	}

	@Override
	public void action() {}

}
