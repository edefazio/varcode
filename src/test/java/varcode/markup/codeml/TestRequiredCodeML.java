package varcode.markup.codeml;

import junit.framework.TestCase;
import varcode.context.VarBindException.NullResult;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.doc.Compose;

/**
 * Test that Required Works as Expected for BindML
 * 
 * @author eric
 */
public class TestRequiredCodeML
	extends TestCase
{
	/** 
	 * Verify that an attempt to tailor the markup will fail
	 * with a precise RequiredButNull Exception
	 * 
	 * @param mark the mark to Compile and Tailor
	 */
	private static void verifyThrows( String mark )
	{
		try
		{
			Compose.asString( CodeML.compile( mark ), VarContext.of() );
			fail( "expected RequiredButNull" );
		}
		catch( NullVar rbn )
		{	
			//expected
			//System.out.println(rbn );
		}
	}
	
	/** 
	 * Verify that an attempt to tailor the markup will fail
	 * with a precise RequiredButNull Exception
	 * 
	 * @param mark the mark to Compile and Tailor
	 */
	private static void verifyThrows( String mark, Class<?> ExceptionClass )
	{
		try
		{
			Compose.asString( CodeML.compile( mark ), VarContext.of() );
			fail( "expected Exception "+ ExceptionClass );
		}
		catch( Exception exception )
		{	
			//expected
			if( !ExceptionClass.isAssignableFrom( exception.getClass() ) )
			{
				fail("Expected Exception "+ ExceptionClass+" got "+ exception.getClass() );
			}
		}
	}
	

	private static void verifyThrowsNullResult( String mark )
	{
		try
		{
			Compose.asString( CodeML.compile( mark ), VarContext.of() );
			fail( "expected RequiredButNull" );
		}
		catch( NullResult rbn )
		{	
			//expected
			//System.out.println(rbn );
		}
	}
	
	public void testTags()
	{
		verifyThrows( "{+requiredButNull*+}" ); // var is not bound
		//required                      ^
		
		verifyThrows( "/*{+requiredButNull*+}*/" ); // var is not bound
		//required                        ^
		
		verifyThrows( "/*{+requiredButNull**/replace/*+}*/" ); // var is not bound
		//required                        ^
		
		verifyThrows( "{+$script()*+}" ); // script is not bound
		//required                ^
		
		verifyThrows( "/*{+$script()*+}*/" ); // script is not bound
		//required                  ^
		
		verifyThrows( "/*{+$script(*/replace/*)*+}*/", VarBindException.class ); // script is not bound
		//required                             ^
		
		verifyThrowsNullResult( "{+$count(notFound)*+}" ); //the var is not bound (result is null)
		//required                                 ^
		verifyThrowsNullResult( "/*{+$count(notFound)*+}*/" ); //the var is not bound (result is null)
		//required                                   ^
		
		verifyThrows( "/*{{+:{+fieldType*+} {+fieldName+}+}}*/" );
		//required                      ^
		
		verifyThrows( "/*{{+:{+fieldType+} {+fieldName*+}+}}*/" );
		//required                                    ^
		
		verifyThrowsNullResult( "/*{{+:{+fieldType+} {+fieldName+}*+}}*/" );
		//required                                                ^
		
		verifyThrows( "/*{_+:{+fieldType*+} {+fieldName+}+_}*/" );
		//required                      ^
		
		verifyThrows( "/*{_+:{+fieldType+} {+fieldName*+}+_}*/" );
		//required                                    ^
		
		
		assertEquals("",
			Compose.asString( CodeML.compile( "/*{_+:{+fieldType+} {+fieldName+}*+_}*/" ), VarContext.of() ) );

	}
}
