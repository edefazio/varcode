package varcode.buffer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import varcode.VarException;
import varcode.buffer.TranslateBuffer.Translator;

public enum JSArrayToArrayTranslate 
	implements Translator
{
	INSTANCE;
    	
	public static final Class<?> JSARRAY_CLASS = ScriptObjectMirror.class;
	
    public Object translate( Object source ) 
	{    	
		if( source instanceof ScriptObjectMirror )
		{
			return getJSArrayAsObjectArray( source );
		}
		return source;
	}
    
    public static Object[] getJSArrayAsObjectArray( Object obj )
	{    
		try
	    {
	    	final Object result = IS_ARRAY_METHOD.invoke( obj );
	    	if( result != null && result.equals( true ) ) 
	    	{                
	    		final Object vals = VALUES_METHOD.invoke( obj );    				
	    		//System.out.println( "GOT VALS " + vals );
	    		if( vals instanceof Collection<?> ) 
	    		{
	    			final Collection<?> coll = (Collection<?>) vals;
	    			return coll.toArray( new Object[ 0 ] );
	    		}
	    	}
	    	return null;	    	
	    }
	    catch( IllegalAccessException e)
	    {
	    	return null;
	    }
	    catch( IllegalArgumentException e)
	    {
	    	return null;
	    }
	    catch( InvocationTargetException e)
	    {
	    	return null;
	    }
	}
    
    private static Method getMethod(String methodName) 
    {
    	try 
    	{
			return JSARRAY_CLASS.getMethod( methodName );
		} 
    	catch( NoSuchMethodException e ) 
    	{
    		throw new VarException( "unable to get method "+methodName, e);
		} 
    	catch (SecurityException e) 
    	{
    		throw new VarException( "unable to get method "+methodName, e) ;
		}
    }
    
    public static final Method IS_ARRAY_METHOD = getMethod( "isArray" );

	public static final Method VALUES_METHOD = getMethod( "values" );
}
    
    