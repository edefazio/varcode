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

import varcode.java.model.cs._forEach;
import varcode.java.lang._code;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _forEachTest
    extends TestCase
{
    
    public static final String N = "\r\n";
    
    public void testSimple()
    {
        //no body
        _forEach f = _forEach.of( int.class, "x", "array" );
        assertEquals(
        "for( int x : array )" + N +
        "{" + N + 
        "}", f.toString() );
                
        f = _forEach.of( int.class, "x", "array" ).body( "System.out.println(x);" );
        assertEquals(
        "for( int x : array )" + N +
        "{" + N + 
        "    System.out.println(x);" + N +        
        "}", f.toString() );                        
    }
    
    public void testNest()
    {
        _forEach f = _forEach.of( int.class, "x", "xvalues" )
            .body( _forEach.of( int.class, "y", "yvalues" )
                .body( "System.out.println( x + \" \" + y );" )
            );
        assertEquals(
            "for( int x : xvalues )" + N + 
            "{" + N + 
            "    for( int y : yvalues )" + N + 
            "    {" + N + 
            "        System.out.println( x + \" \" + y );" + N +          
            "    }" + N + 
            "}", f.toString()         
        );
    }
            
    
    public void testBindIn()
    {
        _forEach f = _forEach.of( "{+type*+}", "x", "array" );
        assertEquals(
        "for( {+type*+} x : array )" + N +
        "{" + N + 
        "}", f.toString() );
        
        //bindIn the type as a List of String
        f.bind( VarContext.of( "type", "List<String>" ) );
        
        assertEquals(
        "for( List<String> x : array )" + N +
        "{" + N + 
        "}", f.toString() );        
        
        f = _forEach.of( "{+type*+}", "x", "array" );
        assertEquals(
        "for( {+type*+} x : array )" + N +
        "{" + N + 
        "}", f.toString() );
        
        //bindIn the type as a List of String
        f.bind( VarContext.of( "type", "List<String>" ) );
        
        assertEquals(
        "for( List<String> x : array )" + N +
        "{" + N + 
        "}", f.toString() );   
        
        f = _forEach.of( int.class, "x", "arr" )
            .body("{+log+}", "System.out.println(x);" );
        
        assertEquals(
        "for( int x : arr )" + N +
        "{" + N +
        "    {+log+}" + N + 
        "    System.out.println(x);" + N +        
        "}", f.toString() );   
        
        f.bind( VarContext.of( "log", "LOG.debug(\"Got Here!\");" ) );
        
        assertEquals(
        "for( int x : arr )" + N +
        "{" + N +
        "    LOG.debug(\"Got Here!\");" + N + 
        "    System.out.println(x);" + N +        
        "}", f.toString() );   
    }
     
    /**
     * Verify that I can bind-in multiple lines of code
     * and the code will be formatted correctly
     */
    public void testBindInCode()
    {
        _forEach f = _forEach.of( int.class, "x", "arr" )
            .body("{+log+}", "System.out.println(x);" );
        
        f.bind( VarContext.of( "log", 
            _code.of(
                "LOG.debug(\"Got Here!\");", 
                "LOG.debug(\"Second Line\");" ) ) );
        
        
        assertEquals(
        "for( int x : arr )" + N +
        "{" + N +
        "    LOG.debug(\"Got Here!\");" + N + 
        "    LOG.debug(\"Second Line\");" + N +         
        "    System.out.println(x);" + N +        
        "}", f.toString() ); 
    }    
}
