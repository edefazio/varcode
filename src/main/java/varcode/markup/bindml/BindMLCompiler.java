package varcode.markup.bindml;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.Lang;
import varcode.VarException;
import varcode.context.VarBindings;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Dom;
import varcode.markup.MarkupException;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * BindML Markup Compiler 
 * 
 * Reads/Parses text <B>line-by-line</B> and to build up and returns 
 * a {@code Markup} containing {@code Mark}s, Text, and Blanks.
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and the {@code tagOpenIndex} 
 *   to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code MarkParser} parse the Mark text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code Mark} to the {@code CompileState} 
 *   (signifying the blank if appropriate)
 *   <LI>set {@code charCursor} to after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLCompiler
{
	private static final Logger LOG = 
	   LoggerFactory.getLogger( BindMLCompiler.class );
	
	private static final String N = "\r\n";
	
    public static final BindMLCompiler INSTANCE = 
        new BindMLCompiler( );
    
    public BindMLCompiler( )
    { }
    
    /** Base Method for compiling a {@code Dom} from markup */
    public static Dom fromString( String markup )
    {
        if( markup == null )
        {
            throw new VarException( "markup cannot be null" );
        }
    	try
		{
			ByteArrayInputStream bais =
			    new ByteArrayInputStream(
					markup.getBytes( "UTF-8" ));

			return fromInputStream( bais );
		}
        catch( UnsupportedEncodingException e )
		{
			throw new VarException("UTF-8 unsupported Encoding" );
		}
    }
    
    public static Dom fromMarkupStream( MarkupStream ms )
    {
    	BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( ms.getInputStream() ) );
            
        return fromReader( br );
    }
    public static Dom fromInputStream( InputStream is )
    {
        BufferedReader br = 
            new BufferedReader( 
                new InputStreamReader( is ) );
        
        return fromReader( br );
    }
    
    public static Dom fromReader( 
        BufferedReader br  )
    {
        return INSTANCE.from( br );
    }
    
    private BindMLParseState initializeParseState( MarkupStream markupStream )
    {   
    	LOG.trace( "1) Initializing Dom Parse State" ); 
        if( markupStream == null )
        {
            throw new VarException( "the MarkupStream  is null " );
        }
    	VarContext vc = VarContext.of( );
    	BindMLParseState parseState = new BindMLParseState( vc );
        VarBindings vb = vc.getOrCreateBindings( VarScope.METADATA );
        
        vb.put( Dom.MARKUP_STREAM_NAME, markupStream.describe() );
        vb.put( Dom.MARKUP_LANGUAGE_NAME, "BindML" );
        vb.put( Dom.MARKUP_ID_NAME, markupStream.getMarkupId() );
        vb.put( Dom.LANG_NAME, Lang.fromCodeId( markupStream.getMarkupId() ) );
        vb.put( Dom.DOM_COMPILE_TIMESTAMP_NAME, System.currentTimeMillis() );
        
        if( LOG.isTraceEnabled() )
        {
        	LOG.trace( "    " + vb );
        }
        return parseState;
    }
    
    public Dom from( MarkupStream markupStream )
        throws MarkupException
    {
    	if( markupStream == null )
        {
            throw new VarException ( "the MarkupStream is null " );
        }        
        BindMLParseState parseState = initializeParseState( markupStream );
        return compile( 
        	new BufferedReader(
                new InputStreamReader( markupStream.getInputStream() ) ),
            parseState );        
    }
    
    
    private BindMLParseState initializeParseState( BufferedReader reader )
    {
    	LOG.trace( "1) Initializing Parse State" ); 
    	VarContext vc = VarContext.of( );

        VarBindings vb = vc.getOrCreateBindings( VarScope.METADATA );
        vb.put( Dom.MARKUP_LANGUAGE_NAME, "BindML" );
        vb.put( Dom.DOM_COMPILE_TIMESTAMP_NAME, System.currentTimeMillis() );
        BindMLParseState parseState = new BindMLParseState( vc );
        if( LOG.isTraceEnabled() )
        {
        	LOG.trace( "    " + vb );
        } 
        return parseState;
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
	public Dom from( BufferedReader theReader )
		throws MarkupException
	{
		return compile( theReader, initializeParseState( theReader ) );
	}
	
	
	public Dom compile( 
	    BufferedReader sourceReader, BindMLParseState parseState )
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
						parseState );
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
			return parseState.compile();
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
		BindMLParseState parseState )
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
	 * @param parseState updates the {@code Dom} being built
	 */
	private void line( 
	    String sourceLine, 
		int lineNumber, 
		BindMLParseState parseState )
	{   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks		    
	    
		String firstOpenTag = BindMLParser.INSTANCE.getFirstOpenTag( sourceLine );
		
		if( firstOpenTag != null )
		{   //Opened a mark this line
		    int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
		    //add everything before the open tag
		    parseState.addText( sourceLine.substring( 0, indexOfOpenMark ) );
		    
		    String matchingCloseTag = 
		    	BindMLParser.INSTANCE.closeTagFor( firstOpenTag );
		    
		    
		    
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
} //BindMLCompiler