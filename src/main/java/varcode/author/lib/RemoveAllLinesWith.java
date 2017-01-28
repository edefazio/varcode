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
 * Instance (PostProcessor)<BR>
 * Separates the source into lines, and excludes lines where any of the target
 * Strings occur.
 *
 * for example:<BR>
 * <CODE>RemoveAllLinesContaining vowels = RemoveAllLinesContaining( "A", "E", "I", "O", "U" );</CODE>
 * <BR>
 * with input:<BR>
 * <PRE>
 * A
 * B
 * C
 * D
 * E
 * F
 * </PRE> will return: <BR>
 * <PRE>
 * B
 * C
 * D
 * F
 * </PRE> excluding the lines with "A" and "E".
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class RemoveAllLinesWith
    implements PostProcessor
{
    private final String[] strings;

    public RemoveAllLinesWith( String... strings )
    {
        this.strings = strings;
    }

    @Override
    public void postProcess( AuthorState authorState )
    {
        String allTheSource = authorState.getTranslateBuffer().toString();

        authorState.getTranslateBuffer().replaceBuffer(
            removeAllLinesContaining( allTheSource, strings ).toString() );
    }

    public static StringBuffer removeAllLinesContaining(
        String source, String... strings )
    {
        StringBuffer withLinesRemoved = new StringBuffer();
        BufferedReader br = new BufferedReader( new StringReader( source ) );
        try
        {
            boolean isFirstLine = true;
            String line = br.readLine();
            while( line != null )
            {
                boolean printIt = true;
                for( int i = 0; i < strings.length; i++ )
                {
                    if( line.contains( strings[ i ] ) )
                    {
                        printIt = false;
                    }
                }
                if( printIt )
                {
                    if( !isFirstLine )
                    {
                        withLinesRemoved.append( System.lineSeparator() );
                    }
                    withLinesRemoved.append( line );
                    isFirstLine = false;
                }
                line = br.readLine();
            }
            return withLinesRemoved;
        }
        catch( IOException e )
        {
            throw new VarBindException( e );
        }
    }

    @Override
    public String toString()
    {
        return getClass().getName();
    }
}
