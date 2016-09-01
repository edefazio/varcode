package varcode.java.model;

import junit.framework.TestCase;

public class _parametersTest
	extends TestCase
{
	public void testParameters()
	{
		_parameters ps = new _parameters();
		assertEquals("(  )", ps.toString() );
		
		ps = _parameters.of( new String[]{"int", "x"} );
		assertEquals("( int x )", ps.toString() );
		
		ps = _parameters.of( new String[]{"int", "x", "String", "y"} );
		assertEquals("( int x, String y )", ps.toString() );
		
		ps = _parameters.of( "int x, String y" );
		assertEquals("( int x, String y )", ps.toString() );
		
		ps = _parameters.of( "int x, HashMap<String,Integer> y" );
		assertEquals("( int x, HashMap<String,Integer> y )", ps.toString() );				
	}
	
	//this doesnt work, I need to fix the parser
	//TODO FIX THIS
	/*
	public void testBreakage()
	{
		_parameters ps = _parameters.of( "int x, HashMap<String, Integer> y" );
		assertEquals("( int x, HashMap<String,Integer> y )", ps.toString() );
		
	}
	*/
}
