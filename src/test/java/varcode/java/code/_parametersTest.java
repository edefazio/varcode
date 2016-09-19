/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import java.util.List;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.code._parameters._parameter;

/**
 *
 * @author eric
 */
public class _parametersTest
    extends TestCase
{
    
    public void testFinalAndAnnotatedParam()
    {
        _parameters p = _parameters.of( "final String x" );
        assertEquals( "( final String x )", p.toString() );
        assertEquals( 1, p.count() );
        assertFalse( p.isEmpty() );
        
        p = _parameters.of( "@PathParam(\"id\") String x" );
        assertEquals( "( @PathParam(\"id\") String x )", p.toString() );
        assertEquals( 1, p.count() );
        assertFalse( p.isEmpty() );
        
        p = _parameters.of( "@PathParam(\"id\") final String x" );
        assertEquals( "( @PathParam(\"id\") final String x )", p.toString() );
        assertEquals( 1, p.count() );
        assertFalse( p.isEmpty() );
    }
    
    public void testNoParams()
    {
        _parameters p = _parameters.of();
        assertEquals( 0, p.count() );
        assertTrue( p.isEmpty() );
        
        try
        {
            p.get( 1 );
            fail( "Expected Exception index out of range" );
        }
        catch( VarException ve )
        {
            //expected 
        }
        
        assertEquals( 0, p.getParameters().size() );
        p.replace("A", "Z");        
        
        assertEquals("(  )", p.author( ) );
        assertEquals("(  )", p.bind( VarContext.of() ) );
    }
    
    public void testOneParam()
    {
        _parameters p = varcode.java.code._parameters.of(  "int a" );
        assertEquals( "int a", p.get(0).toString() );
        assertEquals( "int", p.get(0).getType() );
        assertEquals( "a", p.get(0).getName() );
        
        assertFalse( p.isEmpty() );
        assertEquals( 1, p.count() );
        
        List<_parameter>ps =  p.getParameters();
        assertEquals( 1, ps.size() );
        assertEquals( "( int a )", p.author( ) );
        assertEquals( "( int a )", p.bind( VarContext.of() ) );
        
        p.replace( "a", "b" );
        
        assertEquals( "int b", p.get(0).toString() );
        assertEquals( "int", p.get(0).getType() );
        assertEquals( "b", p.get(0).getName() );
        
        assertEquals( "( int b )", p.author( ) );
        assertEquals( "( int b )", p.bind( VarContext.of() ) );                
    }
    
    public void testParamParam()
    {
        _parameters p = _parameters.of( "{+pType+} a" );
        
         assertEquals( "{+pType+} a", p.get(0).toString() );
        assertEquals( "{+pType+}", p.get(0).getType() );
        assertEquals( "a", p.get(0).getName() );
        
        assertFalse( p.isEmpty() );
        assertEquals( 1, p.count() );
        
        assertEquals( "( {+pType+} a )", p.author( ) );
        assertEquals( "(  a )", p.bind( VarContext.of() ) );
        assertEquals( "( int a )", p.bind( VarContext.of("pType", int.class ) ) );
        
        p = _parameters.of( "int {+name*+}" );
        
         assertEquals( "int {+name*+}", p.get(0).toString() );
        assertEquals( "int", p.get(0).getType() );
        assertEquals( "{+name*+}", p.get(0).getName() );
        
        assertFalse( p.isEmpty() );
        assertEquals( 1, p.count() );
        
        assertEquals( "( int {+name*+} )", p.author( ) );
        
        try
        { 
            assertEquals( "( int  )", p.bind( VarContext.of() ) );
            fail("expected Exception for missing Required");
        }
        catch(VarException ve)
        {
            //expected
        }
        assertEquals( "( int a )", p.bind( VarContext.of( "name", "a" ) ) );        
    }
    
    public void testParameters()
	{
		_parameters ps = new _parameters();
		assertEquals("(  )", ps.toString() );
		
		ps = _parameters.of( new String[]{"int", "x"} );
		assertEquals("( int x )", ps.toString() );
		
		ps = _parameters.of( new String[]{"int", "x", "String", "y"} );
        System.out.println( ps );
		assertEquals("( int x, String y )", ps.toString() );
		
		ps = _parameters.of( "int x, String y" );
		assertEquals("( int x, String y )", ps.toString() );
		
		ps = _parameters.of( "int x, HashMap<String,Integer> y" );
		assertEquals("( int x, HashMap<String,Integer> y )", ps.toString() );				
	}
	
	//this doesnt work, I need to fix the parser
	//TODO FIX THIS
	
	public void testBreakage()
	{
		_parameters ps = _parameters.of( "int x, HashMap<String, Integer> y" );
		assertEquals("( int x, HashMap<String,Integer> y )", ps.toString() );		
	}
	
}
