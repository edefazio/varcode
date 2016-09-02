package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

/**
 * Adds a prefix and Postfix to Elements
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class WrapText
	implements VarScript
{    
	private final String prefix;
	private final String postfix;
	
	public WrapText( String prefix, String postfix )
	{
		this.prefix = prefix;
		this.postfix = postfix;
	}

    public static Object doWrap( Object target, String prefix, String postfix )
    {
    	if ( target == null )
    	{
    		return null;
    	}
        if( target instanceof String )
        {
            //return "\"" + (String)target + "\"";
            return prefix +(String)target + postfix;
        }
        if( target.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( target );
            String[] quoted = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( target, i );
                if( idx != null )
                {
                    quoted[ i ] = 
                    	prefix + idx.toString() + postfix;
                }
                else
                { //watch out for NPEs!
                    quoted[ i ] = null;
                }
            }
            return quoted;
        }
        if( target instanceof Collection )
        {
            Object[] arr = ( (Collection<?>)target ).toArray();
            int len = arr.length;
            String[] quoted = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    quoted[ i ] =
                    	prefix + idx.toString() + postfix;
                }
                else
                { //watch out for NPEs!
                    quoted[ i ] = null;
                }
            }
            return quoted;
        }
        Object[] jsArray = Eval_JavaScript.getJSArrayAsObjectArray( target );
		if( jsArray != null )
		{
			int len = jsArray.length;
			String[] quoted = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = jsArray[ i ];
				if( idx != null )
				{
					 quoted[ i ] =
		                prefix + idx.toString() + postfix;
				}
				else
				{ //watch out for NPEs!
					quoted[ i ] = null;
				}
			}
			return quoted;
		}
        return prefix + target.toString() + postfix;        
    }

    public Object eval( VarContext context, String input )
    {
        return doWrap( 
        	context.resolveVar( input ) , prefix, postfix );
    }
    
	public String toString()
	{
		return this.getClass().getName();
	}

	public void collectAllVarNames( Set<String> varNames, String input ) 
	{
		varNames.add( input );
	}
}