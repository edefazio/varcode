package varcode.doc.lib;

import java.util.Arrays;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

/**
 * Count of the number of Elements of a bound variable 
 * Verify that the varNames (given as a comma separated list)
 * have the same count (or throws an exception)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum SameCount
    implements VarScript
{
    INSTANCE;
    
    public Object eval( VarContext context, String input )
    {
        return sameCount( context, input );
    }

    public Integer getJSArrayCount( Object obj )     	
    {
    	Object[] arr = Eval_JavaScript.getJSArrayAsObjectArray( obj );
    	if( arr != null )
    	{
    		return arr.length;
    	}
    	return null;
    }
    
    public String[] getAllVarNames( String input )
    {
    	return input.split( "," );
    }
    
    public Boolean sameCount( VarContext context, String input )
    {
      //the user passes in the NAME of the one I want index for
        //Object var = context.get( varName );
    	String[] varNames = getAllVarNames( input );
    	if( varNames.length < 2 )
    	{
    		throw new VarException(
    			"Expected at least (2) comma separated varNames, got (" + varNames.length + ") "
    				+ " from \"" + input + "\"" );
    	}
    	Integer count = Count.INSTANCE.getCount(context, varNames[ 0 ] );
    	for( int i = 1; i < varNames.length; i++ )
    	{
    		Integer thisCount = Count.INSTANCE.getCount(context, varNames[ i ] );
    		if( thisCount != count )
    		{
    			throw new VarException( 
    				"Count for varName \""+varNames[ 0 ]+"\" is ("+ count + 
    				") expected same count for "+ varNames[ i ]+"\" with ("+ thisCount +")" );
    		}
    	}      
    	return true;
    }

	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}

	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		String[] varNames = getAllVarNames( input );
		collection.addAll( Arrays.asList( varNames ) );
		//return collection;
	}
}