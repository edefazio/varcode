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
public class _switchTest
    extends TestCase
{
    public static final String N = "\r\n";
    
    public void testSimple()
    {
        _switch s = _switch.of( "a" );
        System.out.println( s );
        assertEquals( 
            "switch( a )" + N +
            "{" + N + N +
            "}", s.toString() );
        
        s.addCase( "1", "System.out.println(1);", false );
        System.out.println( s );
        assertEquals( 
            "switch( a )" + N +
            "{" + N +
            "    case:1" + N +
            "        System.out.println(1);" + N + N +       
            "}", s.toString() );
    }
    
    public void testBreak()
    {
        _switch s = _switch.of( "a" ).addCase("1", "System.out.println(1);", true );
        System.out.println( s );
        assertEquals( 
            "switch( a )" + N +
            "{" + N +
            "    case:1" + N +
            "        System.out.println(1);" + N +
            "    break;" + N +       
            "}", s.toString() );        
    }
    
    public void testDefault()
    {
        _switch s = _switch.of( "a" )
            .addCase("1", "System.out.println(1);", true )
            .defaultCase( "System.out.println(\"default\");" );
        
        System.out.println( s );
        assertEquals( 
            "switch( a )" + N +
            "{" + N +
            "    case:1" + N +
            "        System.out.println(1);" + N +
            "    break;" + N +     
            "    default:" + N +
            "        System.out.println(\"default\");" + N + N +        
            "}", s.toString() );        
    }
}
