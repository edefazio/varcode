/*
 * Copyright 2015 M. Eric DeFazio.
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

import varcode.VarException;
import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.doc.form.Form;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasExpression;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.HasVars;

/**
 * A Form of Code (one or more java statements) that is <I>conditionally</I> 
 * written to the tailored source. If the Expression is evaluated to be
 * true.
 *  
 *  <PRE>/ *{{+?(log==trace): LOG.trace( "Inside {+methodName}() Loop"); }}* /</PRE>
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */

//BindML :
// {{+?(( logLevel > debug )): LOG.debug("Resolved "+ {+vari+}.toString() +" for service call");+}}

//CodeML : 
/*{{+?((logLevel >= debug )):
import {+logFactory};
import {+logger}; 
}}*/

public class AddFormIfExpression
    extends Mark
    implements BlankFiller, HasForm, HasVars, HasExpression
{
    /** the expression to be evaluated */
    private final String expression;
        
    /** form of code conditionally written when tailoring source */ 
    private final Form form;
   
    public AddFormIfExpression( 
        String text, int lineNumber, String expression, Form form )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.form = form;
    }
        
        
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
    	Object res = null;
    	try
    	{
    		res = context.evaluate( expression );
    	}
    	catch( Exception e )
    	{
    		return null;
    	}
    	try
    	{
    		if( res != null && res instanceof Boolean && (Boolean)res )
    		{
    			return form.derive( context );
    		}
    		return null;
    	}
    	catch( Exception e )
    	{
    		 throw new VarException (
    	        "Unable to derive Form for AddFormIfExpression \"" + form + "\" for mark " + N 
    	           + text + N + " on line [" + lineNumber + "]", e );
    	}
    }
    
    public Form getForm()
    {
        return form;
    }    
    
    public void collectVarNames( Set<String>varNames, VarContext context )
    {
        form.collectVarNames( varNames, context );
    }

	public String getExpression() 
	{
		return expression;
	}    
}
