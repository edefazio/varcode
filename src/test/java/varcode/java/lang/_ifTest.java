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
package varcode.java.lang;

import varcode.java.lang._code;
import varcode.java.lang.cs._if;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _ifTest
    extends TestCase
{
    public static String N = "\r\n";
    
    
    public void testIfElseIf()
    {
        _if i = 
            _if.is("a == 0","System.out.println(\"0\");" )
           .elseIf( "a == 1", "System.out.println(\"1\");" );
        System.out.println( i );
        
        assertEquals( 
        "if( a == 0 )" + N +
        "{" + N +
        "    System.out.println(\"0\");" + N +
        "}" + N + 
        "else if( a == 1 )" + N +
        "{" + N +
        "    System.out.println(\"1\");" + N +
        "}", i.toString().trim( ) );
    }
    
    public void testIfElseIfElseIf()
    {
        _if i = 
            _if.is( "a == 0","System.out.println(\"0\");" )
           .elseIf( "a == 1", "System.out.println(\"1\");" )
           .elseIf( "a == 2", "System.out.println(\"2\");"); 
        System.out.println( i );
        
        assertEquals( 
        "if( a == 0 )" + N +
        "{" + N +
        "    System.out.println(\"0\");" + N +
        "}" + N + 
        "else if( a == 1 )" + N +
        "{" + N +
        "    System.out.println(\"1\");" + N +
        "}" + N +  
        "else if( a == 2 )" + N +
        "{" + N +
        "    System.out.println(\"2\");" + N +
        "}",        
        i.toString().trim( ) );
    }
        
    public void testIf_ElseIf_ElseIf_Else()
    {
        _if i = 
            _if.is( "a == 0","System.out.println(\"0\");" )
           .elseIf( "a == 1", "System.out.println(\"1\");" )
           .elseIf( "a == 2", "System.out.println(\"2\");")
           ._else( "System.out.println(\"not 0, 1, 2\");" );
                
        System.out.println( i );
        
        assertEquals( 
        "if( a == 0 )" + N +
        "{" + N +
        "    System.out.println(\"0\");" + N +
        "}" + N + 
        "else if( a == 1 )" + N +
        "{" + N +
        "    System.out.println(\"1\");" + N +
        "}" + N +  
        "else if( a == 2 )" + N +
        "{" + N +
        "    System.out.println(\"2\");" + N +
        "}" + N +
        "else" + N +
        "{" + N +
        "    System.out.println(\"not 0, 1, 2\");" + N +
        "}",        
        i.toString().trim( ) );
    }
    public void testIfElse()
    {
        _if i = 
            _if.is( "a == 0", 
            "System.out.println(\"A is zero\");" )
            ._else( "System.out.println(\"A is NOT zero\");" );
        assertEquals(
        "if( a == 0 )" + N +
        "{" + N + 
        "    System.out.println(\"A is zero\");" + N +
        "}" + N +
        "else" + N +
        "{" + N +
        "    System.out.println(\"A is NOT zero\");" + N +
        "}", i.toString().trim() );
    }
    
    
    public void testIf()
    {
        _if i = _if.is("i > 0", "{+doThis+}","{+doThat+}", 
            _if.is("x < 100", "{+$>(innerDo)+}", "{+$>(innerDoThat)+}" ) );
        
        assertEquals(
            "if( i > 0 )" + N +
            "{" + N +
            "    {+doThis+}" + N +
            "    {+doThat+}" + N +
            "    if( x < 100 )" + N +
            "    {" + N +
            "        {+$>(innerDo)+}" + N +
            "        {+$>(innerDoThat)+}" + N +
            "    }" + N +
            "}", i.toString().trim() );
        
        System.out.println( i );
        
        String bind = i.bind(VarContext.of(
            "doThis", "A", 
            "doThat", "B", 
            "innerDo","AA", 
            "innerDoThat", "BB") ).toString();
        
        //System.out.println(bind );
                       
        assertEquals(
            "if( i > 0 )" + N +
            "{" + N +
            "    A" + N +
            "    B" + N +
            "    if( x < 100 )" + N +
            "    {" + N +
            "            AA" + N +  //indented 4 spaces
            "            BB" + N +  //indented 4 spaces
            "    }" + N +
            "}", bind.trim() );
    }
    
    public void testIfMultiLineNestedIndent()
    {
        _if i = 
            _if.is("i > 0", 
                "{+doThis+}",
                "{+doThat+}", 
               _if.is("x < 100", 
                    "{+$>(innerDo)+}", 
                    "{+$>(innerDoThat)+}" ) );
        
        //this illustrates the priblem
        String bind = i.bind( VarContext.of(
            "doThis", "A", 
            "doThat", "B", 
            "innerDo", "A1\r\n A2\r\n A3\r\n", 
            "innerDoThat", _code.of("B1", "B2", "B3") ) ).author();
        
        /*
        //this illustrates the priblem
        String bind = i.bind( VarContext.of(
            "doThis", "A", 
            "doThat", "B", 
            "innerDo",_code.of("A1", "A2", "A3"), 
            "innerDoThat", _code.of("B1", "B2", "B3")) );
        */ 
        System.out.println( bind );
        
    }
    
}
