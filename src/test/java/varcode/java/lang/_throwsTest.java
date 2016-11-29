/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.lang;

import varcode.java.metalang._throws;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _throwsTest
    extends TestCase
{
    public void testBindIn()
    {
        _throws t = _throws.of( IOException.class );
        System.out.println( t );
        assertEquals(
            N + "    throws java.io.IOException", t.toString()  );
        t  = _throws.of( "{+someException+}" );
        System.out.println( t );
        assertEquals(
            N + "    throws {+someException+}", t.toString()  );
        
        t.bind( VarContext.of( "someException", IOException.class ) );
        System.out.println( t );
        assertEquals(
            N + "    throws java.io.IOException", t.toString()  );        
    }
    
    public void testThrowsNone()
    {
        _throws t = _throws.of();
        assertEquals( "", t.author( ) );
        assertEquals( "", t.bind( VarContext.of() ).author() );
        
        assertEquals(0, t.count());
         
        assertTrue( t.isEmpty() );
        t.replace("A", "B");        
        
    }
    
    public static final String N = "\r\n";
    
    public void testActualThrows()
    {
        _throws t = _throws.of( IOException.class );
        
        assertEquals( N 
            + "    throws java.io.IOException", t.author() );
        assertEquals( N 
            + "    throws java.io.IOException", t.bind( VarContext.of( )).author() );
        
        assertEquals( 1, t.count() );
        assertFalse( t.isEmpty() );
        
        t.replace( "java.io.IOException", "java.io.FileNotFoundException");
        
        assertEquals( 1, t.count() );
        assertFalse( t.isEmpty() );
        
        assertEquals( N 
            + "    throws java.io.FileNotFoundException", t.author() );
        assertEquals( N 
            + "    throws java.io.FileNotFoundException", t.bind( VarContext.of( )).author() );
    }
            
    public void testMultiThrows()
    {
        _throws t = _throws.of( IOException.class );
        t.addThrows( "{+throwThis*+}" );
        
        assertEquals( N 
            + "    throws java.io.IOException, {+throwThis*+}", t.author() );
        
        try
        {
            t.bind( VarContext.of() );
            fail( "expected exception for missing required" );
        }
        catch( VarException ve )
        {
            //expected
        }
        t = _throws.of( IOException.class );
        t.addThrows( "{+throwThis*+}" );
        
        String res = t.bind( 
            VarContext.of("throwThis", FileNotFoundException.class ) ).author( );
        
        assertEquals( N 
            + "    throws java.io.IOException, java.io.FileNotFoundException", 
            res );        
                
    }
    
    /**
     * Since by default we separate each using ',' 
     * we can pass in multiple arguments
     */
    public void testParamThrows()
    {
        _throws t =  _throws.of( "{+throwThese*+}" );
        
        String res = t.bind( 
            VarContext.of(
            "throwThese", 
                new Class[]{ FileNotFoundException.class, RuntimeException.class} ) ).author();
        
        assertEquals( N 
            + "    throws java.io.FileNotFoundException, java.lang.RuntimeException",
            res );        
    }

    public void testThrows()
	{
		_throws t = _throws.of(  );
		assertEquals( t.toString(), "" );
		
		t = _throws.of( IOException.class );
		
		t = _throws.of( "SecurityException", "RuntimeException" );
		assertEquals( t.toString(), 
			System.lineSeparator() + 
			"    throws SecurityException, RuntimeException" );
	}
}
