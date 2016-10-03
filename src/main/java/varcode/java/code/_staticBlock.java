package varcode.java.code;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.dom.Dom;
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
    /**
     * 
     * @param context contains bound variables and scripts to bind data into
     * the template
     * @param directives pre-and post document directives 
     * @return the populated Template bound with Data from the context
     */
    @Override
    public String bind( VarContext context, Directive...directives )
    {
        Dom dom = BindML.compile( author() ); 
        return Compose.asString( dom, context, directives );
    }
    
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
			return Compose.asString( STATIC_BLOCK, VarContext.of( "body", body ), directives );
		}
		return "";
	}

    @Override
	public String toString()
	{
		return author(); 
	}
}
