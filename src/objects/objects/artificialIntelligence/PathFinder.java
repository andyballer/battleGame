package objects.objects.artificialIntelligence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import display.Constant;
import engine.Level;
import jgame.JGPoint;


/**
 * Class for determining best route from a unit's current location
 * to its destination.  Want to allow for smooth transitions between 
 * points on the cell that follow a logical path, i.e. one which does
 * not go through other objects or leave the grid.
 * @author Evan
 *
 */
public class PathFinder {


	/**
	 * Method used to determine optimal series of moves a unit should take to get from one
	 * point on the grid to another, avoiding any cell which is currently occupied.
	 * @param current Current location of the unit
	 * @param destination Destination of the unit
	 * @param level Current level
	 * @return Stack of moves as defined by (x, y) coordinates
	 */
	public static final LinkedList<JGPoint> findOptimalPath(JGPoint current, JGPoint destination, Level level){
		LinkedList<JGPoint> frontier = new LinkedList<JGPoint>();
		Map<JGPoint, JGPoint> relations = new HashMap<JGPoint, JGPoint>();
		frontier.add(current);
		relations.put(current, null);
		while (!frontier.isEmpty()){
			JGPoint curr = frontier.poll();
			if (curr.equals(destination))
				return recoverPath(relations, destination);
			List<JGPoint> successors = getSuccessors(curr, level);
			for (JGPoint s : successors){
				if (!relations.containsKey(s)){
					relations.put(s, curr);
					frontier.add(s);
				}
			}
		}
		return frontier;

	}

	private static final LinkedList<JGPoint> recoverPath(Map<JGPoint, JGPoint> relations, JGPoint destination) {
		LinkedList<JGPoint> path = new LinkedList<JGPoint>();
		path.add(destination);
		JGPoint temp = destination;
		Constant.LOG(PathFinder.class, temp.toString());
		while (relations.get(temp) != null) {
			temp = relations.get(temp);
			path.add(temp);
		}
		return path;
	}

	/**
	 * Gets list of valid single-tile moves from a given tile.  Returns only (x, y)
	 * coordinates that exist on level and do not contain an object.
	 * @param cell current cell
	 * @param l current level
	 * @return List of valid (x, y) coordinate tuples
	 */

	public static final List<JGPoint> getSuccessors(JGPoint cell, Level l) {
		JGPoint[] potentialSuccessors = {new JGPoint(cell.x, cell.y+1), new JGPoint(cell.x, cell.y-1),
										 new JGPoint(cell.x+1, cell.y), new JGPoint(cell.x-1, cell.y)};

		List<JGPoint> validSuccessors = new ArrayList<JGPoint>();

		for (JGPoint tile : potentialSuccessors){
			if (isValidSquare(tile, l)) {
				validSuccessors.add(tile);
			}
		}

		return validSuccessors;

	}

	public static boolean isValidSquare(JGPoint tile, Level l){
		return (tile.x < l.getCols() && tile.x >= 0 && tile.y < l.getRows() && tile.y >= 0); //TODO: refactor
	}
}