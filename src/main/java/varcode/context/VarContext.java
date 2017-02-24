/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.context;

import java.util.Set;
import varcode.context.resolve.InitVarContextBindings;
import varcode.context.resolve.StaticFieldsScraper;

import varcode.context.resolve.Resolve;
import varcode.context.resolve.DirectiveResolver;
import varcode.context.resolve.VarResolver;
import varcode.context.resolve.VarScriptResolver;

/**
 * Container for (vars, varScripts) for applying "specializations" applied to
 {@code Markup}
 *
 * Maintains hierarchical "Scoped"
 * <UL>
 * <LI>Var(s) key value associated variables (where the key is a String and
 * variable is any Object)
 * <LI>VarScript(s) String key associated with {@code VarScript}
 * <LI>Form(s) {@code VarForm}
 * </UL>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarContext implements Context
{
    /** Resolves variables (data) by name */
    private VarResolver varResolver;
    
    /** Resolves VarScripts (methods/functions) by name */
    private VarScriptResolver varScriptResolver;
    
    /** Resolves Directives ({@link PreProcessor}s {@link PostProcessor}s by name */
    private DirectiveResolver directiveResolver;

    @Override
    public void remove( String name )
    {
        this.scopeBindings.remove( name );
    }
    
    public static VarContext ofKeyValueArray( Object[] keyValues )
    {
        //return ofScope( VarScope.INSTANCE, keyValues );        
        if( keyValues.length % 2 != 0 )
        {
            throw new VarBindException(
                "Pairs values must be passed in as pairs, length ("
                + keyValues.length + ") not valid" );
        }
        VarContext context = new VarContext();
        
        for( int i = 0; i < keyValues.length; i += 2 )
        {
            context.set( keyValues[ i ].toString(),
                keyValues[ i + 1 ],
                VarScope.INSTANCE );
        }
        InitVarContextBindings.INSTANCE.registerTo( context );
        return context;
    }
    
    /**
     * Constructs a VarContext and binds values in sequence as key value pairs:
     * for example:
     * <CODE>
     * VarContext vc = VarContext.of( "a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
     * </CODE>
     * will bind :
     * <TABLE>
     * <TR><TD>key</TD><TD>val</TD></TR>
     * <TR><TD>"a"</TD><TD>1</TD></TR>
     * <TR><TD>"b</TD><TD>2</TD></TR>
     * <TR><TD>"c"</TD><TD>3</TD></TR>
     * <TR><TD>"d"</TD><TD>4</TD></TR>
     * <TR><TD>"e"</TD><TD>5</TD></TR>
     * </TABLE>
     * 
     * @param keyValuePairs data passed in as alternating key values
     * @return the Context populated with bound key value pairs (at INSTANCE scope)
     */
    public static VarContext of( Object... keyValuePairs )
    {
        return ofScope( VarScope.INSTANCE, keyValuePairs );
    }
    
    private static VarContext ofScope( VarScope scope, Object... keyValuePairs )
    {
        if( keyValuePairs.length % 2 != 0 )
        {
            System.out.println( "KV " + keyValuePairs );
            System.out.println( "KV " + keyValuePairs.length );
            System.out.println( "KV " + keyValuePairs[0] );
            throw new VarBindException(
                "Pairs values must be passed in as pairs, length ("
                + keyValuePairs.length + ") not valid" );
        }
        VarContext context = new VarContext();
        
        for( int i = 0; i < keyValuePairs.length; i += 2 )
        {
            context.set( keyValuePairs[ i ].toString(),
                keyValuePairs[ i + 1 ],
                scope );
        }
        InitVarContextBindings.INSTANCE.registerTo( context );
        return context;
    }

    /**
     * Bindings (by Scope) of Vars and Scripts by name for use of 
     *  specialization/Tailoring
     */
    private final ScopeBindings scopeBindings;

    protected VarContext()
    {
        this( new ScopeBindings() );
    }

    protected VarContext( ScopeBindings scopeBindings )
    {
        this.scopeBindings = scopeBindings;
    }

    @Override
    public Context clearAllScopeBindings( VarScope scope )
    {
        VarBindings sb = scopeBindings.getBindings( scope );
        if( sb != null )
        {
            sb.clear();
        }
        return this;
    }
    
    @Override
    public Set<String> keySet()
    {
        return this.scopeBindings.keySet();
    }
    
    /** 
     * Gets a Bindings at the given scope or Creates a new Bindings at the scope
     * and returns it
     */
    private VarBindings getOrCreateBindings( int scope )
    {
        return scopeBindings.getOrCreateBindings( VarScope.fromScope( scope ) );
    }

    /**
     * Sets the Class as the Resolve Base, where Vars, (static methods) Scripts,
     * and Directives can be resolved automatically
     *
     * @param resolveBaseClass the base class
     * @return the VarContext
     */
    @Override
    public VarContext setResolveBase( Class resolveBaseClass )
    {
        set( Resolve.BASECLASS_PROPERTY, resolveBaseClass, VarScope.INSTANCE );
        return this;
    }

    @Override
    public VarContext set( String name, Object value )
    {
        return set( name, value, VarScope.INSTANCE );
    }

    
    @Override
    public VarContext set( String name, Object value, VarScope scope )
    {
        VarBindings vb = 
            this.scopeBindings.getOrCreateBindings( scope );
        Object oldValue = vb.put( name, value );
        if( oldValue != null )
        {
            //Log??
        }
        return this;
    }

    @Override
    public Object get( String name)
    {
        return scopeBindings.get( name );
    }

    @Override
    public String toString()
    {
        return "_____________________________________________________"
            + System.lineSeparator()
            + "VarContext" + System.lineSeparator()
            + scopeBindings.toString() + System.lineSeparator()
            + "_____________________________________________________";
    }


    @Override
    public Object resolveVar( String varName )
    {
        return getVarResolver().resolveVar( this, varName );
    }

    @Override
    public VarResolver getVarResolver()
    {
        return this.varResolver;
    }

    @Override
    public Directive resolveDirective( String name )
    {
        if( name.startsWith( "$" ) )
        {
            return scopeBindings.getDirective( name );
        }
        return scopeBindings.getDirective( "$" + name );
    }

    @Override
    public DirectiveResolver getDirectiveResolver()
    {
        return this.directiveResolver;
    }

    @Override
    public VarScriptResolver getVarScriptResolver()
    {
        return this.varScriptResolver;
    }

    @Override
    public VarScript resolveScript( String scriptName, String scriptInput )
    {
        VarScriptResolver sr = getVarScriptResolver();
        return sr.resolveScript( this, scriptName, scriptInput );
    }

    public void setDirectiveResolver ( DirectiveResolver dr )
    {
        this.directiveResolver = dr;
    }

    public void setVarResolver( VarResolver vr )
    {
        this.varResolver = vr;
    }
    
    public void setVarScriptResolver( VarScriptResolver vsr )
    {
        this.varScriptResolver = vsr;
    }
        
    @Override
    public Context register( Class componentsAsStaticFields )
    {
        //Read all the static fields from the Class,
        // if there are any Components
        StaticFieldsScraper.scrapeStaticFields( componentsAsStaticFields )
            .registerTo( this );        
        return this;
    }
}
