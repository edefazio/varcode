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
import varcode.buffer.TranslateBuffer;
import varcode.context.VarBindException.NullResult;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasScript;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.MayBeRequired;
import varcode.script.VarScript;

/**
 * Mark to Add Code within the varcode (given a name) 
 * Optionally provide a default in case the name resolves to null.
 */ 

// usual form, register a script "derive" to the environment
// example      : "/*{+$derive(valueObject, a)}*/"
//	   Prefix   : "/*{+$"
//     Postfix  : "}*/"
//     Context  : default / java
//     Script   : derive
//     params   : {valueObject,"a"}

/*{+$intframe(age[0...130]("years old"),height[0...200]("inches"),weight[0...900]("lbs."))*/

public class AddScriptResult
	extends Mark
	implements BlankFiller, HasScript, MayBeRequired, HasVars
{	
	private final String scriptName;
	
	private final String scriptInput;
	
	private final boolean isRequired;
	
	public AddScriptResult(
        String text, 
        int lineNumber,
        String scriptName,
        String scriptInput,
        boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;     
        this.isRequired = isRequired;
    }
	
	public Object derive( VarContext context ) 
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
            if( derived == null && isRequired )
            {            	
            	throw new NullResult( scriptName, text, lineNumber );
            }
            return derived;
        }
        if( isRequired )
        {
        	throw new NullVar( scriptName, text, lineNumber );
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
    
    public boolean isRequired()
    {
        return isRequired;
    }

	public String getScriptInput() 
	{
		return scriptInput;
	}

	public void collectVarNames( Set<String> varNames, VarContext context ) 
	{
		VarScript script = context.resolveScript( scriptName, scriptInput );
    	if( script == null )
		{ 
			throw new VarBindException( 
				"Unable to resolve script named \"" + getScriptName() 
				+ "\" for Mark :"+ N + text + N + "on line [" + lineNumber + "]");
		}
        script.collectAllVarNames( varNames, scriptInput );
	}
}	