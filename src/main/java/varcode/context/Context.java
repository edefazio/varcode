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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;
import varcode.context.resolve.DirectiveResolver;
import varcode.context.resolve.VarResolver;
import varcode.context.resolve.VarScriptResolver;

/**
 * Provides BOTH data and functionality used in authoring 
 * {@link varcode.markup.Template}s into documents.
 * 
 * <P>
 * The Context is where the State/Specialization for a {@link Template}
 * is contained.  When {@link Template}s are being authored, the {@link Mark}s 
 * request Data (vars) and Functionality ({@link VarScript}s, {@link Directive}s) 
 * bound by name, for example:
 * <UL>
 *  <LI>the Mark : "{+a+}" {@link varcode.markup.mark.AddVar} within a 
 * {@link varcode.markup.Template} will ask the {@link Context} to resolve
 * a value bound for the var "a" using the 
 * {@link varcode.context.resolve.Resolve.VarResolver} when the 
 * {@link #get(java.lang.String)} method is called.
 * 
 *  <LI>the Mark : "{+$>(a)+}" {@link varcode.markup.mark.AddScriptResult} 
 * within a {@link varcode.markup.Template} will ask the {@link Context} to 
 * resolve the {@link VarScript} bound to the name ">" using the 
 * {@link varcode.context.resolve.Resolve.ScriptResolver} when the 
 * {@link #resolveScript(java.lang.String, java.lang.String)} method is called.
 * 
 *  <LI>the Mark : "{$$stripMarks$$}" {@link varcode.markup.mark.AuthorDirective} 
 * within a {@link varcode.markup.Template} will ask the {@link Context} to 
 * resolve the {@link Directive} (either a {@link varcode.author.PreProcessor} 
 * or {@link varcode.author.PostProcessor}) bound to the name "stripMarks" 
 * using the {@link varcode.context.resolve.Resolve.DirectiveResolver} 
 * when the {@link #resolveDirective(java.lang.String)} method is called.
 * 
 * </UL>
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Context
{    
    /**
     * Provide alternative name(s) bindings for some functionality
     * (i.e. {@link VarScript}s and {@link Directive}s)
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface alias
    {
        /** 
         * return all the name bindings 
         * @return all alias names for functionality
         */
        String[] value();
    }
    
    /**
     * Pass in a Class that contains static fields that can be of type 
     * DirectiveResolver, VarResolver, VarScriptResolver
     * and it will bind these tools internally to be used by the Context
     * It will also bind static fields that are
     * or 
     * {@link VarScript}, {@link PreProcessor}, {@link PostProcessor}
     * 
     * and bind 
     * 
     * registered
     * 
     * {@link varcode.context.resolve.DefaultContextBindings}
     * 
     * @param staticFields a class containing
     * @return 
     */
    public Context register( Class staticFields );
    
    void setVarScriptResolver( VarScriptResolver scriptResolver );
    
    /** 
     * Strategy for resolving {@link VarScript} implementations 
     * (i.e. for {@link varcode.markup.mark.AddScriptResult} marks like
     * "trim" for Markup "{+$trim(param)+}"
     * 
     * @return the VarScriptResolver used by the context
     */
    VarScriptResolver getVarScriptResolver();
    
    VarScript resolveScript( String scriptName, String scriptInput );
    

    void setVarResolver( VarResolver varResolver );
    
    /** 
     * strategy for resolving variables (data to bind into templates)
     * (i.e. for {@link varcode.markup.mark.AddVar} marks like
     * "var" for Markup "{+var+}"
     * 
     * @return the VarResolver used by the Context
     */ 
    VarResolver getVarResolver();
    
    Object resolveVar( String varName );
    
    
    void setDirectiveResolver( DirectiveResolver directiveResolver );
    
    /**
     * strategy for resolving {@link varcode.author.PreProcessor} 
     * @return the DirectiveResolver
     */
    DirectiveResolver getDirectiveResolver();
    
    Directive resolveDirective( String name );
    
    
    /**
     * Generic "get" method (will return data, scripts, etc.)
     * @param name
     * @return 
     */
    Object get( String name );
    
    Context set( String name, Object value );

    Context set( String name, Object value, VarScope scope );
    
    void remove( String name );
    
    
    Set<String> keySet();
    
    /** 
     * 
     * Clears all bindings at a given scope, for when the Author 
     * exits a given scope 
     * @param scope the scope to clear of all var-bindings
     * @return the Context with the Scope cleared
     */
    Context clearAllScopeBindings( VarScope scope );
    
    
    /**
     * Sets the Class as the Resolve Base, where Vars, (static methods) Scripts,
     * and Directives can be resolved automatically
     *
     * @param resolveBaseClass the base class
     * @return
     */
    Context setResolveBase( Class resolveBaseClass );
    
}
