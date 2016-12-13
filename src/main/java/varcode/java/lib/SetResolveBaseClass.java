package varcode.java.lib;

import varcode.context.VarScope;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.context.Resolve;

/** 
 * Sets the {@link Resolve#BASECLASS_PROPERTY} Class 
 * "resolve.baseclass" as an instance var on the context 
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SetResolveBaseClass
    implements Directive.PreProcessor
{
	
	private final Class<?> clazz;
	private final VarScope scope;
	
	public SetResolveBaseClass( Class<?> clazz )
	{
		this( clazz, VarScope.STATIC );
	}
	
	public SetResolveBaseClass( Class<?> clazz, VarScope scope )
	{
		this.clazz = clazz;
		this.scope = scope;
	}
	
    @Override
	public void preProcess( DocState docState ) 
	{
            //docState.getContext().set( "markup.class", clazz, this.scope );		
            docState.getContext().set( Resolve.BASECLASS_PROPERTY, clazz, this.scope );
	}
}
