package varcode.doc.lib.text;

import junit.framework.TestCase;
import varcode.doc.lib.text.CondenseMultipleBlankLines;

public class CondenseMultipleBlankLinesTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
	public void testMultipleBlankLines()
	{
		String s = CondenseMultipleBlankLines.doCondense( "A"+ N 
				    + N 
				    + N 
				    + N
				    + N
				    + N
				    + "B" );
		assertEquals( "A" + N + N + "B", s);		
	}
	
	public void testStartsWithBlankLines()
	{
		String s = CondenseMultipleBlankLines.doCondense( N 
			    + N 
			    + N 
			    + N
			    + N
			    + N
			    + "A");
		
		assertEquals( "A", s );
	}
}
