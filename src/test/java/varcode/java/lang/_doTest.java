/*
 * Copyright 2016 M. Eric DeFazio
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
package varcode.java.lang;

import varcode.java.lang._code;
import varcode.java.lang.cs._do;
import junit.framework.TestCase;

/**
 *
 * @author eric
 */
public class _doTest
    extends TestCase
{
    static final String N = "\r\n";
    
    public void testDo()
    {
        //this is the code we will be authoring:
        int i = 0;
        do
        {
            i++;
        }
        while( i < 10 );
        
        _do d = _do.whileIs( "i < 10", "i++;" );
        
        assertEquals( "i++;", d.body.toString() );
        assertEquals( "i < 10", d.condition.toString() );
        assertEquals( 
            "do" + N +
            "{" + N +
            "    i++;" + N +
            "}" + N + 
            "while( i < 10 );", d.toString().trim() );
        
        //add the int i=0; initialization statement before the loop:
        _code c = _code.of( "int i = 0;",
            d );
        
        assertEquals( 
            "int i = 0;" + N +    
            "do" + N +
            "{" + N +
            "    i++;" + N +
            "}" + N + 
            "while( i < 10 );", c.toString().trim() );        
    }
}
