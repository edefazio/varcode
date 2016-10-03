package varcode.doc.lib.text;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class SameCountTest
	extends TestCase
{
	public void testSameCount()
	{
		// "#=" is "shorthand" for "sameCount"
		Dom d = BindML.compile("{$#=(a,b)$}");
		
		Compose.asString( d, "a", 1, "b", 2 );
		
		Compose.asString( d, "a", 1, "b", "hamburger" );
		
		//works if they are both null
		Compose.asString( d, "a", null,"b", null );
		
		try
		{
			Compose.asString( d, "a", 1 ); //counts not the same
		}
		catch( VarException e)
		{
			assertTrue( e.getMessage().contains( "b" ) );
			//expected exception
		}
		
		try
		{
			Compose.asString( d, "a", 1, "b", null ); //counts not the same
		}
		catch( VarException e)
		{
			assertTrue( e.getMessage().contains( "b" ) );
			//expected exception
		}
	}
}
