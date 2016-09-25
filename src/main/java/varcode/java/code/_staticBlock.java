package varcode.java.code;

import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _staticBlock
	extends Template.Base	
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
    
    public static _staticBlock cloneOf( _staticBlock prototype )
    {
        if( prototype == null || prototype.isEmpty() )
        {
            return new _staticBlock();
        }
        return _staticBlock.of( _code.cloneOf( prototype.body) );
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
	
	public boolean isEmpty()
	{
		return body == null || body.isEmpty();		
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
	
    @Override
    public _staticBlock replace( String target, String replacement )
    {
        this.body = this.body.replace( target, replacement );
        return this;
    }
    
    public _staticBlock bindIn( VarContext context )
    {
        if( this.body != null )
        {
            this.body.bindIn( context );
        }
        return this;
    }
    
    @Override
	public String author( Directive... directives ) 
	{
		if( !body.isEmpty() )
		{
			return Author.code( STATIC_BLOCK, VarContext.of( "body", body ), directives );
		}
		return "";
	}

    @Override
	public String toString()
	{
		return author(); 
	}

}
