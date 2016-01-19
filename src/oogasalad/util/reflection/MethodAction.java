package oogasalad.util.reflection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;


/**
 * Allow a void method to be called as the result of an action.
 * 
 * Note, any arguments passed to the method must be established when
 * this action is created.
 * 
 * @author Robert C. Duvall
 */
public class MethodAction implements ActionListener
{
    private Object myReceiver;
    private Method myMethod;
    private Object[] myArgs;


    /**
     * Create action for given no-argument method of the target object.
     */
    public MethodAction (Object target, String methodName)
    {
        this(target, methodName, new Object[0]);
    }


    /**
     * Create action for given method of the target object that takes 
     * arguments.
     */
    public MethodAction (Object target, String methodName, Object ... args)
    {
        try
        {
            myReceiver = target;
            myArgs = args;
            myMethod = target.getClass().getDeclaredMethod(methodName,
                                                           Reflection.toClasses(args));
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            throw new ReflectionException(e.getMessage());
        }
    }


    /**
     * Call the method when this action is performed.
     */
    public void actionPerformed (ActionEvent event)
    {
        try
        {
            myMethod.invoke(myReceiver, myArgs);
        }
        catch (Exception e)
        {
            throw new ReflectionException(e.getMessage());
        }
    }
}
