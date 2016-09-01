package varcode.java.model;

import junit.framework.TestCase;

public class _typeTest
	extends TestCase
{
	public void testType()
	{
		_type t = _type.of( String.class );
		assertEquals( "String", t.toString() );
		
		t = _type.of( "SomeClass" );
		assertEquals( "SomeClass", t.toString() );
		
		
		t = _type.of( "SomeGeneric<Key,Value<Integer,String>>" );
		assertEquals( "SomeGeneric<Key,Value<Integer,String>>", t.toString() );
	}

}
