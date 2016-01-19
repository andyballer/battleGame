package display.animation;

import java.util.ArrayList;
import java.util.List;

import jgame.JGObject;
import display.Constant;
import engine.ViewController;

/*
 * Code adapted from Jiaran Hao who took this course last semester
 */

public class WordAnimation {
	
	private ViewController controller = null;
	private List<Animation> animations = new ArrayList<Animation>();

	/**
	 * @param x: the x coordinate where to print these messages.
	 * @param y: y coordinate
	 * @param str: the message to be printed. Only support letters and digit or space
	 * @param width: the interval between each letters.
	 */
	public WordAnimation(int x , int y, String str, int width, double duration, ViewController con) {
		controller = con;
		int currentX = x - str.length() * width / 2;
		int currentY = y;
		for (int i = 0; i < str.length(); i++) {
			Character c = Character.valueOf((str.charAt(i)));

			if (c.charValue() == ' '){
				currentX += width;
				continue;
			}

			String letter = "Letter" + c.toString();
			String scaledLetter = controller.scaleImage(letter, "#", 0, 50, 61);
			JGObject oneLetter = new JGObject(null, true, 0, 0, 0, scaledLetter);
			Animation a = new FadeAnimation(Constant.wordAnimationTag, currentX, currentY, oneLetter, duration);
			animations.add(a);
			currentX += width;
		}
	}
	
	public WordAnimation(int x, int y, String str, ViewController con) {
		this(x, y, str, Constant.defaultWordInterval, Constant.defaultWordDuration, con);
	}
	
	public boolean isAlive() {
		for (Animation a : animations)
			if (a.isAlive()) return true;
		return false;
	}
	
	public void remove() {
		for (Animation a : animations) {
			a.remove();	
		}
		animations.clear();
	}

}
