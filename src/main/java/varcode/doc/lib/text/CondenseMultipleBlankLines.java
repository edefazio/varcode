package varcode.doc.lib.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Given a String, indents each line a number of spaces
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CondenseMultipleBlankLines 
	implements Directive.PostProcessor
{	
	INSTANCE;
		
	public void postProcess( DocState docState ) 
	{
		String original = docState.getTranslateBuffer().toString();
		docState.getTranslateBuffer().replaceBuffer( condenseEmptyLines( original ).toString() ) ;
		//tailorState.setTextBuffer( new TranslateBuffer().replaceWith( condenseEmptyLines( original ) ) );			
	}

	public StringBuilder condenseEmptyLines( String input )
	{
		if (input == null )
		{
			return new StringBuilder();
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
		String line;
		boolean wasPreviousLineEmpty = true;
		boolean firstLine = true;
		//int linesChomped = 0;
		try 
		{
			line = br.readLine();			
			//boolean firstLine = true;
			while( line != null )
			{
				if( line.trim().length() == 0 )
				{
					if( wasPreviousLineEmpty )
					{  
						//this line is empty and the previous line was empty
						//System.out.println("CHOMP " + linesChomped++);
					}
					else
					{   //this line is empty but the previous line was not empty
						fb.append( System.lineSeparator() );
						wasPreviousLineEmpty = true;
					}
				}
				else
				{
					wasPreviousLineEmpty = false;
					if (! firstLine )
					{
						fb.append( System.lineSeparator() );						
					}
					firstLine = false;
					fb.append( line );
					//	firstLine = false;
				}
				
				line = br.readLine();
			}
			return fb;
		} 
		catch( IOException e ) 
		{
			throw new VarException( "Error indenting spaces" );
		}	
	}
	
	public static String doCondense( String string )
	{
		return INSTANCE.condenseEmptyLines( string ).toString();
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
