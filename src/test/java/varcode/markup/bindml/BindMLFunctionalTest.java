package varcode.markup.bindml;

import junit.framework.TestCase;

public abstract class BindMLFunctionalTest 
	extends TestCase
{
	/** tired of assertEquals everywhere*/
	protected static void is( Object o1, Object o2 )
	{
		assertEquals( o1, o2 );
	}

}
