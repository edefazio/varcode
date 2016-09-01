package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

public enum FirstLower
    implements VarScript
{
    INSTANCE;

    /**
     * Given a String capitalize the first character and return
     * @param string the target string
     * @return 
     * <UL>
     * <LI>null if string is null 
     * <LI>"" if string is ""
     * <LI>"FirstCap" if the string is "firstCap"
     * </UL> 
     */
    private static final String lowercaseFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if ( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toLowerCase() + string.substring( 1 );      
    }
    
    
    public static Object doFirstLower( Object var )
    {
    	if( var == null )
    	{
    		return null;
    	}
        if( var instanceof String )
        {
            return lowercaseFirstChar( ( (String)var ) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstLower[ i ] = 
                        lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        if( var instanceof Collection )
        {
            Object[] arr = ( (Collection<?>)var ).toArray();
            int len = arr.length;
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    firstLower[ i ] = 
                        lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        Object[] jsArray = Eval_JavaScript.getJSArrayAsObjectArray( var );
		if( jsArray != null )
		{
			int len = jsArray.length;
			String[] firstLower = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = jsArray[ i ];
				if( idx != null )
				{
					firstLower[ i ] = lowercaseFirstChar( idx.toString() );
				}
				else
				{ //watch out for NPEs!
					firstLower[ i ] = null;
				}
			}
			return firstLower;
		}
        return lowercaseFirstChar( var.toString() );        
    }

    
    public Object eval( VarContext context, String input)
    {
        return doFirstLower(
        		context.resolveVar( input ) );	
        	//this.getInputParser().parse( context, input ) );
    }
    
	public String toString()
	{
		return this.getClass().getName();
	}
	
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
}