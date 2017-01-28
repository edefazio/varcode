/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.markup.bindml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import varcode.markup.FillInTheBlanks;
import varcode.markup.FillInTheBlanks.Builder;
import varcode.markup.Template;
import varcode.markup.MarkupException;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.Bind;

/**
 * Collects Marks and Text while in the process of Parsing/Compiling
 * the input {@link BindML} document.
 * 
 * Mutable Intermediate Representation (IR) Parse State for producing a
 * {@code Dom} while text is being parsed by the {@code BindMLCompiler}.
 *
 * Internally acts like a "State Machine" that follows:
 *
 * <OL>
 * <LI>seek next "open" tag within the text (at {@code tagOpenIndex})
 * <LI>add all static text between {@code charCursor} and {@code tagOpenIndex}
 * to the document state
 * <LI>seek the matching "close" tag {@code tagCloseIndex} within the text after
 * the {@code tagOpenIndex}.
 * <LI>using the {@code BindMLCompiler} parse the text to a {@code Mark} between
 * {@code tagOpenIndex} and {@code tagCloseIndex} and add the parsed
 * {@code Mark} to the {@code BindMLCompileState} (adding a fill blank, or
 * deriving a static var to the {@code VarContext} when appropriate)
 * <LI>set {@code charCursor} to the char after {@code tagCloseIndex}
 * <LI>goto step (1) until the end of document is reached
 * </OL>
 *
 * <BLOCKQUOTE>
 * The {@code Frame} does not create a
 * <A HREF="https://en.wikipedia.org/wiki/Parse_tree">parse tree</A>
 * or an <A HREF="https://en.wikipedia.org/wiki/Abstract_syntax_tree">"AST"</A>
 * (we don't need a "tree" at all; each "Mark is a self-contained command"). The
 * {@code BindMLCompiler} <B>sequentially</B> reads text and each {@code Mark}s
 * (matching close tags to open tags) within the markup then parses each {
 *
 * @Mark} one (to add to the while the document is being read in)
 * </BLOCKQUOTE>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLCompileState
{
    public static final String N = "\r\n";

    /** Builds a {@BlankBinding} by processing {@code Mark}s in the markup */
    public final FillInTheBlanks.Builder templateBuilder;

    /** character indexes of ALL {@code Mark}s within the markup */
    public final BitSet allMarkIndexes;

    /** All {@code Mark}s extracted from the String markup */
    public final List<Mark> allMarks;

    /**
     * Temporary Buffer for text bound to be a Mark inside a 
     * {@code Mark}s that spans lines. 
     * (we process line by line, and if we reach the end of a line before the
     * end of a mark, (we cant fully create the {@link Mark} implementation)
     * this buffer will keep track of the contents of the mark as we read 
     * from the next line
     */
    private final StringBuilder markBuffer;

    /**
     * * MUTABLE* If the current Mark in the Markup is Open, 
     * the matching close tag to look for (otherwise null)
     */
    private String closeTagForCurrentOpenMark;

    /** *MUTABLE* current character cursor index within the Markup */
    public int cursorIndex;

    public BindMLCompileState( )
    {
        this.templateBuilder = new FillInTheBlanks.Builder();
        this.allMarkIndexes = new BitSet();
        this.allMarks = new ArrayList<Mark>();
        this.markBuffer = new StringBuilder();
        this.cursorIndex = 0;
    }

    public boolean isMarkOpen()
    {
        return markBuffer.length() > 0;
    }

    /**
     * add "static" text to the document
     * @param staticText
     */
    public void addText( String staticText )
    {
        templateBuilder.text( staticText );
        cursorIndex += staticText.length();
    }

    /**
     * Start a new Mark with the open Tag
     * @param markText
     * @param openTag
     * @param lineNumber
     */
    public void startMark( String markText, String openTag, int lineNumber )
    {
        this.closeTagForCurrentOpenMark = BindMLCompiler.matchCloseTag( openTag );
        this.markBuffer.append( markText );
        //if( LOG.isTraceEnabled() )
        // {
        //    LOG.trace( "   found \"" + openTag + "\" on line [" + lineNumber
        //        + "] looking for \"" + this.closeTagForCurrentOpenMark + "\"" );
        //}
    }

    /**
     * adds more text to a Mark
     */
    public void addToMark( String markText )
    {
        this.markBuffer.append( markText );
    }

    /**
     * Reserves a Fillable blank at the current char position in the Form
     */
    public void reserveBlank()
    {
        templateBuilder.blank();
    }

    /**
     * Add content and the closing tag to the current (buffered) mark, then
     * create/add the {@code MarkAction} and return the State
     *
     * @param contentWithCloseTag
     * @param lineNumber
     */
    public void completeMark( String contentWithCloseTag, int lineNumber )
    {
        if( isMarkOpen()
            && !contentWithCloseTag.endsWith( closeTagForCurrentOpenMark ) )
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
        int numLines = countLines( theMarkAsString );
        Mark mark = BindMLCompiler.parseMark(
            //this.compileContext,
            theMarkAsString,
            (lineNumber - (numLines - 1)) //set the line number where the mark STARTS
        );

        allMarks.add( mark );
        //if( LOG.isTraceEnabled() )
        //{
        //    LOG.trace( "    parsed: " + mark );
        //}
        allMarkIndexes.set( cursorIndex );

        this.cursorIndex++;

        if( mark instanceof Bind )
        {
            reserveBlank();
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
     * Convert the completed CompileState (parsed by the compiler)
 into an immutable {@code Template}.
     * @return the immutable {@code Template}
     */
    public Template compile()
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new MarkupException(
                "Unclosed Mark : " + N
                + getMarkContents() + N
                + "expected close Tag \"" + this.closeTagForCurrentOpenMark + "\"" );
        }

        Template template = new Template(
            templateBuilder.compile(),
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes ) ; 
        //LOG.trace( "3) Compiled Template: " + N + template );
        return template;
    }

    public Builder getFillBuilder()
    {
        return templateBuilder;
    }
    
        /** 
     * Counts the total number of lines that exist in the {@code source} 
     * String.
     * 
     * @param source the source string
     * @return the number of lines (1...n) in the source String or
     * 0 if {@code source} is null or empty.
     */
    public static int countLines( String source )
    {
        if( source == null || source.isEmpty() )
        {
            return 0;
        }
        int lines = 1;
        int pos = 0;
        while( (pos = source.indexOf( "\n", pos ) + 1) != 0 )
        {
            lines++;
        }
        return lines;
    }
}
