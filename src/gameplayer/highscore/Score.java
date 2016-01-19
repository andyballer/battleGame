package gameplayer.highscore;

import java.io.Serializable;

public class Score implements Serializable, Comparable<Score> {

	private static final long serialVersionUID = 4154469408690268759L;
	private int score;
    private String name;

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public Score(String name, int score) {
        this.score = score;
        this.name = name;
    }

	@Override
	public int compareTo(Score otherScore) {
		return ((Integer)(otherScore.getScore())).compareTo(getScore());
	}
}
