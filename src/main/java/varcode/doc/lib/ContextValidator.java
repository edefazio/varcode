package varcode.doc.lib;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Provides a means of deeply validating the {@code VarContext} as the input
 * during the pre-processing step of tailoring code.
 * <UL>
 *  <LI>Cross Field Validation
 * </UL>
 *    
 * @see varcode.doc.Author   
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class ContextValidator
	implements Directive.PreProcessor
{	
	public void preProcess( DocState tailorState ) 
	{
		validateContext( tailorState.getContext() );
	}
		
	public abstract void validateContext( VarContext context );
	
	public static String getString( VarContext context, String name )
	{
		Object val = context.resolveVar( name );
		if( val != null )
		{
			return val.toString();
		}
		return null;
	}
		
	public static Integer getCount( VarContext context, String varName )
	{
		return Count.INSTANCE.getCount( context, varName );
	}
	
	public static Integer assertCount( VarContext context, String varName, Integer count )
	{
		Integer actual = getCount( context, varName );
		if( !java.util.Objects.equals(actual, count ) )
		{
			throw new VarException(
				"Count of var \"" + varName + "\" is (" + actual 
                + ") expected (" + count + ")" ); 
		}			
		return actual;
	}

	public static Integer assertCount( 
        VarContext context, String varName, int min, int max )
	{
		Integer actual = getCount( context, varName );
		if ( actual == null || actual < min || actual > max )
		{
			throw new VarException(
				"Count of var \"" + varName+"\" is (" + actual 
              + ") expected value in [" + min + "..." + max + "]" ); 
		}			
		return actual;
	}
	
	private static void assertUnique( Object[] arr )
	{
		Set<Object>all = new HashSet<Object>();
		for(int i = 0; i < arr.length; i++ )
		{
			if( all.contains( arr[ i ] ) )
			{
				throw new VarException( 
                    "Not unique, contains value \"" 
                  + arr[ i ] + "\" multiple times" );
			}
			all.add( arr[ i ] );
		}
	}
	
	public static void assertUnique( VarContext context, String varName )
	{
		Object it = context.resolveVar( varName );
		if( it != null )
		{
			if( it.getClass().isArray() )
			{
				assertUnique( (Object[])it );
			}
			if( it.getClass().isAssignableFrom( Collection.class ) )
			{
				Collection<?> c = (Collection<?>)it;				
				assertUnique( c.toArray( new Object[ 0 ] ) );
			}
		}
	}
}

