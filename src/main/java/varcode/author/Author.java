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
package varcode.author;

import varcode.context.Directive;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.markup.bindml.BindML;
import varcode.markup.mark.Mark;
import varcode.markup.Template;

/**
 * The Process for specializing {@code Template}s with Data/Functionality 
 * provided within a {@code Context} and optionally some {@PreProcessor} 
 * or {@PostProcessor} {@code Directives}.
 * 
 * Not specific to generating Java source, can also can create other 
 * textual based artifacts like:
 * <UL>
 * <LI> IDE project files
 * <LI> Docker files
 * <LI> Maven POMs
 * <LI> SQL queries 
 * <LI> ...
 * </UL>
 * 
 * @see varcode.markup.Fill 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Author
{       
    ;
    
    /**
     * 
     * for example:
     * <PRE>
     * String document = Author.toString( 
     *     "Hi, {+name+}!", 
     * VarContext.of( "name", "Eric") );
     * 
     * // document = "Hi, Eric!"
     * </PRE>
     * 
     * @param markup some markup String (in {@link BindML} )
     * @param context specialization data and functionality
     * @param directives Pre and Post Processing instructions
     * @return an Authored String
     */
    public static final String toString( 
        String markup, Context context, Directive...directives )
    {
        return toString( BindML.compile( markup ), context, directives );
    }
    
    /**
     * For example:
     * <PRE>
     * AuthorState as = Author.toState(
     *   "Hello, {+name+}!", 
     * VarContext.of( "name", "Eric" ) );
     * </PRE>
     * 
     * @param markup toString in {@link BindML}
     * @param context
     * @param directives optional Authoring directives (Pre and post processors)
     * @return 
     */
    public static final AuthorState toState( 
        String markup, Context context, Directive...directives ) 
    {
        return Author.toState( BindML.compile( markup ), 
            context,
            directives );
    }
       
    /** 
     * Compiles the {@link BindML} toState to a {@code Template} 
     * and authors the document using the keyValuesPairs, returning a
     * String representation of the document.     
     * <PRE>
     * String message = Author.toString( 
     *    "Hello, {+thing+}!", 
     *    "thing", "World" ); //the key "thing" mapped to "World"
     * 
     * //message = "Hello, World!"
     * </PRE>
     * @param markup a String that uses {@link BindML} Markup i.e. "{+name+}"
     * @param keyValuePairs data provided in keyValue pairs
     * @return the authored document
     */
    public static String toString( 
        String markup, Object...keyValuePairs )
    {
        return Author.toString( BindML.compile( markup ), keyValuePairs );
    }
    
    /**
     * 
     * <PRE>
     * AuthorState aState = Author.toState( "public interface {+name+} {}", 
     *     "name", "MyInterface" ); //the key "name" mapped to "MyInterface" aState.
     * </PRE>
     * 
     * @param markup String toState in {@link BindML}
     * @param keyValuePairs even number of keyValue Pairs of data values
     * @return an updated AuthorState containing the document and processing 
     * details
     */
    public static AuthorState toState( 
        String markup, Object...keyValuePairs )
    {
        return Author.toState( BindML.compile( markup ), 
            VarContext.of( keyValuePairs ) );
    }
    
    /** 
     * Composes the Document to a String 
     * @param template the dom model for the document
     * @param keyValuePairs data as Key Value Pairs for filling in the document
     * @return the composed document as a String
     */
    public static String toString( 
        Template template, Object...keyValuePairs )
    {
        return toString( template, VarContext.of( keyValuePairs ) );
    }

    /**
     * Author the Template with the keyValue pairs and return the {@code AuthorState}
     * 
     * for example Author.toState()
     * ( NOTE: we return the AuthorState (and NOT a String) for situations where
     * we might need the output to be something different than a String
     * (like an {@link varcode.java.adhoc.AdHocJavaFile} for instance).
     * 
     * @param template the toState 
     * @param keyValuePairs key value Data used to populating the Frames Template
     * @return the AuthorState the updated AuthorState
     */
    public static AuthorState toState( 
        Template template, Object...keyValuePairs )
    {
        return Author.toState( 
            template, VarContext.of( keyValuePairs ), new Directive[ 0 ] ); 
    }
    
    /** 
     * Composes the Document to a String 
     * @param template the structure / marks for the document
     * @param context data (and functionality) used for filling in the document
     * @param directives pre and post processing routines for the document
     * (for instance I might want to run a code LINT AFTER creating code)
     * @return the composed document as a String
     */
    public static final String toString( 
        Template template, Context context, Directive...directives )
    {
    	AuthorState docState = new AuthorState( 
            template, 
    	    context, 
    	    directives );
    	
    	toState( docState );
        
    	return docState.getTranslateBuffer().toString();
    }
    
    /**
     * Composes the Document and returns the DocState
     * @param template the structure of the document
     * @param context data (and functionality) used for filling in the document
     * @param directives pre and post processing routines for the document
     * (for instance I might want to run a code LINT AFTER creating code)
     * @return the DocState containing the Document 
     */
    public static final AuthorState toState( 
        Template template, Context context, Directive...directives )
    {
    	AuthorState initialAuthorState = new AuthorState( 
    	    template, context, directives );
        
    	return toState( initialAuthorState );
    }    
    
    /**
     * Processes the authorState (all changes are updated within the AuthorState) 
     * 
     * Given the "initial" authorState, process the 
     * {@code Template} (within the {@code AuthorState}) with 
     * vars and functionality in the {@code Context}
     * 
     * @param authorState contains a {@code Template}, a {@code Context} 
     * and {@code Directive}s used for updating the AuthorState.     
     * @return the updated authorState 
     */ 
    public static final AuthorState toState( AuthorState authorState )
        throws VarBindException 
    {    	
        PreProcessor[] preProcess = authorState.getPreProcessors();
        //LOG.trace("1) Pre-process (" + preProcess.length + ") directives" );
        
        if( preProcess.length > 0 )
        {
            for( int i = 0; i < preProcess.length; i++ )
            {        		
                //if( LOG.isTraceEnabled() ) 
                //{ 
                //    LOG.trace("   pre-process [" + i + "]: " + preProcess[ i ] ); 
                //}
                preProcess[ i ].preProcess(authorState );
            }
        }        
        //LOG.trace( "2) Derive / Bind instance vars to toState" );
        Mark[] marks = authorState.getTemplate().getMarks();
        
        for( int i = 0; i < marks.length; i++ )
        {   //derive and bind all the dynamically defined Vars in the VarContext
            if( marks[ i ] instanceof Mark.Dynamic )
            {            	
                Mark.Dynamic dynamicBound = (Mark.Dynamic)marks[ i ];
                dynamicBound.define( authorState.getContext() ); //this will derive the var, then update the context
                //if( LOG.isTraceEnabled() ) 
                //{ 
                //    String name = dynamicBound.getVarName();
                //    Object varValue = authorState.getContext().resolveVar( name );
                	//logValueBuffer.append( varValue );
                //    LOG.trace("  bound: " + marks[ i ] +" as : \"" + name 
                //            + "\"->" + TRANSLATE.translate( varValue ) );
                //}
            }
            //it might be derived but not bound (i.e. input validation scripts, EvalScript)
            else if ( marks[ i ] instanceof Mark.Derived 
                && !( marks[ i ] instanceof Mark.Bind ) )//don't derive fillers until they are to be populated 
                //&& !( marks[ i ] instanceof Mark.Static ) ) //we don't need to derive static vars
            {            	
                Mark.Derived dd = (Mark.Derived) marks[ i ];
                Object derived = dd.derive(authorState.getContext() );
                
                //if( LOG.isTraceEnabled() ) 
                //{
                //    LOG.trace("  derived: " + marks[ i ] + 
                //        " as \"" + TRANSLATE.translate( derived ) + "\"" );
                //}
            }
        }        
        Mark.Bind[] bindToBlankMarks = authorState.getTemplate().getBindMarks();
        //LOG.trace("3) Fill-in toState with (" + bindToBlankMarks.length + ") blanks" );
        
        Object[] fillSequence = new Object[ bindToBlankMarks.length ];
        for( int i = 0; i < bindToBlankMarks.length; i++ )
        {
            Object derived = bindToBlankMarks[ i ].derive( authorState.getContext() );
            fillSequence[ i ] = derived;
            //if( LOG.isTraceEnabled() ) 
            //{
            //    LOG.trace("   filled[" + i + "]: " 
            //        + bindToBlankMarks[ i ].toString() + " with \"" 
            //        + TRANSLATE.translate( derived ) + "\"");
            //}            
        }
        authorState.getTemplate().getBlankBinding().bind(authorState.getTranslateBuffer(), fillSequence );
        
        //5) Post Processing All Directives        
        PostProcessor[] postProcessors = authorState.getPostProcessors( );
        //LOG.trace( "4) Post-process (" + postProcessors.length + ") directives" );
        if( postProcessors.length > 0 )
        {
            for( int i = 0; i < postProcessors.length; i++ )
            {
                //if( LOG.isTraceEnabled() ) 
                //{ 
                //    LOG.trace( "   post-process[" + i + "]: " + postProcessors[ i ] ); 
                //}
                postProcessors[ i ].postProcess(authorState );
            }
        }             
        return authorState;
    }        
}
