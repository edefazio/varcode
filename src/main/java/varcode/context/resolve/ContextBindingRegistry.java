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
package varcode.context.resolve;

import java.util.HashMap;
import java.util.Map;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarScope;
import varcode.context.VarScript;

/**
 * Maintains Bindings to {@link Resolver}s, {@link VarScript}s {@link Directive}s
 * to be "registered" into a {@list Context}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ContextBindingRegistry
{
    /** 
     * VarScript implementations that are to be 
     * bound by field name and @alias({...}) 
     */
    public final Map<String,VarScript>nameToVarScriptBinding = 
        new HashMap<String,VarScript>();
    
    /** 
     * Directive (PreProcessor and PostProcessor) implementations 
     * bound by field name and @alias({...]})
     */
    public final Map<String,Directive>nameToDirectiveBinding = 
        new HashMap<String, Directive>();
    
    public VarResolver varResolver;
    public VarScriptResolver varScriptResolver;
    public DirectiveResolver directiveResolver;
    
    /**
     * 
     * @return the Bindings associated with the names
     */
    @Override
    public String toString()
    {
        return 
            this.nameToVarScriptBinding + System.lineSeparator() +
            this.nameToDirectiveBinding;
    }
    
    
    public ContextBindingRegistry setResolver( Resolve resolve )
    {
        if( resolve instanceof VarScriptResolver )
        {
            this.varScriptResolver = (VarScriptResolver)resolve;
        }
        if( resolve instanceof VarResolver )
        {
            this.varResolver = (VarResolver)resolve;
        }
        if( resolve instanceof DirectiveResolver )
        {
            this.directiveResolver = (DirectiveResolver) resolve;
        }
        return this;
    }
    
    /**
     * Register all of the static fields to the Context 
     * 
     * @param context the content to register the bindings to
     */
    public void registerTo( Context context )
    {
        if( this.directiveResolver != null )
        {
            context.setDirectiveResolver( directiveResolver );
        }
        if( this.varResolver != null )
        {
            context.setVarResolver( varResolver );
        }
        if( this.varScriptResolver != null )
        {
            context.setVarScriptResolver( varScriptResolver );
        }
        
        String[] scriptKeys = nameToVarScriptBinding.keySet().toArray( 
            new String[ 0 ] );
        
        for( int i = 0; i < scriptKeys.length; i++ )
        {
            context.set( "$" + scriptKeys[ i ], 
                nameToVarScriptBinding.get( scriptKeys[ i ] ), VarScope.LIBRARY );            
        }
        
        String[] directiveKeys = nameToDirectiveBinding.keySet().toArray( new String[0]);
        for( int i = 0; i < directiveKeys.length; i++ )
        {
            context.set( "$" + directiveKeys[ i ], 
                nameToDirectiveBinding.get( directiveKeys[ i ] ), VarScope.LIBRARY );            
        }
    }
}
