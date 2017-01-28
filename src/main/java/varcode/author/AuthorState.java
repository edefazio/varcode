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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import varcode.context.Context;
import varcode.context.VarBindException;

import varcode.markup.Template;
import varcode.markup.mark.AuthorDirective;
import varcode.markup.mark.Mark;

import varcode.translate.TranslateBuffer;

/**
 * The State maintained when authoring the document from the 
 * {@code Frame} via {@code Mark}s and {@code VarContext})  
 * 
 *  @author M. Eric DeFazio eric@varcode.io
 */
public class AuthorState
{
    /** 
     * the immutable {@code Template} containing 
     * {@code Mark}s and {@code FillInTheBlanks.BindTemplate} 
     */
    private Template template;
     
    /** 
     * Bound input vars (name/value pairs), components and a mutable workspaces
     * for deriving / binding instance data used for filling in the
     * used whiled tailoring the {@code Template}, contains:
     * <UL>
     * <LI>input data for deriving and filling in the {@code FillInTheBlanks.FillTemplate}
     * <LI>functionality ({@code ExpressionEvaluator}, {@code VarNameAudit} 
     * <LI>bound {@code VarScript}s 
     * <LI>{@code Metadata}
     * </UL> 
     */
    private Context modelContext;
     
    /** 
     * Translates Objects to text and writes text to document buffer
     */
    private TranslateBuffer translateBuffer; 
    
    /**
     * Directives CAN be specified within the {@code Template} AND/OR
     * as input to the Author.XXX method, (directives in the {@code Template} 
     * and these in the will be evaluated.
     * 
     * We store Pre Processors and Post Processors in a single Array, 
     * since SOME Directives may be both Pre-Process and Post-Process
     */
    private Directive[] directives; 
    
    public AuthorState( 
        Template template, Context context, Directive...directives )
    {
    	this( template, context, new TranslateBuffer(), directives );
    }
    
    public AuthorState( 
        Template template, 
        Context context, 
        TranslateBuffer translateBuffer, 
        Directive...directives )
    {
        this.template = template;
        this.modelContext = context;
        this.translateBuffer = translateBuffer;
        this.directives = collectDirectives( 
            template, this.modelContext, directives );
    }

    public Directive[] getDirectives()
    {
    	return this.directives;
    }
    
    public PreProcessor[] getPreProcessors( )
    {
    	if( directives == null )
    	{
            return new PreProcessor[ 0 ];
    	}
    	List<PreProcessor> preProcessors = new ArrayList<PreProcessor>();
    	for( int i = 0; i < directives.length; i++ )
    	{
            if( directives[ i ] instanceof PreProcessor )
            {
    		preProcessors.add( (PreProcessor) directives[ i ] );
            }
    	}
    	return preProcessors.toArray( new PreProcessor[ 0 ] );
    }

    public PostProcessor[] getPostProcessors( )
    {
    	if( directives == null )
    	{
            return new PostProcessor[ 0 ];
    	}
    	List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();
    	for( int i = 0; i < directives.length; i++ )
    	{
            if( directives[ i ] instanceof PostProcessor )
            {
    		postProcessors.add( (PostProcessor) directives[ i ] );
            }
    	}
    	return postProcessors.toArray( new PostProcessor[ 0 ] );
    }
    
    public Template getTemplate() 
    {
	return template;
    }

    public void setTemplate( Template template ) 
    {
	this.template = template;
    }

    public Context getContext() 
    {
	return modelContext;
    }
	
    public void setContext( Context varContext ) 
    {
	this.modelContext = varContext;
    }

    public TranslateBuffer getTranslateBuffer() 
    {
	return translateBuffer;
    }

    public void setTranslateBuffer( TranslateBuffer translateBuffer ) 
    {
	this.translateBuffer = translateBuffer;
    }        
	
    private static Directive[] collectDirectives( 
        Template template, Context context, Directive[] directives )
    {
	List<Directive> allDirectives = 
	    new ArrayList<Directive>();
	    
	Mark[] allMarks = template.getMarks();
	    
	//add all of the Dom directives
	for( int i = 0; i < allMarks.length; i++ )
	{   
            if( allMarks[ i ] instanceof AuthorDirective )
	    {
	    	AuthorDirective directiveMark = (AuthorDirective)allMarks[ i ];	
	    	Directive d = context.resolveDirective( directiveMark.getName() );
	    	if( d == null )
	    	{
                    throw new VarBindException( 
                        "Could not find Directive by name \"" 
                        + directiveMark.getName() + "\"" 
	    		+ " for Mark : " + "\r\n"
	    		+ directiveMark.getText() + "\r\n"
                        + "on line [" + directiveMark.getLineNumber() + "]"  );
	    	}
	    	allDirectives.add( d );
            }
	}
        
	//add all of the  passed in directives
	allDirectives.addAll( Arrays.asList( directives ) );
	    
	return allDirectives.toArray( new Directive[ 0 ] );
    }
}	