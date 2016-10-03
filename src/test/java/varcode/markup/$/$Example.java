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
package varcode.markup.$;

import varcode.markup.$ml.$CodeForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import varcode.doc.FillInTheBlanks;
import varcode.java.JavaMarkupRepo;
import varcode.markup.repo.MarkupRepo;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 *
 * @author eric
 */
public class $Example
{
    
    private static class $T
        extends $CodeForm
    {
        class $type${ }
        $type$[] $fieldName$;

    public $type$ get$FieldName$At( int index )
    {
/*{{$*/
    if( this.$fieldName$ == null )
    {
        throw new IndexOutOfBoundsException( "$fieldName$ is null" );
    }
    if( index < 0 || index > this.$fieldName$.length )
    {
        throw new IndexOutOfBoundsException( 
            "index " + index + " is not in [0..." + this.$fieldName$.length + "]" );
    }
    return this.$fieldName$[ index ];            
/*$}}*/        
    }

    }   
    
    public static final String OPEN = "/*{{$*/";
    public static final String CLOSE = "/*$}}*/";
    
    public static void main( String[] args )
        throws IOException
    {
        JavaMarkupRepo jmr = JavaMarkupRepo.INSTANCE;
        MarkupStream ms = jmr.markupStream( $T.class );
        String s = ms.asString();
        int startIndex = s.indexOf( OPEN );
        int endIndex = s.indexOf( CLOSE );
        String inner = s.substring( startIndex + OPEN.length() , endIndex );
        //System.out.println( inner );  
        
        parseTemplate( inner );
    }
    
    public static void parseTemplate( String $template )
        throws IOException
    {
        BufferedReader sourceReader = 
            new BufferedReader( new StringReader( $template ) ); 
        
        String line = null;
        
        List<String> params = new ArrayList<String>();
        
        FillInTheBlanks.Builder fillBuilder = new FillInTheBlanks.Builder();
        
        int lineNumber = 0;
        while( ( line = sourceReader.readLine() ) != null ) 
        {
            if( lineNumber > 0 )
            {
                fillBuilder.text( "\r\n" );
            }
            parse( line, fillBuilder, params );
            lineNumber++;
        }
        
        FillInTheBlanks.FillTemplate template = fillBuilder.compile(); 
        System.out.println( fillBuilder.compile() );
        System.out.println( params );
        
        System.out.println( 
            template.fill( (Object[]) params.toArray( new String[0] ) ) ); 
    }
    
    public static void parse( String line, FillInTheBlanks.Builder fillBuilder, List<String> params )
        throws IOException
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
                parse( line.substring( $closeIndex + 1 ), fillBuilder, params );                    
            }
            else
            {
                fillBuilder.text( line );
            }
        }                
    }
    
    public static String firstLower( String in )
    {
        return Character.toLowerCase( in.charAt( 0 ) ) + in.substring( 1 );
    }
    
    private static String tagFor$Token$( String $token$ )
    {
        if( Character.isUpperCase( $token$.charAt( 1 ) ) )
        {
            return "{+$^(" + 
                firstLower( $token$.substring( 1, $token$.length() ) ) + ")*+}";
        }
        else
        {
            return "{+" + $token$.substring( 1, $token$.length() ) + "*+}";
        }
    }
    
}
