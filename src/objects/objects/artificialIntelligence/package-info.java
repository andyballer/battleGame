/**
 *
 * We created an AI engine for the enemy units in
 * our game.
 *
 * The user can create their own strategy
 * by passing the reward values that an enemy should
 * associate with accomplishing certain tasks
 * (attacking, defending itself, collecting items, securing objectives)
 * into the strategy class.  The enemy will take these
 * values into account when deciding which course of
 * action it should pursue (ie: which tile it should move to)
 * Evan learned the idea of a reward structure in his AI class,
 * so we're applying it here.
 *
 * The INTELLIGENCE of the enemy is particularly special
 * The intelligence determines how complex the algorithms are that
 * the enemy uses to decide which square to move to.
 * Ie: low intelligence enemies just stand still,
 * medium intelligence enemies look within their move range to decide on a course of action,
 * high intelligence enemies take the state of the entire map into account.
 *
 * All the different algorithm classes extend the abstract
 * class AIAlgorithm -- we just swap in different algorithms to
 * the different methods when creating a new algorithm class.
 * This leaves plenty of room for future programmers to add
 * new algorithms
 * This is an application of the strategy design pattern:
 * http://www.oodesign.com/strategy-pattern.html
 *
 * We attempted to decouple the algorithm creation from the
 * actual game, although some code would need to
 * be changed.  For this, we used the factory design pattern:
 * http://www.oodesign.com/factory-pattern.html
 * 
 * @author Teddy and Matt
 */
package objects.objects.artificialIntelligence;