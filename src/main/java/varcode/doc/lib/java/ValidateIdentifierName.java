package varcode.doc.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.JavaNaming;
import varcode.context.eval.VarScript;

/**
 * Validates a var represents a valid Java identifier name
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum ValidateIdentifierName
    implements VarScript
{
    INSTANCE;
    
    @Override
    public Object eval( VarContext context, String input )
    {
        return validate( context, input );
    }

    public Object validate( VarContext context, String varName )
    {
      //the user passes in the NAME of the one I want index for
        //Object var = context.get( varName );
        Object var = context.resolveVar( varName );
        return validate( varName, var );
    }
    
    public Object validate( String varName, Object var )
    {
        //System.out.println( "VarName "+varName+" : "+ context.getAttribute( varName ) );
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
                    	JavaNaming.IdentifierName.validate( o.toString() );
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
                        JavaNaming.IdentifierName.validate( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null identifier name at index [" + i + "]" );
                    }
                }
                return var;
            }
            JavaNaming.IdentifierName.validate( var.toString() );
            return var;
        }
        throw new VarException( 
            "invalid, null identifier name for var \"" + varName + "\"" );
    }     
    
    @Override
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
	
    @Override
	public String toString()
	{
		return this.getClass().getName();  
	}
}