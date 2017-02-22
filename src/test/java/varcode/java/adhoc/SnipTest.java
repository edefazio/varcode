/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.adhoc;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class SnipTest
    extends TestCase
{
    
    public void testSimpleEval()
    {
        assertEquals( 14, Snip.eval("5 + 9;") );
        assertEquals( 14, Snip.eval("5 + 9;") );
        assertEquals( 14, Snip.eval("return 5 + 9;") );
        assertEquals( 14, Snip.eval("return 5 + 9;") );
        
    }
    
    public void testEvalWithInput()
    {
        assertEquals( 2, Snip.eval( "(Integer i){ return i + 1; }", 1 ) );
    }
    
    public void testSnip()
    {
        Snip s = Snip.of( "System.out.println( \"noinoutsnippet\" );");
        assertEquals( null, s.call() );        
    }
    
    public void testSnipIn()
    {
        //a Snip that accepts input
        Snip inSnippet = Snip.of( "(Object o){ System.out.println( o );}" ); 
        assertEquals( null, inSnippet.call( UUID.randomUUID() ) );
    }
    
    public void testSnippetOut()
    {        
        Snip outSnippet = Snip.of( UUID.class, "return UUID.randomUUID();" );
        assertTrue( outSnippet.call( ) instanceof UUID ); //verify we return a UUID
    }
    
    public void testVarArgs()
    {
        Snip varargs = Snip.of( 
            _method.of(
                "public int sum( int...vars)",
                "int total = 0;",
                "for(int i = 0; i< vars.length; i++)",
                "{",
                "    total+= vars[i];",
                "}",
                "return total;")                
        );
        assertEquals( 6, varargs.call(1,2,3) );
    }
    
    public void testInOutSnippet()
    {
        Snip inOutSnippet = Snip.of(  String.class , 
            "(String prefix){ return prefix + java.util.UUID.randomUUID().toString();}" );
        
        assertTrue( ((String)inOutSnippet.call( "prefix" )).startsWith( "prefix" ) );
    }
    
    public void testMethodSnippet()
    {
        Snip metSnip = Snip.of( 
            _method.of( "int multiply(int a, int b)",
                "return a * b;" ) );
        assertEquals(10, metSnip.call( 2, 5 ) );
    }
    
    public void testMethodSnippetImports()
    {
        Snip metImportSnip = Snip.of( 
            _imports.of( UUID.class), 
            _method.of( "String prefixUUID( String prefix)",
                "return prefix + UUID.randomUUID().toString();" )
            );
        String s = (String) metImportSnip.call( "pre" );
        assertTrue( s.startsWith( "pre" ) );
    }
        
    
    
        
     
    //snippet is the most simple thing takes a String and makes code     
    // |returnType| |name| |( paramType paramName...)| { }    
    public static void main( String[] args )
    {
        Snip add = Snip.of( "int(int a,int b){return a + b;}" );
            //                     ---|-----------| |-----------|
            //             return Type  parameters    body            
            
            //NOTE: the return Type will ALWYAS be returned as an Object or null
        
        Snip onlyBody = Snip.of( "System.out.println( new java.util.Date() );" );                
        Snip returns = Snip.of( "String{return java.util.UUID.randomUUID().toString();}" );
        Integer sum = (Integer)add.call( 1 , 2 );
        assertEquals( sum, new Integer(5) );
        
        //Snippet varargs = Snip.of( 
        //    "(int...vars){for(int i=0; i< vars.length; i++){ System.out.println(vars[i]); } }" );
        
        Snip varargs = Snip.of( 
            "int(int...vars){int total = 0; for(int i=0; i< vars.length; i++){ total+=vars[i]; }return total; }" );
        assertEquals(6, varargs.call( 1,2,3 ) );
        
    }
    
}
