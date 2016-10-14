/*
 * Copyright 2016 eric.
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
package varcode.markup.codeml.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import varcode.VarException;
import varcode.doc.FillInTheBlanks;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _Parse
{
    /** The Tag to mark the beginning of content and the "left gutter" */
    public static final String OPEN = "/*$*/";
    
    /** The Tag to mark the end of content and the "left gutter" */
    public static final String CLOSE = "/*$*/";
    
    // /*$*/ returns content within here /*$*/
    /**
     * 
     * @param _methodClass the class containing the /$/ /$/ Markup
     * (NOTE: this allows multiple classes within the same source code to define
     * their own templates)
     * @param sourceCode the full source code of a "Container class" 
     * (usually a "Top Level Class will contain one or more "_Method" 
     * or "_Constructor" or "_Code" classes with their own code.
     * 
     * @return String content within the demarcated $'s
     */    
    public static String extractContentIn$$( Class _methodClass, String sourceCode )
    {
        //NOW, within the stream, parse out the /*$*/ ... /*$*/
        int classIndex = sourceCode.indexOf( "class " + _methodClass.getSimpleName() );
        int startIndex = sourceCode.indexOf( OPEN, classIndex );
        int endIndex = sourceCode.indexOf(  CLOSE, startIndex + 1 );
        String inner = sourceCode.substring( startIndex + OPEN.length(), endIndex ).trim();
        return inner;
    }
    
    /**
     * Code "in the wild" is prefixed with spaces... 
     * based on the hierarchal structure
     * 
     * this will normalize / extract the prefix spaces to the minimal
     * amount (as if the code is written left justified)
     * 
     * remove excess prefix spaces on each line of the formString 
     * @param formString
     * @return 
     */
    protected static String chopPrefixSpaces(
        String formString, int prefixSpaces )
    {       
        try
        {
            LineNumberReader lnr = new LineNumberReader( 
                new StringReader( formString ) );
                        
            String line = lnr.readLine();
            //System.out.println( "\""+ line +"\"" );
            StringBuilder normalized = new StringBuilder();
            boolean firstLine = true;
            while( line != null )
            {
                //we trimmed the first line of spaces
                if( !firstLine ) 
                {
                    //prefix a line break before the next line
                    normalized.append( "\r\n" ); 
                    normalized.append( line.substring( prefixSpaces ) );
                }
                else
                {
                    normalized.append( line );
                }
                line = lnr.readLine();
                firstLine = false;
            }
            return normalized.toString();
        }
        catch( IOException ioe )
        {
            throw new VarException( "Unable to read lines", ioe );
        }
    }
    
    /**
     * Looks at the amount of spaces I can remove from each line in a block of 
     * text to have the same structure, only have the left gutter justified at
     * char 0
     * 
     * @param line
     * @param currentPrefixSpaces
     * @return 
     */
    public static int countPrefixSpaces( String line, int currentPrefixSpaces )
    {
        int preSpaces = 0;
        for( int i = 0; i < line.length(); i++ )
        {
            if( Character.isWhitespace( line.charAt( i ) ) )
            {
                preSpaces++;
            }
            else
            {
                break;
            }
        }
        if( currentPrefixSpaces < 0 )
        {
            return preSpaces;
        }
        else
        {
            return Math.min( currentPrefixSpaces, preSpaces );
        }
    }
    
    /**
     * Takes in a $Template 
     * (where "parameters" are signified by leading and trailing $'s, like:
     * 
     * "public $returnType$ get$Name$()" ($returnType$ and $Name$ are parameters)
     * ... and replaces them with CodeMLTags:
     */ 
     //public "/*{+returnType+}*/ get/*{+$^(name)+}*/()"
    
     /* 
     * @param $template template with $'s demarcating parameters
     * @return the CodeML String
     */
    public static String toCodeML( String $template )
    {
        BufferedReader sourceReader = 
            new BufferedReader( new StringReader( $template ) ); 
        
        String line = null;
        
        //$...$ delineated content that is converted into CodeML {+marks+}        
        List<String> marks = new ArrayList<String>();
        
        // contains static content and "blanks" where marks will be inserted
        // to create CodeML markup document        
        FillInTheBlanks.Builder docBuilder = new FillInTheBlanks.Builder();
        
        int lineNumber = 0;
        int prefixSpaces = -1;
        try
        {
            while( ( line = sourceReader.readLine() ) != null ) 
            {
                if( lineNumber > 0 )
                {
                    docBuilder.text( "\r\n" );
                    prefixSpaces = countPrefixSpaces( line, prefixSpaces );
                }
                parseLine( line, docBuilder, marks );
                lineNumber++;
            }
        
            //compile the static text and blanks
            FillInTheBlanks.FillTemplate template = docBuilder.compile(); 
        
            //fill in the CodeML {+marks+} where the $...$s were
            String codeMLDoc = 
                template.fill((Object[]) marks.toArray( new String[0] ) );
            
            //left-justify the CodeML document before returning
            return _Parse.chopPrefixSpaces( codeMLDoc, prefixSpaces );             
        }
        catch( IOException ioe )
        {
            throw new VarException( "Unable to parse template ", ioe );
        }
    }
    
    /**
     * Given a line, find any $...$ marks and convert them to {+...+} marks
     * and populate "blanks" for all marks (using a FillBuilder) to compile
     * into a CodeML template.
     * 
     * takes a line like this:
     * <PRE>
     * "Hi $salutation$, I am $Name$ from accounting"
     * 
     * ...and parses out the $...$ parameters
     * "Hi ____________, I am ______ from accounting"
     * 
     * creating Marks for these parameters
     * {+salutation+}, {+$^(name)+}
     * 
     * ...and (upon completion) fills the "blanks" with Marks to create a 
     * CodeML compliant :
     * "Hi {+salutation+}, I am {+$^(name)+} from accounting"
     * 
     * </PRE>
     * @param line the source line
     * @param fillBuilder the incremental builder keeping track of all lines
     * @param marks marks to be populated within the text to create CodeML marks
     */
    protected static void parseLine( 
        String line, FillInTheBlanks.Builder fillBuilder, List<String> marks )
    {
        int $openIndex = line.indexOf( '$' );
        if( $openIndex < 0 )
        {   //no $..$ on this line
            fillBuilder.text( line );
        }
        else 
        {   //there MAY be a $...$ token on this line
            int $closeIndex = line.indexOf( '$', $openIndex + 1 );  
            if( $closeIndex > $openIndex )
            {
                //add everything BEFORE the open
                fillBuilder.text( line.substring( 0, $openIndex ) );
                
                //add a blank for the Mark
                fillBuilder.blank();
                String $token$ = line.substring( $openIndex, $closeIndex );
                
                //System.out.println( theTag );
                marks.add( markFor$Token$( $token$ ) );
                parseLine(
                    line.substring( $closeIndex + 1 ), fillBuilder, marks );                    
            }
            else
            {   // No $...$ on this line
                fillBuilder.text( line );
            }
        }                
    }
    
    private static String firstLower( String in )
    {
        return Character.toLowerCase( in.charAt( 0 ) ) + in.substring( 1 );
    }
    
    /** 
     * given a "$token$" returns the appropriate CodeML "{+mark+}" 
     * @param $token$ a $denoted$ token i.e. $name$ or $Name$
     * @return the appropriate Mark, i.e. {+name+} or {+$^(name)+}
     */
    protected static String markFor$Token$( String $token$ )
    {
        //if the character AFTER the $ is uppercase, means I want variable uppercased
        if( Character.isUpperCase( $token$.charAt( 1 ) ) )
        {
            String tokenName = firstLower(
                $token$.substring( 1, $token$.length() ) );
            return "{+$^(" + tokenName + ")*+}";
        }
        else
        {
            String tokenName = $token$.substring( 1, $token$.length() );
            return "{+" + tokenName + "*+}";            
        }
    }
}
