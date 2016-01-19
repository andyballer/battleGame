package saveLoad;

/**
 * <p>
 * The idea behind this Definition class is to have an easy way to load/save the objects that we
 * want to save in the game XML files. The Definition holds information about what class should be
 * created as well as the information that will be important to reconstruct the game state.
 * </p>
 * <p>
 * The idea is that each class that game developers will want to save to the game XML file will
 * implement Definable (an interface) and will have a method that will return the Definition of the
 * object. This Definition has only get methods for its instance variables. The Definition can be
 * used to access the object's old states. Alternatively, the Definition can also be used as a
 * parameter in the Constructor of an Object to easily recreate the object.
 * </p>
 * <p>
 * The public get methods of the Definition class allows us to use Java's built in XMLEncoder class
 * to save the objects but allows for save access of the instance variables of a class. These
 * definitions would mainly be used to either clone an object OR at save-time to save the state of
 * the game.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * {@code
 * public class DemoObject implements Definable {
 *      public int idThatDefinesDemoObject;
 *      
 *      public DemoObject (DemoObjectDef def) {
 *              // Constructor using definition classes
 *              idThatDefinesDemoObject = def.getId();
 *      }
 *      public DemoObject (int i) {
 *              // Alternative constructor
 *              idThatDefinesDemoObject = i;
 *      }
 *      // Interface method
 *      public Definition getDefinition () {
 *              return new DemoObjectDef(idThatDefinesDemoObject);
 *      }
 * }
 * 
 * public class DemoObjectDef extends Definition {
 *      public int idThatDefinesDemoObject; 
 *      
 *      public DemoObjectDef (Class c, int i) {
 *              super(c);
 *              idThatDefinesDemoObject = i;
 *      }
 *      
 *      public int getId() {
 *              return idThatDefinesDemoObject;
 *      }
 * }
 * }
 * </pre>
 * 
 * @author Thanh-Ha Nguyen
 * 
 */
public abstract class Definition {

}