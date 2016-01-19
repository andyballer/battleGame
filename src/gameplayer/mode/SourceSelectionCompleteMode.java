package gameplayer.mode;

import java.util.Iterator;
import java.util.Set;

import objects.objects.GameObject;
import objects.objects.Unit;

import org.junit.Assert;

import engine.ViewController;
import jgame.JGPoint;

public class SourceSelectionCompleteMode extends State {

	public SourceSelectionCompleteMode(String l, ViewController c,
			StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		Assert.assertTrue(selectables != null);
		Iterator<JGPoint> it = selectables.iterator();
		Assert.assertTrue(it.hasNext());
		JGPoint selected = it.next();
		GameObject obj = controller.getCurrLevel().getObject(selected);
		Assert.assertTrue(obj instanceof Unit);
		Unit unit = (Unit)obj;
		controller.getStateManager().addPlaying(selected, unit);
		unit.incrementTurnCount();
		controller.setJGameEngineSelectable(false);
		controller.setObjectSelectorPanelEnabled(true);
	}

	@Override
	public void action() {}

}
