package varcode.java.model;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.model._constructors.constructor;
import varcode.java.model._constructors.constructor.signature;

public class _constructorsTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
	public void testFailOnBadModifiers()
	{
		_constructors cs = 
			new _constructors(  );
		try
		{
			cs.addConstructor( "final MyObject", "" );
			fail("Expected Exception for final modifier");
		}
		catch(VarException ve)
		{
			//expected
		}
		try
		{
			_constructors.of( " ", (Object[]) null );
			fail("Expected Exception for no signature");
		}
		catch( VarException e)
		{
			//expected
		}
		try
		{
			cs.addConstructor( "blah MyObject", "" );
			fail("Expected Exception for bad modifier");
		}
		catch(VarException ve)
		{
			//expected
		}
		
		try
		{
			cs.addConstructor( "public protected MyObject", "" );
			fail("Expected Exception for bad modifier");
		}
		catch(VarException ve)
		{
			//expected
		}
	}
	
	//verify that I cant add a constructor of the same Type
	public void testFailOnMatching()
	{
		_constructors cs = 
			new _constructors(  );
		cs.addConstructor( "MyObject", "" );
		try
		{
			cs.addConstructor( "MyObject", "public int a = 100;" );
			fail("expected Exception for mathcing Constructor");
		}
		catch( VarException ve )
		{
			//expected
		}
		
		cs = new _constructors(  );
		cs.addConstructor( "public MyObject( String a)", "this.a = a;" );
		try
		{
			cs.addConstructor( "public MyObject( String jeffrey )", "this.jeffrey = jefferey + jefferey;" );
			fail("expected Exception for matching Constructor");
		}
		catch( VarException ve )
		{
			//expected
		}		
	}
	
	public void testSimple()
	{
		constructor c = 
			new constructor( "MyObject" );
		assertEquals( 
				"MyObject(  )" + N +
				"{" + N +
                N +
				"}", c.toString() );
		
		System.out.println( c );
		
		c = new constructor( "MyObject" ).body("super();");
		assertEquals( 
				"MyObject(  )" + N +
				"{" + N +
                "    super();" + N +
				"}", c.toString() );


		c = new constructor( "private MyObject" ).body("super();");
		assertEquals( 
			"private MyObject(  )" + N +
			"{" + N +
	        "    super();" + N +
		    "}", c.toString() );
			
		c = new constructor( "private MyObject" ).body("super();");
		assertEquals( 
			"private MyObject(  )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
	}
	
	public void testWithParameters()
	{
		constructor c = new constructor( "public MyObject( String s )" ).body("super();");
		assertEquals( 
			"public MyObject( String s )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
			
		c = new constructor( 
			"public MyObject( String s, Map<Integer,String> map )" ).body("super();");
		assertEquals( 
			"public MyObject( String s, Map<Integer,String> map )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );		
	}
	
	public void testWithThrows()
	{
		constructor c = new constructor( "public MyObject(String s) throws RuntimeException, SerializationException" ).body("super();");
		assertEquals( 
			"public MyObject( String s )" + N +
			"    throws RuntimeException, SerializationException" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
		
	}
	
	public static void main( String[] args )
	{
		signature s = signature.of( "WolfHeimer" );
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer" );
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer()" );
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer( String x, int y )" );		
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer( String x,int y )" );		
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer( String x, int y )" );		
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer( String x ,int y )" );		
		System.out.println( s );
		
		s = signature.of( "public WolfHeimer( String x , int y )" );		
		System.out.println( s );
		
		s = signature.of( "private A( ) throws DumbException" );
		System.out.println( s );
	}
	
	
}
