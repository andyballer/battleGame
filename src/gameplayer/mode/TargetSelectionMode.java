package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import display.Constant;
import engine.ViewController;
import jgame.JGPoint;

public class TargetSelectionMode extends State {

	public TargetSelectionMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(final Set<JGPoint> s) {
		if (s == null || s.size() == 0) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					stateManager.setState(Constant.exitMode, true);
				}
			});
			return;
		}
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				controller.setJGameEngineEnabled(true);
				controller.setJGameEngineSelectable(true);
				controller.addSelectables(s, true);
			}
		});
	}

	@Override
	public void action() {}
}
