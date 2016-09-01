package varcode.java.model;

import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _implements
	implements SelfAuthored
{
	public static final _implements NONE = new _implements();
	
	public static _implements from( _implements prototype ) 
	{
		_implements impl = new _implements();
		if( prototype != null )
		{
			for( int i = 0; i < prototype.count(); i++ )
			{
				impl.impls.add( _class.name.from( prototype.impls.get( i ) ) );
			}
		}
		return impl;
	}
	
	private List<_class.name> impls;
	
	public static Dom IMPLEMENTS = BindML.compile(
		"{{#implementsList:{+impls+}, #}}" +	
		"{{+?implementsList:" + N +
	    "    implements {+implementsList+}+}}" );
	
	public _implements()
	{
		 impls = new ArrayList<_class.name>();
	}
	
	public _implements addImplements( String implementerClass )
	{
		impls.add( _class.name.of(implementerClass ) );
		return this;
	}
	
	public int count()
	{
		return impls.size();
	}
	
	public static _implements of( String[] tokens )
	{
		//className[] classNames = new className[tokens.length];
		_implements impl = new _implements();
		for( int i = 0; i < tokens.length; i++ )
		{
			impl.addImplements( tokens[ i ] );
		}
		return impl;
	}
	
	@Override
	public String toCode( Directive... directives ) 
	{
		VarContext vc = VarContext.of( "impls", impls );
		return Author.code( IMPLEMENTS, vc, directives );
	}
	
	public String toString()
	{
		return toCode();
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
