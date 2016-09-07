package varcode.markup.forml;

import java.io.*;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.form.BetweenTokens;
import varcode.form.Form;
import varcode.form.FormTemplate;
import varcode.form.SeriesFormatter;
import varcode.form.VarForm;
import varcode.markup.MarkupException;

/**
 * Parses/Compiles the Text/ Markup of a {@code Form}
 * and returns the appropriate {@code Form} implementation
 * <UL>
 *   <LI>{@code VarForm} if the Markup contains {@code MarkAction}s
 *   <LI>{@code StaticForm} if the Markup does not contain {@code MarkAction}s 
 * </UL>   
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ForMLCompiler
{    
	public static final String N = "\r\n";
	
	/** The standard Context used for Parsing */
	private static final VarContext PARSE_CONTEXT = VarContext.of( );
	
    /** The Default Instance */
    public static final ForMLCompiler INSTANCE = 
        new ForMLCompiler( 
            PARSE_CONTEXT, 
            BetweenTokens.BaseBetweenTokens.INSTANCE );
    
    private final BetweenTokens betweenTokens;
    
    /** parseContext contains the components for parsing */
    private final VarContext parseContext;
    
    public ForMLCompiler(
        VarContext parseContext,
        BetweenTokens betweenTokens )
    {
        this.parseContext = parseContext;
        this.betweenTokens = betweenTokens;        
    }
    
    public Form compile( String forMLDoc )
    {
        return fromString( parseContext, -1, null, forMLDoc );
    }
    
    public Form compile( String name, String forMLDoc )
    {
        return fromString( parseContext, -1, name, forMLDoc );
    }
    
    public Form fromString( int lineNumber, String name, String forMLDoc )
    {
        return fromString( parseContext, lineNumber, name, forMLDoc );
    }
   
    protected static BufferedReader readerFromString( String forMLDoc )
    {
        try
        {
            ByteArrayInputStream bais =
               new ByteArrayInputStream(
                    forMLDoc.getBytes( "UTF-8" ) );
            return new BufferedReader(
                    new InputStreamReader( bais ) );
        }
        catch( UnsupportedEncodingException e )
        {
            throw new VarException("Unsupported Encoding", e);
        }
    }
    
   
    // {{ }} this "means" form and tail series test
    // {_ _} this "means" form and NO tail Series test
    public Form fromString( 
        VarContext context, int lineNumber, String name, String text )
    {   
        // does the form text "end" with a between token? 
        // if so, I need to chop this off BEFORE parsing the Form
        String formNoTail = chopTailOf( text );
    
        FormTemplate formMarkup = 
            compileTemplate(  
                context, 
                readerFromString( formNoTail ) );
        
        if( formMarkup.getMarks().length == 0 )
        {   //there are NO marks (its just static text) 
            return new Form.StaticForm( text );
        }
        //get the tail
        String theTail = text.substring( formNoTail.length() );
        
        if( theTail.trim().length() == 0 )
        {   //each Form instance doesnt have a tail
            //a series of forms can be placed inline
            VarForm vf = new VarForm(
                formMarkup,
                new SeriesFormatter.AfterEach( theTail ) );
            return vf;
        }
        //I have a "Between" tail...
        //let me get at the Tail that I Chopped off
        String tailText = text.substring( 
            formNoTail.length(), text.length() );
        
        VarForm vf = new VarForm( 
            formMarkup, 
            new SeriesFormatter.BetweenTwo( tailText ) );
        return vf;        
    }
    
    /**
     * I need to return the Tail
     * AND the Tail
     * @param text
     * @return
     */
    public String chopTailOf( String text )
    {
        String betweenToken = betweenTokens.endsWithToken( text );
        //String betweenTail = null;
        
        if( betweenToken != null )
        {   // The Text ENDS with a "Between" Token (like a ',', '&', etc.)
            // which signifies when we have a series of Forms, they need
            // to have this token to separate Tokens in a Series:
            //<PRE>
            // if the form is : "{+var}, "
            //                         ^^
            //</PRE>
            // ...we recognize the "tail"
            // and remove it, since when we put a series of forms together
            // we want this:<PRE>
            // "A, B, C"
            //   ^^ ^^
            //        
            // NOT this (with a dangling separator):<PRE>
            // "A, B, C, "</PRE>
            //            
            //found a between token, remove it from the "form" proper
            String betweenTail 
                = text.substring( text.lastIndexOf( betweenToken ) );            
            
            return text.substring( 0, text.length() - betweenTail.length() );
        }
        //The Form DOESNT end with a "BEtween Token", but lets chop off any trailing
        // Whitespace ( spaces, tabs, lineFeeds )
        //trim the tail
        int lastIndex = text.length() -1;
        while( lastIndex > 1 
            && Character.isWhitespace( text.charAt( lastIndex ) ) )
        {
            lastIndex --;
        }
        text = text.substring( 0, lastIndex + 1 );
        return text;              
    }
    
    public static FormTemplate compileTemplate( String forML )
    {
    	return compileTemplate( VarContext.of( ), readerFromString( forML ) );
    }
    
    public static FormTemplate compileTemplate( 
        VarContext varContext,            
        BufferedReader theReader )
    {
        ForMLParseState parseState = new ForMLParseState( varContext );
        try
        {
            String line = "";
            int lineNumber = 1; //initialize the line Number
             
            boolean firstLine = true;
             
            while( ( line = theReader.readLine() ) != null ) 
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
            //REACHED THE END OF Markup
            if( parseState.isMarkOpen() )
            {   //UNCLOSED MARK
                throw new MarkupException( 
                    "Unclosed Mark with text " + N 
                    + parseState.getMarkContents() );
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
                    theReader.close();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
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
        private static void lineOpenMark( 
            String sourceLine, 
            int lineNumber, 
            ForMLParseState builder )
        {   //check if the open Mark is closed this line
            String closeTag = builder.getCloseTagForOpenMark();
            
            int indexOfCloseMark = 
                sourceLine.indexOf( closeTag );
            
            if( indexOfCloseMark >= 0 )
            {
                builder.completeMark( 
                    sourceLine.substring( 
                        0, 
                        indexOfCloseMark + closeTag.length() ),                 
                    lineNumber );
                
                //process what is left of the line (after the close mark)
                line( 
                    sourceLine.substring( indexOfCloseMark + closeTag.length() ), 
                    lineNumber, 
                    builder );
                return;
            }
            builder.addToMark( sourceLine );
        }       
        
   /**
    * Process the data within the {@code sourceLine} at {@code lineNumber}
    * (update the {@code builder} with any tags, internal test or document
    * text  
    * 
    * @param sourceLine the source line to parse
    * @param lineNumber 
    * @param builder updates the {@code VarCode} being built
    */
    private static void line( 
        String sourceLine, 
        int lineNumber, 
        ForMLParseState builder )
    {   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks          
        
        String firstOpenTag = ForMLParser.INSTANCE.getFirstOpenTag( sourceLine );
         
        if( firstOpenTag != null )
        {   //Opened a mark this line
            int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
            //add everything before the open tag
            builder.addText( sourceLine.substring( 0, indexOfOpenMark ) );
               
            String matchingCloseTag = 
            	ForMLParser.INSTANCE.closeTagFor( firstOpenTag );
                
            //find a close tag AFTER the OPEN Tag
            int closeTagIndex = sourceLine.indexOf( 
                matchingCloseTag, indexOfOpenMark + firstOpenTag.length() );
                
            if( closeTagIndex >= 0 )
            {   //the Mark is closed this line
                builder.completeMark( 
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
                    builder );
                return;
            }
            else
            {   //the mark is not closed this line
                builder.startMark( 
                    sourceLine.substring( indexOfOpenMark ), 
                    firstOpenTag );
                return;
            }
        }
        else
        {   //NO Tags/ Marks this line (just text)
            builder.addText( sourceLine );
        }           
    }       
}
