package objects.objects.artificialIntelligence;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import engine.ResourceLoader;
import engine.ViewController;

/**
 * Creates instances of algorithms
 * Could be decoupled from the game and would only need to change
 * lines 51-59 based on the constructors of the algorithms in the
 * application this is used in
 * @author Teddy
 *
 */
public class AlgorithmFactory {

	private String myAlgorithmProperties;
	private ViewController myController;
	private int myMaxIntelligence;

	public AlgorithmFactory(String algorithmProperties, ViewController controller, int maxIntelligence) {
		myAlgorithmProperties = algorithmProperties;
		myController = controller;
		myMaxIntelligence = maxIntelligence;
	}

	/**
	 * Chooses and instantiates an algorithm based on the intelligence of the thing
	 * it is selecting an algorithm for
	 * @param intelligence the intelligence
	 * @return the algorithm
	 */
	public final AIAlgorithm selectAlgorithm(int intelligence) {
		Map<String,String> algorithmNamesToPaths = ResourceLoader.load("", myAlgorithmProperties);
		
		Map<Integer,String> algorithmComplexityToPaths = new HashMap<Integer, String>();
			
		for(String algorithmName : algorithmNamesToPaths.keySet()) {
			Scanner in = new Scanner(algorithmName).useDelimiter("[^0-9]+");
			int complexity = in.nextInt();
			String path = algorithmNamesToPaths.get(algorithmName);
			algorithmComplexityToPaths.put(complexity, path);
		}
		
		int myAlgorithmComplexity = (intelligence / (myMaxIntelligence / algorithmComplexityToPaths.size()));
		
		String algorithmPath = algorithmComplexityToPaths.get(myAlgorithmComplexity);
		try {
			return (AIAlgorithm) Class.forName(algorithmPath)
					 .getConstructor(ViewController.class)
					 .newInstance(myController);
		} catch (Exception e) {
			System.out.println("FAILED TO LOAD ALGORITHM");
			e.printStackTrace();
			return new DumbAIAlgorithm(myController);
		}
	}
}
