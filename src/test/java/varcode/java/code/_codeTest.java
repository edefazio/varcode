/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import java.io.IOException;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _codeTest
    extends TestCase
{
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
        
        c.catchHandleException(
            IOException.class, 
            "System.out.println(\"{+message|failure+}\");" );
        
        b = c.bind(
            VarContext.of("vari", "TheVariable") );
        
        assertEquals( 
            "try" + N +
            "{" + N + 
            "    int TheVariable = 100;" + N +
            "}" + N +
            "catch( java.io.IOException e )" + N +        
            "{" + N +
            "    System.out.println(\"failure\");" +N +
            "}", b );
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
	
	public void testTryCatch()
	{
		_code c = new _code( );
		c.catchHandleException(
			IOException.class, 
			"System.out.println(\"Handling dat IO Exception\");" );
		
		assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N 
            , c.toString() );
		
		c.catchHandleException(
            "SimpleException", 
            "LOG.error(\"Got Simple Exception\");");		
        
        assertEquals( 
			"try" + N + 
		    "{" + N +
		    N+
		    "}" + N +
            "catch( java.io.IOException e )" + N +
            "{" + N +
            "    System.out.println(\"Handling dat IO Exception\");" + N +
            "}" + N +
            "catch( SimpleException e )" + N +
            "{" + N +
            "    LOG.error(\"Got Simple Exception\");" + N +
            "}" + N     
            , c.toString() );
	}
	
	
	public void testTryWithResources()
	{
		_code c = new _code();
		c.tryWith(
            "BufferedReader br = new BufferedReader( new FileReader( path ) ) )");
		
		System.out.println( c );
		assertEquals( 
			"try( BufferedReader br = new BufferedReader( new FileReader( path ) ) ) )" + N +
		    "{" + N +
            N + 
		    "}" + N , c.toString() );				
	}
	
	public void testFinally()
	{
		_code c = new _code();
		c.addTailCode("file.read();");
		c.finallyBlock( 
			"//do this at the end regardless" , 
			"System.out.println(\"DONE\");");
		System.out.println( c );
		assertEquals(
			"try" + N +
			"{" + N + 
            "    file.read();" + N +
            "}" + N + 
            "finally" + N +
            "{" + N +
            "    //do this at the end regardless" + N +
            "    System.out.println(\"DONE\");" + N +
            "}" + N,
            c.toString() );		
	}
}
