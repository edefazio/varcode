package varcode.doc.lib;

import junit.framework.TestCase;
import varcode.context.VarContext;

public class RowifyTest
	extends TestCase
{
	public void test2Columns()
	{
		VarContext vc = VarContext.of( 
			"columns", new String[]{"letters", "numbers"},
			"letters", new String[]{"A", "B", "C", "D"},
			"numbers", new int[]{ 1, 2, 3, 4} );
		String[] rows = Rowify.doRowify( vc, "columns" );
		assertEquals("\"A\", 1",  rows[ 0 ] );
		assertEquals("\"B\", 2",  rows[ 1 ] );
		assertEquals("\"C\", 3",  rows[ 2 ] );
		assertEquals("\"D\", 4",  rows[ 3 ] );
	}
}
