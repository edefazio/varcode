package varcode.java.model;

import junit.framework.TestCase;

public class _javadocTest
	extends TestCase	
{
	public static final String N = System.lineSeparator();
	
	public void testBasic()
	{
		_javadoc jc = new _javadoc( );
		assertEquals( "", jc.toString() );
		
		jc = new _javadoc( "HEY LEEROY" );
		assertEquals( 
			"/**" + N +
			" * HEY LEEROY" + N +
			" */" + N, jc.toString() );
		
		jc = new _javadoc( "HEY LEEROY" + N + "THIS IS IT" );
		assertEquals( 
			"/**" + N +
			" * HEY LEEROY" + N +
			" * THIS IS IT" + N +
			" */" + N, jc.toString() );
		
	}
}
