package varcode.java.lang;

import java.util.ArrayList;
import java.util.List;
import varcode.VarException;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Declaration on a method the throwing of exception
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _throws
    implements Model
{            
	public static final _throws NONE = new _throws();
	
	private List<String> throwsException;
	
	public static _throws cloneOf( _throws prototype )
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
	
    
    @Override
    public _throws bind( VarContext context )
    {
        for( int i = 0; i < throwsException.size(); i++ )
        {
            throwsException.set(i , 
                Compose.asString( 
                    BindML.compile( throwsException.get( i ) ), context ) );            
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
		VarContext vc = VarContext.of( "throwsException", throwsException );
		return Compose.asString( THROWS, vc, directives );
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
    
    @Override
    public _throws replace( String target, String replacement )
    {
        List<String> replacedNames = new ArrayList<String>();
        for( int i = 0; i < this.throwsException.size(); i++ )
        {
            replacedNames.add(
                this.throwsException.get( i ).replace( target, replacement ) );
        }
        this.throwsException = replacedNames;
        return this;
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
	
    @Override
	public String toString()
	{
		return author();
	}
}
