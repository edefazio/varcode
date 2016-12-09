package varcode.java.lang;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;

/**
 * package representation
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _package
    implements JavaMetaLang
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
        public static _package of( String packageName )
        {
            if( packageName == null )
            {
                return new _package( null );
            }
            if( packageName.startsWith( "package " ) )
            {
                packageName = packageName.substring( "package ".length() );                
            }
            if( packageName.endsWith( ";" ) )
            {
                packageName = packageName.substring( 0, packageName.length() - 1 );                
            }
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
        if( this.name != null )
        {
            this.name = this.name.replace( target, replacement );
        }
        return this;
    }
    
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
    
    @Override
	public String author( Directive... directives ) 
	{
		return Compose.asString( 
            PACKAGE, 
            VarContext.of( "name", this.name ), directives );
	}

    @Override
    public _package bind( VarContext context )
    {
        if( this.name != null )
        {
            String res = Compose.asString( BindML.compile( this.name ), context );
            this.name = res;
        }
        return this;
    }
}
