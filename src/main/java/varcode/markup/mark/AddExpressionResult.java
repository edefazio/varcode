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

import varcode.buffer.TranslateBuffer;
import varcode.context.VarContext;
import varcode.eval.EvalException;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasExpression;

/**
 * Add the Result of the Evaluated Expression to the Tailored Text
 */ 

//BindML:
// "{+(( Math.sqrt( a * a + b * b ) ))+}"

//CodeML:
// "/*{+(( Math.sqrt( a * a + b * b ) ))+}*/"

public class AddExpressionResult
	extends Mark
	implements BlankFiller, HasExpression
{	
	private final String expression;
	
	public AddExpressionResult(
        String text, 
        int lineNumber,
        String expression )
    {
        super( text, lineNumber );
        this.expression = expression;        
    }
	
	public Object derive( VarContext context ) 
	{	
        try
        {
        	Object res = context.getExpressionEvaluator().evaluate( 
        		context.getScopeBindings(), expression );
        	return res;
        }
        catch( Exception e )
        {
        	 throw new EvalException( 
                 "Error evaluating Expression \"" + expression 
               + "\" for mark " + N + text + N 
               + "on line [" + lineNumber + "]", e );
        }
	}
	
	public void fill( VarContext context, TranslateBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}

	public String getExpression() 
	{
		return this.expression;
	}
}	
