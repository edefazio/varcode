package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.context.eval.Eval_JavaScript;
import varcode.context.eval.VarScript;

/**
 * Given a Java Object or primitive
 * 
 * @author eric
 */
public enum PrintAsLiteral 
	implements VarScript
{
	//simple list of elements                "1,2,3,4"
	COMMA_SEPARATED_LIST( false),
	
	//this will encase the data in { }'s so "{1,2,3,4}"
	// for use in initialization, assignment i.e. int[] arr = {1,2,3,4};
	USE_ARRAY_NOTATION( true );
	
	private final boolean useArrayNotation;
	
	private PrintAsLiteral( boolean useArrayNotation )
	{
		this.useArrayNotation = useArrayNotation;
	}
	
	public String eval( VarContext varContext, String varName )
	{
		return printAsLiteral( varContext.resolveVar( varName ), this.useArrayNotation ); 
	}
	
	public static String printAsLiteral( Object object )
	{
		return printAsLiteral( object, false );
	}
	
	/**
     * Given an Object, print it as a literal
     * 
     * This includes:
     * 
     * @param object
     * @return
     */
    public static String printAsLiteral( Object object , boolean useArrayNotation )
    {
    	if( object == null )
    	{
    		return "null";
    	}
    	//Javascript array
    	if( jdk.nashorn.api.scripting.ScriptObjectMirror.class.isAssignableFrom( object.getClass() ) )
    	{
    		return printAsLiteral( 
    			Eval_JavaScript.getJSArrayAsObjectArray( object ) , useArrayNotation);
    	}
    	//collection
    	if( Collection.class.isAssignableFrom( object.getClass() ) )
    	{
    		Collection<?> coll = (Collection<?>)object;
    		Object[] arr = coll.toArray( new Object[ 0 ] );
    		
    		return printAsLiteral( arr , useArrayNotation);
    	}
    	//
    	if( object.getClass().isArray() )
    	{
    		StringBuilder sb = new StringBuilder();
    		if( useArrayNotation ) { sb.append( "{" ); }
    		for( int i = 0; i < Array.getLength( object ); i++ )
    		{    			
    			if( i > 0 )
    			{
    				sb.append( ", " );
    			}
    			sb.append( printAsLiteral( Array.get( object, i ), useArrayNotation ) ); 
    		}
    		if( useArrayNotation ) { sb.append("}" ); }
    		
    		return sb.toString();
    	}
    	
    	if( object instanceof Number )
    	{
    		if( object instanceof Integer )
    		{
    			return object.toString();
    		}
    		if( object instanceof Long )
    		{
    			return object.toString() + "L";
    		}
    		if( object instanceof Byte )
    		{
    			return "(byte)"+object.toString();
    		}
    		if( object instanceof Short )
    		{
    			return "(short)" + object.toString();
    		}
    		if( object instanceof Float )
    		{
    			return object.toString() + "f";
    		}
    		if( object instanceof Double )
    		{
    			return object.toString() + "d";
    		}
    	}
    	if( object instanceof Boolean )
    	{
    		return object.toString();
    	}
    	if( object instanceof Character )
    	{
    		return "'" + EscapeString.escapeJavaString( ((Character)object).toString() ) + "'";
    	}
    	if( object instanceof String )
    	{
    		return "\"" + EscapeString.escapeJavaString( (String)object ) + "\"";
    	}    	
    	return "\"" + object.toString() + "\"";
    }

	public void collectAllVarNames( Set<String> varNames, String input )
	{
		varNames.add( input );
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
