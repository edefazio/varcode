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
package varcode.markup.mark;

import java.util.Set;
import varcode.context.Context;
import varcode.context.VarBindException;

import varcode.translate.TranslateBuffer;
import varcode.markup.form.Form;

/** 
 * "Action" associated with a textual {@code Mark} within Markup file / 
 * {@link varcode.markup.Template}.
 * 
 * {@code Mark}s provide ways of manipulating the {@code Template} to 
 * author the textual output
 * 
 * {@code Mark}s are like a <A HREF="https://en.wikipedia.org/wiki/Scriptlet">scriptlet</A> 
 * tag in a JSPs, but they DO NOT ENFORCE Syntax.
 * <P>
 * --We use the term "Mark" to imply more of a 
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup</A>
 * nature of the document.
 * </P>
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class Mark
{
    protected static final String N = "\r\n";
    
    /** text representation the {@code Mark} i.e. "{+name+}" */
    protected final String text;
    
    /** the line number within the {@code Dom}*/ 
    protected final int lineNumber;
    
    public Mark( String text, int lineNumber )
    {
        this.text = text;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString()
    {
    	return "\""+ this.text + "\" (" + this.getClass().getSimpleName() + ") on line [" + lineNumber + "]";
    }
    public String getText()
    {
        return text;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }
    
    /** Gets the names of the vars name used in processing the mark */
    public interface HasVar
    {
        public String getVarName();
    }
    
    /**
     * A Mark that May contain one or more named Vars
     */
    public interface HasVars
    {
        public Set<String>getVarNames();
    }
    
    /** A Mark whose "value" is derived / evaluated using the {@code Context} */
    public interface Derived
    {       
        /** Derive the result given the {@code context} and return it*/
        public Object derive( Context context )
            throws VarBindException;        
    }
        
    /** 
     * Mark that is derived from a {@code Form} that can be bound
     * 
     * @see ReplaceWithForm
     * @see AddFormIfVar
     */
    public interface HasForm
        extends Derived
    {        
        /** the Form (either a {@code VarForm} of {@code StaticForm} for the Content*/
        public Form getForm();        
    }
    
    /** Does the mark have or use and expression? */
    public interface HasExpression
    {
    	public String getExpression();
    }
    
    /** Does this mark action use a bound {@code VarScript}?
     * 
     * @see AddScriptResult
     */ 
    public interface HasVarScript    	
    {
    	/** gets the name of the VarScript */
        public String getVarScriptName();
        
        /** gets the input String to the Script */
        public String getVarScriptInput();        
    }
    
    
    /** 
     * Binds text into a predefined blank when Tailoring {@code Markup}.<BR><BR>
     * 
     * it is MarkupStateAware, because (while parsing the Marks from the source)
     * it needs to know where (within the Tailored output code) it needs to start 
     * writing content/filling in the blank). 
     */
    public interface Bind
        extends Derived
    {   
        /** Populate a "blank" with derived value in the DocBuffer */
        public void bind( Context context, TranslateBuffer buffer );        
    }
    
    /** 
     * Some mark types have the ability to be "required" meaning a non-null
     * value must be supplied for Tailoring to take place (otherwise failure)
     */
    public interface MayBeRequired
    {    	
        public boolean isRequired();        
    }    
}
