package varcode.context;

import java.util.List;

import varcode.VarException;
import varcode.context.Resolve.DirectiveResolver;
import varcode.context.Resolve.ScriptResolver;
import varcode.context.Resolve.VarResolver;
import varcode.context.VarBindings.SelfBinding;
import varcode.doc.Directive;
import varcode.doc.lib.Library;
import varcode.context.eval.Evaluator;
import varcode.markup.VarNameAudit;
import varcode.context.eval.VarScript;

/**
 * Container for (vars, scripts) for applying "specializations" 
 * applied to {@code Markup}
 * 
 * Maintains hierarchical "Scoped" 
 * <UL>
 *   <LI>Var(s) key value associated variables 
 *   (where the key is a String and variable is any Object) 
 *   <LI>VarScript(s) String key associated with {@code VarScript}
 *   <LI>Form(s) {@code VarForm}  
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarContext
{   
    public static VarContext load( Library... libraries )
    {
    	VarContext context = new VarContext();
    	for( int i = 0; i < libraries.length; i++ )
    	{
    		libraries[ i ].loadAtScope( context, VarScope.CORE_LIBRARY );
    	}
    	return context;
    }
    
    /**
     * Merges all (non-conflicting)properties from {@code anotherContext}
     * to this context 
     * @param anotherContext
     */
    public void merge( VarContext anotherContext )
    {
    	ScopeBindings otherBindings = anotherContext.getScopeBindings();
    	this.scopeBindings.merge( otherBindings );
    }
    
    public static VarContext of( Object... nameValuePairs )
    {
        return ofScope( VarScope.INSTANCE.getValue(), nameValuePairs );          
    }
    
    public static VarContext ofScope( VarScope scope, Object... nameValuePairs )
    {    	
        return ofScope( scope.getValue(), nameValuePairs );
        
    }

    public static VarContext ofScope( int scope, Object... nameValuePairs )
    {
        if( nameValuePairs.length % 2 != 0 )
        {
            throw new VarException( 
                "Pairs values must be passed in as pairs, length ("
                + nameValuePairs.length + ") not valid" );
        }

        if( nameValuePairs.length == 0 )
        {
            VarContext vc = new VarContext( );
            Bootstrap.init( vc );
        }
        VarContext context = new VarContext( );

        for( int i = 0; i < nameValuePairs.length; i += 2 )
        {
            context.set( 
                nameValuePairs[ i ].toString(), 
                nameValuePairs[ i + 1 ], 
                scope );
        }
        Bootstrap.init( context );
        return context;
    }

    /** Bindings (by Scope) of Vars and Scripts by name for use of 
     *  specialization/Tailoring*/
    private final ScopeBindings scopeBindings;
    
    protected VarContext()
    {   
        this( new ScopeBindings() );        
    }           

    protected VarContext( ScopeBindings scopeBindings )
    {
        this.scopeBindings = scopeBindings;
         
    }  
    
    public ScopeBindings getScopeBindings()
    {
        return scopeBindings;
    }
    
    public VarBindings getBindings( VarScope scope )
    {
        return scopeBindings.getBindings( scope );
    }
    
    public VarBindings getBindings( int scope )
    {
        return scopeBindings.getBindings( scope );
    }
    
    public VarBindings getOrCreateBindings( VarScope scope )
    {
        return scopeBindings.getOrCreateBindings( scope );
    }
    
    public VarBindings getOrCreateBindings( int scope )
    {
        return scopeBindings.getOrCreateBindings( VarScope.fromScope( scope ) );
    }  
    
    public VarContext set( SelfBinding selfBinding )
    {
    	return set( selfBinding, VarScope.INSTANCE );
    }
    
    public VarContext set( SelfBinding selfBinding, VarScope scope )
    {
    	selfBinding.bindTo( this.getOrCreateBindings( scope ) );
    	return this;
    }
    
    public VarContext set( Var var )
    {
    	return set( var.getName(), var.getValue() );
    }
    
    public VarContext set( Var var, VarScope scope )
    {
    	return set( var.getName(), var.getValue(), scope );
    }
    
    public VarContext set( String name, Object value )
    {
    	return set( name, value, VarScope.INSTANCE );
    }
    
    public VarContext set( String name, Object value, VarScope scope )
    {
    	 VarBindings vb = getOrCreateBindings( scope );
    	 Object oldValue = vb.put( name, value );
    	 if( oldValue != null )
    	 {
    		 //Log??
    	 }
         return this;
    }
    
    public VarContext set( String name, Object value, int scope )
    {
        VarBindings vb = getOrCreateBindings( scope );
        vb.put( name, value );
        return this;
    }
    
    public Object get( String name )
    {
        return scopeBindings.get( name );
    }
    
    public Object get( String name, VarScope scope )
    {
        return scopeBindings.get( name, scope.getValue() );
    }
    
    public Object get( String name, int scope )
    {
        return scopeBindings.get( name, scope );
    }

    public List<Integer>getScopes()
    {
        return ScopeBindings.ALL_SCOPES;
    }
    
    /** The scope of the var with name, -1 if not found */
    public int getScopeOf( String name )
    {
        return scopeBindings.getScopeOf( name );
    }

    public Object clear( String name, int scope )
    {
        VarBindings vb = scopeBindings.getBindings( scope );
        if( vb != null )
        {
            return vb.remove( name );
        }
        return null;
    }
    
    public String toString()
    {
    	return "_____________________________________________________" 
               + System.lineSeparator() +
    			"VarContext" + System.lineSeparator() +     		     
    			scopeBindings.toString()+ System.lineSeparator() +
    	       "_____________________________________________________";
    }
 
    public static final String VAR_RESOLVER_NAME = "_VAR_RESOLVER";

    public static final String DIRECTIVE_RESOLVER_NAME = "_DIRECTIVE_RESOLVER";
    
    public static final String SCRIPT_RESOLVER_NAME = "_SCRIPT_RESOLVER";
    
    public static final String VAR_NAME_AUDIT_NAME = "_VAR_NAME_AUDIT";
    
    public static final String EXPRESSION_EVALUATOR_NAME = "_EXPRESSION_EVALUATOR";

	public Object resolveVar( String varName ) 
	{
		return getVarResolver().resolveVar( this, varName );
	}
	
	public VarResolver getVarResolver() 
	{
		return (VarResolver)get( VAR_RESOLVER_NAME );
	}
	
	public VarNameAudit getVarNameAudit()
	{
		return (VarNameAudit)get( VAR_NAME_AUDIT_NAME );
	}
	
	public Evaluator getExpressionEvaluator()
	{
		return (Evaluator)
			scopeBindings.get( EXPRESSION_EVALUATOR_NAME );
	}

	public Directive getDirective( String name )
	{
		if( name.startsWith( "$" ) )
	    {
			return scopeBindings.getDirective( name );
	    }
	    return scopeBindings.getDirective( "$" + name );
	}	
	
	public Directive resolveDirective( String name )
	{
		if( name.startsWith( "$" ) )
	    {
			return scopeBindings.getDirective( name );
	    }
	    return scopeBindings.getDirective( "$" + name );
	}
	 
	public DirectiveResolver getDirectiveResolver()
	{
		return (DirectiveResolver)get( DIRECTIVE_RESOLVER_NAME );
	}
	
	public ScriptResolver getScriptResolver() 
	{
		return (ScriptResolver)get( SCRIPT_RESOLVER_NAME );
	}
	
	public VarScript resolveScript( String scriptName, String scriptInput ) 
	{
		ScriptResolver sr = (ScriptResolver)get( SCRIPT_RESOLVER_NAME );		
		return sr.resolveScript( this, scriptName, scriptInput );
	}
	
	public Object evaluate( String expression )
	{
		return getExpressionEvaluator().evaluate( this, expression );
	}

}
