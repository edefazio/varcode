package varcode.markup.codeml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.FillInTheBlanks;
import varcode.doc.FillInTheBlanks.Builder;
import varcode.dom.Dom;
import varcode.markup.MarkupParseState;
import varcode.markup.MarkupException;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.BoundStatically;
import varcode.markup.mark.Mark.IsNamed;

/**
 * Mutable Intermediate Representation (IR) Parse State for producing a 
 * {@code Dom} while text is being read in and parsed by the 
 * {@code CodeMLParser}.<BR><BR> 
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next open tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and 
 *   {@code tagOpenIndex} to the state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code CodeMLParser} parse the text between {@code tagOpenIndex}
 *   and {@code tagCloseIndex} and add the parsed {@code MarkAction} to 
 *   the {@code CodeMLParseState} (adding a fill blank if appropriate)
 *   <LI>set {@code charCursor} to the char after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * <BLOCKQUOTE>
 * VarCode does not create a <A HREF="https://en.wikipedia.org/wiki/Parse_tree">parse tree</A> 
 * or an <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">"AST"</A> 
 * (we don't need a "tree" at all; each "Mark is a self-contained command"). 
 * The {@code CodeMarkCompiler} <B>sequentially</B> reads each {@code MarkAction} 
 * (matching close tags to open tags) then parses each {@MarkAction} 
 * one (to add to the while the document is being read in
 * </BLOCKQUOTE>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CodeMLParseState 
    implements MarkupParseState
{
	private static final Logger LOG = 
		LoggerFactory.getLogger( CodeMLParseState.class );
	
    public static final String N = System.lineSeparator();
    
    /** Contains the state of Variables as they are being parsed */
    public final VarContext parseContext;

    /** Captures text and {@code Mark} locations (in a {@code BitSet}) */
    public final FillInTheBlanks.Builder domBuilder;
    
    /** stores the character indexes of ALL {@code Mark}s within the {@code Markup} */
    public final BitSet allMarkIndexes;
    
    /** All {@code MarkAction}s extracted from the {@code Markup} */  
    public final List<Mark> allMarks;

    /** Buffer for text inside a {@code MarkAction}s that spans lines */
    private final StringBuilder markBuffer;
    
    /** * MUTABLE* If the current Mark is Open,  the matching close tag to look 
     * for (otherwise null) */ 
    private String closeTagForCurrentOpenMark;
    
    /** *MUTABLE* current character cursor index within the Markup */
    public int cursorIndex;
    
    /**
     * Builds and returns a Simple {@code MarkupState} with defaults  
     * @return
     */
    public CodeMLParseState()
    {
        this( VarContext.of( ) );
    }
    
    public CodeMLParseState( 
        VarContext parseContext )
    {
        this.parseContext = parseContext;
        this.domBuilder = new FillInTheBlanks.Builder();
        this.allMarkIndexes = new BitSet();
        this.allMarks = new ArrayList<Mark>();
        this.markBuffer = new StringBuilder();
        this.cursorIndex = 0;
    }
    
    public boolean isMarkOpen()
    {
        return markBuffer.length() > 0;
    }
    
    public String getFirstOpenTag( String line )
    {
    	return CodeMLParser.INSTANCE.getFirstOpenTag( line );
    }
    
    public String getCloseTagFor( String openTag )
    {
    	return CodeMLParser.INSTANCE.closeTagFor( openTag );
    }
    
    /** add "static" text to the document */ 
    public void addText( String staticText )
    {
        domBuilder.text( staticText );
        cursorIndex += staticText.length();
    }
    
    /** Append text to the exiting open mark*/
    public void startMark( String markText, String openTag, int lineNumber )
    {
        this.closeTagForCurrentOpenMark = 
        	CodeMLParser.INSTANCE.closeTagFor( openTag );
        this.markBuffer.append( markText );
        if( LOG.isTraceEnabled() )
    	{
    		LOG.trace( "   found \"" + openTag + "\" on line [" + lineNumber 
    			+ "] looking for \"" + this.closeTagForCurrentOpenMark + "\"" );
    	}
    }
    
    /** adds more text to a Mark */
    public void addToMark( String markText )
    {
        this.markBuffer.append( markText );
    }
    
    /**Reserves a Fillable blank at the current char position in the Form */
    public void reserveBlank()
    {
    	domBuilder.blank(); 
    }
    
    /** 
     * Add content and the closing tag to the current (buffered) mark, 
     * then create/add the {@code MarkAction} and return the State
     * @param contentWithCloseTag
     * @param lineNumber
     */
    public void completeMark( String contentWithCloseTag, int lineNumber )
    {
        if( isMarkOpen() && 
            !contentWithCloseTag.endsWith( closeTagForCurrentOpenMark ) )
        {
            throw new MarkupException(
                "could not complete a Mark : " + N
              + markBuffer.toString() + N + " with : " + N 
              + contentWithCloseTag + N  
              + "... expected content ending with closing tag \""
              + closeTagForCurrentOpenMark + "\"" );
        }
        markBuffer.append( contentWithCloseTag );
        String theMarkAsString = markBuffer.toString();
        
        //count the number of lines the Mark spans 
        int numLines = Lines.countTotal( theMarkAsString );
        Mark mark = CodeMLParser.INSTANCE.parseMark(
            this.parseContext,
            theMarkAsString, 
            ( lineNumber - ( numLines -1 ) ) //set the line number where the mark STARTS
            );
        
        allMarks.add( mark );
        if( LOG.isTraceEnabled() )
        {
        	LOG.trace("    parsed: " + mark );
        }
        allMarkIndexes.set( cursorIndex );
        
        this.cursorIndex ++;

        if( mark instanceof Mark.BlankFiller )
        {   //Marks often need to update the existing ParseState 
            //(i.e. add a Blank at a certain location of the {@Dom} so it
            //can populate it later, 
            //OR define / derive a static var
        	reserveBlank();
        }           
        //If the Mark is a Static Var assignment 
        //Setting MetaData for the Markup 
        if( mark instanceof BoundStatically )
        {
        	BoundStatically boundStatically = (BoundStatically)mark;
            boundStatically.onMarkParsed( this );
            if( LOG.isTraceEnabled() && mark instanceof IsNamed )
            {
            	String name = ((IsNamed)mark).getVarName();
            	Object val = this.parseContext.resolveVar( name );
            	LOG.trace( "    bound: " + mark + " as \"" + name 
            		+ "\"->" + val );
            }
        }
        resetMark();
    }
    
    public String getCloseTagForOpenMark()
    {
        return this.closeTagForCurrentOpenMark;
    }
    
    public String getMarkContents()
    {
        return markBuffer.toString();
    }
    
    private void resetMark()
    {
        markBuffer.delete( 0, markBuffer.length() );
        closeTagForCurrentOpenMark = null;
    }
    
    /** 
     * Convert the mutable Intermediate Representation (parsed by the compiler) 
     * into an immutable {@code Markup}.
     * 
     * @return the immutable {@code Markup}  
     */
    public Dom compile(  ) 
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new MarkupException( 
                "Unclosed Mark : " + N 
              + getMarkContents() + N 
              + "expected close Tag \"" + this.closeTagForCurrentOpenMark + "\"" );
        }    
        return new Dom( 
            domBuilder.compile(), 
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes, 
            parseContext );
    }

    public Builder getFillBuilder()
    {
        return domBuilder;
    }
    
    public VarContext getParseContext()
    {
        return this.parseContext;
    }

	public void setStaticVar( String varName, Object value ) 
	{
		parseContext.getOrCreateBindings( VarScope.STATIC ).put( varName, value );
	}   
}