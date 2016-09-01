package varcode.doc.lib.text;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.script.VarScript;

public enum AllCap
	implements VarScript
{
	INSTANCE;

	public static Object doAllCaps( Object var )
	{
		if( var == null )
		{
			return null; 
		}
		if( var instanceof String )
		{
			return ( (String)var ).toUpperCase();
		}
		if( var.getClass().isArray() )
		{ //need to "firstCap" each element within the array
			int len = Array.getLength( var );
			String[] allCaps = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = Array.get( var, i );
				if( idx != null )
				{
					allCaps[ i ] = idx.toString().toUpperCase();
				}
				else
				{   //watch out for NPEs!
					allCaps[ i ] = null;
				}
			}
			return allCaps;
		}
		if( var instanceof Collection )
		{
			Object[] arr = ( (Collection<?>)var ).toArray();
			int len = arr.length;
			String[] allCaps = new String[ len ];
			for( int i = 0; i < len; i++ )
			{
				Object idx = arr[ i ];
				if( idx != null )
				{
					allCaps[ i ] = idx.toString().toUpperCase();
				}
				else
				{ //watch out for NPEs!
					allCaps[ i ] = null;
				}
			}
			return allCaps;
		}
		
		 Object[] jsArray = Eval_JavaScript.getJSArrayAsObjectArray( var );
		 if( jsArray != null )
		 {
			 int len = jsArray.length;
			 String[] allCaps = new String[ len ];
			 for( int i = 0; i < len; i++ )
			 {
				 Object idx = jsArray[ i ];
				 if( idx != null )
				 {
					 allCaps[ i ] = idx.toString().toUpperCase();
			     }
				 else
				 { //watch out for NPEs!
					allCaps[ i ] = null;
				  }
		      }
			  return allCaps;
		 }
		 return var.toString().toUpperCase();    		
	 }

	public String toString()
	{
		return this.getClass().getName();
	}
	
	public Object eval( VarContext context, String input )
	{
		return doAllCaps( context.resolveVar( input ) );
			//getInputParser().parse( context, input ) );
	}
	
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
}
