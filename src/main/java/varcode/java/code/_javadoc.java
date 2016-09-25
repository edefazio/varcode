package varcode.java.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.doc.lib.text.EscapeString;
import varcode.dom.Dom;
import varcode.Template.Base;
import varcode.markup.bindml.BindML;

public class _javadoc
    extends Base
{
	public static _javadoc cloneOf( _javadoc jdoc )
	{
		if( jdoc != null && !jdoc.isEmpty() )
		{
			return new _javadoc( jdoc.comment );
		}
		return new _javadoc();
	}
	
	public static _javadoc of( String... commentLines )
	{
		return new _javadoc( commentLines );
	}
	
    public _javadoc bindIn( VarContext context )
    {
        if( this.comment != null )
        {
            this.comment = Author.code( BindML.compile( this.comment ), context );
        }
        return this;
    }
    
	public boolean isEmpty()
	{
		return comment == null || comment.trim().length() == 0;
	}
	
	public static final Dom JAVADOC_DOM = BindML.compile(
		"{{+?comment:" +	
		"/**" + N +
		"{+$formatCommentLines(comment)+}" + N +
		" */" + N + 
		"+}}");


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
		if (input == null )
		{
			return null;
		}
		StringBuilder fb = new StringBuilder();
		
		BufferedReader br = new BufferedReader( 
			new StringReader( input ) );
		
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
			throw new VarException( "Error formatting Comment Lines" );
		}	
	}
	
    /**
     * Add additional Content to the end of the Javadoc comment
     * 
     * @param content content to appear at the end of the Javadoc comment
     * @return 
     */
    public _javadoc append( String...content )
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
	
	public _javadoc( )
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
		//this.comment = comment;
	}
	
	public String getComment()
	{
		if( comment != null )
		{
			return comment;
		}
		return "";
	}
	
	public _javadoc replace( String target, String replacement )
	{
		if( this.comment != null )
		{
			this.comment = this.comment.replace(target, replacement);
		}
		return this;
	}
	
	public String author( Directive... directives ) 
	{
		return Author.code( 
			this.getClass(), 
			JAVADOC_DOM, 
			VarContext.of( "comment", comment ), 
			directives );
	}
	
	public String toString()
	{
		return author();
	}
}
