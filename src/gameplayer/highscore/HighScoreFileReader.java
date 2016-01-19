package gameplayer.highscore;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for human-reading of a given .dat
 * file, since they look like arse
 * otherwise.  Just to test to make sure
 * scores are being saved.
 * @author Evan
 *
 */

public class HighScoreFileReader {
	
	public static void main(String[] args){
		HighScoreManager hm = new HighScoreManager();
		hm.initGame("Game 1");
		List<Score> scores = hm.getScores();
		
		for(int i = 0; i < scores.size(); i++){
			//System.out.println((i+1) + "\t" + scores.get(i).getName() + "\t" + scores.get(i).getScore());
		}
	}
}
