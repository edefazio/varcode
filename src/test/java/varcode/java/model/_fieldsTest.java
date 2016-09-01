package varcode.java.model;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.model._fields.field;

public class _fieldsTest
	extends TestCase
{	

	public void testBadModifiers()
	{
		try
		{
			_fields.of("public strictfp float myfloat;");
			fail("expected exception for bad modifier");
		}
		catch( VarException ve )
		{
			
		}
		
		try
		{
			_fields.of( "private volatile static final int y = 100;");
		}
		catch(Exception e)
		{
			//expected
		}
	}
	
	public static String N = System.lineSeparator();
	
	public void testField()
	{
		field f = field.of( "int x;" );		
		assertEquals( "int x;", f.toString() );
		
		f = field.of("doc", "int x;" );
		assertEquals( 
			"/**" + N +
			" * doc" + N +
			" */" + N +
			"int x;", f.toString() );
		
		f = field.of("int x = 100;");
		
		assertEquals( f.toString(), "int x = 100;" );
		
		
		f = field.of( "doc", "int x = 100;" );
		
		assertEquals( 
			"/**" + N +
			" * doc" + N +
			" */" + N +
			"int x = 100;", f.toString() );
	}
	
	
	public static void main( String[] args )
	{
		_fields m = _fields.of( "int x" );
		System.out.println( m );
		
		m = _fields.of( "int x", "int y", "private static String NAME= \"Eric\";" );
		System.out.println( m );
		
		m = _fields.of( "int x;" );
		System.out.println( m );
		
		m = _fields.of( "private static final int y = 100;");
		System.out.println( m );
		
		m = _fields.of( "private static final int y = (int)(1L << 20 ^ new Random());");
		System.out.println( m );
		
	
	}

}