package varcode.java.model;

import junit.framework.TestCase;

public class _staticBlockTest
	extends TestCase
{ 
	public static String N = System.lineSeparator();
	
	public void testSimple()
	{
		_staticBlock sb = new _staticBlock( (Object[])null ); //"int j = 100;" );
		assertEquals( "", sb.toString() );
				
		
		sb = new _staticBlock( "int j = 100;" );
		assertEquals( 
			"static" + N + 
			"{" + N + 
			"    int j = 100;" + N + 
			"}", 
			sb.toString() );
		
		sb = new _staticBlock( );
		assertEquals( "",  sb.toString() );
		
		sb = new _staticBlock( 
			"int a = 100;", 
			"int b = 200;", 
			"int c = a + b;" );
		
		assertEquals( 
			"static" + N + 
			"{" + N + 
			"    int a = 100;" + N +
			"    int b = 200;" + N +
			"    int c = a + b;" + N +
			"}", 
			sb.toString() );		
	}
	
	
}
