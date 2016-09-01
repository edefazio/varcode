package varcode.java.model;

import junit.framework.TestCase;

public class _literalTest 
	extends TestCase
{
	
	public void testLit()
	{
		_literal lit = new _literal( "i" );
		assertEquals( "\"i\"", lit.toString() );
		
		lit = new _literal( 0 );
		assertEquals( "0", lit.toString() );
		
		lit = new _literal( 10L );
		assertEquals( "10L", lit.toString() );
		
		lit = _literal.of( null );
		assertEquals( "null", lit.toString() );
		
		lit = _literal.of( (short)2 );
		assertEquals( "(short)2", lit.toString() );
		
		lit = _literal.of( (byte)2 );
		assertEquals( "(byte)2", lit.toString() );
		
		lit = _literal.of( '2' );
		assertEquals( "'2'", lit.toString() );
		
		lit = _literal.of( 2.3f );
		assertEquals( "2.3F", lit.toString() );
		
		lit = _literal.of( 2.3d );
		assertEquals( "2.3d", lit.toString() );
		
		lit = _literal.of( 23L );
		assertEquals( "23L", lit.toString() );
		
		lit = _literal.of( true );
		assertEquals( "true", lit.toString() );
		
	}

}
