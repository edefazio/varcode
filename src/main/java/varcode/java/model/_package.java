package varcode.java.model;

import varcode.CodeAuthor;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.JavaNaming;
import varcode.markup.bindml.BindML;

public class _package
    implements CodeAuthor 
{
	public static final Dom PACKAGE = 
		BindML.compile( 
            "{{+?name:package {+name+};" + N + N +
			"+}}" );    			
	
	public static _package from ( _package prototype )
	{		
		return of( prototype.name );
	}
	
	public static _package of( Object packageName )	
	{		
		return new _package( packageName );
	}
	
    public void replace( String target, String replacement )
    {
        this.name = this.name.replace( target, replacement );
    }
    
	private String name;
	
	public boolean isEmpty()
	{
		return name == null || name.trim().length() == 0;
	}
	
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
			JavaNaming.PackageName.validate( this.name );
		}
	}
	
	public String toString()
	{
		return author();
	}

	public String author( Directive... directives ) 
	{
		return Author.code( PACKAGE, VarContext.of( "name", this.name ), directives );
	}

}
