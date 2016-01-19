package oogasalad.util.JGObjectState;

import objects.objects.Unit;
import jgame.JGObject;


public class JGObjectStateChecker {

    /**
     * This method takes 2 JGObjects and compares the states of the JGObjects, checking that each
     * state is the same.
     * 
     * Assumes that the important properties for a JGObject state are: x, y, xspeed, yspeed, xdir,
     * ydir, expiry, resume_in_view, is_suspended
     * 
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean checkEqualJGObjectStates (JGObject obj1, JGObject obj2) {
        if (obj1 == null || obj2 == null) { return checkEqualNullObjects(obj1, obj2); }
        if (!checkEqualJGObjects(obj1, obj2)) { return false; }
        return (obj1.x == obj2.x) &&
               (obj1.y == obj2.y) &&
               (obj1.xspeed == obj2.xspeed) &&
               (obj1.yspeed == obj2.yspeed) &&
               (obj1.xdir == obj2.xdir) &&
               (obj1.ydir == obj2.ydir) &&
               (obj1.expiry == obj2.expiry) &&
               (obj1.resume_in_view == obj2.resume_in_view) &&
               (obj1.is_suspended == obj2.is_suspended);
    }

    /**
     * This method takes 2 JGObjects and checks if they are "the same object"
     * JGObjects (stateless) are defined by: name, graphicsName, & collisionId
     */
    public static boolean checkEqualJGObjects (JGObject obj1, JGObject obj2) {
        if (obj1 == null || obj2 == null) { return checkEqualNullObjects(obj1, obj2); }
        return (obj1.getName().equals(obj2.getName())) &&
               (obj1.colid == obj2.colid) &&
               (obj1.getGraphic().equals(obj2.getGraphic()));
    }

    /**
     * Checks if 2 objects are both null.
     * 
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean checkEqualNullObjects (Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null);
    }

    /**
     * This method returns a "hashCode" based on the current state of the JGObject. Very rough at
     * this point. Probably can be improved in the future.
     * 
     * @return
     */
    public static int getJGObjectCurrentStateHashCode (JGObject obj) {
        int objCode = getJGObjectStatelessHashCode(obj);
        int objectVariables =
                (int) (
                       2 * obj.x +
                       3 * obj.y +
                       5 * obj.xspeed +
                       7 * obj.yspeed +
                       13 * obj.xdir +
                       17 * obj.ydir +
                       23 * obj.colid +
                       29 * obj.expiry);
        int stateCode = objectVariables + objCode;
        if (obj.resume_in_view) {
            stateCode += 41;
        }
        if (obj.is_suspended) {
            stateCode += 43;
        }
        return stateCode;
    }

    /**
     * Returns a hash code for JGObjects. Assumes that JGObjects are identified by hash code, name &
     * collision ID's
     * 
     * @param obj
     * @return
     */
    public static int getJGObjectStatelessHashCode (JGObject obj) {
        return  47 * Integer.parseInt(obj.getName()) + 
                51 * obj.colid + 
                57 * Integer.parseInt(obj.getGraphic());
    }
}
