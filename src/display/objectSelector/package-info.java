/**
 * The object selector is how the user selects
 * the properties of any object when it is placed
 * on the map.  Try running main and clicking on
 * any of the grid squares after creating a blank
 * level -- you're looking at the object selector.
 *
 * The object selector uses the chain of responsibility
 * design pattern in order to be as general as possible.
 * When the user either chooses a type of object to create
 * or clicks okay after entering all of the necessary
 * information, this chain of responsibility is started.
 * Information and the individual components of the selector
 * are passed to the selector protocol being used (DefaultSelectorProtocol).
 * The selector protocol has a SelectorProcessor that has an ordered
 * hierarchy of ObjectSelectorHandlers to try sending the information to.
 * If the individual handler can handle the request, it updates the display
 * or creates the object (depending on the method being called).  Otherwise,
 * it forwards the information along to its successor.
 *
 * In our case, if a user tries to create a new weapon on the map, the
 * information is passed through the SelectorProcessor, which polls
 * the PlayerHandler, which can't create a weapon.
 * The PlayerHandler therefore forwards the information to the EnemyHandler,
 * which forwards the information to the WeaponHandler,
 * which creates the object.
 *
 * **This is useful** because it would allow us to add a new TYPE of object to
 * be placed on the map by just adding a new ObjectSelectorHandler to
 * some part of the chain and implementing necessary code.
 *
 * Read more about Chains of Responsibility here:
 * http://www.oodesign.com/chain-of-responsibility-pattern.html
 *
 * @author Teddy Ward
 */
package display.objectSelector;
