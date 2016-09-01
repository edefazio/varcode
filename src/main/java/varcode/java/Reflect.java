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
        
    protected static boolean isParamAssignable( Class<?> target, Object source )
    {
        return source == null || source.getClass().isInstance( target ) 
              || ( source.getClass().isPrimitive() && ( translatesTo( source, target ) ) );
    }

    protected static boolean allParamsAssignable( Class<?>[] target, Object... source )
    {
        if( target == null && source == null || target.length == 0 && source.length == 0 )
        {
            return true;
        }
        if( target.length == source.length )
        {   //they have the same number of arguments, but are they type compatible?
            for( int pt = 0; pt < target.length; pt++ )
            {
                if( !isParamAssignable( source[ pt ].getClass(), target[ pt ] ) )
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    
    public static Method getStaticMethod( Method[] methods, String methodName, Object[] params )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( Modifier.isStatic( methods[ i ].getModifiers() )
                && methods[ i ].getName().equals( methodName ) )
            {
                if( allParamsAssignable( methods[ i ].getParameterTypes(), params ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    public static Method getMethod( Method[] methods, String methodName, Object... params )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                if( allParamsAssignable( methods[ i ].getParameterTypes(), params ) )
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
