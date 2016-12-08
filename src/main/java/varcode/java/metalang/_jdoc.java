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
import java.util.HashMap;
import java.util.Map;
import varcode.Model;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.lib.text.EscapeString;
import varcode.markup.bindml.BindML;

/**
 *
 * @author 
 * @deprecated 
 * @param 
 * @see 
 * @serial 
 * @since 
 * @version 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class _jdoc 
    implements JavaMetaLang
{
    
    /* one of these */
    public class _inlineComment
        extends _jdoc
    {
        public String comment;
        
        public _inlineComment( String commentContents )
        {
            commentContents = commentContents.trim();
            if( commentContents.startsWith( "/**" ) )
            {
                commentContents = commentContents.substring( 3 );
            }
            if( commentContents.startsWith( "/*" ) )
            {
                commentContents = commentContents.substring( 2 );
            }
            if( commentContents.endsWith( "*/" )  )
            {
                commentContents = commentContents.substring(
                    0, commentContents.length() - 2 );
            }
            this.comment = commentContents;
        }

        @Override
        public JavaMetaLang replace( String target, String replacement ) 
        {
            this.comment = this.comment.replace(target, replacement);
            return this;
        }

        @Override
        public String author() 
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives ) 
        {
            return "/* " + 
                Compose.asString( BindML.compile( comment ), 
                    VarContext.of(), 
                    directives ) +
                " */";                    
        }

        @Override
        public Model bind( VarContext context ) 
        {
            if( this.comment != null )
            {
                this.comment = "/*" + 
                    Compose.asString( BindML.compile( this.comment ), context )
                    + "*/" ;
            }
            return this;
        }                       
    }/* _inlinecomment */
    
    
        // method.annDeprecate();
    // method.annOverride();
    // method.annGenerated();
    // method.annSuppressWarnings(...);
    
    //method.docLink(...) {@link varcode.
    //method.docSince()
    //method.docVersion()
    //method.docAuthor()
    //method.docParam( String paramName, String...documentation)
    //method.docReturn( String...documentation )
    //method.docSeeLocalMethod( methodName ) //@see #customizeProxyFactory
    //method.docLink
    //method.docThrows(
    //method.doc
    //method.seeDoc( ... )
    //method.paramDoc( String paramName, String...documentation)
    //method.returnDoc( String..documentation )
    //method.throwsDoc( String exception, String comment)
    
    public static class _detailComment
    {
        protected Map<String, Object>javadocProperties = 
            new HashMap<String, Object>();
        
    }
    

        /*
    public _jdoc setAuthor( String author )
    {
        
    }
    */
    
    /**
     * This method is called as a Directive 
     * and / used when authoring the javadoc comments
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
