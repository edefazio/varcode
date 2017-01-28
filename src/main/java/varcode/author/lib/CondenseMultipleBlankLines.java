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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import varcode.author.AuthorState;
import varcode.author.PostProcessor;
import varcode.context.VarBindException;

/**
 * Given a String, indents each line a number of spaces
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CondenseMultipleBlankLines
    implements PostProcessor
{
    INSTANCE;

    @Override
    public void postProcess( AuthorState authorState )
    {
        String original = authorState.getTranslateBuffer().toString();
        authorState.getTranslateBuffer().replaceBuffer( 
            condenseEmptyLines( original ).toString() );
    }

    public StringBuilder condenseEmptyLines( String input )
    {
        if( input == null )
        {
            return new StringBuilder();
        }
        StringBuilder fb = new StringBuilder();

        BufferedReader br = new BufferedReader(
            new StringReader( input ) );

        String line;
        boolean wasPreviousLineEmpty = true;
        boolean firstLine = true;
        try
        {
            line = br.readLine();
            while( line != null )
            {
                if( line.trim().length() == 0 )
                {
                    if( !wasPreviousLineEmpty )
                    {   //this line is empty but the previous line was not empty
                        fb.append( "\r\n" );
                        wasPreviousLineEmpty = true;
                    }
                }
                else
                {
                    wasPreviousLineEmpty = false;
                    if( !firstLine )
                    {
                        fb.append( "\r\n" );
                    }
                    firstLine = false;
                    fb.append( line );
                    //	firstLine = false;
                }

                line = br.readLine();
            }
            return fb;
        }
        catch( IOException e )
        {
            throw new VarBindException( "Error indenting spaces", e );
        }
    }

    public static String doCondense( String string )
    {
        return INSTANCE.condenseEmptyLines( string ).toString();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
