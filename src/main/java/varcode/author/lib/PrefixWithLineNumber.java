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
import java.util.Set;

import varcode.author.AuthorState;
import varcode.author.PostProcessor;
import varcode.context.VarBindException;

/**
 * Given a String, indents each line a number of spaces
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PrefixWithLineNumber
    implements PostProcessor
{
    public static final PrefixWithLineNumber INSTANCE = new PrefixWithLineNumber();

    public PrefixWithLineNumber()
    {
    }

    @Override
    public void postProcess( AuthorState authorState )
    {
        String original = authorState.getTranslateBuffer().toString();
        authorState.getTranslateBuffer().replaceBuffer(
            doPrefixLineNumber( original ).toString() );
    }

    public StringBuilder doPrefixLineNumber( String input )
    {
        if( input == null )
        {
            return new StringBuilder();
        }
        StringBuilder fb = new StringBuilder();

        BufferedReader br = new BufferedReader(
            new StringReader( input ) );

        int lineNumber = 1;
        String line;
        try
        {
            line = br.readLine();
            //boolean firstLine = true;
            while( line != null )
            {
                fb.append( System.lineSeparator() );
                fb.append( String.format( "%3d", lineNumber ) );
                fb.append( "]" );
                fb.append( line );
                //firstLine = false;
                lineNumber++;
                line = br.readLine();
            }
            return fb;
        }
        catch( IOException e )
        {
            throw new VarBindException( "Error indenting spaces", e );
        }
    }

    public void collectAllVarNames( Set<String> collection, String input )
    {
    }

    public static String doPrefix( String string )
    {
        return INSTANCE.doPrefixLineNumber( string ).toString();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
