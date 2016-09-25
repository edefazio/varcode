package varcode.java.code;

import varcode.Template;
import varcode.CodeAuthor;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _package
    implements CodeAuthor, Template 
{
	public static final Dom PACKAGE = 
		BindML.compile( 
            "{{+?name:package {+name+};" + N + N +
			"+}}" );
	
	public static _package cloneOf( _package prototype )
	{		
		return of( prototype.name );
	}
	
	public static _package of( Object packageName )	
	{		
		return new _package( packageName );
	}
	
	private String name;
	
	public boolean isEmpty()
	{
		return name == null || name.trim().length() == 0;
	}
	
    /** Gets the package name (in canonical '.' form) */
	public String getName()
	{
		return name;
	}
	
	public _package( Object name )
	{
		if( name == null )
		{
			this.name = null;
		}		
		else if( name instanceof _package )
		{
			this.name = ((_package)name).name;			
		}
		else
		{
			this.name = name.toString();						
		}
	}
	
	public String toString()
	{
        return author( );        
	}

    public _package replace( String target, String replacement )
    {
        this.name = this.name.replace( target, replacement );
        return this;
    }
    
	public String author( Directive... directives ) 
	{
        
		return Author.code( 
            PACKAGE, 
            VarContext.of( "name", this.name ), directives );
	}

    public _package bindIn( VarContext context )
    {
        if( this.name != null )
        {
            String res = Author.code( BindML.compile( this.name ), context );
            System.out.println ( "RED" + res );
            this.name = res;
        }
        return this;
    }
    
    @Override
    public String bind( VarContext context, Directive... directives )
    {
        Dom dom = BindML.compile(author() );
        return Author.code( dom, context, directives );
    }

}
