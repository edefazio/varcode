package varcode.doc.lib.java;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.JavaNaming;
import varcode.script.VarScript;

/**
 * Creates an index count (an array of sequential indexes) for all
 * elemnts in the array
 * 
 * For example:
 * <PRE> 
 * if I have the input String[]{ "A", "B", "C", "D", "E" };
 * it will return int[]{ 0, 1, 2, 3, 4 };
 * 
 * if I have the input String[]{"Yes" "No", "Maybe"};
 * it will return int[]{ 0, 1, 2 };
 * </PRE>
 */
public enum ValidateTypeName
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
        //Object var = context.get( varName );
        Object var = context.resolveVar( varName );
        return validateType( varName,  var );
    }
    
    public Object validateType( String varName, Object var )
    {
        if( var instanceof Class )
        {
        	return var;
        }
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
                    	if(! ( o instanceof Class ) )
                    	{
                    		JavaNaming.TypeName.validate( o.toString() );
                    	}
                    }
                    else
                    {
                        throw new VarException( 
                            "type name for \"" + varName + 
                            "\" at index [" + i + "] cannot be null" );
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
                    	if(! ( o instanceof Class ) )
                    	{
                    		JavaNaming.TypeName.validate( o.toString() );
                    	}
                    }
                    else
                    {
                        throw new VarException( 
                            "type name for \"" + varName + "\" at [" + i + "] null "
                            + "identifier name at index [" + i + "]" );
                    }
                }
                return var;
            }
            JavaNaming.TypeName.validate( var.toString() );
            return var;
        }
        throw new VarException( 
            "type name for \"" + varName + "\" cannot be null" );
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