package varcode.doc.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.JavaNaming;
import varcode.script.VarScript;

/**
 * Validates a var represents a valid Java Class name
 * 
 */
public enum ValidateClassName
    implements VarScript
{
    INSTANCE;
        
    
    public Object eval( VarContext context, String input )
    {
        return validate( context, input );
    }

    public Object validate( VarContext context, String varName )
    {
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
                        JavaNaming.ClassName.validateSimpleName( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null class name for \"" + varName + 
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
                        JavaNaming.ClassName.validateSimpleName( o.toString() );
                    }
                    else
                    {
                        throw new VarException( 
                            "null class name at index [" + i + "]" );
                    }
                }
                return var;
            }
            try
            {
            	JavaNaming.ClassName.validateSimpleName( var.toString() );
            }
            catch( Exception e )
            {
            	throw new VarException( e); 
            }
            return var;
        }
        throw new VarException( 
            "invalid, null class name for var \"" + varName + "\"" );
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