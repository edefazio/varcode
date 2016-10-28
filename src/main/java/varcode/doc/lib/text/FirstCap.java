package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import varcode.context.VarContext;
import varcode.context.eval.Eval_JavaScript;
import varcode.context.eval.VarScript;

public enum FirstCap
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
    public static String capitalizeFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if ( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toUpperCase() + string.substring( 1 );      
    }
    
    public static Object doFirstCaps( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
        	return capitalizeFirstChar( ( (String)var ) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstCaps[ i ] = 
                        capitalizeFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        if( var instanceof Collection )
        {
        	//List<String> l = (List<?>)var;
        	
            Object[] arr = ( (Collection<?>)var ).toArray();
            int len = arr.length;
            String[] firstCaps = new String[ len ];
            
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                	//l.set(i, capitalizeFirstChar( idx.toString() ) );
                    
                	firstCaps[ i ] = 
                        capitalizeFirstChar( idx.toString() );
                    
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }            
            return firstCaps;
        }
        Object[] jsArray = Eval_JavaScript.getJSArrayAsObjectArray( var );
		if( jsArray != null )
		{
			int len = jsArray.length;
			String[] firstCaps = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = jsArray[ i ];
				if( idx != null )
				{
					firstCaps[ i ] = capitalizeFirstChar( idx.toString() );
				}
				else
				{ //watch out for NPEs!
					firstCaps[ i ] = null;
				}
			}
			return firstCaps;
		}
        return capitalizeFirstChar( var.toString() );        
    }

    
    public Object eval( VarContext context, String input)
    {
    	Object resolved = 
    		context.resolveVar( input );	
    		//this.getInputParser().parse( context, input );
        return doFirstCaps( resolved );
    }
    
	
	public Object parse( VarContext context, String scriptInput ) 
	{
		if( scriptInput != null && 
			scriptInput.startsWith( "$" ) &&    				
			scriptInput.endsWith( ")" ) &&
			scriptInput.indexOf( '(' ) > 0 )
		{
				//I first need to 
		        // {#id:$quote($uuid())#}
		        //             $uuid()
				
				//               (
			int openIndex = scriptInput.indexOf( '(' );
			String scriptName = scriptInput.substring( 1, openIndex ); 
			//VarScript innerScript = context.getVarScript( scriptName );
			VarScript innerScript = context.resolveScript( scriptName, scriptInput );
			String innerScriptInput = 
				scriptInput.substring( 
					openIndex + 1,
					scriptInput.length() -1 );
			Object innerScriptResult = 
				innerScript.eval( context, innerScriptInput );
			return innerScriptResult;
		}
		return context.resolveVar( scriptInput );
	}

	
	public Set<String> getAllVarNames( String input ) 
	{
		if( input != null )
		{
			Set<String> s = new HashSet<String>();
			s.add( input.replace( "$(","").replace( ")", "" ) );
			return s;
		}
		return Collections.emptySet();
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