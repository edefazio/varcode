package varcode.context;

import java.util.List;

import varcode.VarException;
import varcode.context.Resolve.DirectiveResolver;
import varcode.context.Resolve.ScriptResolver;
import varcode.context.Resolve.VarResolver;
import varcode.context.VarBindings.SelfBinding;
import varcode.doc.Directive;
import varcode.eval.Evaluator;
import varcode.markup.VarNameAudit;
import varcode.script.VarScript;

/**
 * A VarContext implementation that has a Lazy "Named Queue" 
 * strategy for resolving variables.
 * 
 * Instead of directly binding variables by name, 
 * we pass in an array of objects to be bound:
 * <PRE>
 * LazyNamedQueueContext context = 
 *     new LazyNamedQueueContext( String.class, "id" ); 
 * </PRE>
 * 
 * This operates like a hybrid "Queue" with Lazy Binding.
 * 
 * The {@code Dom}, might represent the following document:
 * <PRE>
 * Dom dom = BindML.compile( 
 *     "public {+type+} get{+fieldName+}()" + N
 *     "{" + N + 
 *     "    return this.{+fieldName+};" + N +
 *     "}");  
 * </PRE>
 * 
 * NOTE: the above context only provided (2) values: {String.class, "id"}
 * and the Dom specifies (3) marks... ({+type+} {+fieldName+} {+fieldName+})
 * 
 * when "processing" the {@code Dom} we incrementally and lazily Bind
 * the values:
 * <TABLE BORDER = 1>
 *  <TR ALIGN=CENTER><TD COLSPAN =2>var</TD></TR>
 *  <TR><TD>name</TD><TD>value</TD></TR>
 *  <TR><TD>type</TD><TD>String.class</TD></TR>
 *  <TR><TD>fieldName</TD><TD>"id"</TD></TR>
 * </TABLE>
 *  
 * <OL>
 * <LI>when the Dom processes the first Mark {+type+}, the table
 * has no value bound for "type":
 * <TABLE BORDER = 1>
 *  <TR ALIGN=CENTER><TD COLSPAN =2>var</TD></TR>
 *  <TR><TD>name</TD><TD>value</TD></TR>
 * </TABLE> 
 * 
 * ...so it takes the first value from the Queue, which is "String.class"
 * and binds it to the var {+type+}:
 * <TABLE BORDER = 1>
 *  <TR ALIGN=CENTER><TD COLSPAN =2>var</TD></TR>
 *  <TR><TD>name</TD><TD>value</TD></TR>
 *  <TR><TD>type</TD><TD>String.class</TD></TR>
 * </TABLE>   
 * then populates the mark.
 *  
 * <LI>the next mark {+fieldName+}
 * ..."fieldName" does not appear in the table, so it takes the next value in
 * the queue "id", binds it to the var "fieldName", and populates the Mark.
 * 
 * <TABLE BORDER = 1>
 *  <TR ALIGN=CENTER><TD COLSPAN =2>var</TD></TR>
 *  <TR><TD>name</TD><TD>value</TD></TR>
 *  <TR><TD>type</TD><TD>String.class</TD></TR>
 *  <TR><TD>fieldName</TD><TD>"id"</TD></TR>
 * </TABLE> 
 * 
 * <LI> the next mark {+fieldName+} is found in the table 
 * (it was lazily bound above), so the Dom uses this to populate the
 * Document, and we're done.
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class LazyBindQueueContext
	extends VarContext
	implements VarResolver
{
	public static LazyBindQueueContext of( Object...queueValues )
	{
		return new LazyBindQueueContext( queueValues ); 
	}
	
	private int currentIndex = 0;
	
	private Object[] queue;
	
	/** Lazily bound context */
	private final VarContext varContext = VarContext.of( );
	
	public LazyBindQueueContext( Object...queueValues )
	{
		Bootstrap.init( this );
		this.queue = queueValues;		
	}

	public void merge( VarContext anotherContext ) 
	{
		varContext.merge( anotherContext );
	}

	public ScopeBindings getScopeBindings() 
	{
		return varContext.getScopeBindings();
	}

	public VarBindings getBindings( VarScope scope ) 
	{
		return varContext.getBindings( scope );
	}

	public VarBindings getBindings( int scope ) 
	{
		return varContext.getBindings(scope);
	}

	public VarBindings getOrCreateBindings( VarScope scope ) 
	{
		return varContext.getOrCreateBindings( scope );
	}

	public VarBindings getOrCreateBindings( int scope ) 
	{
		return varContext.getOrCreateBindings( scope );
	}

	public VarContext set( SelfBinding selfBinding ) 
	{
		return varContext.set(selfBinding);
	}

	public VarContext set( SelfBinding selfBinding, VarScope scope ) 
	{
		return varContext.set( selfBinding, scope );
	}

	public VarContext set( Var var ) 
	{
		return varContext.set( var );
	}

	public VarContext set( Var var, VarScope scope ) 
	{
		return varContext.set( var, scope );
	}

	public VarContext set( String name, Object value ) 
	{
		return varContext.set( name, value );
	}

	public VarContext set( String name, Object value, VarScope scope ) 
	{
		return varContext.set( name, value, scope );
	}

	public VarContext set( String name, Object value, int scope ) 
	{
		return varContext.set( name, value, scope );
	}

	public Object get( String name ) 
	{
		Object val = varContext.resolveVar( name );
		if( val == null )
		{
			if( currentIndex < queue.length )
			{
				// use the sequence bindings
				val = queue[ currentIndex ];
				
				//set the value on the VarContext
				varContext.set( name , val );
				//increment
				currentIndex ++;
			}
			else
			{
				throw new VarException(
					"Could not resolve varName \""+ name+"\" and value Sequence is depleted");
			}
		}
		return val;
	}
 
	public Object get( String name, VarScope scope ) 
	{
		return get( name, scope.getValue() );
	}

	public Object get( String name, int scope ) 
	{		
		Object val = varContext.resolveVar( name );
		if( val == null )
		{
			if( currentIndex < queue.length )
			{
				// use the sequence bindings
				val = queue[ currentIndex ];
				
				//set the value on the VarContext
				varContext.set( name , val );
				//increment
				currentIndex ++;
			}
			else
			{
				throw new VarException(
					"Could not resolve varName \""+ name+"\" and value Sequence is depleted");
			}
		}
		return val;
	}

	public List<Integer> getScopes() 
	{
		return varContext.getScopes();
	}

	public int getScopeOf( String name ) 
	{
		return varContext.getScopeOf( name );
	}

	public Object clear( String name, int scope ) 
	{
		return varContext.clear( name, scope );
	}

	public Object resolveVar( VarContext context, String varName ) 
	{
		Object var = resolveVar( varName );
		return var;
	}
	
	public Object resolveVar( String varName ) 
	{
		Object var = varContext.resolveVar( varName );
		if( var == null )
		{
			if( currentIndex < queue.length )
			{
				// use the sequence bindings
				var = queue[ currentIndex ];
				
				//set the value on the VarContext
				varContext.set( varName , var );
				//increment
				currentIndex ++;
			}
			else
			{
				throw new VarException(
					"Could not resolve varName \""+ varName+"\" and value Sequence is depleted");
			}
		}
		return var;
		
	}

	public VarResolver getVarResolver() 
	{
		return this;
	}

	public VarNameAudit getVarNameAudit() 
	{
		return varContext.getVarNameAudit();
	}

	public Evaluator getExpressionEvaluator() 
	{
		return varContext.getExpressionEvaluator();
	}

	public Directive getDirective(String name) 
	{
		return varContext.getDirective(name);
	}

	public Directive resolveDirective(String name) 
	{	
		return varContext.resolveDirective(name);
	}

	public DirectiveResolver getDirectiveResolver() 
	{
		return varContext.getDirectiveResolver();
	}

	public ScriptResolver getScriptResolver() 
	{
		return varContext.getScriptResolver();
	}

	public VarScript resolveScript(String scriptName, String scriptInput) 
	{
		return varContext.resolveScript(scriptName, scriptInput);
	}

	public Object evaluate(String expression) 
	{
		return varContext.evaluate(expression);
	}
}
