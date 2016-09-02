package varcode.doc.lib;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

/**
 * Count of the number of Elements of a  bound variable 
 */
public enum Count
    implements VarScript
{
    INSTANCE;
    
    public Object eval( VarContext context, String input )
    {
        return getCount( context, input );
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
    
    public Integer getCount( VarContext context, String varName )
    {
      //the user passes in the NAME of the one I want index for
        //Object var = context.get( varName );
        Object var = context.resolveVar( varName );
        //System.out.println( var.getClass() );
        return getCount( var );
    }
    
    public Integer getCount( Object var )
    {
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                return Array.getLength( var );
            }
            if( var instanceof Collection )
            {
                return ((Collection<?>)var).size();
            }
            Integer jsCount = getJSArrayCount( var );
            if( jsCount != null )
            {
            	return jsCount;
            }
            return 1;
        }
        return null;
    }

	public String toString()
	{
		return this.getClass().getName() + "." + super.toString();
	}

	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
}