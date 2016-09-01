package varcode.java.model;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _staticBlock
	implements SelfAuthored	
{	
	public static final Dom STATIC_BLOCK = 
		BindML.compile(
			"static" + N +	
			"{" + N + 
			"{+$indent4(body)+}" + N +
			"}" );

	public static _staticBlock of( Object... linesOfCode )
	{
		return new _staticBlock( linesOfCode );
	}
	
	public static _staticBlock of( _code code )
	{
		return new _staticBlock( code );
	}
	
	private _code body;
	
	public _staticBlock( )
	{
		this.body = new _code();
	}
	
	public _staticBlock( Object... linesOfCode )
	{		
		this.body = _code.of( linesOfCode );
	}
	
	public int count()
	{
		if( body != null && !body.isEmpty() )
		{
			return 1;
		}
		return 0;	
	}
	
	public _code getBody()
	{
		return this.body;
	}
	
	public _staticBlock addTailCode( Object... codeSequence )
	{
		body.addTailCode( codeSequence );		
		return this;
	}
	
	public _staticBlock addHeadCode( Object... codeSequence )
	{
		body.addHeadCode( codeSequence );		
		return this;
	}
	
	public String toCode( Directive... directives ) 
	{
		if( !body.isEmpty() )
		{
			return Author.code( STATIC_BLOCK, VarContext.of( "body", body ), directives );
		}
		return "";
	}

	public String toString()
	{
		return toCode(); 
	}

}
