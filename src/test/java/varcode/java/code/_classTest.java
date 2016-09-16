/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.java.JavaCase;
import varcode.java.adhoc.Workspace;

/**
 *
 * @author eric
 */
public class _classTest
    extends TestCase
{
    public void testClassSimple()
    {
        _class c = 
            _class.of( "ex.varcode", "public class MyClass" );
        
        assertEquals( "ex.varcode", c.getClassPackage().getName() );
        assertEquals( "ex.varcode.MyClass", c.getFullyQualifiedClassName() );
        
        
        //ensure all subcomponents are NON-NULL
        assertEquals( 0, c.getConstructors().count() );
        assertEquals( 0, c.getFields().count() );        
        assertEquals( 0, c.getImports().count() );
        assertTrue( c.getJavadoc().isEmpty() );
        assertEquals( 0, c.getMethods().count() );
        assertEquals( 0, c.getNests().count() );
        assertEquals( 0, c.getSignature().getExtends().count() );
        assertEquals( 0, c.getSignature().getImplements().count() );
        assertTrue( c.getSignature().getModifiers().containsAll( "public" ) );        
        assertTrue( c.getStaticBlock().isEmpty() );      
        
        
        //this should complete 
        c.replace( "varcode", "barcode" );
        
        assertEquals( "ex.barcode", c.getClassPackage().getName() );
        assertEquals( "ex.barcode.MyClass", c.getFullyQualifiedClassName() );
    }
    
    public void testCP()
    {
        _class c = 
            _class.of( "public class {+className*+}" );
        
        c.author( );
        
    }
    
    public void testClassParameterized()
    {
        _class c = 
            _class.of( "ex.varcode.{+pkg+}", "public class {+className+}" );
        
        assertEquals( "ex.varcode.{+pkg+}", c.getClassPackage().getName() );
        assertEquals( "ex.varcode.{+pkg+}.{+className+}", c.getFullyQualifiedClassName() );
        
        c.constructor( "private {+className+}()" );
        
        JavaCase jc = c.toJavaCase(
            VarContext.of( "pkg", "pack", "className", "CN" ) );
        
        //System.out.println( jc.toString() );
        assertTrue( jc.toString().contains("package ex.varcode.pack;"));
        assertTrue( jc.toString().contains("private CN"));        
        assertEquals( "ex.varcode.pack.CN", jc.getClassName() );        
    }
    	public static final String N = System.lineSeparator();
	
	public void testClassWithJavaDoc()
	{
		_class c = _class.of(
			"this is a class Javadoc", 
			"ex.varcode", 
			"public class MyClass" );
		assertEquals(
			"package ex.varcode;" + N +
			N +
			"/**" + N +
			" * this is a class Javadoc" + N +
			" */" + N +
			"public class MyClass" + N +
			"{" + N +
			"}",  
			c.toString() );
	}
	
	
	public void testStaticBlock()
	{
		_class c = new _class( "public class WithStaticBlock" )
			.staticBlock( "List<String> l = new ArrayList();" + N +"l.toString();" );
		
		System.out.println( c );				
	}
	
	public void testAll()
	{
		_class c = new _class( "public class AllComponentsBlock extends SomeBaseClass implements Serializable" )
			.staticBlock( "List<String> l = new ArrayList();" + N +"l.toString();" )
			.staticBlock( "int a;" )
			.javaDoc( "This is an example \"Authored\" class" + N + 
					"it is using the API to the fullest" )
		    .field( "public int r;" )
		    .field( "protected static final String ID = UUID.randomUUID().toString();")		   
		    .method(
		    		"public static void main(String[] args) throws IOException, ReflectiveOperationException", 
		    		"System.out.println(\"In main method\");")
		    .method( "public String getID()",
		    		"return ID;" )
			.imports( List.class, ArrayList.class, UUID.class, Serializable.class, "varcode.SomeBaseClass", IOException.class, ReflectiveOperationException.class )
			.packageName("varcode.classExample");
		
		System.out.println( c );			
	}
	
	public void testAddAbstractMethodToConcreteClass()
	{
		try
		{
            //_method m = 
			new _class( "public class Concrete" )
				.method( "public abstract void absMethod()" );
			fail("Expected Exception for concrete class with abstract method");
		}
		catch( VarException ve )
		{
			//expected 
		}
	}
	public void testClassMethods()
	{
		_class c = new _class( "public class B extends A implements G" );
		c.packageName("io.varcode")
		.imports("java.util.*", Map.class, UUID.class )		
		.field("public String w = \"1---1---1\";" )
		.field("private static final String id = UUID.randomUUID();" )
		.field("int x;")
		.method(
			"public static final void main (String[] args)",
			"UUID.randomUUID();" )
		.method(
			"public String getW()",
			"return this.w;" );
		//.addMethod(
		//	"public abstract String getPattern()" );
		System.out.println( c );
	}
	
	public void testClassMember()
	{
		_class c = new _class( "public class B extends A implements G" );
		c.packageName("io.varcode")
		.imports("java.util.*", Map.class )
		.field("public String w = \"1---1---1\";" )
		.field("int x;");
		System.out.println( c );
	}
	
	public void testClassMethod()
	{
		_class c = new _class(
			"public final class WithMethods extends AnObject implements Serializable" );
		c.method(
			"public static final void main (String[] args)", 
			"System.out.println(\"Got Here\");" )
		.imports(Map.class)
		.imports(UUID.class)
		.packageName("varcode.ex")
		.field("public int f = 100;");
		
		
		System.out.println(c);
	}
	
	
	public void testCycle()
        throws ClassNotFoundException
	{
		//NOTE One relies on Two and Two relies on One
		_class one = 
			_class.of("varcode.java.model", "public class One")
			.field("public int value;")
		    .field("public Two two;");
		
		_class two = 
			_class.of("varcode.java.model", "public class Two")
			.field("public String name")
			.field("public One one");
		
		JavaCase oneCase = one.toJavaCase( );
		JavaCase twoCase = two.toJavaCase( );
		Workspace sw = Workspace.of( oneCase, twoCase );
		
        System.out.println( oneCase );
        System.out.println( twoCase );
        
		ClassLoader cl = sw.compile( );
		Class<?> oneClass = cl.loadClass( one.getFullyQualifiedClassName() );
		Class<?> twoClass = cl.loadClass( two.getFullyQualifiedClassName() );
		assertTrue( oneClass != null );
		assertEquals( "One", oneClass.getSimpleName() );
		assertEquals( "Two", twoClass.getSimpleName() );
		
		_class claus = _class.of("varcode.java.model", "public class Top")
			.field( "int A" )
			.nest( _class.of( "public class Nested" ) );
		claus.toJavaCase().loadClass();
		
	}
	
	//when I nest a class
	// I need to remove the package
	// I need to MOVE the imports to the container
	// dont print EITHER the 
	
	/**
	 * Verify that when I nest classes inside other classes
	 * 1) I trikle the imports to the top level class
	 * 2) I DON'T print out the imports or packages in the nested classes
	 * 3) 
	 */
	public void testNestClass()
	{
		_class nestLevel2 = new _class( "public class NestLevel2" )
				.imports( Map.class ); 
		_class top = 
			new _class( "ex.varcode.nest", "public class Top" )
				.nest(
					new _class( "public static class NestLevel1" )
						.field("int i = 100;" )
						.imports( UUID.class ) //verify this import "trikles up"
						.method("public String getUUID()",
							"return UUID.randomUUID().toString();")
						.nest( nestLevel2 ) )
				.nest(
					new _class( "public class MemberClass" )
						.imports( TreeSet.class )
						.field("public String blue;" )
					);
		
		//System.out.println( top );
		
		//verify the nested class import "trikles up" from the nested classes
		assertTrue( top.getImports().contains("java.util.UUID" ) );
		assertTrue( top.getImports().contains("java.util.Map" ) );
		
		//verify that it DOESNT currently import arrayList
		assertFalse( top.getImports().contains("java.util.ArrayList" ) );
		
		//I should be able to change something then DYNAMICALLY
		// like adding ANOTHER import to nestLevel2, verify that I trikle up
		// the import to the top level imports
		nestLevel2.imports( ArrayList.class );
		assertTrue( top.getImports().contains("java.util.ArrayList" ) );
		
		//Class<?> clazz = top.toJava().loadClass();
		Object o = top.toJavaCase( ).instance( ); //create an instance
		
		Class<?>[] classes = o.getClass().getClasses();
		assertEquals( 2, classes.length );
		Set<String> nestNames = new HashSet<String>();
		nestNames.add("ex.varcode.nest.Top$NestLevel1");
		nestNames.add("ex.varcode.nest.Top$MemberClass");
		for( int i = 0; i < classes.length; i++ )
		{
			assertTrue( nestNames.contains( classes[ i ].getName() ) );
			//System.out.println( classes[ i ].getName() );
			if( classes[ i ].getName().equals("ex.varcode.nest.Top$NestLevel1" ) )
			{
				Class<?>[] nest2 = classes[ i ].getClasses();
				assertTrue( nest2.length == 1 );
				assertEquals("ex.varcode.nest.Top$NestLevel1$NestLevel2", nest2[0].getName() );
			}
			
		}
		//Java.invoke(o, "getUUID" );
		//System.out.println( top );
		
	}
	
	public void testClassSignature()
	{
		_class._signature cs = _class._signature.of( "class MyClass" );
		assertEquals( "class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "private class MyClass");
		assertEquals( "private class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "private final class MyClass");
		assertEquals( "private final class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "private static class MyClass");
		assertEquals( "private static class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "private static final class MyClass");
		assertEquals( "private static final class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "protected class MyClass");
		assertEquals( "protected class MyClass", cs.author( ) );
		
		cs = _class._signature.of( "protected abstract class MyClass");
		assertEquals( "protected abstract class MyClass", cs.author( ) );
		
		
		cs = _class._signature.of(
            "public static final class MyClass extends A implements B, C, D" );
		System.out.println( cs );		
	}
	
	
	public void testImplements()
	{
		_class._signature cs = 
            _class._signature.of( "class MyClass implements fss" );
		
		assertEquals( 
            "class MyClass" + System.lineSeparator()
		   +"    implements fss", cs.author( ) );
		
		cs = _class._signature.of( "class MyClass implements fss great" );
		
	}
	
	public void testsExtends()
	{
        _class._signature.of( "class MyClass extends ABaseClass" );
		
		try
		{
			_class._signature.of( "class MyClass extends implements" );
		}
		catch( VarException ve )
		{
			
		}
		try
		{
			_class._signature.of( "class MyClass extends ABaseClass, AnotherClass" );
			fail("expected exception for multiple extension");
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
	
	
	public void testClassModifiersShouldFail()
	{ 
		try
		{
			_class._signature.of( "abstract final class MyClass");
			fail("expected Exception");
		}
		catch ( VarException ve )
		{
			//expected
			//assertTrue( ve.getMessage().contains( "synchronized" ) );
		}
		try
		{
			_class._signature.of( "synchronized class MyClass");
			fail("expected Exception");
		}
		catch ( VarException ve )
		{
			//expected
			//assertTrue( ve.getMessage().contains( "synchronized" ) );
		}
		try
		{
			_class._signature.of( "native class MyClass");
			fail("expected Exception");
		}
		catch ( VarException ve )
		{
			//expected
			//assertTrue( ve.getMessage().contains( "native" ) );
		}
		try
		{
			_class._signature.of( "transient class MyClass");
			fail("expected Exception");
		}
		catch ( VarException ve )
		{
			//expected
			//assertTrue( ve.getMessage().contains( "transient" ) );
		}
		try
		{
			_class._signature.of( "volatile class MyClass");
			fail("expected Exception");
		}
		catch ( VarException ve )
		{
			//expected
			//assertTrue( ve.getMessage().contains( "volatile" ) );
		}
	}
    
}
