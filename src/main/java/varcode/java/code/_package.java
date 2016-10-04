package varcode.java.code;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * package representation
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _package
    implements Model
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
	
    /** 
     * Gets the package name (in canonical '.' form)
     * @return  the package name in canonical form 
     */
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
	
    @Override
	public String toString()
	{
        return author( );        
	}

    @Override
    public _package replace( String target, String replacement )
    {
        this.name = this.name.replace( target, replacement );
        return this;
    }
    
    @Override
	public String author( Directive... directives ) 
	{
		return Compose.asString( 
            PACKAGE, 
            VarContext.of( "name", this.name ), directives );
	}

    @Override
    public _package bindIn( VarContext context )
    {
        if( this.name != null )
        {
            String res = Compose.asString( BindML.compile( this.name ), context );
            System.out.println ( "RED" + res );
            this.name = res;
        }
        return this;
    }
}
