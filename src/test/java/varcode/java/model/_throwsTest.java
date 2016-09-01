package varcode.java.model;

import java.io.IOException;

import junit.framework.TestCase;

public class _throwsTest
	extends TestCase
{

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
	
	public static void main(String[] args)
	{
		_throws none = _throws.of( );
		System.out.println( none );
		_throws t = _throws.of( "SecurityException", "RuntimeException" );
		System.out.println( t );
	}
}
