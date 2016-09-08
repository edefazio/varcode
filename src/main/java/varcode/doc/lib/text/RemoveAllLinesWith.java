package varcode.doc.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import varcode.VarException;

import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Instance (PostProcessor)<BR> 
 * Separates the source into lines, and excludes lines where 
 * any of the target Strings occur.
 * 
 * for example:<BR>
 * <CODE>RemoveAllLinesContaining vowels = RemoveAllLinesContaining( "A", "E", "I", "O", "U" );</CODE> <BR>
 * with input:<BR>
 * <PRE>
 * A
 * B
 * C
 * D
 * E
 * F
 * </PRE>  
 * will return: <BR>
 * <PRE>
 * B
 * C
 * D
 * F
 * </PRE>
 * excluding the lines with "A" and "E". 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class RemoveAllLinesWith
	implements Directive.PostProcessor
{		
	private final String[] strings;
	
	public RemoveAllLinesWith( String...strings )
	{
		this.strings = strings;
	}
	
	public void postProcess( DocState tailorState ) 
	{			
		String allTheSource = tailorState.getTranslateBuffer().toString();
		
		tailorState.getTranslateBuffer().replaceBuffer( 
            removeAllLinesContaining( allTheSource, strings ).toString() );
	}
	
	public static StringBuffer removeAllLinesContaining(
        String source, String... strings )
	{
		StringBuffer withLinesRemoved = new StringBuffer();
		BufferedReader br = new BufferedReader( new StringReader( source ) );
		try 
		{
			boolean isFirstLine = true;
			String line = br.readLine();
			while( line != null )
			{
				boolean printIt = true;
				for( int i = 0; i < strings.length; i++ )
				{
					if( line.contains( strings[ i ] ) )
					{
						printIt = false;
					}
				}
				if( printIt )
				{												
					if( !isFirstLine )
					{
						withLinesRemoved.append( System.lineSeparator() );	
					}
					withLinesRemoved.append( line );
					isFirstLine = false;
				}
				line = br.readLine(); 
			}
			return withLinesRemoved;
		} 			
		catch( IOException e ) 
		{
			throw new VarException( e ); 
		}			
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}