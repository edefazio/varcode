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
import varcode.java.lang._literal;
import varcode.java.lang._enum;
import varcode.java.lang._parameters;
import varcode.java.lang._class;
import varcode.java.lang._interface;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import varcode.VarException;
import varcode.java._Java;
import varcode.java.JavaCase;
import varcode.java.lang._constructors._constructor;
import varcode.java.lang._enum._signature;
import varcode.java.lang._enum._constants;
import varcode.java.lang._enum._constants._constant;
import varcode.java.lang._parameters._parameter;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _enumTest
    extends TestCase
{
    public static final String N = "\r\n";
    
    public void testNoValues()
    {
        _enum e = _enum.of("enum Empty");
        
        System.out.println( e.toString() );
        assertEquals(
            "enum Empty" + N +
            "{" + N 
          + ";" + N + N
          + "}" ,e.toString() );
        
        assertEquals( 0, e.getConstructors().count() );
        e.getContext();
        e.getDom();
        assertEquals( "Empty", e.getFullyQualifiedClassName() );
        assertEquals( 0, e.getImports().count() );
        assertTrue( e.getJavadoc().isEmpty() );
        assertTrue( e.getMethods().isEmpty() );
        assertTrue( e.getNesteds().count() == 0 );
        assertTrue( e.getNesteds().isEmpty() );
        assertTrue( e.getPackage().isEmpty() );
        assertEquals( "Empty", e.getSignature().getName() );
        assertEquals( 0, e.getSignature().getImplements().count() );
        assertTrue( e.getSignature().getImplements().isEmpty() );
        assertEquals( 0, e.getSignature().getModifiers().count() );
        
        assertTrue( e.getStaticBlock().isEmpty() );
        assertEquals( 0, e.getValueConstructs().count() );     
        
        Class enumClazz = e.toJavaCase( ).loadClass();
        
        assertEquals( "Empty", enumClazz.getCanonicalName() );        
    }

      public void testStringInit()
    {
        _enum _e = _enum.of( "public enum A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        
        _e = _enum.of( "io.varcode", "public enum A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackageName() );
        
        _e = _enum.of( "/*comment*/", "public enum A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains( "public" ) );        
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        
        _e = _enum.of( "/*comment*/", "io.varcode", "public enum A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackageName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        
        
        _e = _enum.of( "/*comment*/", "@Deprecated", "io.varcode", "public enum A" );
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().contains("public" ) );        
        assertEquals( "io.varcode", _e.getPackageName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        
        _e = _enum.of( "/*comment*/", "@Deprecated", "io.varcode", 
            "public enum A", 
            Map.class, Date.class );
        
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().containsAll( "public" ) );        
        assertEquals( "io.varcode", _e.getPackageName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _e.getImports().containsAll( Map.class, Date.class ) ); 
        
        _e = _enum.of( 
            "/**comment*/", 
            "@Deprecated", 
            "io.varcode", 
            "public enum A", 
            Map.class, Date.class,
            _fields._field.of( "public static final int ID = 100;" )
                .javadoc( "comment" ),
            _methods._method.of( "public String doIt" )    
            );
        
        assertEquals( "A", _e.getName());
        assertTrue( _e.getModifiers().containsAll( "public" ) );        
        assertEquals( "io.varcode", _e.getPackageName() );
        assertEquals( "comment", _e.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _e.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _e.getImports().containsAll( Map.class, Date.class ) ); 
        assertEquals( 1, _e.getFields().count() );
        assertEquals( 1, _e.getMethods().count() );        
    } 
    
    public void testJDocClass()
    {
        _enum _c = _enum.of( 
            _package.of( "io.varcode" ),
            _imports.of( Map.class, Date.class ),
            _javadoc.of( "comment" ),             
            _annotations._annotation.of( "@Deprecated" ),            
            "public enum A"                         
            );
        assertEquals( "A", _c.getName());
        assertTrue( _c.getModifiers().contains( "public" ) );        
        assertEquals( "io.varcode", _c.getPackageName() );
        assertEquals( "comment", _c.getJavadoc().getComment() ); 
        assertEquals( "@Deprecated ", _c.getAnnotations().getAt( 0 ).toString() );
        assertTrue( _c.getImports().containsAll( Map.class, Date.class ) );
    }
    
    public void testSingleValue()
    {
        _enum e = _enum.of("enum One").constant( "IT" );
        
        System.out.println( e.toString() );
        assertEquals(
            "enum One" + N +
            "{" + N 
          + "    IT;" + N + N
          + "}" ,e.toString() );
        
        assertEquals( 0, e.getConstructors().count() );
        e.getContext();
        e.getDom();
        assertEquals( "One", e.getFullyQualifiedClassName() );
        assertEquals( 0, e.getImports().count() );
        assertTrue( e.getJavadoc().isEmpty() );
        assertTrue( e.getMethods().isEmpty() );
        assertTrue( e.getNesteds().count() == 0 );
        assertTrue( e.getNesteds().isEmpty() );
        assertTrue( e.getPackage().isEmpty() );
        assertEquals( "One", e.getSignature().getName() );
        assertEquals( 0, e.getSignature().getImplements().count() );
        assertTrue( e.getSignature().getImplements().isEmpty() );
        assertEquals( 0, e.getSignature().getModifiers().count() );
        
        assertTrue( e.getStaticBlock().isEmpty() );
        assertEquals( 1, e.getValueConstructs().count() );    
        
        //load it as a Class
        Class enumClazz = e.toJavaCase( ).loadClass();
        
        assertEquals( "One", enumClazz.getCanonicalName() );        
        
        Enum en = (Enum)enumClazz.getEnumConstants()[ 0 ];
        assertEquals( 0, en.ordinal() );
        assertEquals( "IT", en.name() );        
    }
    
    /** Test an Enum Full of Stuff */
    public void testFullEnum()
    {
        _enum e = _enum.of(
            "ex.varcode.e", "public enum FullEnum implements Serializable" )
        .imports( Serializable.class )
        .field( "public static final int ID = 1;")
        .field( "private final String name;" )        
        .staticBlock( "System.out.println(ID);" )        
        .constructor( "private FullEnum(String name)", "this.name = name;" )
        .constant( "A", _literal.of( "a" ) )
        .constant( "B", _literal.of( "b" ) )
        .method( "public String getName()", "return this.name;" )
        .nest(
            _interface.of( "public interface Marker" )
                .field( "public static final String GUID = UUID.randomUUID().toString();" )
                .imports( UUID.class) //test that a nested import "trikles up" to the top level class
            );
        
        //verify the constructors
        assertEquals( 1, e.getConstructors().count() );
        _constructor cons = e.getConstructors().getAt( 0 );
        
        assertEquals("FullEnum", cons.getSignature().getClassName() );
        assertTrue( cons.getSignature().getModifiers().contains("private") );        
        _parameters params = cons.getSignature().getParameters();
        assertEquals( 1, params.count() );
        _parameter p = params.getAt( 0 );
        assertEquals( "name", p.getName() );
        assertEquals( "String", p.getType() );
        
        //now author, compile and load the enum
        Class enumClass = e.toJavaCase( ).loadClass();
        
        assertEquals("ex.varcode.e.FullEnum", enumClass.getCanonicalName());
        assertEquals( 5, enumClass.getDeclaredFields().length );
        //assertEquals( 1, enumClass.getConstructors().length );
        assertEquals( 2, enumClass.getEnumConstants().length );
        
        Enum e1 = (Enum) enumClass.getEnumConstants()[0];
        Enum e2 = (Enum) enumClass.getEnumConstants()[1];
        
        assertEquals( "A",e1.name() );
        assertEquals( "B",e2.name() );
        
        assertEquals( "a", _Java.invoke( e1, "getName" ) );
        assertEquals( "b", _Java.invoke( e2, "getName" ) );
        
        
        //check nested classes
        assertEquals( 1, enumClass.getDeclaredClasses().length );
        
        Class nestedInterface = enumClass.getDeclaredClasses()[0];
        //verify I can get the field value from the nested interface
        assertNotNull( _Java.getFieldValue( nestedInterface, "GUID" ) );        
    }    
    
    public void testNest()  
    {
	//NOTE: the nested class imports UUID
	// which is "propagated" to TopLevel class
	_enum e = _enum.of( "ex.varcode.model", 
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
		_constants es = new _constants();
		
		//enums CAN have no enumerated values
		assertEquals( ";" + System.lineSeparator(), es.toString() );
		
		es.addConstant(_constant.of( "A" ) );
		
		assertEquals( "    A;" + System.lineSeparator(), es.toString() );
		
		es.addConstant(_constant.of( "B" ) );
		assertEquals( "    A," + System.lineSeparator() + 
				      "    B;" + System.lineSeparator(), es.author( ) );
		
		try
		{
			es.addConstant(_constant.of( "A" ) );
			fail("expected Exception for adding duplicate");
		}
		catch(VarException ve)
		{
			//expected
		}
		
		//here the enum C calls a constructor with arguments ("Hey" and 1)
		es.addConstant(_constant.of( "C", "\"Hey\"", 1 ) );
		
		assertEquals( "    A," + System.lineSeparator() + 
			          "    B," + System.lineSeparator() +
			          "    C( \"Hey\", 1 );"+ System.lineSeparator(), es.author( ) );
		
		//System.out.println( es.toCode( SelfAuthored.INDENT ) );
	}
	
	public void testSignature()
	{
		_signature sig = _signature.of(
				"enum MyEnum");
	
		assertEquals( "enum MyEnum", sig.toString() );
	
		sig = _signature.of(
				"public enum MyEnum implements Serializable,Externalizable");
			
		assertTrue( Modifier.isPublic( sig.getModifiers().getBits() ) );
		assertTrue( sig.getName().equals( "MyEnum" ) );
		assertEquals( 2, sig.getImplements().count() );
		assertEquals( "Serializable", sig.getImplements( 0 ).toString() );
		assertEquals( "Externalizable", sig.getImplements( 1 ).toString() );
		
		
		try
		{
			_signature.of(
				"public static enum MyEnum implements Serializable,Externalizable");
		}
		catch( VarException ve )
		{
			
		}
		
		try
		{
			_signature.of( " " );
			fail( "Expected exception " );
		}
		catch( VarException e )
		{
			
		}
		
		try
		{
			_signature.of( "enum" );
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
		e.constant( "A" );
		e.field("public final String rid = \"5\";");
		JavaCase jc = e.toJavaCase( );
		//System.out.println( jc );
		
		Class<?>enumClass = jc.loadClass();
		
		//enumClass = e.toJava( ).loadClass();
		assertEquals( "E", enumClass.getSimpleName() );		
		Object o = enumClass.getEnumConstants()[0];
		System.out.println( o );
		
		System.out.println( _Java.getFieldValue(o, "rid" ) );
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
		e.constant( "_1", "\"One\"" );
		
		//System.out.println( e );
		
		Class<?>enumClass = e.toJavaCase().loadClass();
		assertTrue( enumClass.isEnum() );
		//Enum en = enumClass;
		assertEquals( 1, enumClass.getEnumConstants().length );
		assertEquals( "_1", enumClass.getEnumConstants()[ 0 ].toString() );
		
		
		//Note: here we are "updating" the enum (adduing a value)
		// AFTER we already loaded it once, (so we are loading a new 
		// Enum Class.
		e.constant( "_2", "\"Two\"" );
		
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
		
		e.constant("ERIC", "\"Eric\"", 42 );
		e.constant("MARK", "\"Mark\"", 54 );
		e.constant("SALLY", "\"Sally\"", 34 );
		e.method("public String getName()", 
			"return this.name;");
		e.method("public int getAge()", 
			"return this.age;");
			
		System.out.println( e.author( ) );
		Class<?> c = e.toJavaCase( ).loadClass();
		assertTrue( c.isEnum() );
		assertEquals( 3, c.getEnumConstants().length );
		assertEquals( "Eric", _Java.invoke( c.getEnumConstants()[0], "getName" ) );
		assertEquals( "Mark", _Java.invoke( c.getEnumConstants()[1], "getName" ) );
		assertEquals( "Sally", _Java.invoke( c.getEnumConstants()[2], "getName" ) );
		
		assertEquals( 42, _Java.invoke( c.getEnumConstants()[0], "getAge" ) );
		assertEquals( 54, _Java.invoke( c.getEnumConstants()[1], "getAge" ) );
		assertEquals( 34, _Java.invoke( c.getEnumConstants()[2], "getAge" ) );		
	}
	
	public void testEnumWithStaticBlock()
	{
		_enum e = _enum.of("public enum WithPackageAndStaticBlock");
		e.staticBlock( "System.out.println(\"In Static block\");");
		e.packageName("ex.varcode.e");
		e.constant( "A" );
		System.out.println( e.toJavaCase() );
		Class<?>enumClass = e.toJavaCase().loadClass();
		
		//we actually dont run the static block until AFTER accessing one of the Enum 
		// members (if we comment this out, "In static block" will not appear)
		Object o = enumClass.getEnumConstants()[0];
		assertEquals( "A" ,o.toString() );
		//System.out.println( );				
	}	
}
