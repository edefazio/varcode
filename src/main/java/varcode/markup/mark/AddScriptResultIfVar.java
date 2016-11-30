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
import static varcode.markup.mark.Mark.N;

/**
 * If a var is either (not null or equal to a target value)
 * then print the result of calling a $script with some parameter(s)
 * 
 */ 
//CodeML : 
// "/*{+?env:$capture(inputVO)+}*/" if (env != null) { print capture(inputVO)}
// "/*{+?env=dev:$capture(params)+}*/" if (env ==dev ) { print capture(params)}

//BindML :
// {+?env:$>(env)+}
// {+?env==test:$>(env)+}
//     Script     : capture
//     params     : inputVO
public class AddScriptResultIfVar
    extends Mark
    implements BlankFiller, HasScript 
{	
    private final String varName;
    
    private final String targetValue;
	
    private final String scriptName;
	
    private final String scriptInput;
	
    public AddScriptResultIfVar(
        String text, 
        int lineNumber,
        String varName,
        String targetValue,
        String scriptName,
        String scriptInput )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.targetValue = targetValue;
        this.scriptName = scriptName;
        this.scriptInput = scriptInput;             
    }
	
    @Override
    public Object derive( VarContext context ) 
    {	
	Object resolved = context.resolveVar( varName );
        if( resolved == null )
        {        	
            return null;
        }
        if( targetValue == null )
        {
            return runScript( context );                    	
        }
        if ( resolved.toString().equals( targetValue ) )
        {
            return runScript( context );            
        }
        return null;
    }

    private Object runScript( VarContext context ) 
    {	
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
                    "Error evaluating AddScriptResultIfVar Mark \"" + N + text + N 
                  + "\" on line [" + lineNumber + "] as :" + N 
                  + this.toString() + N, e );
            }
            return derived;
        }        
        //the script wasnt required
        return "";
    }
    
    @Override
    public void fill( VarContext context, TranslateBuffer buffer )
    {
	buffer.append( derive( context ) ); 
    }

    @Override
    public String getScriptName()
    {
        return scriptName;
    }

    public String getVarName()
    {
        return this.varName;
    }
    
    public String getTargetValue()
    {
        return this.targetValue;
    }
    
    @Override
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
                + "\" for Mark :" + N + text + N + "on line [" + lineNumber + "]");
	}
        script.collectAllVarNames( varNames, scriptInput );
    }
}	