package varcode.java.lang;

import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.java.lang.JavaMetaLang._facet;
import varcode.markup.bindml.BindML;

public class _implements
    implements JavaMetaLang, _facet
{            
    public static final _implements NONE = new _implements();
	
    public static _implements cloneOf( _implements prototype ) 
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
	
    @Override
    public _implements bind( VarContext context )
    {
        List<String>replaced = new ArrayList<String>();
        for( int i = 0; i < impls.size(); i++ )
        {
            replaced.add(
                Compose.asString( BindML.compile( impls.get( i ) ) , context ) );
        }
        this.impls = replaced;
        return this;
    }
        
    public _implements implement( Class... interfaceClass )
    {
        for( int i = 0; i < interfaceClass.length; i++ )
        {
            impls.add( interfaceClass[ i ].getCanonicalName() );
        }
	return this;
    }
    
    public _implements implement( String... interfaceClass )
    {
	for( int i = 0; i < interfaceClass.length; i++ )
        {
            impls.add( interfaceClass[ i ] );
        }
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
	
    public boolean contains( String interfaceClassName )
    {
        return this.impls.contains( interfaceClassName );
    }
    
    public boolean contains( Class interfaceClass )
    {
        return contains( interfaceClass.getCanonicalName() );
    }
    
    @Override
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
            impl.implement( classes[ i ] );
	}
	return impl;
    }
    
    public static _implements of( String... tokens )
    {
	//className[] classNames = new className[tokens.length];
	_implements impl = new _implements();
	for( int i = 0; i < tokens.length; i++ )
	{
            impl.implement( tokens[ i ] );
	}
	return impl;
    }
	
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
        
    @Override
    public String author( Directive... directives ) 
    {
	VarContext vc = VarContext.of( "impls", impls );
	return Compose.asString( IMPLEMENTS, vc, directives );
	}
	
    @Override
	public String toString()
	{
		return author();
	}

	public String getAt( int index ) 
	{
		if( index > impls.size() -1 )
		{
			throw new ModelException( 
				"index [" + index + "] is outside of implements range [0..."
			    + ( impls.size() -1 ) + "]" );
		}
		return impls.get( index );
	}
}
