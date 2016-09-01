package varcode.doc.lib;

import junit.framework.TestCase;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.markup.bindml.BindML;
import varcode.markup.codeml.CodeML;

public class CountTest
	extends TestCase
{

	public static String bindML( String s, VarContext vc )
	{
		return Author.code( BindML.compile( s ), vc );
	}
	
	public static String codeML( String s, VarContext vc )
	{
		return Author.code( CodeML.compile( s ), vc );
	}
	
	public void testCount()
	{
		assertEquals( "", bindML( "{+$#(a)+}", VarContext.of() ) );
		
		assertEquals( "1", bindML( "{+$#(a)+}", VarContext.of("a", "a") ) );		
		
		assertEquals( "0", bindML( "{+$#(a)+}", VarContext.of("a", new String[ 0 ] ) ) );
		
		assertEquals( "0", codeML( "{+$#(a)+}", VarContext.of("a", new int[0] ) ) );
		
		assertEquals( "1", bindML( "{+$#(a)+}", VarContext.of("a", new String[] {"1"} ) ) );
		
		assertEquals( "1", codeML( "{+$#(a)+}", VarContext.of("a", new String[] {"1"} ) ) );
		
		assertEquals( "3", codeML( "{+$#(a)+}", VarContext.of("a", new String[] {"1", "2", "3"} ) ) );
		
		assertEquals( "3", codeML( "{+$#(a)+}", VarContext.of("a", new String[] {"1", null, "3"} ) ) );
		
		assertEquals( "3", codeML( "{+$#(a)+}", VarContext.of("a", new int[] { 1,2,3} ) ) );
		try
		{
			assertEquals( "", bindML("{+$#(a)*+}", VarContext.of() ) );
			fail("expected exception for Required");
		}
		catch( VarBindException vbe)
		{
			
		}
		
	}
}
