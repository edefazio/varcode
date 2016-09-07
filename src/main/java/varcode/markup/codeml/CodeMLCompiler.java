package varcode.markup.codeml;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.Lang;
import varcode.VarException;
import varcode.context.VarBindings;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.dom.Dom;
import varcode.markup.MarkupException;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * Compiles CodeML (Code Markup Language) a DSML 
 * Domain Specific Markup Language
 * that "hides" marks within Code Comments for popular programming languages
 * (that use "/ *" "* /" style comments")
 *   
 * (Reads in the source text which could be: 
 * <UL>
 *  <LI>java
 *  <LI>c
 *  <LI>c++
 *  <LI>javascript
 *  <LI>...
 * </UL> 
 * Captures all text, and interprets only text that represents
 * {@code Mark}s within the source.
 * 
 * Reads/Parses text <B>line-by-line</B> and to build up and returns 
 * a {@code Markup} containing {@code MarkAction}s, Text, and Blanks.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and the {@code tagOpenIndex} 
 *   to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code CodeMLParser} parse the Mark text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code Mark} to the {@code CodeMLParseState} 
 *   (signifying the blank if appropriate)
 *   <LI>set {@code charCursor} to after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CodeMLCompiler
{
	private static final String N = "\r\n";
	
	private static final Logger LOG = 
	    LoggerFactory.getLogger( CodeMLCompiler.class );
	
    public static final CodeMLCompiler INSTANCE = 
        new CodeMLCompiler(  );
    
    public CodeMLCompiler( )
    { }
    
    /** Base Method for compiling CodeML text source to a {@code Markup} */
    public static Dom fromString( String codeMLText )
    {
    	try
		{
			ByteArrayInputStream bais =
					new ByteArrayInputStream(
							codeMLText.getBytes( "UTF-8" ));
			return fromInputStream( bais );
		}
		catch( UnsupportedEncodingException e )
		{
			throw new VarException("Unsupported Format Conversion");
		}

    }
    
    public static Dom fromMarkupStream( MarkupStream codeMLStream )
    {
    	BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( codeMLStream.getInputStream() ) );
            
        return fromReader( br );
    }
    
    public static Dom fromInputStream( InputStream codeMLInputStream )
    {
        BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( codeMLInputStream ) );
        
        return fromReader( br );
    }
    
    public static Dom fromReader( BufferedReader codeMLBufferedReader  )
    {
        return INSTANCE.compile( codeMLBufferedReader );
    }
    
    private CodeMLParseState initializeParseState( MarkupStream markupStream )
    {
    	LOG.trace( "1) Initializing Dom Parse State" ); 
        if( markupStream == null )
        {
            throw new VarException( "the MarkupStream  is null " );
        }
        VarContext vc = VarContext.of( );
        
        CodeMLParseState parseState = new CodeMLParseState( vc );
        VarBindings vb = vc.getOrCreateBindings( VarScope.METADATA );
        vb.put( Dom.MARKUP_STREAM_NAME, markupStream.describe() );
        vb.put( Dom.MARKUP_LANGUAGE_NAME, "CodeML" );
        vb.put( Dom.MARKUP_ID_NAME, markupStream.getMarkupId() );
        vb.put( Dom.LANG_NAME, Lang.fromCodeId( markupStream.getMarkupId() ) );
        vb.put( Dom.DOM_COMPILE_TIMESTAMP_NAME, System.currentTimeMillis() );
        
        if( LOG.isTraceEnabled() )
        {
        	LOG.trace( "    " + vb );
        }
        return parseState;
    }
    
    private CodeMLParseState initializeParseState( BufferedReader reader )
    {
    	LOG.trace( "1) Initializing Parse State" ); 
    	VarContext vc = VarContext.of( );
        VarBindings vb = vc.getOrCreateBindings( VarScope.METADATA );
        vb.put( Dom.MARKUP_LANGUAGE_NAME, "CodeML" );
        vb.put( Dom.DOM_COMPILE_TIMESTAMP_NAME, System.currentTimeMillis() );
        CodeMLParseState parseState = new CodeMLParseState( vc );
        if( LOG.isTraceEnabled() )
        {
        	LOG.trace( "    " + vb );
        } 
        return parseState;
    }
    
    public Dom compile( MarkupStream markupStream )
        throws MarkupException
    {
    	CodeMLParseState parseState = initializeParseState( markupStream );
        return compile( 
        	new BufferedReader(
                new InputStreamReader( markupStream.getInputStream() ) ),
            parseState );        
    }
    
	/**  
	 * read/parse the source Markup for {@code MarkAction}s 
	 * and returns the {@code Markup}.
	 * 
	 * @param theReader reader for the text of the {@code Markup} 
	 * NOTE: we use a BufferedReader since it has the readLine() method.
	 * @return {@code Markup} able to be specialized 
	 * @throws MarkupException if the compilation fails
	 */
	public Dom compile( 
	    BufferedReader theReader )
		throws MarkupException
	{
	    CodeMLParseState parseState = initializeParseState( theReader );	    
	    return compile( theReader, parseState );
	}
	
	
	public Dom compile( 
	    BufferedReader sourceReader, CodeMLParseState parseState )
	{
		LOG.trace( "2) Parsing Markup" ); 
		try
		{
			String line = "";
			int lineNumber = 1; //initialize the line Number
			
			boolean firstLine = true;
			
			while( ( line = sourceReader.readLine() ) != null ) 
			{
				if( !firstLine )   
				{   //"prepend" the new line before processing the line 
					if( parseState.isMarkOpen() ) 
					{   //previous line ended and a Mark wasn't closed
					    parseState.addToMark( N );
					}
					else
					{   //move static text to next line
						parseState.addText( N ); 
					}
				}
				else 
				{
					firstLine = false;
				}				
				if( parseState.isMarkOpen() ) 
				{   //if previous MARKS aren't closed  
					lineOpenMark( 
				        line,   
						lineNumber, 
						parseState 
					);
				}
				else
				{
					line( 
						line, 
						lineNumber, 
						parseState );						
				}			
				lineNumber++;
			}
			LOG.trace( "3) Compiling Dom" ); 
			return parseState.compile( );
		}
		catch( IOException ioe )			
		{
			throw new MarkupException( 
			    "Problem reading from Reader ", ioe );
		}
		finally
		{
		    try
            {
                sourceReader.close();
            }
            catch( IOException e )
            {
            	LOG.warn( "Exception closing Input Reader ", e );
            }
		}		
	}
	
	/**
	 * Process the next line of text from the code varcode source, 
	 * knowing that a mark (from some previous line) has not been closed.<BR>
	 *  
	 * (a Mark spans multiple lines)
	 * 
	 * @return an Empty StringBuilder if we have closed the marks 
	 *         -or- a StingBuilder containing internal Mark data  from the line 
	 */
	private void lineOpenMark( 
	    String sourceLine, 
		int lineNumber, 
		CodeMLParseState parseState )
	{   //check if the open Mark is closed this line
	    String closeTag = parseState.getCloseTagForOpenMark();
	    
		int indexOfCloseMark = 
	        sourceLine.indexOf( closeTag );
		
		if( indexOfCloseMark >= 0 )
		{
			if( LOG.isTraceEnabled() )
			{
				LOG.trace( 
					"    found \"" + closeTag + "\" for open Mark \"" + parseState.getMarkContents()
					+ "\" on line [" + lineNumber + "]");
			}
		    parseState.completeMark( 
		        sourceLine.substring( 
		            0, 
		            indexOfCloseMark + closeTag.length() ),			        
		        lineNumber );
		    
			//process what is left of the line (after the close mark)
			line( 
			    sourceLine.substring( indexOfCloseMark + closeTag.length() ), 
				lineNumber, 
				parseState );
			return;
		}
		//mark was NOT closed this line, add this line contentx to existing Open Mark
		parseState.addToMark( sourceLine );
	}		
    
	/**
	 * Process the data within the {@code sourceLine} at {@code lineNumber}
	 * (update the {@code builder} with any tags, internal test or document
	 * text  
	 * 
	 * @param sourceLine the source line to parse
	 * @param lineNumber 
	 * @param parseState state of the {@code Markup} being built 
	 */
	private void line( 
	    String sourceLine, 
		int lineNumber, 
		CodeMLParseState parseState )
	{   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks		    
	    
		String firstOpenTag =
			parseState.getFirstOpenTag( sourceLine );	
		
		if( firstOpenTag != null )
		{   //Opened a mark this line
			LOG.trace( firstOpenTag );
		    int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
		    //add everything before the open tag
		    parseState.addText( sourceLine.substring( 0, indexOfOpenMark ) );
		    
		    String matchingCloseTag =
		    	parseState.getCloseTagFor( firstOpenTag );	
		    
		    //find a close tag AFTER the OPEN Tag
		    int closeTagIndex = sourceLine.indexOf( 
		        matchingCloseTag, indexOfOpenMark + firstOpenTag.length() );
		    
		    if( closeTagIndex >= 0 )
		    {   //the Mark is closed this line
		        parseState.completeMark( 
                    sourceLine.substring( 
                        indexOfOpenMark, 
                        closeTagIndex + matchingCloseTag.length() ), 
                    lineNumber );
		        
		        //process the rest of the line (AFTER the CLOSE TAG of MARK)
                line( 
                    sourceLine.substring( 
                        closeTagIndex + matchingCloseTag.length(), 
                        sourceLine.length() ),
                    lineNumber,                 
                    parseState );
                return;
		    }
		    else
		    {   //the mark is not closed this line
		        parseState.startMark( 
		            sourceLine.substring( indexOfOpenMark ), 
		            firstOpenTag,
		            lineNumber );
		        return;
		    }
		}
		else
		{   //NO Tags/ Marks this line (just text)
		    parseState.addText( sourceLine );
		}			
	}		
} //CodeMLCompiler