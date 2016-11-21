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
import java.io.StringReader;
import varcode.VarException;

/**
 *
 * @author eric
 */
public class _Javadoc
{
 
    /**
     * Parses out the "content" within a Javadoc Comment
     *  
     * @param javadocComment a JavaDoc Comment
     * @return the String contents without the slashes / stars, etc.
     */
    public static String parseJavadocCommentContent( String javadocComment )
    {
        javadocComment = javadocComment.trim();
        
        //chop the "/*" from the front of the comment
        javadocComment = javadocComment.substring(javadocComment.indexOf( "/**" ) + 2  );
        //chop off the "*/" from the tail of the comment
        javadocComment = javadocComment.substring(0, javadocComment.lastIndexOf("*/") );
        
        //now go through each line, trimming and removing prefix *'s
        StringBuilder sb = new StringBuilder();
        BufferedReader sourceReader = 
            new BufferedReader( new StringReader( javadocComment ) ); 
        
        String line = null;
        try
        {
            int lineNumber = 0;
            while( ( line = sourceReader.readLine() ) != null ) 
            {
                if( lineNumber > 1 )
                {
                    sb.append( "\r\n" );
                }
                line = line.trim();
                if( line.startsWith( "* " ) )
                {
                    sb.append( line.substring( 2 ) );
                }
                else if( line.startsWith( "*" ) )
                {
                    sb.append( line.substring( 1 ) );
                }
                else
                {
                    sb.append( line );
                }
                lineNumber++;
            }
            return sb.toString();
        }        
        catch( IOException e )
        {
            throw new VarException( "Unable to parse comment", e );
        }        
    }
}
