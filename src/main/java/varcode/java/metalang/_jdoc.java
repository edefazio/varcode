/*
 * Copyright 2016 Eric.
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
package varcode.java.metalang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import varcode.Model;
import varcode.context.VarContext;
import varcode.doc.lib.text.EscapeString;

/**
 *
 * @author Eric
 */
public abstract class _jdoc 
{
    
    /**
     * This method is called / used when authoring the javadoc comments
     * 
     * @param ctx the varcontext
     * @param varName the name of the var that contains the Javadoc contents
     * @return String representing the 
     */
    public static String formatCommentLines( VarContext ctx, String varName )
    {
	String val = (String)ctx.get( varName );
	if( val == null )
	{
            return null;
	}
	return formatCommentLines( val );
    }
	
    public static String formatCommentLines( String input )
    {
	if( input == null )
	{
            return null;
	}
	StringBuilder fb = new StringBuilder();
		
	BufferedReader br = new BufferedReader( new StringReader( input ) );
		
	String line;
	try 
	{
            line = br.readLine();
            boolean firstLine = true;
            while( line != null )
            {
		if(! firstLine )
		{
                    fb.append( "\r\n" );
                }
		fb.append( " * " );
		fb.append( EscapeString.escapeJavaString( line ) );
		firstLine = false;
		line = br.readLine();
            }
            return fb.toString();
        } 
	catch( IOException e ) 
	{
            throw new Model.ModelException( "Error formatting Comment Lines" );
	}	
    }
}
