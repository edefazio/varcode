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
package varcode.java.langmodel;

import varcode.java.langmodel._class;
import varcode.java.langmodel.cs._forCount;
import varcode.java.langmodel.cs._for;
import java.util.Random;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.langmodel._methods._method;

/**
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _forTest
    extends TestCase
{
    public static final String N = "\r\n";
        
    /**
     * This is how JavaPoet does this:
     * 
     * MethodSpec main = MethodSpec.methodBuilder("main")
       .addStatement("int total = 0")
       .beginControlFlow("for (int i = 0; i < 10; i++)")
       .addStatement("total += i")
       .endControlFlow()
       .build();
     */
    public void testCountMethodIt()
    {
        //todo should I add ; if they arent there??
        _method m = _method.of( "public void main" )
            .body( 
                "int total = 0;",
                _for.count( "i", 10 )
                    .body( "total += i;" ) );
        
        //verify this method compiles
        //System.out.println( m );
        _class.of( "Shell" ).method( m ).instance( );                        
    }
    /**
     * private MethodSpec computeRange(String name, int from, int to, String op) {
     *    return MethodSpec.methodBuilder(name)
              .returns(int.class)
              .addStatement("int result = 0")
              .beginControlFlow("for (int i = " + from + "; i < " + to + "; i++)")
              .addStatement("result = result " + op + " i")
              .endControlFlow()
              .addStatement("return result")
              .build();
      } 
     */     
    public static _method computeRange( String name, int from, int to, String op )
    {
        return _method.of( "public int " + name + "()")
            .body( "int result = 0;",
                _for.count( "i", from, (to + 1) )
                    .body( "result = result " + op + " i;"),
                "return result;" );
    }
    
    /**
     * Gauss Sum (quick method for summing numbers from 1...n)
     * n * (n + 1) / 2
     * 
     * @param max
     * @return 
     */
    private static final int sumOf1To( int max )
    {
        return ( max * ( max + 1 ) ) / 2; 
    }
    
    public void testSingle()
    {
        assertEquals( 3, sumOf1To( 2 ) );
        assertEquals( 6, sumOf1To( 3 ) );
        
        //create a range method to sum from 1 to 100
        _method m = computeRange( "sum", 1, 100, "+" );
        
        System.out.println( m );
        
        _class testClass = _class.of("A").method( m );
        //System.out.println( testClass );
        Object instance = testClass.instance( );
        
        assertEquals( 5050, Java.invoke( instance, "sum" ) );
        
        _method mstar = computeRange("multiply10to20", 10, 20, "*" );
        _class c = _class.of("B").method( m );
        
        instance = c.instance( );
        
        
    }
    //now TEST that the code compiles AND returns the result you expect
    // for MANY random cases
    public void testSumFromToMethod()
    {                
        Random r = new Random();
        
        //lets create and reuse the same classLoader
        // clearing it out after each iteration
        AdHocClassLoader reuseClassLoader = new AdHocClassLoader();
        for( int i = 0; i < 2; i ++ )
        {
            //unload the class from the classLoader, 
            //so I can reuse the classloader
            reuseClassLoader.unloadAll();
            
            //randomly choose a max number from 2 to 1000
            int max = r.nextInt( 999 ) + 2;
            
            //create the range method
            _method m = computeRange( "sum", 1, max, "+" );       
            
            System.out.println( m );
            
            
            //now author, compile, and load the class
            Object o = _class.of( "A" + i ).method( m ).instance( reuseClassLoader );
            
            //Now verify that calling the method produces the correct result
            assertEquals( Java.invoke( o, "sum" ), sumOf1To( max ) );
        }           
    }
    
    
    public void testSimpleCount()
    {
        assertEquals(
            "for( int i = 0; i < 5; i++ )" + N +
            "{" + N +
            "}", _for.count( 5 ).author( ) );
    }
    
    /**
     * Body Count, Body Count, 
     * Body Count, Body Body Body Count
     * Body Counts' in the House! (Sorry couldn't resist)
     */
    public void testCountBody()
    {
        
        assertEquals(
            "for( int i = 0; i < 5; i++ )" + N +
            "{" + N +
            "    LOG.debug(i);" + N +
            "}", _for.count( 5 ).body("LOG.debug(i);").author( ) );
        
    }
    
   
    
    public static void main( String[] args )
    {

        _forCount fc = _for.count( 5 ).body("LOG.debug(i);");
        System.out.println ("BODY" + N +  fc.getBody() + "END");
        
        System.out.println( fc.author( ) );
        
        System.out.println( 
            _for.count( 5 ) );
        
        
        System.out.println( _for.count( 5 )
            .body("System.out.println(\"Hey\");" ) );
            //.head( "LOG.debug(i);" ) );
            
        
        System.out.println( _for.countDown( 5 ) );
        
        //nested for 
        System.out.println(_for.count( "x", 100 )
                .body(
                    _for.count( "y", 100 ) ) );
        
        System.out.println(_for.count( 5 )
            .body( "System.out.println(\"Hey\");" ) );            
    }
    
}
