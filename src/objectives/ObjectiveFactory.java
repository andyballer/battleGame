package objectives;

import java.util.List;
import java.util.Map;

import engine.ResourceLoader;
import engine.ViewController;

/**
 * Class for creating new Objectives.
 * @author Teddy
 */
public class ObjectiveFactory {

	private Map<String, String> objectiveResources;

	/**
	 * Constructor for class ObjectiveFactory.
	 * @param paths maps the names of objectives to their file paths
	 */
	public ObjectiveFactory(final String objectivesFile) {
		objectiveResources = ResourceLoader.load("", objectivesFile);
	}

	/**
	 * Creates a new Objective with the specified parameters.
	 * @param name the name of the objective to add
	 * @param args specific values that need to be achieved
	 * @return the newly created objective
	 */
	public final Objective create(final String name, final List<Integer> args, ViewController c) {
		Objective objective = null;
		try {
			String filePath = objectiveResources.get(name);
			objective = (Objective) Class.forName(filePath)
										 .getConstructor(String.class, List.class, ViewController.class)
										 .newInstance(name, args, c);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FAILED TO LOAD OBJECTIVE");
		}
		return objective;
	}
	
}
