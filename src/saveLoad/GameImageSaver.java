package saveLoad;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import display.Constant;
import display.jgame.GameEnv;

/**
 * Class for saving an image of a game
 * to display to the user when selecting
 * a game to load in uneditable mode
 * @author Evan
 *
 */

public class GameImageSaver {
	
	/**
	 * Saves resized image of game
	 * @param c Controller for current game
	 * @param component  JGame canvas
	 */
	public static void saveGameImage(GameEnv component, String fileName){ 
		Point p = component.getLocationOnScreen();
		int w   = component.getWidth();
		int h   = component.getHeight();

		Rectangle rectangle = new Rectangle( p.x, p.y, w, h );
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		BufferedImage image = (BufferedImage)robot.createScreenCapture(rectangle);
		BufferedImage buffered = null;
		try {
			buffered = getScaledImage(image, Constant.defaultGameThumbnailWidth, Constant.defaultGameThumbnailHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int start = fileName.lastIndexOf("/") + 1;
		int end = fileName.lastIndexOf(".");
		if (start >= 0 && end >= 0)
			saveToFile(fileName.substring(start, end), buffered);
		
	}

	/**
	 * Save image to file in 
	 * gamepics folder 
	 * @param filename name of image file
	 * @param image image to save
	 */
	private static void saveToFile(String filename, BufferedImage image) {
	        try {
	            String fn = filename + ".jpg";
	            File file = new File(new File(".") + Constant.gameImageFilePath + "/" + fn);
	            ImageIO.write(image, "jpg", file);
	        } catch (IOException e) {}
	 }
	 
	 /**
	  * Create BufferedImage of dimensions
	  * specified by parameters
	  * @param image Image to scale
	  * @param width scaled width
	  * @param height scaled height
	  * @return scaled BufferedImage
	  * @throws IOException
	  */
	 private static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
		    int imageWidth  = image.getWidth();
		    int imageHeight = image.getHeight();

		    double scaleX = (double)width/imageWidth;
		    double scaleY = (double)height/imageHeight;
		    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		    return bilinearScaleOp.filter(
		        image,
		        new BufferedImage(width, height, image.getType()));
		}
}
