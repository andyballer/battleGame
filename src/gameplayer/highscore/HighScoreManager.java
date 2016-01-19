package gameplayer.highscore;

import java.util.*;
import java.io.*;

import display.Constant;

/**
 * Class for handling loading and saving of high scores.
 * Writes scores to .dat file unique to each game.
 * @author Evan
 *
 */

public class HighScoreManager {
	private List<Score> scores = null;
	private String currGame = null;
	private int currentScore = 0;
	private File currFilePath = null;
	private String defaultPath = null;

	ObjectOutputStream outputStream = null;
	ObjectInputStream inputStream = null;

	public void initGame(String gameName) {
		if (gameName.equals(currGame)) return;
		inputStream = null;
		outputStream = null;
		scores = new ArrayList<Score>();
		currGame = gameName;
		currentScore = 0;
		defaultPath = new File(".").getAbsolutePath() + Constant.highScoreFilePath;
		
		currFilePath = new File(defaultPath + "/" + gameName + Constant.defaultHighScoreFilePathSuffix);
		if (!currFilePath.exists()){
			try {
				createNewFile();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function for getting high scores.
	 * @return ArrayList of scores
	 */
	public List<Score> getScores() {
		loadScoreFile(currFilePath.getAbsolutePath());
		sort();
		return scores;
	}

	/**
	 * Function for sorting scores from high to low
	 * (in the event that a new score was added and not 
	 * appropriately sorted).
	 */
	private void sort() {
		Collections.sort(scores);
	}


	/**
	 * Adds new score to high scores.
	 * @param name Name of player
	 * @param score Player's score
	 */
	public void addScore(String name, int score) {
		loadScoreFile(currFilePath.getAbsolutePath());
		scores.add(new Score(name, score));
		updateScoreFile(currFilePath.getAbsolutePath());
	}


	/**
	 * Loads file in which high scores 
	 * for game are saved
	 */
	@SuppressWarnings("unchecked")
	private void loadScoreFile(String filepath) {
		try {
			inputStream = new ObjectInputStream(new FileInputStream(filepath));
			scores = (ArrayList<Score>) inputStream.readObject();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Error: " + e.getMessage());
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
					outputStream = null;
				}
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
		}
	}


	/**
	 * Modifies existing high score file with added scores.
	 * If no file exists, creates a new one.
	 */
	private void updateScoreFile(String filepath) {
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(filepath));
			outputStream.writeObject(scores);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		} finally {
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
					outputStream = null;
				}
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
		}
	}

	/**
	 * For use in presenting high scores to player
	 * @return 2D String array of formatted scores
	 */
	public String[][] getHighscoreData() {
		int max = 10;
		List<Score> scores = getScores();
		int x = Math.min(scores.size(), max);

		String[][] highscores = new String[x][Constant.highScoreColumns.length];

		if(scores.isEmpty())
			return highscores;
		
		for(int i = 0 ; i < x; i++) {
			String[] entry = {  
				Integer.toString(i+1), 
				scores.get(i).getName(), 
				Integer.toString(scores.get(i).getScore())  
			};
			highscores[i] = entry;
		}
		return highscores;
	}


	public void awardPoints(int points){
		currentScore += points;
	}

	public int getCurrScore(){
		return currentScore;
	}
	
	public void resetScore() {
		currentScore = 0;
	}

	/**
	 * If a game of a given name is being played for
	 * the first time, it does not have a scorekeeping
	 * file associated with it.  This creates that file
	 * from a template file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */

	private void createNewFile() throws IOException, ClassNotFoundException{
		currFilePath.createNewFile();

		outputStream = new ObjectOutputStream(new FileOutputStream(currFilePath));
		outputStream.writeObject(scores);

		outputStream.flush();
		outputStream.close();

	}    
}