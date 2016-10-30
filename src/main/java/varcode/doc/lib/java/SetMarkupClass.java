package varcode.doc.lib.java;

import varcode.context.VarScope;
import varcode.doc.Directive;
import varcode.doc.DocState;

/** 
 * Sets the Markup Class "markup.class" as an instance var on the context 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SetMarkupClass
	implements Directive.PreProcessor
{
	
	private final Class<?> clazz;
	private final VarScope scope;
	
	public SetMarkupClass( Class<?> clazz )
	{
		this( clazz, VarScope.STATIC );
	}
	
	public SetMarkupClass( Class<?> clazz, VarScope scope )
	{
		this.clazz = clazz;
		this.scope = scope;
	}
	
    @Override
	public void preProcess( DocState docState ) 
	{
		docState.getContext().set( "markup.class", clazz, this.scope );		
	}
}
