package display;

import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import display.util.Layout;
import engine.ResourceLoader;
import objects.definitions.PickupObjectDef;
import objects.objects.StatSheet;
import objects.objects.Unit;


@SuppressWarnings("serial")
public class UnitStatsView extends JFrame {
	
	private Layout layout;

	public UnitStatsView(String s, int windowLength, int windowHeight) {
		setTitle(s);
		setSize(windowLength, windowHeight);		// size
		setLocationRelativeTo(null);		// center of screen
		setDefaultCloseOperation(HIDE_ON_CLOSE); 		// close enabled
		setLocationRelativeTo(null);
		setUndecorated(true);
		pack();
		setVisible(false);
		layout = new Layout(false, true, false);
		setContentPane(layout.getPanel());
	}
	
	private void updateLayout(Unit u) {
		StatSheet stats = u.getStats();
		layout.clearPanel();
		layout.addSetting("Type", String.class, "", u.getType(), false);
		layout.addSetting(Constant.currentLevelTag, Integer.class, "", (Object)u.getLevel(), false);
		layout.addSetting(Constant.experienceTag, Integer.class, "", (Object)u.getExperience(), false);
		layout.addSetting(Constant.needlevelUpTag, Integer.class, "", (Object)u.getNextLevelExperienceRequirement(), false);
		layout.addSetting(Constant.initialHealthTag, Integer.class, "", (Object)stats.getMaxHealth().intValue(), false);
		layout.addSetting(Constant.currentHealthTag, Integer.class, "", (Object)stats.getCurrentHealth().intValue(), false);
		layout.addSetting(Constant.initialPowerTag, Integer.class, "", (Object)stats.getPower().intValue(), false);
		layout.addSetting(Constant.initialDefenseTag, Integer.class, "", (Object)stats.getDefense().intValue(), false);
		layout.addSetting(Constant.initialSpeedTag, Integer.class, "", (Object)stats.getSpeed().intValue(), false);
		layout.addSetting(Constant.initialRangeTag, Integer.class, "", (Object)stats.getRange().intValue(), false);
		
		Map<String, String> namesToPaths = ResourceLoader.load(Constant.weaponSmallIconTag, Constant.imagePropertyFile);
		Set<PickupObjectDef> weapons = u.getWeapons();
		if (weapons != null)
			for (PickupObjectDef n : weapons)
				layout.addViewItem(Constant.weaponTypeTag + "s: ", namesToPaths.get(n.objName));
		
		Set<String> actions = u.getActions();
		if (actions != null) {
			int i = 1;
			for (String s : actions) {
				layout.addSetting(Constant.abilityTag + i + ": ", String.class, "", s, false);
				i++;
			}
		}
		setLocation(u.getPixelLocation().x + Constant.statsWindowXDeviation, u.getPixelLocation().y + Constant.statsWindowYDeviation);
		pack();
	}
	
	public void setVisible(boolean b, Unit u) {
		if (u != null) {
			updateLayout(u);	
			setAlwaysOnTop(b);
		}
		super.setVisible(b);
	}

}