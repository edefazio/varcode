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
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasScript;
import varcode.context.eval.VarScript;

/**
 * Mark to Add Code within the varcode (given a name) 
 * Optionally provide a default in case the name resolves to null.
 */ 
//CodeML : 
// "/*{+?(( env == test )):$capture(inputVO)+}*/"
// "/*{+?(( expression )):$script(params)+}*/"

//BindML :
// {+?(( env == test )):$capture(inputVO)+}
//     Context  : default / java
//     Expression : 
//     Script     : capture
//     params     : inputVO


public class AddScriptResultIfExpression
	extends Mark
	implements BlankFiller, HasScript 
{	
	private final String expression;
	
	private final String scriptName;
	
	private final String scriptInput;
	
	public AddScriptResultIfExpression(
        String text, 
        int lineNumber,
        String expression,
        String scriptName,
        String scriptInput)
    {
        super( text, lineNumber );
        this.expression = expression;
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;             
    }
	
	public Object derive( VarContext context ) 
	{	
		Object conditionResult = null;
		try
		{
			conditionResult = context.evaluate( expression );
		}
		catch( Exception e )
		{
			if( e instanceof VarException )
			{
				throw (VarException)e;
			}
			throw new VarException(
				"Expression error in \"" + expression + "\" for mark : " + N + text + N 
				+ "on line [" + lineNumber + "]" );
		}
		if( conditionResult instanceof Boolean && (Boolean)conditionResult )
		{
			 //VarScript script = context.getVarScript( scriptName );
			 VarScript script = context.resolveScript( scriptName, scriptInput );
		     if( script != null )
		     {
		    	 Object derived = null;
		         try
		         {
		        	 derived = script.eval( context, scriptInput );                
		         }
		         catch( Exception e )
		         {
		        	 throw new VarException( 
		                "Error evaluating AddScriptResult Mark \"" + N + text + N 
		                + "\" on line [" + lineNumber + "] as :" + N 
		                + this.toString() + N, e );
		         }
		         return derived;
		     }	
		}		
        //the script wasnt required
        return "";
	}
	
	public void fill( VarContext context, TranslateBuffer buffer )
	{
	    buffer.append( derive( context ) );
	}

    
    public String getScriptName()
    {
        return scriptName;
    }

	
	public String getScriptInput() 
	{
		return scriptInput;
	}

    public void getAllVarNames( Set<String>varNames, VarContext context )
    {
    	VarScript script = context.resolveScript( scriptName, scriptInput );
    	if( script == null )
		{ 
			throw new VarBindException( 
				"Unable to resolve script named \"" + getScriptName() 
				+ "\" for Mark :"+ N + text + N + "on line [" + lineNumber + "]");
		}
        script.collectAllVarNames(varNames, scriptInput );
    }
}	