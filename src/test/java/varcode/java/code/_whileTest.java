/*
 * Copyright 2016 eric.
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
package varcode.java.code;

import junit.framework.TestCase;

/**
 *
 * @author eric
 */
public class _whileTest
    extends TestCase
{
    static final String N = "\r\n";
    
    public void testSimple()
    {
        _while w = _while.is( "i < 100", 
            "System.out.println( i );",  
            "i++;" );
        
        //System.out.println( w );
        
        assertEquals(
            "while( i < 100 )" + N +
            "{" + N +        
            "    System.out.println( i );" + N +
            "    i++;" + N +         
            "}", w.toString() );
        
        assertEquals( "i < 100", w.condition.toString() );
        
        assertEquals( "System.out.println( i );" + N +
                      "i++;", w.body.toString() );        
    }
}
