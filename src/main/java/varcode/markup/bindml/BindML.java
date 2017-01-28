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

import java.io.*;

import varcode.markup.Template;
import varcode.markup.MarkupException;
import varcode.load.Source.SourceStream;
import varcode.markup.MarkupLanguage;

/** 
 * A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A> for 
 * logically binding data into text to produce "tailored documents". 
 * 
 * Aggregates the components of the Bind Markup Language
 * (an implementations for producing {@code Markup})   
 * BindML expands upon ForML in being able to:
 *  
 * <UL>
 * <LI> define {# #} {##  ##} entities (that aren't immediately added to the document)
 * <LI> build and use expressions (( )) and call scripts {$ $} 
 * to have MORE logic to the document
 * <LI> define and embed FORMS {{ }} 
 * <LI> include metadata {@ @} about the document
 * </UL>
 * 
 * Whereas a ForML Markup document is immediate and straightforward 
 * (the purpose of all ForML marks is to adds variable text into a preset document).  
 * A BindML Markup document contains a layer of logic, for more complex document bindings
 * ( the Markup itself may define its own variables, derive and mutate input data,
 * scripts for validating the input, and manipulating the variables in the context 
 * BOTH when the Markup is being compiled (statically) and when the Markup 
 * is being Tailored (dynamically).   
 * 
 * Bind Markup Language supports the following Marks for Binding Text:
 * <UL>
 * <LI><CODE>"{+name+}"</CODE>  {@link varcode.markup.mark.AddVar}
 * <LI><CODE>"{+$script()+}"</CODE>   {@link varcode.markup.mark.AddScriptResult}
 * <LI><CODE>"{+?var:addThis+}"</CODE>  {@link varcode.markup.mark.AddIfVar}
 * <LI><CODE>"{{+:{+fieldType+} {+fieldName+}+}}"</CODE> {@link varcode.markup.mark.AddForm}
 * <LI><CODE>"{_+:{+fieldType+} {+fieldName+}+_}"</CODE> {@link varcode.markup.mark.AddForm}
 * <LI><CODE>"{{+?a==1: implements {+impl+}+}}"</CODE> {@link varcode.markup.mark.AddFormIfVar}
 * <LI><CODE>"{_+?a==1: implements {+impl+}+_}"</CODE> {@link varcode.markup.mark.AddFormIfVar}
 * </UL>
 * 
 * 
 * <UL>
 * <LI><CODE>"{$print(a)$}"</CODE>  {@link varcode.markup.mark.RunScript}		
 * <LI><CODE>"{$$removeEmptyLines()$$}"</CODE>  {@link varcode.markup.mark.AuthorDirective}		
 * </UL>
 * 
 * Reads/Parses text <B>line-by-line</B> and to build up and returns a
 * {@code Markup} containing {@code Mark}s, Text, and Blanks.
 *
 * Internally acts like a "State Machine" that follows:
 *
 * <OL>
 * <LI>seek next open tag within the text (at {@code tagOpenIndex})
 * <LI>add all static text between {@code charCursor} and the
 * {@code tagOpenIndex} to the state
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
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum BindML
    implements MarkupLanguage
{
    ; //singleton enum idiom
    
    private static final String N = "\r\n";

    private BindML()
    { }

    /**
     * Creates a template based on the lines of text 
     * @param lines lines of text
     * @return Template representing the compiled lines
     */
    public static Template compileLines( String...lines )
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < lines.length; i++ )
        {
            sb.append( lines[ i ] );            
            sb.append( System.lineSeparator() );
        }
        return compile( sb.toString() );
    }
    
    public static Template compile( String bindMLMarkup )
    {
        try
        {
            ByteArrayInputStream bais
                = new ByteArrayInputStream(
                    bindMLMarkup.getBytes( "UTF-8" ) );

            return BindML.compile( bais );
        }
        catch( UnsupportedEncodingException e )
        {
            throw new MarkupException( "UTF-8 unsupported Encoding", e );
        }
    }
    
    /**
     * 
     * @param inStream the input stream containing the Test with markup
     * @return the compiled {@code Template}
     */
    public static Template compile( InputStream inStream )
    {
        BufferedReader buffReader
            = new BufferedReader(
                new InputStreamReader( inStream ) );

        return BindML.compile( buffReader );
    }
    
    public static Template compile( BufferedReader bufferedReader )
    {
        return compile( bufferedReader, new BindMLCompileState( ) );
    }

    public static Template compile( SourceStream sourceStream )
        throws MarkupException
    {
        if( sourceStream == null )
        {
            throw new MarkupException( "the SourceStream is null " );
        }
        return compile( new BufferedReader(
            new InputStreamReader( sourceStream.getInputStream() ) ),
            new BindMLCompileState() );
    }
    /**
     * 
     * @param sourceReader
     * @param compileState
     * @return 
     */
    public static Template compile(
        BufferedReader sourceReader, BindMLCompileState compileState )
    {
        try
        {
            String line = "";
            int lineNumber = 1; //initialize the line Number

            boolean firstLine = true;

            while( (line = sourceReader.readLine()) != null )
            {
                if( !firstLine )
                {   //"prepend" the new line before processing the line 
                    if( compileState.isMarkOpen() )
                    {   //previous line ended and a Mark wasn't closed
                        compileState.addToMark( N );
                    }
                    else
                    {   //move static text to next line
                        compileState.addText( N );
                    }
                }
                else
                {
                    firstLine = false;
                }
                if( compileState.isMarkOpen() )
                {   //if previous MARKS aren't closed  
                    lineOpenMark(line,
                        lineNumber,
                        compileState );
                }
                else
                {
                    line(line,
                        lineNumber,
                        compileState );
                }
                lineNumber++;
            }
            return compileState.compile();
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
                //LOG.warn( "Exception closing Input Reader ", e );
            }
        }
    }

    /**
     * Process the next line of text compile the code varcode source, knowing that
     * a mark (compile some previous line) has not been closed.<BR>
     *
     * (a Mark spans multiple lines)
     *
     * @return an Empty StringBuilder if we have closed the marks -or- a
     *  StringBuilder containing internal Mark data compile the line
     */
    private static void lineOpenMark(
        String sourceLine,
        int lineNumber,
        BindMLCompileState compileState )
    {   //check if the open Mark is closed this line
        String closeTag = compileState.getCloseTagForOpenMark();

        int indexOfCloseMark
            = sourceLine.indexOf( closeTag );

        if( indexOfCloseMark >= 0 )
        {
            //if( LOG.isTraceEnabled() )
            //{
            //    LOG.trace("    found \"" + closeTag + "\" for open Mark \"" 
            //        + compileState.getMarkContents()
            //        + "\" on line [" + lineNumber + "]" );
            //}
            compileState.completeMark(
                sourceLine.substring( 0, indexOfCloseMark + closeTag.length() ),
                lineNumber );

            //process what is left of the line (after the close mark)
            line(sourceLine.substring( indexOfCloseMark + closeTag.length() ),
                lineNumber,
                compileState );
            return;
        }
        //mark was NOT closed this line, add this line contentx to existing Open Mark
        compileState.addToMark( sourceLine );
    }

    /**
     * Process the data within the {@code sourceLine} at {@code lineNumber}
     * (update the {@code builder} with any tags, internal test or document text
     *
     * @param sourceLine the source line to parse
     * @param lineNumber
     * @param compileState updates the {@code Dom} being built
     */
    private static void line(
        String sourceLine,
        int lineNumber,
        BindMLCompileState compileState )
    {   //this is the CLOSE Mark for EITHER ALONE or REPLACE Marks		    

        String firstOpenTag = BindMLCompiler.firstOpenTag( sourceLine );

        if( firstOpenTag != null )
        {   //Opened a mark this line
            int indexOfOpenMark = sourceLine.indexOf( firstOpenTag );
            //add everything before the open tag
            compileState.addText( sourceLine.substring( 0, indexOfOpenMark ) );

            String matchingCloseTag
                = BindMLCompiler.matchCloseTag( firstOpenTag );

            //find a close tag AFTER the OPEN Tag
            int closeTagIndex = sourceLine.indexOf(
                matchingCloseTag, indexOfOpenMark + firstOpenTag.length() );

            if( closeTagIndex >= 0 )
            {   //the Mark is closed this line
                compileState.completeMark(
                    sourceLine.substring(
                        indexOfOpenMark,
                        closeTagIndex + matchingCloseTag.length() ),
                    lineNumber );

                //process the rest of the line (AFTER the CLOSE TAG of MARK)
                line(sourceLine.substring(
                        closeTagIndex + matchingCloseTag.length(),
                        sourceLine.length() ),
                    lineNumber,
                    compileState );
                return;
            }
            else
            {   //the mark is not closed this line
                compileState.startMark(
                    sourceLine.substring( indexOfOpenMark ),
                    firstOpenTag,
                    lineNumber );
                return;
            }
        }
        else
        {   //NO Tags/ Marks this line (just text)
            compileState.addText( sourceLine );
        }
    }
} //BindML
