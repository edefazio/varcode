package varcode.java.model;

import junit.framework.TestCase;

public class _packageTest 
	extends TestCase
{
	public void testPackage()
	{
		_package pack = _package.of( null );
		assertEquals( "", pack.toString() ); 
		
		pack = _package.of("a");
		assertEquals("package a;"+System.lineSeparator() + System.lineSeparator(), 
			pack.toString());
		
	}

	
	public static void main( String[] args )
	{
		System.out.println( new _package( null ) );
		System.out.println( new _package( "io.varcode" ) );
		//System.out.println( new _package( "io.varcode.2" ) ); throws exception, good
	}
}
