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

import java.util.Map;

import varcode.author.AuthorState;
import varcode.author.PostProcessor;

/**
 * Find->replace that happens Post Tailoring (during Post Processing)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PostReplace
    implements PostProcessor
{
    /**
     * the source string to look for
     */
    private final String[] lookFor;

    /**
     * the target String to replace with
     */
    private final String[] replaceWith;

    public PostReplace( String lookFor, String replaceWith )
    {
        this.lookFor = new String[]
        {
            lookFor
        };
        this.replaceWith = new String[]
        {
            replaceWith
        };
    }

    public PostReplace( Map<String, String> targetToReplacement )
    {
        lookFor = targetToReplacement.keySet().toArray( new String[ 0 ] );
        replaceWith = new String[ lookFor.length ];
        for( int i = 0; i < lookFor.length; i++ )
        {
            replaceWith[ i ] = targetToReplacement.get( lookFor[ i ] );
        }
    }

    @Override
    public void postProcess( AuthorState authorState )
    {
        String s = authorState.getTranslateBuffer().toString();
        for( int i = 0; i < lookFor.length; i++ )
        {
            s = s.replace( lookFor[ i ], replaceWith[ i ] );
        }

        authorState.getTranslateBuffer().clear().append( s );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < lookFor.length; i++ )
        {
            sb.append( System.lineSeparator() );
            sb.append( "        " );
            sb.append( lookFor[ i ] );
            sb.append( " -> " );
            sb.append( replaceWith[ i ] );
        }
        return getClass().getName() + sb.toString();
    }
}
