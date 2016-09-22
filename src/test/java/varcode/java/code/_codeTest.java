/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import java.io.IOException;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _codeTest
    extends TestCase
{
    public void testStringOrCodeBind()
    {
        //adding Strings or code just appends code to the codeblock
        _code outer = _code.of(
            "int i=0;",
            "int j = 0;",
             _code.of(
            "System.out.println(\"inner 1\");",
            "System.out.println(\"inner 2\");" ) );
        
        System.out.println( outer );
        assertEquals(
            "int i=0;" + N +
            "int j = 0;" + N +
            "System.out.println(\"inner 1\");" + N +
            "System.out.println(\"inner 2\");", outer.toString().trim() );        
    }
    
    
    public void testCodeEmptyToAddHeadTail()
    {
        _code c = _code.of( );
        assertEquals( "", c.toString() );
        
        c = _code.of( null );
        assertEquals( "", c.toString() );
        
        c.addHeadCode("A");
        assertEquals( "A", c.toString() );
        
        c.addHeadCode("B");
        
        assertEquals( "B" + System.lineSeparator()
            + "A", c.toString() );
        
        c.addTailCode("C");
        
        assertEquals( "B" + System.lineSeparator()
            + "A" + System.lineSeparator()
            + "C", c.toString() );
        
    }   
    
    
    public void testBind()
    {
        _code c = _code.of( "int {+vari*+} = 100;" );
        
        String b = c.bind( VarContext.of("vari", "TheVariable") );
        assertEquals( "int TheVariable = 100;", b );
        
        _try t = _try.catchAndHandle(c, 
            IOException.class, 
            "System.out.println(\"{+message|failure+}\");" );
        
        b = t.bind(
            VarContext.of("vari", "TheVariable") );
        
        assertEquals( 
            "try" + N +
            "{" + N + 
            "    int TheVariable = 100;" + N +
            "}" + N +
            "catch( java.io.IOException e )" + N +        
            "{" + N +
            "    System.out.println(\"failure\");" +N +
            "}", b.trim() );
    }
    
    public static final String N = "\r\n";
    
    public void testHeadCode()
	{
		_code c = new _code( );
		assertEquals( "",  c.toString() );
		c.addHeadCode("System.out.println();");
		assertEquals( "System.out.println();",  c.toString() );
		c.addHeadCode("//This is the first line" ); //add this line BEFORE the other line
		
		assertEquals( "//This is the first line" + N + 
				      "System.out.println();",  c.toString() );
	
		_code toHead = new _code( );
		toHead.addHeadCode(
			"//This is the Head Block",
			"//It is a CodeBlock being added to the head" );
		
		c.addHeadCode( toHead ); //add a _codeBlock to the head of an existing _codeBlock 
		
		assertEquals( 
			"//This is the Head Block" + N +
			"//It is a CodeBlock being added to the head" + N +
			"//This is the first line" + N + 
			"System.out.println();",  c.toString() );		
	}
	
	public void testTailCode() 
	{
		_code c = new _code( );
		c.addTailCode( "System.out.println();");
		assertEquals( "System.out.println();",  c.toString() );
		c.addTailCode("//this is at the end");
		assertEquals( "System.out.println();" + N +
				"//this is at the end",  c.toString() );		
		
		_code toTail = new _code( );
		toTail.addTailCode(
			"//This is the Tail Block",
			"//It is a CodeBlock being added to the tail" );
		
		c.addTailCode( toTail );
		assertEquals( 
			"System.out.println();" + N +
			"//this is at the end" + N + 
			"//This is the Tail Block" + N +
			"//It is a CodeBlock being added to the tail",  c.toString() );					
	}
	
	
	public void testReplace() 
	{
		_code c = new _code( );
		c.addTailCode( "System.out.println(\"Hey\");");
		c.addTailCode("//this is at the end");
		
		_code toTail = new _code( );
		toTail.addTailCode(
			"//This is the Tail Block",
			"//It is a CodeBlock being added to the tail" );
		
		c.addTailCode( toTail );
		
		c.replace("System.out.println", "LOG.debug" );
		assertEquals( 
			"LOG.debug(\"Hey\");" + N +
			"//this is at the end" + N + 
			"//This is the Tail Block" + N +
			"//It is a CodeBlock being added to the tail",  c.toString() );					
	}
	
}
