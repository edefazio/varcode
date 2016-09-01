package varcode.java.model;

import junit.framework.TestCase;

public class _extendsTest
	extends TestCase
{
	public void testSimple()
	{
		_extends exs = new _extends();
		assertEquals( "", exs.toString() );
		
		exs = _extends.of( "Serializable" );
		assertEquals( 
			System.lineSeparator() + "    extends Serializable", 
			exs.toString() );
		
		exs = _extends.of( "Serializable", "Externalizable" );
		
		assertEquals( 
			System.lineSeparator() + "    extends Serializable, Externalizable", 
			exs.toString() );
		
	}
}
