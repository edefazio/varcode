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

package varcode.java.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.author.lib.EscapeString;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarScript;
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.markup.bindml.BindML;
import varcode.ModelException;
import varcode.markup.Fill;

/**
 * A JavaDoc comment
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _javadoc
    implements _Java, Authored
{    
    private static final String N = System.lineSeparator();
    
    public _javadoc( _javadoc prototype )
    {
        if( prototype != null )
        {
            this.comment = prototype.comment;
        }
    }
    
    public static _javadoc cloneOf( _javadoc jdoc )
    {
        return new _javadoc( jdoc );
    }

    public static _javadoc of( String... commentLines )
    {
        return new _javadoc( commentLines );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public boolean isEmpty()
    {
        return comment == null || comment.trim().length() == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.comment );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            return true;
        }
        if( obj == null )
        {
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            return false;
        }
        final _javadoc other = (_javadoc)obj;
        if( !Objects.equals( this.comment, other.comment ) )
        {
            return false;
        }
        return true;
    }

    /**
     * if the javadoc comment fits on one line like this one
     */
    public static final Template INLINE_JAVADOC_FRAME = BindML.compile(
        "{{+?javadoc:"
        + "/**" + N
        + "{+$formatCommentLines(javadoc)+}" + N
        + " */" + N
        + "+}}" );

    public static final Template JAVADOC_FRAME = BindML.compile(
        "{{+?javadoc:"
        + "/**" + N
        + "{+$formatCommentLines(javadoc)+}" + N
        + " */" + N
        + "+}}" );

    /**
     * This class will register the 
     */
    public static class FormatRegister
    {
        //Here I could provide aliases for the script with the alias annotation
        //@alias({"A","B"})
        public static VarScript formatCommentLines = new VarScript( ) 
        {
            @Override
            public Object eval( Context context, String varName )
                throws VarBindException
            {
                String val = (String)context.get( varName );
                if( val == null )
                {
                    return null;
                }
                return formatCommentLines( val );
            }            
        };

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
                    if( !firstLine )
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
                throw new ModelException( "Error formatting Comment Lines" );
            }
        }
    }

    public _javadoc set( String...content )
    {
        this.comment = "";
        for( int i = 0; i < content.length; i++ )
        {
            if( i > 0 )
            {
                comment += System.lineSeparator();
            }
            comment += content[ i ];
        }
        return this;
    }
    
    
    /**
     * Creates an "@author" 
     * @param version
     * @return 
     */
    public _javadoc atVersion( Object version )
    {   
        add( Fill.of( "@version {+version+}", version ) );
        return this;
    }
    
    public _javadoc atLink( Object target )
    {
        this.comment += 
            Fill.of( " {@link {+target+}} ", target );
        return this;    
    }
    
    public _javadoc atSee( Object target )
    {
        add(
            Fill.of( "@see {+target+}", target ) );
        return this;    
    }
        
    public _javadoc atSince( Object since )
    {
        if( since instanceof Date )
        {
            SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd:hh:mm:ss" );
            String dateString = sdf.format( (Date)since );
            add( 
                Fill.of( "@version {+since+}", dateString ) );
            return this;
        }
        return add( 
            Fill.of( "@since {+since+}", since ) );
    }
    
    /**
     * Creates an "@author" 
     * @param name
     * @return 
     */
    public _javadoc atAuthor( String name )
    {   
        add( Fill.of( "@author {+name+}", name ) );
        return this;
    }
    
    /**
     * Creates an "@param" 
     * @param name
     * @param description
     * @return 
     */
    public _javadoc atParam( String name, String description )
    {   
        add( Fill.of( "@param {+name+} {+description+}", 
            name, 
            description ) );
        return this;
    }
    
    /**
     * 
     * @param description
     * @return 
     * @throws RuntimeException 
     */
    public _javadoc atReturn( String description )
    {
        add( "@return " + description );
        return this;
    }
    
    public _javadoc atThrow( String throwType, String throwDescription )
    {
        add( "@throws "+ throwType + " "+ throwDescription );
        return this;
    }
    
    /**
     * Add additional Content to the end of the Javadoc comment
     *
     * @param content content to appear at the end of the Javadoc comment
     * @return
     */
    public _javadoc add( String... content )
    {
        for( int i = 0; i < content.length; i++ )
        {
            if( comment == null )
            {
                comment = "";
            }
            else
            {
                comment += System.lineSeparator();
            }
            comment += content[ i ];
        }
        return this;
    }

    private String comment;

    public _javadoc()
    {
        this.comment = null;
    }

    public _javadoc( String... commentLines )
    {
        if( commentLines != null && commentLines.length > 0 )
        {
            StringBuilder sb = new StringBuilder();

            for( int i = 0; i < commentLines.length; i++ )
            {
                if( i > 0 )
                {
                    sb.append( "\r\n" );
                }
                sb.append( commentLines[ i ] );
            }
            this.comment = sb.toString();
        }
    }

    public String getComment()
    {
        if( comment != null )
        {
            return comment;
        }
        return "";
    }

    @Override
    public _javadoc replace( String target, String replacement )
    {
        if( this.comment != null )
        {
            this.comment = RefRenamer.apply( this.comment, target, replacement );
        }
        return this;
    }

    @Override
    public Context getContext()
    {
        return VarContext.of(
            "javadoc", comment ).register( FormatRegister.class );
    }

    @Override
    public Template getTemplate()
    {
        return JAVADOC_FRAME;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( getTemplate(),
            getContext(),
            directives );
    }

    @Override
    public String toString()
    {
        return author();
    }
}
