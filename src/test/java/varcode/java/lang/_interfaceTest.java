/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.lang;

import varcode.java.lang._interface;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.Dom;
import varcode.java.Java;
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
        assertEquals("MyInterface", in.getFullyQualifiedClassName());
        assertEquals(0, in.getImports().count());
        assertTrue( in.getJavadoc().isEmpty() );
        assertEquals(0, in.getMethods().count() );
        assertEquals(0,in.getNesteds().count() );
        assertEquals( "", in.getPackageName() );
        assertEquals(0, in.getSignature().getExtends().count() );
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
        assertEquals( 100, Java.getFieldValue( theInterfaceClass, "ID" ) );
        
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
		assertEquals( "",  interf.getPackageName() );
		assertEquals( 0, interf.getSignature().getExtends().count() );
		assertEquals( "", interf.getJavadoc() );
		
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
		
		assertEquals( "ex.varcode", interf.getPackageName() );
		assertEquals( 2, interf.getSignature().getExtends().count() );
		//assertTrue( 
		//	interf.interfaceSignature.extendsFrom.extendsFrom.contains( "Serializable") ) );
		//assertTrue( interf.interfaceSignature.extendsFrom.extendsFrom.contains( "Externalizable" ) );
		assertEquals( "", interf.getJavadoc() );
		
		assertEquals( 1, interf.getFields().count() );
		assertNotNull( interf.getFields().getByName("name") );
		assertEquals( 2, interf.getMethods().count() );
		assertEquals( 1, interf.getNesteds().count() );
		
	}
}
