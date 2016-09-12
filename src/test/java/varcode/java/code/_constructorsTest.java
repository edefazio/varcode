/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;


import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.code._constructors._constructor;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class _constructorsTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
    
    public void testReplace()
    {
        _constructors c = new _constructors();
        assertEquals( 0, c.count() );
        assertEquals("", c.toString() );
        c.replace("A", "B");
        assertEquals("", c.toString() );        
    }
    
    public void testBind()
    {
        _constructors cs = new _constructors();
        assertEquals("", cs.bind(VarContext.of() ) );
        
        cs.addConstructor( "public {+name+}()" );
        //System.out.println ( cs.toString() );
        assertEquals(
            "public {+name+}(  )" + N +
            "{" + N +
            N +
            "}" + N
            , cs.toString() );
        
        String res = cs.bind( VarContext.of("name", "MyClass") );
        System.out.println(res);
        assertEquals( 
            "public MyClass(  )" + N +
            "{" + N +
            N +
            "}", res.trim() ); 
    }
    
    
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
        /*
		try
		{
			_constructors.of( " ", (Object[]) null );
			fail("Expected Exception for no signature");
		}
		catch( VarException e)
		{
			//expected
		}
*/
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
		_constructor c = 
			new _constructor( "MyObject" );
		assertEquals( 
				"MyObject(  )" + N +
				"{" + N +
                N +
				"}", c.toString() );
		
		System.out.println( c );
		
		c = new _constructor( "MyObject" ).body("super();");
		assertEquals( 
				"MyObject(  )" + N +
				"{" + N +
                "    super();" + N +
				"}", c.toString() );


		c = new _constructor( "private MyObject" ).body("super();");
		assertEquals( 
			"private MyObject(  )" + N +
			"{" + N +
	        "    super();" + N +
		    "}", c.toString() );
			
		c = new _constructor( "private MyObject" ).body("super();");
		assertEquals( 
			"private MyObject(  )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
	}
	
	public void testWithParameters()
	{
		_constructor c = new _constructor( "public MyObject( String s )" ).body("super();");
		assertEquals( 
			"public MyObject( String s )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
			
		c = new _constructor( 
			"public MyObject( String s, Map<Integer,String> map )" ).body("super();");
		assertEquals( 
			"public MyObject( String s, Map<Integer,String> map )" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );		
	}
	
	public void testWithThrows()
	{
		_constructor c = new _constructor( "public MyObject(String s) throws RuntimeException, SerializationException" ).body("super();");
		assertEquals( 
			"public MyObject( String s )" + N +
			"    throws RuntimeException, SerializationException" + N +
			"{" + N +
	        "    super();" + N +
			"}", c.toString() );
		
	}	
	
}
