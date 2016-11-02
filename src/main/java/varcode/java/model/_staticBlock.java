package varcode.java.model;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Model of a Static block
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _staticBlock
    implements Model
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
    
    @Override
    public _staticBlock bind( VarContext context )
    {
        if( this.body != null )
        {
            this.body.bind( context );
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
		if( !body.isEmpty() )
		{
			return Compose.asString( 
                STATIC_BLOCK, VarContext.of( "body", body ), directives );
		}
		return "";
	}

    @Override
	public String toString()
	{
		return author(); 
	}
}
