package objectives;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class testObjectives {

	@Test
	public void testCapturePoint() {
		List<Integer> testArgs = new ArrayList<Integer>();
		testArgs.add(0);
		testArgs.add(1);
		//should be a player at copy[0][1]
//		ObjectiveFactory of = new ObjectiveFactory(new Level("test game", "test level", 30, 30, 64, Constant.moderateTag), Constant.objectivePropertyFile);
		//assertFalse(of.create("Capture Point", testArgs).isAchievedTest());
	}

	@Test
	public void testLeaveNEnemies() {
//		ObjectiveFactory of = new ObjectiveFactory(new Level("test game", "test level", 30, 30, 64, Constant.moderateTag), Constant.objectivePropertyFile);
		List<Integer> testArgs = new ArrayList<Integer>();
		testArgs.add(0);
		//should be an enemy at copy[1][2]
		//assertTrue(of.create("Kill Enemies", testArgs).isAchievedTest());
	}
	
	@Test
	public void testKillBoss() {
//		ObjectiveFactory of = new ObjectiveFactory(new Level("test game", "test level", 30, 30, 64, Constant.moderateTag), Constant.objectivePropertyFile);
		//should be a boss at copy[0][2]
		//assertTrue(of.create("Kill Boss", null).isAchievedTest());
	}
	
	@Test
	public void testLeaveNObjects() {
//		ObjectiveFactory of = new ObjectiveFactory(new Level("test game", "test level", 30, 30, 64, Constant.moderateTag), Constant.objectivePropertyFile);
		List<Integer> testArgs = new ArrayList<Integer>();
		testArgs.add(0);
		//should be an object at copy[0][1]
		//assertTrue(of.create("Collect Objects", testArgs).isAchievedTest());
	}
	
}
