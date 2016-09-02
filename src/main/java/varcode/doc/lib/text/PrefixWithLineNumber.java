package varcode.doc.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Given a String, indents each line a number of spaces
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PrefixWithLineNumber 
	implements Directive.PostProcessor
{	
	public static final PrefixWithLineNumber INSTANCE = new PrefixWithLineNumber();
	
	public PrefixWithLineNumber(  )
	{ }

	public void postProcess( DocState tailorState ) 
	{
		String original = tailorState.getTranslateBuffer().toString();
		tailorState.getTranslateBuffer().replaceBuffer( 
            doPrefixLineNumber( original ).toString() );
	}

	public StringBuilder doPrefixLineNumber( String input )
	{
		if (input == null )
		{
			return new StringBuilder();
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
		int lineNumber = 1;
		String line;
		try 
		{
			line = br.readLine();
			//boolean firstLine = true;
			while( line != null )
			{
				fb.append( System.lineSeparator() );									
				fb.append( String.format( "%3d", lineNumber ) );
				fb.append( "]" );
				fb.append( line );
				//firstLine = false;
				lineNumber++;
				line = br.readLine();
			}
			return fb;
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error indenting spaces" );
		}	
	}
	
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
	}
	
	public static String doPrefix( String string )
	{
		return INSTANCE.doPrefixLineNumber( string ).toString();
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
