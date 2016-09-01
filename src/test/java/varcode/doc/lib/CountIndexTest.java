package varcode.doc.lib;

import junit.framework.TestCase;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.markup.bindml.BindML;
import varcode.markup.codeml.CodeML;

public class CountIndexTest
	extends TestCase
{

	public static final String COUNT_INDEX_SCRIPT_NAME = "[#]";
	
	public static String bindML( String s, VarContext vc )
	{
		return Author.code( BindML.compile( s ), vc );
	}
	
	public static String codeML( String s, VarContext vc )
	{
		return Author.code( CodeML.compile( s ), vc );
	}
	
	public void testCountIndexMarks()
	{
		assertEquals( "", bindML( "{+$[#](a)+}", VarContext.of() ) );
		
		assertEquals( "0", bindML( "{+$[#](a)+}", VarContext.of("a", "a") ));		
		
		assertEquals( "", bindML( "{+$[#](a)+}", VarContext.of("a", new String[ 0 ] ) ) );
		
		assertEquals( "", codeML( "{+$[#](a)+}", VarContext.of("a", new int[0] ) ) );
		
		assertEquals( "0", bindML( "{+$[#](a)+}", VarContext.of("a", new String[] {"1"} ) ) );
		
		assertEquals( "0", codeML( "{+$[#](a)+}", VarContext.of("a", new String[] {"1"} ) )  );
		
		assertEquals( "0, 1, 2", codeML( "{+$[#](a)+}", VarContext.of("a", new String[] {"1", "2", "3"} ) ) );
		
		assertEquals( "0, 1, 2", codeML( "{+$[#](a)+}", VarContext.of("a", new String[] {"1", null, "3"} ) ) );
		
		assertEquals( "0, 1, 2", codeML( "{+$[#](a)+}", VarContext.of("a", new int[] { 1,2,3} ) ) );
		try
		{
			bindML("{+$[#](a)*+}", VarContext.of() );
			fail("expected exception for Required");
		}
		catch( VarBindException vbe)
		{
			
		}
		
	}
}
