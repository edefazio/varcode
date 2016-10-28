package varcode.doc;

import junit.framework.TestCase;
import varcode.markup.bindml.BindML;

public class AuthorTest
	extends TestCase
{
	public void testFillCode()
	{
		Dom d = BindML.compile("{+a+}{+a+}{+a+}");		
		assertEquals("111", Compose.inlineToString( d, "1" ) );
	}
	
}
