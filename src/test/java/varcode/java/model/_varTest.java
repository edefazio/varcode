package varcode.java.model;

import junit.framework.TestCase;

public class _varTest
	extends TestCase
{

	public void testSimpleVar()
	{
		assertEquals( "String X", new _var( "String", "X" ).toString() );
		assertEquals( "int X", new _var( int.class, "X" ).toString() );		
		assertEquals( "SomeClass X", new _var( "SomeClass", "X" ).toString() );
		
		assertEquals( "SomeGenericClass<A, B, C> X", new _var( "SomeGenericClass<A, B, C>", "X" ).toString() );
	}
}
