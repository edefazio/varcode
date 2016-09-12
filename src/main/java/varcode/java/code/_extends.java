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

public class _extends
    extends Template.Base    
{
	public static final _extends NONE = new _extends();
	
	private List<String> extendsFrom;
	
	public static _extends from( _extends prototype )
	{
		_extends e = new _extends();
		if( prototype != null )
		{
			for( int i = 0; i < prototype.count(); i++ )
			{
				e.extendsFrom.add( prototype.extendsFrom.get( i )  );
			}
		}
		return e;
	}
	
    
    public void replace( String source, String replacement ) 
    {
        List<String>replaced = new ArrayList<String>();
        for(int i=0; i<extendsFrom.size(); i++)
        {
            replaced.add( extendsFrom.get( i ).replace( source, replacement ) );
        }
        this.extendsFrom = replaced;
    }
    
	public static Dom EXTENDS = BindML.compile(
		"{{#extendsList:{+extendsFrom+}, #}}" +	
		"{{+?extendsList:" + N +
	    "    extends {+extendsList+}+}}" );
	
	public _extends()
	{
		 extendsFrom = new ArrayList<String>();
	}
	
	public int count()
	{
		return extendsFrom.size();
	}
	
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
	public String get( int index )
	{
		if( index > count() -1 )
		{
			throw new VarException(
				"extends["+index+"] is out of range[0..." + ( count() -1 )+"]" );
		}
		return extendsFrom.get( index ).toString();
	}
	public _extends( String extendsFromClass )
	{
		 extendsFrom = new ArrayList<String>();
		 extendsFrom.add( extendsFromClass );
	}
	
    public _extends addExtends( Class classToExtendFrom )
    {
        extendsFrom.add( classToExtendFrom.getCanonicalName() );
        return this;
    }
    
	public _extends addExtends( String extendClass )
	{
		extendsFrom.add( extendClass );
		return this;
	}
	
	public static _extends of( String... tokens )
	{
		_extends xtends = new _extends();   
		for( int i = 0; i < tokens.length; i++ )
		{
			xtends.addExtends( tokens[ i ] );
		}
		return xtends;
	}
	
	public String author( Directive... directives ) 
	{
		VarContext vc = VarContext.of( "extendsFrom", extendsFrom );
		return Author.code( EXTENDS, vc, directives );
	}
	
	public String toString()
	{
		return author();
	}
}