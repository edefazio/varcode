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

public class _throws
	extends Template.Base
{
	public static final _throws NONE = new _throws();
	
	private List<String> throwsException;
	
	public static _throws from( _throws prototype )
	{
		_throws t = new _throws();
		for( int i = 0; i < prototype.count(); i++ )
		{
			t.addThrows( prototype.throwsException.get( i ) );
		}
		return t;
	}
	
	public static Dom THROWS = BindML.compile(
		"{{#throwsList:{+throwsException+}, #}}" +	
		"{{+?throwsList:" + N +
	    "    throws {+throwsList+}+}}" );
	
	public String author( Directive... directives ) 
	{
		VarContext vc = VarContext.of( "throwsException", throwsException );
		return Author.code( THROWS, vc, directives );
	}
	
    
    public String getAt( int index )
    {
        if( index < count() )
        {
            return throwsException.get( index );
        }
        throw new VarException(" invalid index ["+index+"]");
    }
    
	public _throws()
	{
		 throwsException = new ArrayList<String>();
	}
	
	public _throws( String throwsClass )
	{
		 throwsException = new ArrayList<String>();
		 throwsException.add( throwsClass );
	}
	
	public _throws addThrows( Object throwsClass )
	{
		if( throwsClass instanceof String )
		{
			addThrows( (String)throwsClass );
		}
		else if (throwsClass instanceof Class )
		{
			addThrows( ( (Class<?>)throwsClass ).getCanonicalName() );			
		}		
		return this;
	}
    
    public void replace( String target, String replacement )
    {
        List<String> replacedNames = new ArrayList<String>();
        for(int i=0; i<this.throwsException.size(); i++)
        {
            replacedNames.add(
                this.throwsException.get( i ).replace( target, replacement ) );
        }
        this.throwsException = replacedNames;
    }
	
	public _throws addThrows( String throwsClass )
	{
		throwsException.add(  throwsClass );
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
		
	public List<String> getThrows()
	{
		return this.throwsException;
	}
	
	public int count()
	{
		return this.throwsException.size();	
	}
    
    public boolean isEmpty()
    {
        return count() == 0;
    }
	
	public String toString()
	{
		return author();
	}
}