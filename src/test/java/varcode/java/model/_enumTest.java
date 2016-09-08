package varcode.java.model;

import java.lang.reflect.Modifier;
import java.util.UUID;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.model._enum.enumValueConstructs;
import varcode.java.model._enum.enumValueConstructs.enumValueConstruct;
import varcode.java.model._enum.signature;

public class _enumTest
	extends TestCase
{
	
	public void testNest()  
	{
		//NOTE: the nested class imports UUID
		// which is
		_enum e = _enum.of(
			"ex.varcode.model", 
			"public enum TopLevel")
				.field("public int index;")
		        .nest( 
		        	_class.of(
		        		"some.ignored.pack.age", 
		        		"public static class InnerClass")		        	    
		        		.field( "public String ID = UUID.randomUUID().toString()" )
		        		.imports(UUID.class)
		        	);
		JavaCase js = e.toJavaCase( );
		System.out.println( js );
		js.loadClass();		
	}
	
    
    public void testConstructors()
    {
        _enum e = _enum.of( "public enum E" );
        try
        {
            e.constructor("public E()" );            
            fail("Expected Exception for public constructor");
        }
        catch( VarException ve )
        {
            //e
        }
        
        try
        {
            e.constructor("protected E()" );            
            fail("Expected Exception for protected constructor");
        }
        catch( VarException ve )
        {
            //e
        }
        
    }
	public void testEnumValueConstructs()
	{
		enumValueConstructs es = new enumValueConstructs();
		
		//enums CAN have no enumerated values
		assertEquals( ";" + System.lineSeparator(), es.toString() );
		
		es.addEnumValue( enumValueConstruct.of( "A" ) );
		
		assertEquals( "    A;" + System.lineSeparator(), es.toString() );
		
		es.addEnumValue( enumValueConstruct.of( "B" ) );
		assertEquals( "    A," + System.lineSeparator() + 
				      "    B;" + System.lineSeparator(), es.author( ) );
		
		try
		{
			es.addEnumValue( enumValueConstruct.of( "A" ) );
			fail("expected Exception for adding duplicate");
		}
		catch(VarException ve)
		{
			//expected
		}
		
		//here the enum C calls a constructor with arguments ("Hey" and 1)
		es.addEnumValue( enumValueConstruct.of( "C", "\"Hey\"", 1 ) );
		
		assertEquals( "    A," + System.lineSeparator() + 
			          "    B," + System.lineSeparator() +
			          "    C( \"Hey\", 1 );"+ System.lineSeparator(), es.author( ) );
		
		//System.out.println( es.toCode( SelfAuthored.INDENT ) );
	}
	
	public void testSignature()
	{
		signature sig = signature.of(
				"enum MyEnum");
	
		assertEquals( "enum MyEnum", sig.toString() );
	
		sig = signature.of(
				"public enum MyEnum implements Serializable,Externalizable");
			
		assertTrue( Modifier.isPublic( sig.getModifiers().getBits() ) );
		assertTrue( sig.getEnumName().equals( "MyEnum" ) );
		assertEquals( 2, sig.getImplements().count() );
		assertEquals( "Serializable", sig.getImplements( 0 ).toString() );
		assertEquals( "Externalizable", sig.getImplements( 1 ).toString() );
		
		
		try
		{
			signature.of(
				"public static enum MyEnum implements Serializable,Externalizable");
		}
		catch( VarException ve )
		{
			
		}
		
		try
		{
			signature.of( " " );
			fail( "Expected exception " );
		}
		catch( VarException e )
		{
			
		}
		
		try
		{
			signature.of( "enum" );
			fail( "Expected exception " );
		}
		catch( VarException e )
		{
			
		}
	}
	
	public void testEnum()
	{
		_enum e = _enum.of( "enum E" );
		e.packageName("varcode.ex");
		//e.addValue( "A" );
		//e.addMember("public static final String rid = \"5\";");
		JavaCase jc = e.toJavaCase( );
		//System.out.println( jc );
		
		Class<?>enumClass = jc.loadClass();
		
		//enumClass = e.toJava( ).loadClass();
		assertEquals( "E", enumClass.getSimpleName() );		
		assertEquals( 0, enumClass.getEnumConstants().length );
		
		//System.out.println( Java.getFieldValue(enumClass.getEnumConstants()[0], "rid" ) );
		//System.out.println( Java.getFieldValue(enumClass, "rid" ) );		
	}
	
	public void testEnumStaticField()
	{
		_enum e = _enum.of( "public enum E" );
		e.packageName("varcode.ex");
		e.value( "A" );
		e.field("public final String rid = \"5\";");
		JavaCase jc = e.toJavaCase( );
		//System.out.println( jc );
		
		Class<?>enumClass = jc.loadClass();
		
		//enumClass = e.toJava( ).loadClass();
		assertEquals( "E", enumClass.getSimpleName() );		
		Object o = enumClass.getEnumConstants()[0];
		System.out.println( o );
		
		System.out.println( Java.getFieldValue(o, "rid" ) );
		//System.out.println( Java.getStaticField( enumClass, "rid" ) );		
	}
	
	
	public void testEnumWithValues()
	{
		_enum e = _enum.of( "enum E" ) ;
		assertEquals( 
			"enum E" + System.lineSeparator()+
			"{" + System.lineSeparator() +
			";" + System.lineSeparator() + System.lineSeparator() +
			"}", e.toString() );
		e.field( "final String message;" );
		e.constructor("E(String e)", "this.message = e;" );
		e.value( "_1", "\"One\"" );
		
		//System.out.println( e );
		
		Class<?>enumClass = e.toJavaCase().loadClass();
		assertTrue( enumClass.isEnum() );
		//Enum en = enumClass;
		assertEquals( 1, enumClass.getEnumConstants().length );
		assertEquals( "_1", enumClass.getEnumConstants()[ 0 ].toString() );
		
		
		//Note: here we are "updating" the enum (adduing a value)
		// AFTER we already loaded it once, (so we are loading a new 
		// Enum Class.
		e.value( "_2", "\"Two\"" );
		
		enumClass = e.toJavaCase().loadClass(); //load the new class with changes
		assertTrue( enumClass.isEnum() ); 
		//Enum en = enumClass;
		assertEquals( 2, enumClass.getEnumConstants().length );
		assertEquals( "_1", enumClass.getEnumConstants()[ 0 ].toString() );
		assertEquals( "_2", enumClass.getEnumConstants()[ 1 ].toString() );
		
	}
	
	public void testEnumWithMultiValues()
	{
		try
		{
			_enum.of("public enum");
			fail( "Expected exception " );
		}
		catch(VarException ve)
		{
			//expected
		}
		_enum e = _enum.of("public enum MultiValue");
		
		e.field("private final String name;");
		e.field("private final int age;");
		
		
		
		e.constructor("private MultiValue(String name, int age)", 
			"this.name = name;",
			"this.age = age;");
		
		e.value("ERIC", "\"Eric\"", 42 );
		e.value("MARK", "\"Mark\"", 54 );
		e.value("SALLY", "\"Sally\"", 34 );
		e.method("public String getName()", 
			"return this.name;");
		e.method("public int getAge()", 
			"return this.age;");
			
		System.out.println( e.author( ) );
		Class<?> c = e.toJavaCase( ).loadClass();
		assertTrue( c.isEnum() );
		assertEquals( 3, c.getEnumConstants().length );
		assertEquals( "Eric", Java.invoke( c.getEnumConstants()[0], "getName" ) );
		assertEquals( "Mark", Java.invoke( c.getEnumConstants()[1], "getName" ) );
		assertEquals( "Sally", Java.invoke( c.getEnumConstants()[2], "getName" ) );
		
		assertEquals( 42, Java.invoke( c.getEnumConstants()[0], "getAge" ) );
		assertEquals( 54, Java.invoke( c.getEnumConstants()[1], "getAge" ) );
		assertEquals( 34, Java.invoke( c.getEnumConstants()[2], "getAge" ) );		
	}
	
	public void testEnumWithStaticBlock()
	{
		_enum e = _enum.of("public enum WithPackageAndStaticBlock");
		e.staticBlock( "System.out.println(\"In Static block\");");
		e.packageName("ex.varcode.e");
		e.value( "A" );
		System.out.println( e.toJavaCase() );
		Class<?>enumClass = e.toJavaCase().loadClass();
		
		//we actually dont run the static block until AFTER accessing one of the Enum 
		// members (if we comment this out, "In static block" will not appear)
		Object o = enumClass.getEnumConstants()[0];
		assertEquals( "A" ,o.toString() );
		//System.out.println( );				
	}	
}
