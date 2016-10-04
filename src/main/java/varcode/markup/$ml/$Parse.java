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
package varcode.markup.$ml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import varcode.VarException;
import varcode.doc.FillInTheBlanks;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum $Parse    
{        
    ; //singleton enum idiom
    
    
    /** signifies the beginning of the code form within a class */ 
    public static final String OPEN = "/*{$*/";
    
    /** signifies the end of the code form within a class */ 
    public static final String CLOSE = "/*$}*/";
 
    protected static String parseTemplate( String $template )
    {
        BufferedReader sourceReader = 
            new BufferedReader( new StringReader( $template ) ); 
        
        String line = null;
        
        List<String> params = new ArrayList<String>();
        
        FillInTheBlanks.Builder fillBuilder = new FillInTheBlanks.Builder();
        
        int lineNumber = 0;
        int prefixSpaces = -1;
        try
        {
            while( ( line = sourceReader.readLine() ) != null ) 
            {
                if( lineNumber > 0 )
                {
                    fillBuilder.text( "\r\n" );
                    prefixSpaces = countPrefixSpaces( line, prefixSpaces );
                }
                parseLine( line, fillBuilder, params );
                lineNumber++;
            }
        
            FillInTheBlanks.FillTemplate template = fillBuilder.compile(); 
        
            String formString = template.fill( (Object[]) params.toArray( new String[0] ) );
        
            //System.out.println( "PREFIX SPACES " + prefixSpaces );
            
            formString = normalizeFormPrefixSpaces( formString, prefixSpaces ); 
            
            return formString;
        }
        catch( IOException ioe )
        {
            throw new VarException( "Unable to parse template ", ioe );
        }
    }
    
    protected static Dom parseTemplateDom( String $template )
    {
        String formString = parseTemplate( $template );
        return BindML.compile( formString );        
    }
    
    public static String normalizeFormPrefixSpaces( String formString, int prefixSpaces )
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
                
                //if( line.length() > prefixSpaces )
                //{
                
                //}
                //else
                //{
                //    normalized.append( line );
                //}
                line = lnr.readLine();
                firstLine = false;
            }
            return normalized.toString();
        }
        catch( IOException e )
        {
            throw new VarException( "Unable to read lines" );
        }
    }
    
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
    
    protected static void parseLine( 
        String line, FillInTheBlanks.Builder fillBuilder, List<String> params )
    {
        int $openIndex = line.indexOf( '$' );
        if( $openIndex < 0 )
        {
            fillBuilder.text( line );
        }
        else 
        {
            int $closeIndex = line.indexOf( '$', $openIndex + 1 );  
            if( $closeIndex > $openIndex )
            {
                //add everything BEFORE the open
                fillBuilder.text( line.substring( 0, $openIndex ) );
                
                //add a blank
                fillBuilder.blank();
                String theTag = line.substring($openIndex, $closeIndex );
                
                //System.out.println( theTag );
                params.add( tagFor$Token$( theTag ) );
                parseLine( line.substring( $closeIndex + 1 ), fillBuilder, params );                    
            }
            else
            {
                fillBuilder.text( line );
            }
        }                
    }
    
    private static String firstLower( String in )
    {
        return Character.toLowerCase( in.charAt( 0 ) ) + in.substring( 1 );
    }
    
    private static String tagFor$Token$( String $token$ )
    {
        if( Character.isUpperCase( $token$.charAt( 1 ) ) )
        {
            String tokenName = firstLower( 
                $token$.substring( 1, $token$.length() ) );
            return "{+$^(" + tokenName + ")*+}";
        }
        else
        {
            String tokenName = $token$.substring( 1, $token$.length() );
            //return "{+" + tokenName + "*+}";
            //TODO make this an or
            return "{+" + tokenName + "|" + tokenName + "+}";
            
        }
    }
}
