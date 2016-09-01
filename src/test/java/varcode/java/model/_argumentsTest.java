package varcode.java.model;

import junit.framework.TestCase;

public class _argumentsTest
	extends TestCase
{

	public void testSingleArguments()
	{
		test.with(_arguments.of() ).is("(  )", "toString");
		test.with(_arguments.of() ).toString("(  )" );
		assertEquals("(  )", _arguments.of( ).toString() );
		assertEquals("(  )", new _arguments().toString() );
		assertEquals("( 10 )", _arguments.of( "10" ).toCode( ) );
		assertEquals("( true )", _arguments.of( "true" ).toCode( ) );
		assertEquals("( 10.34d )", _arguments.of( "10.34d" ).toCode( ) );
		assertEquals("( 10.34d )", _arguments.of( "10.34d" ).toCode( ) );
		assertEquals("( \"A\" )", _arguments.of( "\"A\"" ).toCode( ) );
		
		assertEquals("( null )", _arguments.of( "null" ).toCode( ) );
		assertEquals("(  )", _arguments.of( (Object[])null ).toCode( ) );
		assertEquals("( null )", _arguments.of( new Object[] {null} ).toCode( ) );
		//a Reference
		assertEquals("( A )", _arguments.of( "A" ).toCode( ) );
		//field reference
		assertEquals("( A.field )", _arguments.of( "A.field" ).toCode( ) );
		
		assertEquals("( A.method() )", _arguments.of( "A.method()" ).toCode( ) );	
	}
	
	public void testMultipleArugments()
	{
		test.with(_arguments.of("A", "B", "C" ) ).toString( "( A, B, C )"  );
		assertEquals( "( A, B, C, D )", _arguments.of( "A","B","C","D" ).toString() );
		assertEquals( "( 1, 2, 3, 4 )", _arguments.of( 1,2,3,4 ).toString() );
		assertEquals( "( 1.0F, 2.0F, 3.0F, 4.0F )", _arguments.of( 1.0f,2.0f,3.0f,4.0f ).toString() );
		assertEquals( "( 'c', 1, true, 3.0F, 4.0d, (byte)2, (short)5, 11504L )", 
			_arguments.of( 'c',1,true,3.0f,4.0d, (byte)2, (short)5, (long)11504 ).toString() );
		assertEquals( "( 1, 2, 3, null )", _arguments.of( 1,2,3,null ).toString() );
		
	}
	

	public static void main(String[] args)
	{
		_arguments argu = new _arguments();
		System.out.println( argu );
		
		argu = 
			_arguments.of("5", "new HashMap<Integer,String>()", "new Date()", "100.0f" );
		System.out.println( argu );
		
	}
}
