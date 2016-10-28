package varcode.doc.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.JavaNaming;
import varcode.context.eval.VarScript;

/**
 * Creates an index count (an array of sequential indexes) for all
 * elements in the array
 * 
 * For example:
 * <PRE> 
 * if I have the input String[]{ "A", "B", "C", "D", "E" };
 * it will return int[]{ 0, 1, 2, 3, 4 };
 * 
 * if I have the input String[]{"Yes" "No", "Maybe"};
 * it will return int[]{ 0, 1, 2 };
 * </PRE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum ValidatePackageName
    implements VarScript
{
    INSTANCE;
        
    
    public Object eval( VarContext context, String input )
    {
        return validate( context, input );
    }

    public Object validate( VarContext context, String varName )
    {
      //the user passes in the NAME of the one I want index for
        //bject var = context.get( varName );
    	Object var = context.resolveVar( varName );
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                int len = Array.getLength( var );                    
                for( int i = 0; i < len; i++ )
                {
                    Object o = Array.get( var, i );
                    if( o != null )
                    {
                        JavaNaming.PackageName.validate( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null identifier name for \"" + varName + 
                            "\" at index [" + i + "]" );
                    }
                }
                return var;
            }
            if( var instanceof Collection )
            {
                Object[] coll = ((Collection<?>) var).toArray( new Object[ 0 ] );
                
                for( int i = 0; i < coll.length; i++ )
                {
                    Object o = coll[ i ];
                    if( o != null )
                    {
                    	JavaNaming.PackageName.validate( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null identifier name at index [" + i + "]" );
                    }
                }
                return var;
            }
            JavaNaming.PackageName.validate( var.toString() );
            return var;
        }
        throw new VarException( 
            "invalid, null identifier name for var \"" + varName + "\"" );
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