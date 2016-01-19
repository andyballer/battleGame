package gameplayer.mode;

import java.util.Set;

import javax.swing.SwingUtilities;

import org.junit.Assert;

import display.Constant;
import objects.objects.GameObject;
import objects.objects.artificialIntelligence.AIUnit;
import engine.ViewController;
import jgame.JGPoint;

public class EnemyMode extends State {

	public EnemyMode(String l, ViewController c, StateManager manager) {
		super(l, c, manager);
	}

	@Override
	public void start(Set<JGPoint> selectables) {
		if (selectables == null || selectables.size() == 0) {
			SwingUtilities.invokeLater( new Runnable() {
				@Override
				public void run() {
					controller.displayStatus("Player Won, Game Over");
					stateManager.nextLevel();
				}
			});
			return;
		}
		stateManager.displayState("ENEMY TURNS");
		do {
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (stateManager.animationIsAlive());
		action();
	}
	
	@Override
	public void action() {
		while (stateManager.hasNextEnemy()) {
			JGPoint p = controller.getStateManager().getNextEnemy();
			GameObject obj = controller.getCurrLevel().getObject(p);
			Assert.assertTrue(obj instanceof AIUnit);
			AIUnit enemy = (AIUnit)obj;
			controller.displayStatus("ENEMY Move: " + enemy.getUnitName());
			controller.getStateManager().addPlaying(p, enemy);
			if (Constant.enableAI) enemy = enemy.moveEnemy();
			stateManager.moveObject(enemy, p.x, p.y);
		}
		stateManager.nextPlayer();	
	}

}
