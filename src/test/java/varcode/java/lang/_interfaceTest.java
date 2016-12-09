/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.lang;

import varcode.java.lang._methods;
import varcode.java.lang._annotations;
import varcode.java.lang._package;
import varcode.java.lang._javadoc;
import varcode.java.lang._imports;
import varcode.java.lang._fields;
import varcode.java.lang._interface;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import varcode.context.VarContext;
import varcode.doc.Dom;
import varcode.java._Java;
import varcode.java.JavaCase;
import varcode.java.lang._interface._signature;

/**
 *
 * @author eric
 */
public class _interfaceTest
    extends TestCase
{
    public static final String N = "\r\n";
    
    public void testSimpleInterface()
    {
        _interface in = _interface.of("interface MyInterface");
        assertEquals(
            "interface MyInterface" + N +
            "{" + N +
            "}",
            in.author( ) );
        
        VarContext vc = in.getContext();
        Dom dom = in.getDom();
        assertEquals( 0, in.getFields().count() );
        assertEquals( "MyInterface", in.getFullyQualifiedClassName());
        assertEquals( 0, in.getImports().count());
        assertTrue( in.getJavadoc().isEmpty() );
        assertEquals( 0, in.getMethods().count() );
        assertEquals( 0,in.getNesteds().count() );
        assertTrue( in.getPackage().isEmpty() );
        assertEquals( 0, in.getSignature().getExtends().count() );
        assertEquals( "MyInterface", in.getSignature().getName() );        
        
        in.replace("MyInterface", "YourInterface");
        
        assertEquals(
            "interface YourInterface" + N +
            "{" + N +
            "}",
            in.author( ) );
        
        assertEquals( "YourInterface", in.getFullyQualifiedClassName());
        assertEquals( "YourInterface", in.getSignature().getName() );        
    }
    public void testParam()
    {
        _interface in = _interface.of("interface {+InterfaceName*+}");
        assertEquals(
            "interface {+InterfaceName*+}" + N +
            "{" + N +
            "}",
            in.author( ) );
        
        assertEquals(
            "interface MyInterface" + N +
            "{" + N +
            "}",
            in.toJavaCase( 
                VarContext.of( "InterfaceName", "MyInterface" ) ).toString() );        
    }
    
    public void testInterfaceFullLoad()
    {
        _interface in = _interface.of(
            "ex.varcode.in", 
            "public interface MyInterface extends java.io.Serializable, java.io.Externalizable");
        in.defaultMethod(
            "public default void sayHello(String name)", 
            "System.out.println(\"Hello \"+name );");
        in.field("public static final int ID = 100;");
        in.field("public String aUUID = UUID.randomUUID().toString();");
        in.imports(UUID.class);
        in.nest( _interface.of("public interface Marker") );
        in.method("public int getUUID()");
        in.staticMethod("public static int getId()", 
            "return ID;" );
        System.out.println( in.author( ) );
        JavaCase jc = in.toJavaCase( );
        
        assertEquals( "ex.varcode.in.MyInterface", jc.getClassName() );
        
        //create and load the interface class
        Class theInterfaceClass = jc.loadClass();
        
        assertEquals( 
            "ex.varcode.in.MyInterface", 
            theInterfaceClass.getCanonicalName() );
        
        Field[] fields = theInterfaceClass.getDeclaredFields();
        
        assertEquals( 2, fields.length );        
        assertEquals( 100, _Java.getFieldValue( theInterfaceClass, "ID" ) );
        
        Class[] declaredClasses = 
            theInterfaceClass.getDeclaredClasses();
        
        //verify I can get the nested inner class
        assertEquals( 1, declaredClasses.length );
        
        assertEquals("ex.varcode.in.MyInterface.Marker",
            declaredClasses[ 0 ].getCanonicalName() );            
    }
    
	
	public void testInterfaceSig()
	{
		_interface._signature sig = _signature.of( "interface MyInterface");
		
		sig = _signature.of(
		    "public static final interface MyInterface extends Something, AndSomethingElse");
		//assertEquals( 2, sig.extendsFrom.extendsFrom.size() );
		assertEquals( "Something", sig.getExtends().getAt(0).toString() );
		assertEquals( "AndSomethingElse", sig.getExtends().getAt(1).toString() );
		
	}
	
	public void testBaselineInterface()
	{
		_interface interf = _interface.of( "interface Marker" );
		
		assertEquals( 0, interf.getImports().count() );
		assertTrue( interf.getPackage().isEmpty() );
		assertEquals( 0, interf.getSignature().getExtends().count() );
		assertEquals( "", interf.getJavadoc().author() );
		
		assertEquals(0, interf.getFields().count() );
		assertEquals(0, interf.getMethods().count() );
		assertEquals(0, interf.getNesteds().count() );
		assertEquals( 
			"interface Marker"+ N +
			"{" + N +
			"}", interf.toString() );
		//System.out.println( interf );
	}
	
	
	
	//test :
	// package
	// extending multiple interfaces
	// field
	// default method
	// abstract interface method	
	// nested 
	public void testFullInterface()
	{
		_interface interf = _interface.of(
			"ex.varcode", "public interface Full extends Serializable Externalizable")
				.imports( Serializable.class, Externalizable.class )
				.defaultMethod( "public default void sayHello()", 
					"System.out.println(\"Hello\");" )
				.field( "public static final String name = \"TheNameUWanted\";" )
				.method( "public int getID()" )
				.nest( 
					_interface.of( "public interface Marker" ) ); 
		System.out.println( interf.toString() );
		
		//Make sure it's OK by the JAVAC compiler
		Class<?> c = interf.toJavaCase( ).loadClass();
		
		assertEquals( "Full", c.getSimpleName() );
		assertEquals( 1, c.getClasses().length ); //there is 1 nested class
		assertEquals( "Marker", c.getClasses()[0].getSimpleName() );
		assertEquals( 2, c.getDeclaredMethods().length );
		assertEquals( 1, c.getDeclaredFields().length );
		
		assertEquals( 2, interf.getImports().count() );
		assertTrue( interf.getImports().contains( Serializable.class.getCanonicalName() ) );
		assertTrue( interf.getImports().contains( Externalizable.class.getCanonicalName() ) );
		
		assertEquals( "ex.varcode", interf.getPackage().getName() );
		assertEquals( 2, interf.getSignature().getExtends().count() );
		//assertTrue( 
		//	interf.interfaceSignature.extendsFrom.extendsFrom.contains( "Serializable") ) );
		//assertTrue( interf.interfaceSignature.extendsFrom.extendsFrom.contains( "Externalizable" ) );
		assertEquals( "", interf.getJavadoc().author() );
		
		assertEquals( 1, interf.getFields().count() );
		assertNotNull( interf.getFields().getByName("name") );
		assertEquals( 2, interf.getMethods().count() );
		assertEquals( 1, interf.getNesteds().count() );
		
	}
        
    public void testStringInit()
    {
        _interface _e = _interface.of( "public interface A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        
        _e = _interface.of( "io.varcode", "public interface A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackage().getName() );
        
        _e = _interface.of( "/*comment*/", "public interface A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        System.out.println( _e );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        
        
        _e = _interface.of( "/*comment*/", "io.varcode", "public interface A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackage().getName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        
        
        _e = _interface.of( "/*comment*/", "@Deprecated", "io.varcode", "public interface A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackage().getName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        
        _e = _interface.of( "/*comment*/", "@Deprecated", "io.varcode", 
            "public interface A", 
            Map.class, Date.class );
        
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().containsAll( "public" ) );        
        assertEquals( "io.varcode", _e.getPackage().getName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _e.getImports().containsAll( Map.class, Date.class ) ); 
        
        _e = _interface.of( 
            "/**comment*/", 
            "@Deprecated", 
            "io.varcode", 
            "public interface A", 
            Map.class, Date.class,
            _fields._field.of( "public static final int ID = 100;" )
                .javadoc( "comment" ),
            _methods._method.of( "public String doIt" )    
            );
        
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().containsAll( "public" ) );        
        assertEquals( "io.varcode", _e.getPackage().getName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _e.getImports().containsAll( Map.class, Date.class ) ); 
        assertEquals( 1, _e.getFields().count() );
        assertEquals( 1, _e.getMethods().count() );        
    }
    
    public void testJDocClass()
    {
         _interface _c = _interface.of( 
            _package.of( "io.varcode" ),
            _imports.of( Map.class, Date.class ),
            _javadoc.of( "comment" ),             
            _annotations._annotation.of( "@Deprecated" ),            
            "public interface A"                         
            );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains( "public" ) );        
        assertEquals( "io.varcode", _c.getPackage().getName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _c.getImports().containsAll( Map.class, Date.class ) );
    }
}
