package varcode.java.model;

import java.io.Externalizable;
import java.io.Serializable;

import junit.framework.TestCase;
import varcode.java.model._interface.signature;

public class _interfaceTest
	extends TestCase
{

	public static final String N = System.lineSeparator();
	
	public void testInterfaceSig()
	{
		_interface.signature sig = signature.of( "interface MyInterface");
		
		sig = signature.of(
		    "public static final interface MyInterface extends Something, AndSomethingElse");
		//assertEquals( 2, sig.extendsFrom.extendsFrom.size() );
		assertEquals( "Something", sig.getExtends().get(0).toString() );
		assertEquals( "AndSomethingElse", sig.getExtends().get(1).toString() );
		
	}
	
	public void testBaselineInterface()
	{
		_interface interf = _interface.of( "interface Marker" );
		
		assertEquals( 0, interf.getImports().count() );
		assertNull( interf.getPackageName() );
		assertEquals( 0, interf.getSignature().getExtends().count() );
		assertEquals( "", interf.getJavadoc() );
		
		assertEquals(0, interf.getFields().count() );
		assertEquals(0, interf.getMethods().count() );
		assertEquals(0, interf.getNests().count() );
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
					"System.out.println(\"Hello\")" )
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
		assertNotNull( interf.getFields().byName("name") );
		assertEquals( 2, interf.getMethods().count() );
		assertEquals( 1, interf.getNests().count() );
		
	}
	
	public static void main( String[] args )
	{
		
	
		//	System.out.println ( sig.className );
		//	System.out.println ( sig.extendsFrom );
		//System.out.println ( sig.modifiers );
		//System.out.println ( sig.implementsFrom );
		//System.out.println( sig );
	
		
	
		//System.out.println( sig );
	
		/*
		sig = tokenize("class MyClass");
		System.out.println ( sig.className );
		System.out.println ( sig.extendsFrom );
		System.out.println ( sig.modifiers );
	
		System.out.println( sig );
		 */	
	}
}
