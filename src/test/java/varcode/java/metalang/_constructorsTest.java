/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.metalang;


import varcode.java.metalang._code;
import varcode.java.metalang._constructors;
import java.io.IOException;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.metalang._constructors._constructor;


public class _constructorsTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
    /** 
     * Test different mechanisms for Bindin 
     * name,
     * parameters,
     * throws Exceptions
     * 
     */
    public void testBindIn()
    {
        _constructors c = new _constructors();
        c.bind(VarContext.of() );
        
        c = new _constructors();
        c.addConstructor( "public A()" );
        c.bind(VarContext.of() );
        assertEquals( 
            "public A(  )" + N + 
            "{" + N + N +
            "}", c.toString().trim() );
        
        c = new _constructors();
        c.addConstructor( "public {+className+}()" );
        c.bind( VarContext.of("className", "AClass") );
        
        System.out.println( c );
        assertEquals( 
            "public AClass(  )" + N + 
            "{" + N + N +
            "}", c.toString().trim() );        
        
        c = new _constructors();
        c.addConstructor( "public {+className+}({+paramType+} a)" );
        c.bind( VarContext.of("className", "AClass", "paramType", int.class ) );
        
        System.out.println( c );
        assertEquals( 
            "public AClass( int a )" + N + 
            "{" + N + N +
            "}", c.toString().trim() );        
        
        c = new _constructors();
        c.addConstructor( "public {+className+}({+paramType+} a) throws {+e+}" );
        c.bind( VarContext.of(
            "className", "AClass", "paramType", int.class,
            "e", IOException.class ) );
        
        System.out.println( c );
        assertEquals( 
            "public AClass( int a )" + N + 
            "    throws java.io.IOException" + N +        
            "{" + N + N +
            "}", c.toString().trim() );        
        
        c = new _constructors();
        c.addConstructor( 
            "public {+className+}({+paramType+} a) throws {+e+}",
             "{+body+}" );
        
        c.bind( VarContext.of(
            "className", "AClass", "paramType", int.class,
            "e", IOException.class,
            "body", _code.of( "System.out.println(\"In constructor\");" ) ) );
        
        System.out.println( c );
        assertEquals( 
            "public AClass( int a )" + N + 
            "    throws java.io.IOException" + N +        
            "{" + N + 
            "    System.out.println(\"In constructor\");" + N + 
            "}", c.toString().trim() );      
        
        c = new _constructors();
        c.addConstructor( 
            "public {+className+}({+paramType+} a) throws {+e+}",
             "{+body+}" );
        
        c.getAt( 0 ).javadoc( "{+comment+}" );
        
        c.bind( VarContext.of(
            "comment", "THE COMMENT",    
            "className", "AClass", "paramType", int.class,
            "e", IOException.class,
            "body", _code.of( "System.out.println(\"In constructor\");" ) ) );
        
        assertEquals( 
            "/**" + N + 
            " * THE COMMENT" + N + 
            " */" + N +         
            "public AClass( int a )" + N + 
            "    throws java.io.IOException" + N +        
            "{" + N + 
            "    System.out.println(\"In constructor\");" + N + 
            "}", c.toString().trim() );      
        
         c = new _constructors();
        c.addConstructor( 
            "public {+className+}({+paramType+} a) throws {+e+}",
             "{+body+}" );
        
        c.getAt( 0 ).javadoc( "{+comment+}" ).annotate("@{+someAnno+}");
        
        c.bind( VarContext.of(
            "someAnno", "Deprecated",    
            "comment", "THE COMMENT",    
            "className", "AClass", "paramType", int.class,
            "e", IOException.class,
            "body", _code.of( "System.out.println(\"In constructor\");" ) ) );
        
        assertEquals( 
            "/**" + N + 
            " * THE COMMENT" + N + 
            " */" + N +     
            "@Deprecated" + N +        
            "public AClass( int a )" + N + 
            "    throws java.io.IOException" + N +        
            "{" + N + 
            "    System.out.println(\"In constructor\");" + N + 
            "}", c.toString().trim() );      
    }
    
    public void testReplace()
    {
        _constructors c = new _constructors();
        assertEquals( 0, c.count() );
        assertEquals( "", c.toString() );
        c.replace( "A", "B" );
        assertEquals( "", c.toString() );        
    }
    
    public void testBind()
    {
        _constructors cs = new _constructors();
        assertEquals("", cs.bind(VarContext.of() ).author( ) );
        
        cs.addConstructor( "public {+name+}()" );
        //System.out.println ( cs.toString() );
        assertEquals(
            "public {+name+}(  )" + N +
            "{" + N +
            N +
            "}" + N
            , cs.toString() );
        
        String res = cs.bind( VarContext.of("name", "MyClass") ).author( );
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
