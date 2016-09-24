package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.Template;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _implements
	extends Template.Base
{
	public static final _implements NONE = new _implements();
	
	public static _implements from( _implements prototype ) 
	{
		_implements impl = new _implements();
		if( prototype != null )
		{
			for( int i = 0; i < prototype.count(); i++ )
			{
				impl.impls.add(  prototype.impls.get( i )  );
			}
		}
		return impl;
	}
	
	private List<String> impls;
	
	public static Dom IMPLEMENTS = BindML.compile(
		"{{#implementsList:{+impls+}, #}}" +	
		"{{+?implementsList:" + N +
	    "    implements {+implementsList+}+}}" );
	
	public _implements()
	{
		impls = new ArrayList<String>();
	}
	
    public _implements bindIn( VarContext context )
    {
        List<String>replaced = new ArrayList<String>();
        for( int i = 0; i < impls.size(); i++ )
        {
            replaced.add( 
                Author.code(BindML.compile( impls.get( i ) ) , context ) );
        }
        this.impls = replaced;
        return this;
    }
        
    public _implements addImplements( Class implementerClass )
	{
		impls.add( implementerClass.getCanonicalName() );
		return this;
	}
    
	public _implements addImplements( String implementerClass )
	{
		impls.add( implementerClass );
		return this;
	}
	
	public int count()
	{
		return impls.size();
	}
    
    public boolean isEmpty()
    {
        return count() == 0;
    }
	
    public _implements replace( String target, String replacement )
    {
        List<String> replaced = new ArrayList<String>();
        
        for(int i=0; i<impls.size(); i++)
        {
            replaced.add( 
                this.impls.get( i ).replace( target, replacement ) ); 
        }
        this.impls = replaced;
        return this;
    }
    
    public static _implements of( Class...classes )
	{
		//className[] classNames = new className[tokens.length];
		_implements impl = new _implements();
		for( int i = 0; i < classes.length; i++ )
		{
			impl.addImplements( classes[ i ] );
		}
		return impl;
	}
    
	public static _implements of( String... tokens )
	{
		//className[] classNames = new className[tokens.length];
		_implements impl = new _implements();
		for( int i = 0; i < tokens.length; i++ )
		{
			impl.addImplements( tokens[ i ] );
		}
		return impl;
	}
	
	public String author( Directive... directives ) 
	{
		VarContext vc = VarContext.of( "impls", impls );
		return Author.code( IMPLEMENTS, vc, directives );
	}
	
	public String toString()
	{
		return author();
	}

	public String get( int index ) 
	{
		if( index > impls.size() -1 )
		{
			throw new VarException( 
				"index [" + index + "] is outside of implements range [0..."
			    + ( impls.size() -1 ) + "]" );
		}
		return impls.get( index ).toString();
	}
}
