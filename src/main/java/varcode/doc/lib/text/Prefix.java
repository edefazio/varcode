package varcode.doc.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.script.VarScript;

/**
 * Given a String, indents each line a number of spaces
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Prefix 
	implements VarScript, Directive.PostProcessor
{
	public static Prefix INDENT_4_SPACES = new Prefix( "    " );
    
    public static Prefix INDENT_8_SPACES = new Prefix( "        " );
    
    public static Prefix INDENT_12_SPACES = new Prefix( "            " );
    
    public static Prefix INDENT_16_SPACES = new Prefix( "                " );
	
	public static Prefix INDENT_TAB = new Prefix( '\t' + "" );
	
	public final String prefix;
	
	public Prefix( String prefix )
	{
		this.prefix = prefix;
	}
	
	public void postProcess( DocState tailorState ) 
	{
		String original = tailorState.getTranslateBuffer().toString();
		
		tailorState.getTranslateBuffer().replaceBuffer( doPrefix( original ).toString() );
		//tailorState.setTextBuffer( new FillBuffer(  doPrefix( original ) ) );			
	}

	public String prefixForms( Object input )
	{
		if( input == null )
		{
			return ""; 
		}
		if( input.getClass().isArray() )
		{
			StringBuilder sb = new StringBuilder();
			for( int i = 0; i < java.lang.reflect.Array.getLength( input ); i++ )
			{
				if( i > 0 )
				{
					sb.append( System.lineSeparator() );
				}
				sb.append( prefixForms( Array.get( input, i ) ) );				
			}
			return sb.toString();
		}
		if( input instanceof Collection )
		{
			Collection<?> c = (Collection<?>)input;
			return prefixForms( c.toArray() );
		}
		return doPrefix( input.toString() ).toString();		
	}
	
	public StringBuilder doPrefix( String input )
	{
		if (input == null )
		{
			return new StringBuilder();
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
		String line;
		try 
		{
			line = br.readLine();
			boolean firstLine = true;
			while( line != null )
			{
				if(! firstLine )
				{
					fb.append( System.lineSeparator() );					
				}
				//fb.append( "    " );
				if( line.trim().length() > 0 )
				{
					fb.append( prefix );
				}
				fb.append( line );
				firstLine = false;
				line = br.readLine();
			}
			return fb;
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error prefixing" );
		}	
	}
	
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
	
	public String doPrefixObject( Object val )
	{
		if( val == null )
		{
			return null;
		}
		if( val instanceof String )
		{
			return doPrefix( (String)val ).toString();
		}
		if( val.getClass().isArray() )
		{
			StringBuilder sb = new StringBuilder();
			int len = Array.getLength( val );
			for( int i = 0; i < len; i++)
			{
				sb.append( doPrefixObject( Array.get( val, i ) ) ); 
			}
			return sb.toString();
		}
		if( val instanceof Collection )
		{			
			return doPrefixObject( ((Collection<?>)val).toArray( new Object[ 0 ] ) );
		}
		return doPrefixObject( val.toString() );
	}
	
	public Object eval( VarContext context, String input ) 
	{
		Object val = context.resolveVar( input );
		if( val != null )
		{
			return doPrefixObject( val );
		}
		return null;		
	}
	
	public String toString()
	{
		return this.getClass().getName() + " with \"" + prefix + "\"";
	}
}
