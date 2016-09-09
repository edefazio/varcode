package varcode.java.model;

import varcode.CodeAuthor;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.model._methods._method;
import varcode.java.model._methods._method.signature;

public class _methodsTest
	extends TestCase
{
	public void testMethods()
	{
		_methods m = new _methods();
		
		m.addMethod( "public void setX( int x ) throws InvalidOperationException, BadThingsException", "this.x = x;" );
		m.addMethod( "protected void setY( int y )", "this.y = y;" );
		m.addMethod( "public int getY( )", "this.y = y;" );
		m.addMethod( "public static final String getID( )", "return UUID.randomUUID.toString();" );
		
		assertEquals( 4, m.count() );
		
		m.author( );
		
		//System.out.println( m );
		//System.out.println( m.toCode( m.INDENT ) );
	}
	

	public void testComplicatedParameterList()
	{
		_methods m = new _methods();
		m.addMethod( 
			"final SomeObject<String,UUID> methodName( Double d, int[] arr ) throws SomeException, AnotherException",
			"return new SomeObject<String,UUID>();" );
		m.addMethod(
			"protected synchronized Map<String,List<Integer>> someMethod() throws SomeException",
			"return null;" );
		
		// NOTE: this space causes a bug-----------|  fix later
		//m.addMethod(                             |
		//		"protected synchronized Map<String, List<Integer>> someMethod() throws SomeException",
		//		"return null;" );
				
		m.author( );
		//System.out.println( m );
	}
	
	public void testBadModifiers()
	{
		_methods m = new _methods();
		try
		{
			m.addMethod(
				"transient void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
				
		try
		{
			m.addMethod(
				"volatile void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
		
		try
		{
			m.addMethod(
				"public private void methodName( )",
					"throwsException" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
	
	public void testMethodMatches()
	{
		_methods m = new _methods();
		m.addMethod( "public void setX( int x )", "this.x = x;" );
		try
		{
			m.addMethod( "public void setX( int anyVar ) throws IOException", "doesnt matter should fail;" );
			fail( "expected exception" );
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
	public void testSignature()
	{
		assertEquals("void method(  )", 
			signature.of("void method()").toString() );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator() +
			"    throws IOException", 
		signature.of(
			"public static final void main( String[] args ) throws IOException" ).toString() );
		//System.out.println( 
		//	) );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator()+
			"    throws IOException, ReflectiveOperationException",
		signature.of(
			"public static final void main( String[] args ) "
			+ "throws IOException, ReflectiveOperationException" ).toString() );
	}

	public static final String N = System.lineSeparator();
	
	public void testMethod()
	{
		_method m = 
			_method.of( "public final String getX()")
			.body( "return this.x;" );
		
		assertEquals(
			"public final String getX(  )" + System.lineSeparator()+ 
			"{" + N +
			"    return this.x;" + N +
			"}",
			m.toString() );
		
		String indented = m.author( CodeAuthor.INDENT );
		assertEquals(
		"    public final String getX(  )" + System.lineSeparator()+ 
		"    {" + N +
		"        return this.x;" + N +
		"    }",
		indented );
				
	}	
}
