package varcode.doc;

import junit.framework.TestCase;
import varcode.doc.Author;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class AuthorTest
	extends TestCase
{
	public void testFillCode()
	{
		Dom d = BindML.compile("{+a+}{+a+}{+a+}");		
		assertEquals("111", Author.fillCode( d, "1" ) );
	}
	
}
