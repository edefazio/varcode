package varcode.eval;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import varcode.VarException;
import varcode.context.VarContext;

/**
 * Evaluates String expressions using Java's built in "JavaScript" 
 * / Nashorn expression library.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Eval_JavaScript
    implements Evaluator
{
    INSTANCE;
	  
    private final AtomicBoolean isLoaded = new AtomicBoolean( false );
    
    private ScriptEngineManager scriptManager = null; 
    
    private ScriptEngine JSEngine = null;
                   
    private Eval_JavaScript()
    { }
    
    /**
     * Added this Lazy load, since it was taking 3+ seconds to bootstrap 
     * Nashorn engine, and (quite frankly) we arent using the 
     * Javascript engine THAT much (so lets only load it Lazily when needed)
     * (After bootstrap everything works fine, anyways) 
     */
    private synchronized void loadLazily()
    {
        scriptManager = new ScriptEngineManager();
        JSEngine = scriptManager.getEngineByName( "JavaScript" );
        isLoaded.set( true );
    }
    
	public Object evaluate( VarContext context, String expressionText ) 
		throws EvalException 
	{
		return evaluate( context.getScopeBindings(), expressionText );
	}
	
    public Object evaluate( Bindings bindings, String expression )
    	throws VarException
    {
        if( ! isLoaded.get() )
        {
            loadLazily();
        }
        try
        {
            return JSEngine.eval( expression, bindings );
        }
        catch( ScriptException e )
        {
        	if( e.getCause() instanceof VarException )
        	{
        		throw (VarException)e.getCause();
        	}
        	else
        	{
        		throw new EvalException( e.getCause() );
        	}         	          
        }        
    }

	public String getName() 
	{
		return this.getClass().getSimpleName();
	}

	public String getVersion() 
	{
		return "0.2";
	}

	public String toString()
	{
		return this.getName() + "." + getVersion();
	}
	
    public static final Class<?> JAVASCRIPT_OBJECT_CLASS = 
        jdk.nashorn.api.scripting.ScriptObjectMirror.class;
        
    private static Method getMethod(String methodName) 
    {
        try 
        {
            return JAVASCRIPT_OBJECT_CLASS.getMethod( methodName );
    	} 
        catch( NoSuchMethodException e ) 
        {
            throw new VarException ("unable to get isArrayMethod", e);
    	} 
        catch (SecurityException e) 
        {
        	throw new VarException ("unable to get isArrayMethod", e) ;
    	}
    }
        
    public static final Method IS_ARRAY_METHOD = getMethod( "isArray" );
    
    public static final Method VALUES_METHOD = getMethod( "values" );
    
	public static Object[] getJSArrayAsObjectArray( Object obj )
	{    
		try
	    {
	    	if( obj != null 
                && JAVASCRIPT_OBJECT_CLASS.isAssignableFrom( obj.getClass() ) ) 
	    	{            
	    		final Object result = IS_ARRAY_METHOD.invoke( obj );
	    		if( result != null && result.equals( true ) ) 
	    		{                
	    			final Object vals = VALUES_METHOD.invoke( obj );    				
	    			if( vals instanceof Collection<?> ) 
	    			{
	    				final Collection<?> coll = (Collection<?>) vals;
	    				return coll.toArray( new Object[ 0 ] );
	    			}
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
	 
	public static final Set<String>RESERVED_WORDS = new HashSet<String>();
	static
	{
		String[] reserved = {
		"abstract","else","instanceof","super", "arguments", 
		"boolean","enum","int","switch",  
		"break","export","interface","synchronized",  
		"byte","extends","let","this",  
		"case","false","long","throw",  
		"catch","final","native","throws",  
		"char","finally","new","transient",  
		"class","float","null","true",  
		"const","for","package","try",  
		"continue","function","private","typeof",  
		"debugger","goto","protected","var",  
		"default","if","public","void",  
		"delete","implements","return", "volatile",  
		"do","import","short","while",  
		"double","in","static","with",
		"alert","frames","outerHeight",
		"all","frameRate","outerWidth","anchor","function","packages","anchors",
        "getClass","pageXOffset",
		"area","hasOwnProperty","pageYOffset",
		"Array","hidden","parent",
		"assign","history","parseFloat",
		"blur","image","parseInt",
		"button","images","password",
		"checkbox","Infinity","pkcs11",
		"clearInterval","isFinite","plugin",
		"clearTimeout","isNaN","prompt",
		"clientInformation","isPrototypeOf","propertyIsEnum",
		"close","java","prototype",
		"closed","JavaArray","radio",
		"confirm","JavaClass","reset",
		"constructor","JavaObject","screenX",
		"crypto","JavaPackage","screenY",
		"Date","innerHeight","scroll",
		"decodeURI","innerWidth","secure",
		"decodeURIComponent","layer","select",
		"defaultStatus","layers","self",
		"document","length","setInterval",
		"element","link","setTimeout",
		"elements","location","status",
		"embed","Math","String",
		"embeds","mimeTypes","submit",
		"encodeURI",
		"encodeURIComponent","NaN","text",
		"escape","navigate","textarea",
		"eval","navigator","top",
		"event","Number","toString",
		"fileUpload","Object","undefined",
		"focus","offscreenBuffering","unescape",
		"form","open","untaint",
		"forms","opener","valueOf",
		"frame","option","window",
		"onbeforeunload","ondragdrop","onkeyup","onmouseover",
		"onblur","onerror","onload","onmouseup",
		"ondragdrop","onfocus","onmousedown","onreset",
		"onclick","onkeydown","onmousemove","onsubmit",
		"oncontextmenu","onkeypress","onmouseout","onunload" };
		
		RESERVED_WORDS.addAll( Arrays.asList( reserved ) );		
	}
	
	public boolean isReservedWord( String name ) 
	{
		return RESERVED_WORDS.contains( name );	
	}
}
