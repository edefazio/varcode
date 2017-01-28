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
package varcode.markup.forml;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import varcode.markup.FillInTheBlanks;
import varcode.markup.form.FormTemplate;
import varcode.markup.MarkupException;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.Bind;

/**
 * Mutable Intermediate Representation (IR) Parse State for producing a
 * {@code Form} while source is being read in and parsed
 *
 * Contains {@code MarkActions}s assigned to FillBlanks and static text.
 *
 * Internally acts like a "State Machine" that follows:
 *
 * <OL>
 * <LI>seek next open tag within the text (at {@code tagOpenIndex})
 * <LI>add all static text between {@code charCursor} and {@code tagOpenIndex}
 * to the state
 * <LI>seek the matching "close" tag {@code tagCloseIndex} within the text after
 * the {@code tagOpenIndex}.
 * <LI>using the {@code MarkParser} parse the Mark text between
 * {@code tagOpenIndex} and {@code tagCloseIndex} and add the parsed
 * {@code Mark} to the {@code CompileState} (signifying the blank if
 * appropriate)
 * <LI>set {@code charCursor} to after {@code tagCloseIndex}
 * <LI>goto step (1) until the end of document is reached
 * </OL>
 *
 * The following methods mutate the contents of the State
 * <UL>
 * <LI>startMark
 * <LI>appendToMark
 * <LI>completeMark
 * <LI>appendText
 * </UL>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ForMLCompileState
//implements CompileState
{
    public static final String N = "\r\n";

    /**
     * All static text and a BitSet indicating {@code MarkAction}s locations
     */
    public final FillInTheBlanks.Builder fillBuilder;

    /**
     * stores the character indexes of ALL {@code Mark}s within the form markup
     */
    public final BitSet allMarkIndexes;

    /**
     * All marks extracted from the form markup
     */
    public final List<Mark> allMarks;

    /**
     * If the current mark is open, the matching close tag to complete the mark
     * (otherwise null)
     */
    private String closeTagForCurrentOpenMark;

    /**
     * Buffer for text inside a Mark that spans lines
     */
    private final StringBuilder markBuffer;

    /**
     * *MUTABLE* current character cursor index within the markup
     */
    public int cursorIndex;

    public ForMLCompileState()
    {
        this.fillBuilder = new FillInTheBlanks.Builder();
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
     */
    public void addText( String staticText )
    {
        this.fillBuilder.text( staticText );
        this.cursorIndex += staticText.length();
    }

    /**
     * Append text to the exiting open mark
     */
    public void startMark( String markText, String openTag )
    {
        this.closeTagForCurrentOpenMark = ForMLCompiler.closeTagFor( openTag );
        this.markBuffer.append( markText );
    }

    /**
     * adds text to a Mark Buffer
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
        this.fillBuilder.blank();
    }

    /**
     * Add the appropriate {@link Mark} based on the contents of the markBuffer
     * after adding the contentandCloseMarkTag
     *
     * @param contentWithCloseTag the text that completes the Markup
     * {@link Mark}
     * @param lineNumber the line Number the {@link Mark} is closed on
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
                + "... expected content ending with closing Mark \""
                + closeTagForCurrentOpenMark + "\"" );
        }
        markBuffer.append( contentWithCloseTag );

        Mark ma = ForMLCompiler.compileMark(
            markBuffer.toString(),
            lineNumber );
        allMarks.add( ma );

        allMarkIndexes.set( cursorIndex );

        this.cursorIndex++;

        if( ma instanceof Bind )
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
     * Convert the mutable Intermediate Representation (parsed by the compiler)
     * into an immutable {@link FormTemplate}.
     *
     * @return the immutable {@link FormTemplate}
     */
    public FormTemplate compile()
    {
        if( isMarkOpen() )
        {   //UNCLOSED MARK
            throw new MarkupException(
                "Unclosed Form Mark :" + N
                + getMarkContents() + N
                + "...expected close Tag \"" + this.closeTagForCurrentOpenMark + "\"" );
        }
        return new FormTemplate(
            fillBuilder.compile(),
            allMarks.toArray( new Mark[ 0 ] ),
            allMarkIndexes );
    }
}
