package varcode.markup.bindml;

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
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.BoundStatically;
import varcode.markup.mark.Mark.IsNamed;

/**
 * Mutable Intermediate Representation (IR) Parse State for producing a 
 * {@code Dom} while text is being parsed by the 
 * {@code BindMLParser}. 
 * 
 * Internally acts like a "State Machine" that follows: 
 * 
 * <OL>
 *   <LI>seek next "open" tag within the text (at {@code tagOpenIndex})
 *   <LI>add all static text between {@code charCursor} and 
 *   {@code tagOpenIndex} to the document state
 *   <LI>seek the matching "close" tag {@code tagCloseIndex} within the 
 *   text after the {@code tagOpenIndex}.
 *   <LI>using the {@code BindMLParser} parse the text to a {@code Mark} 
 *   between {@code tagOpenIndex} and {@code tagCloseIndex} and 
 *   add the parsed {@code Mark} to the {@code BindMLParseState} 
 *   (adding a fill blank, or deriving a static var to the {@code VarContext} 
 *   when appropriate)
 *   <LI>set {@code charCursor} to the char after {@code tagCloseIndex}
 *   <LI>goto step (1) until the end of document is reached  
 * </OL>
 * 
 * <BLOCKQUOTE>
 * The {@code Dom} does not create a <A HREF="https://en.wikipedia.org/wiki/Parse_tree">parse tree</A> 
 * or an <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">"AST"</A> 
 * (we don't need a "tree" at all; each "Mark is a self-contained command"). 
 * The {@code BindMLCompiler} <B>sequentially</B> reads text and each {@code Mark}s 
 * (matching close tags to open tags) within the markup then parses each {@Mark} 
 * one (to add to the while the document is being read in)
 * </BLOCKQUOTE>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLParseState 
    implements MarkupParseState
{
	private static final Logger LOG = 
	    LoggerFactory.getLogger( BindMLParseState.class );
	
    public static final String N = "\r\n";
    
    /** Contains Utilities and Statically defined vars in the {@code Dom}*/
    public final VarContext parseContext;
        
    /** Builds a document with blanks and {@code Mark}s*/
    public final FillInTheBlanks.Builder domBuilder;
    
    /** stores the character indexes of ALL {@code Mark}s within the {@code Dom} */
    public final BitSet allMarkIndexes;
    
    /** All {@code Mark}s extracted from the {@code Dom} */  
    public final List<Mark> allMarks;

    /** Buffer for text bound to be a Mark inside a {@code Mark}s that spans lines */
    private final StringBuilder markBuffer;
    
    /** * MUTABLE* If the current Mark is Open,  the matching close tag to look 
     * for (otherwise null) */ 
    private String closeTagForCurrentOpenMark;
    
    /** *MUTABLE* current character cursor index within the Markup */
    public int cursorIndex;
    
    public BindMLParseState( VarContext parseContext )
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
    
    /** add "static" text to the document */ 
    public void addText( String staticText )
    {
        domBuilder.text( staticText );
        cursorIndex += staticText.length();
    }
    
    /** Start a new Mark with the open Tag */
    public void startMark( String markText, String openTag, int lineNumber )
    {    	
        this.closeTagForCurrentOpenMark = BindMLParser.INSTANCE.closeTagFor( openTag );
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
        Mark mark = BindMLParser.INSTANCE.parseMark(
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
        
        if( mark instanceof BlankFiller )
        {
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
     * into an immutable {@code VarCode}.
     * 
     * @return the immutable {@VarCode}  
     */
    public Dom compile() 
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new MarkupException( 
                "Unclosed Mark : " + N 
              + getMarkContents() + N 
              + "expected close Tag \"" + this.closeTagForCurrentOpenMark + "\"");
        }    
        
        Dom dom = new Dom(
            domBuilder.compile(), 
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes, 
            parseContext
            );
        
        LOG.trace( "3) Compiled Dom: " + N +  dom );
        return dom;
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