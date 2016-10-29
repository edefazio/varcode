/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._class;
import varcode.java.model._methods;
import varcode.java.model._code;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.lib.text.Prefix;
import varcode.java.model._methods._method;
import varcode.java.model._methods._method._signature;

/**
 *
 * API is it "structurally correct"?
 * is it "logically correct"? (we'll use the compiler for this)
 * does it do what I want (well, test it)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _methodsTest
    extends TestCase
{
    public void testIndentBind()
    {
        
        _method m = _method.of( 
            "public {+returnType*+} call() throws Exception",
            _code.of(
                "{+init+}",                 
                "for( int i = startIndex; i < startIndex + count; i++)",
                "{",
                "{+$>(match)*+}",
                "}",
                "{+reduce*+}" ) );
         
        //System.out.println( m );
        //System.out.println 
        //    ( m.bindIn(VarContext.of("returnType", int.class, "match", "1 == 1", "reduce", "return 1;") ) );
         
        _class c = _class.of("Something")
           .method( m );
        
        //System.out.println( c );
        
        //String s = c.bindIn( VarContext.of( "returnType", int.class, "match", "1 == 1", "reduce", "return 1;" ) ).author( );
        //System.out.println( s );
        
        String s = c.bind( VarContext.of( "returnType", int.class, "match", _code.of("if( i< 100 )", "x++;"), "reduce", "return 1;" ) ).author();
        System.out.println( s );
    }
    
    public void testInfer()
    {
        _method m = _method.of("someName"); //default method "someName" with no parameters
        
        assertEquals( m.getName(), "someName");
        assertEquals( 0, m.getSignature().getModifiers().count() );
        assertEquals( 0, m.getSignature().getParameters().count() );
        assertEquals(
            "void someName(  )" + N +
            "{" + N + N +
            "}", m.toString() );
        
    }
    
    /**
     * Test that creating a method with parameters
     */
    public void testAnnotated()
    {
        _method m = new _method( 
            "public Response findById( @PathParam(\"id\") final long id )", 
            "return Response.ok().build();" )                
            .annotate( "@Path(\"/{id}\")", "@GET" );
        
        assertEquals( 2, m.getAnnotations().count() );
        Object ann = m.getAnnotations().getAt( 0 );
        assertEquals( "@Path(\"/{id}\")", ann.toString() );
        assertEquals( "@GET", m.getAnnotations().getAt( 1 ).toString() );
        
        assertEquals(
            "@Path(\"/{id}\")" + N + 
            "@GET" + N +
            "public Response findById( @PathParam(\"id\") final long id )" + N +
            "{" + N +
            "    return Response.ok().build();" + N +
            "}", m.toString() );                
        
        //check about replace within annotations
        m.replace("GET", "POST");
        
        assertEquals(
            "@Path(\"/{id}\")" + N + 
            "@POST" + N +
            "public Response findById( @PathParam(\"id\") final long id )" + N +
            "{" + N +
            "    return Response.ok().build();" + N +
            "}", m.toString() );                
        
        
        m = new _method( "public void mustOverride()" ).annotate("@Override");
        
        assertEquals(
        "@Override" + N +
        "public void mustOverride(  )" + N +
        "{" + N + N +
        "}", m.toString() );
    }
    
    public void testNoMethods()
    {
        _methods m = new _methods();
        assertEquals( 0, m.count() );
        assertEquals( null, m.getByName("anything") );
        assertTrue( m.isEmpty() );
        m.replace("A", "Z");
    }

    public void testOneReplace()
    {
        _methods m = new _methods();
        m.addMethod("public abstract int methodName()");
        
        assertEquals( 1, m.count() );
        assertEquals( 1, m.getByName( "methodName" ).size() );
        assertFalse( m.isEmpty() );
        m.replace( "methodName", "blahName" );
        
        //verify that if I change the name of a method it works
        assertEquals( 1, m.count() );
        assertEquals( 1, m.getByName( "blahName" ).size() );
        assertFalse( m.isEmpty() );        
    }
    
    public static final String N = "\r\n";
    
    public void testParameterizedMethodName()
    {        
        _methods m = new _methods();
        m.addMethod( "public String {+methodName+}()" );
        String res = m.bind( VarContext.of("methodName", "M") ).author();
        System.out.println( res );
        assertEquals( "public String M(  )" + N + "{"+ N + N + "}", res.trim() );        
    }
    
    public void testParameterizedReturnType()
    {        
        _methods m = new _methods();
        m.addMethod( "public {+returnType+} method()" );
        String res = m.bind( VarContext.of( "returnType", "Gizmo") ).author();
        System.out.println( res );
        assertEquals( "public Gizmo method(  )" + N + "{"+ N + N + "}", res.trim() );        
    }
    
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
	/* Let the compiler handle this
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
*/
	public void testSignature()
	{
		assertEquals("void method(  )", 
			_signature.of("void method()").toString() );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator() +
			"    throws IOException", 
		_signature.of(
			"public static final void main( String[] args ) throws IOException" ).toString() );
		//System.out.println( 
		//	) );
	
		assertEquals(
			"public static final void main( String[] args )" + System.lineSeparator()+
			"    throws IOException, ReflectiveOperationException",
		_signature.of(
			"public static final void main( String[] args ) "
			+ "throws IOException, ReflectiveOperationException" ).toString() );
	}

	
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
		
		String indented = m.author( Prefix.INDENT_4_SPACES );
		assertEquals(
		"    public final String getX(  )" + System.lineSeparator()+ 
		"    {" + N +
		"        return this.x;" + N +
		"    }",
		indented );
				
	}	
 
    //make sure there is no method body for abstract methods
    public void testAbstractMethods()
    {
        _methods m = new _methods();
        m.addMethod("public abstract int blah()");
        String res = m.author( );
        assertEquals("public abstract int blah(  );", res.trim());
    }
}
