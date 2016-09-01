package varcode.doc.lib.text;

import junit.framework.TestCase;
import varcode.doc.lib.text.RemoveAllLinesWith;

public class RemoveAllLinesWithTest
	extends TestCase
{

	static final String N = System.lineSeparator();
		
	public void testRemoveAllLinesWith()
	{
		
		//RemoveAllLinesWith rl = new RemoveAllLinesWith( 
		String theLines = "A" + N +"B" +N +"C"+ N + "D" + N + "E" + N + "F";
		StringBuffer result = RemoveAllLinesWith.removeAllLinesContaining( theLines, "A", "E", "I", "O", "U" );
		//System.out.println ( result.toString() );
		
		assertEquals( result.toString(), "B" + N + "C" + N + "D" + N + "F" );
	}
	
	public void testNoLinesWith()
	{
		
		String theLines = "K" + N +"B" +N +"C"+ N + "D" + N + "J" + N + "F";
		StringBuffer result = RemoveAllLinesWith.removeAllLinesContaining( theLines, "A", "E", "I", "O", "U" );
		assertEquals( result.toString(), "K" + N + "B" + N + "C" + N + "D" + N + "J" + N + "F" );
	}
	
}
