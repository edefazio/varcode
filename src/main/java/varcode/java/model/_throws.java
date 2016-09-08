package varcode.java.model;

import java.util.ArrayList;
import java.util.List;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.model._class.name;
import varcode.markup.bindml.BindML;

public class _throws
	implements SelfAuthored
{
	public static final _throws NONE = new _throws();
	
	private List<_class.name> throwsException;
	
	public static _throws from( _throws prototype )
	{
		_throws t = new _throws();
		for( int i = 0; i < prototype.count(); i++ )
		{
			t.addThrows( name.from( prototype.throwsException.get( i ) ) );
		}
		return t;
	}
	
	public static Dom THROWS = BindML.compile(
		"{{#throwsList:{+throwsException+}, #}}" +	
		"{{+?throwsList:" + N +
	    "    throws {+throwsList+}+}}" );
	
	public String toCode( Directive... directives ) 
	{
		VarContext vc = VarContext.of( "throwsException", throwsException );
		return Author.code( THROWS, vc, directives );
	}
	
	public _throws()
	{
		 throwsException = new ArrayList<_class.name>();
	}
	
	public _throws( String throwsClass )
	{
		 throwsException = new ArrayList<_class.name>();
		 throwsException.add( _class.name.of( throwsClass ) );
	}
	
	public _throws addThrows( Object throwsClass )
	{
		if( throwsClass instanceof String )
		{
			addThrows( (String)throwsClass );
		}
		else if (throwsClass instanceof Class )
		{
			addThrows( _class.name.of( (Class<?>)throwsClass ) );			
		}
		else if (throwsClass instanceof name )
		{
			addThrows( (name)throwsClass );			
		}
		return this;
	}
    
    public void replace( String target, String replacement )
    {
        List<name> replacedNames = new ArrayList<name>();
        for(int i=0; i<this.throwsException.size(); i++)
        {
            replacedNames.add( 
                name.of( this.throwsException.get( i ).toString().replace( target, replacement ) ) );
        }
    }
	
	public _throws addThrows( name throwsClassName )
	{
		throwsException.add( throwsClassName );
		return this;
	}
	
	public _throws addThrows( String throwsClass )
	{
		throwsException.add( _class.name.of( throwsClass ) );
		return this;
	}
	
	public static _throws of( Object... tokens )
	{
		_throws throwsExceptions = new _throws();   
		for( int i = 0; i < tokens.length; i++ )
		{
			throwsExceptions.addThrows( tokens[ i ] );
		}
		return throwsExceptions;
	}
		
	public List<_class.name> getThrows()
	{
		return this.throwsException;
	}
	
	public int count()
	{
		return this.throwsException.size();	
	}
	
	public String toString()
	{
		return toCode();
	}
}
