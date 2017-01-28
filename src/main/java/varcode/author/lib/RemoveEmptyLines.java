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
package varcode.author.lib;

import varcode.context.Context;
import varcode.author.AuthorState;
import varcode.author.PostProcessor;
import varcode.context.VarScript;

/**
 * Accepts String (code) as input, removes any empty lines (those with only
 * whitespace and carriage returns) then returns the String (code) as output.
 * Can be called as a Directive (which will remove all empty lines within the
 * source in a postprocess manner (AFTER Tailoring):
 *
 * /*{#$removeEmptyLines()}
 */
/**
 * Can be called as a VarScript to replace Wrapped Code:
 */
/*{+$removeEmptyLines(*/
//this
// has
// empty lines
/*)}*/
 /* 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum RemoveEmptyLines
    implements VarScript, PostProcessor
{
    INSTANCE;

    /**
     * Parse the String into multiple lines and return the lines (Blank lines
     * with ONLY carriage returns are omitted)
     *
     * @param source
     * @return
     */
    public static String[] separateOmitBlanks( String source )
    {
        if( source == null )
        {
            return null;
        }
        if( source.isEmpty() )
        {
            return new String[ 0 ];
        }
        return source.split( "\\r?\\n" );
    }

    public static String from( String sourceCode )
    {
        String[] lines = separateOmitBlanks( sourceCode );
        StringBuilder sb = new StringBuilder();

        for( int i = 0; i < lines.length; i++ )
        {
            if( !(lines[ i ].trim().length() == 0) )
            {
                sb.append( lines[ i ] );
                if( i < lines.length - 1 )
                {
                    sb.append( System.lineSeparator() );
                }
            }
        }
        String res = sb.toString();
        if( (lines.length > 1) && countLines( res ) == 1 )
        {
            return res + System.lineSeparator();
        }
        return res;
    }

    @Override
    public Object eval( Context context, String input )
    {
        return from( input );
    }

    @Override
    public void postProcess( AuthorState authorState )
    {
        String theResult = RemoveEmptyLines.from( 
            authorState.getTranslateBuffer().toString() );
        authorState.getTranslateBuffer().clear();
        authorState.getTranslateBuffer().append( theResult );
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
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
