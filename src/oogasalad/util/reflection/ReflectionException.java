package oogasalad.util.reflection;


/**
 * A general exception that represents all possible Java Reflection exceptions. 
 * 
 * @author Robert C. Duvall
 */
@SuppressWarnings("serial")
public final class ReflectionException extends RuntimeException
{
    public ReflectionException (String s)
    {
        super(s);
    }
}
