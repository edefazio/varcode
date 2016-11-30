/*
 * Copyright 2016 Eric.
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

import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 * If a Var is either (not null or equal to a target value)
 * then print the result of calling a $script with some parameter(s)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AddScriptResultIfVarTest 
    extends TestCase
{
    public void testMarkNoEquality()
    {
        String text = "{+?a:$>(a)+}"; 
        int lineNumber = 100;
        String varName ="a";
        String targetValue = null;
        String scriptName = ">";
        String scriptInput = "a";
                
        AddScriptResultIfVar asr = 
            new AddScriptResultIfVar(
                text, 
                lineNumber,
                varName,
                targetValue,
                scriptName,
                scriptInput );
        
        //a is not present, so return null
        assertEquals( null, asr.derive( VarContext.of( ) ) );
        
        //a is present so call > (indent( a ) and print
        assertEquals( "    1", asr.derive( VarContext.of( "a", 1 ) ) );
    }
    
    public void testMarkEquals()
    {
        String text = "{+?a:$>(a)+}"; 
        int lineNumber = 100;
        String varName ="a";
        String targetValue = "1";
        String scriptName = ">";
        String scriptInput = "a";
        
        AddScriptResultIfVar asr = 
            new AddScriptResultIfVar(
                text, 
                lineNumber,
                varName,
                targetValue,
                scriptName,
                scriptInput );
        
        //a is not present, so return null
        assertEquals( null, asr.derive( VarContext.of( ) ) );
        
        //a is == 1; so call > (indent( a ) and print
        assertEquals( "    1", asr.derive( VarContext.of( "a", 1 ) ) );
        
        //a is NOT == 1
        assertEquals( null, asr.derive( VarContext.of( "a", 2 ) ) );
        
    }
    
    
}
