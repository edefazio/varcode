package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

public enum Quote
    implements VarScript
    {
    	INSTANCE;

    public static Object doQuote( Object target )
    {
    	if ( target == null )
    	{
    		return null;
    	}
        if( target instanceof String )
        {
            //return "\"" + (String)target + "\"";
            return "\"" + EscapeString.escapeJavaString( (String)target ) + "\"";
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
                    quoted[ i ] = "\"" + EscapeString.escapeJavaString( idx.toString() ) + "\"";
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
                        "\"" + EscapeString.escapeJavaString( idx.toString() ) + "\"";
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
		                "\"" + EscapeString.escapeJavaString( idx.toString() ) 
                      + "\"";
				}
				else
				{ //watch out for NPEs!
					quoted[ i ] = null;
				}
			}
			return quoted;
		}
        return "\"" + EscapeString.escapeJavaString( target.toString() ) + "\"";        
    }

    
    public Object eval( VarContext context, String input )
    {
        return doQuote( 
        	context.resolveVar( input ) );		
        	//this.getInputParser().parse( context, input) );
    }
    
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}