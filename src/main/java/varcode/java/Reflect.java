package varcode.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

/**
 * Methods to simplify using Java Reflection
 * specifically for finding a specific method on a class, or for 
 * capturing CheckedExceptions and throwing RuntimeExceptions
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Reflect 
{
	;

    private static final Logger LOG = 
        	LoggerFactory.getLogger( Reflect.class );
    
    private static final Map<Class<?>, Set<Class<?>>> SOURCE_CLASS_TO_TARGET_CLASSES= 
        new HashMap<Class<?>, Set<Class<?>>>();
        
    static
    {
        Set<Class<?>>byteMapping = new HashSet<Class<?>>();
        byteMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   byte.class, Byte.class, short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class} ) );
            
        SOURCE_CLASS_TO_TARGET_CLASSES.put( byte.class, byteMapping );
            
        Set<Class<?>>shortMapping = new HashSet<Class<?>>();
        shortMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class } ) );
         
        SOURCE_CLASS_TO_TARGET_CLASSES.put( short.class, shortMapping );
           
        Set<Class<?>>intMapping = new HashSet<Class<?>>();
        intMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   int.class, Integer.class, long.class, Long.class } ) );
         
        SOURCE_CLASS_TO_TARGET_CLASSES.put( int.class, intMapping );
         
        Set<Class<?>>longMapping = new HashSet<Class<?>>();
        longMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   long.class, Long.class } ) );
            
        SOURCE_CLASS_TO_TARGET_CLASSES.put( long.class, longMapping );
    }
        
    protected static boolean translatesTo( Object source, Class<?>target )
    {
        Set<Class<?>> clazzes = SOURCE_CLASS_TO_TARGET_CLASSES.get( source.getClass() );
        if( target != null )
        {
            return clazzes.contains( target );
        }
        return false;
    }
        
    /**
     * is this arg assignable to the target class?
     * @param target
     * @param arg
     * @return 
     */
    protected static boolean isArgAssignable( Class<?> target, Object arg )
    {
        return arg == null || arg.getClass().isInstance( target ) 
              || ( arg.getClass().isPrimitive() 
                  && ( translatesTo(arg, target ) ) );
    }

    /**
     * Try and "match" the arguments of a method with the 
     * 
     * @param target the target arguments
     * @param arg the actual arguments
     * @return 
     */
    protected static boolean allArgsAssignable( Class<?>[] target, Object... arg )
    {
        if( target == null && arg == null 
            || target.length == 0 && arg.length == 0 )
        {
            return true;
        }
        if( target.length == arg.length )
        {   //they have the same number of arguments, but are they type compatible?
            for( int pt = 0; pt < target.length; pt++ )
            {
                if( !isArgAssignable(arg[ pt ].getClass(), target[ pt ] ) )
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    
    public static Method getStaticMethod( 
        Method[] methods, String methodName, Object[] args )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( Modifier.isStatic( methods[ i ].getModifiers() )
                && methods[ i ].getName().equals( methodName ) )
            {
                if( allArgsAssignable(methods[ i ].getParameterTypes(), args ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    public static Method getMethod( 
        Method[] methods, String methodName, Object... args )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                if( allArgsAssignable(methods[ i ].getParameterTypes(), args ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }
    
	public static Object getStaticFieldValue( Class<?> clazz, String fieldName ) 
	{
		try 
		{
			Field f = clazz.getField( fieldName );
			return f.get( clazz );
		} 
		catch( Exception e ) 
		{
			throw new VarException( e );
		} 		
	}
	
    public static Object getStaticFieldValue( Field field )
	{
    	try 
	    {
	    	return field.get( null );
	    } 
	    catch( Exception e ) 
	    {
	    	LOG.debug( "Unable to get Field \"" + field + "\"" );
	    	return null;
	    } 	    
    }
}
