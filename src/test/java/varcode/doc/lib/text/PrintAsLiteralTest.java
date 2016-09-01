package varcode.doc.lib.text;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.lib.text.PrintAsLiteral;
import varcode.dom.Dom;
import varcode.java.Reflect;
import varcode.java.JavaCase;
import varcode.markup.codeml.CodeML;

public class PrintAsLiteralTest
	extends TestCase
{

	public void testSingle()
	{
		assertEquals( "\"A\"", PrintAsLiteral.printAsLiteral( "A" ) );
		assertEquals( "'A'", PrintAsLiteral.printAsLiteral( 'A' ) );
		assertEquals( "1", PrintAsLiteral.printAsLiteral( 1 ) );
		
		assertEquals( "1.0f", PrintAsLiteral.printAsLiteral( 1.0f ) );
		assertEquals( "3.14d", PrintAsLiteral.printAsLiteral( 3.14d ) );
		assertEquals( "43562L", PrintAsLiteral.printAsLiteral( 43562L ) );
		assertEquals( "true", PrintAsLiteral.printAsLiteral( true ) );
		
		//
		assertEquals( "(short)1", PrintAsLiteral.printAsLiteral( (short)1 ) );
		assertEquals( "(byte)1", PrintAsLiteral.printAsLiteral( (byte)1 ) );
		
		assertEquals( "\"A\\\"B\"", PrintAsLiteral.printAsLiteral( "A\"B" ) );
		//assertEquals( "'\u00EA'", PrintAsLiteral.printAsLiteral( '\u00ea' ) );
		
	}
	
	//verify that I can "write" the escaped unicode chars and Strings into a Field of a Class
	// and I can read them out (reflectively) and verify they are the same 
	public void testEscapeCharsByTailoring()
	{
		char unicode = '\u00EA';
		
		//create the code of a class
		Dom dom = CodeML.compile(
			"public class C { public static final {+type+} F = {+$lit[](value)+}; }" );
		
		Class<?> clazz = 
			JavaCase.of( "C", dom,  VarContext.of( "type", char.class, "value", unicode ) ).loadClass();
		
		char resolved = (Character)Reflect.getStaticFieldValue( clazz, "F" );
		assertEquals( unicode, resolved );
		
		char[] unicodeArray = new char[]{ '\u00ea', '\u00f1', '\u00fc' };
		
		Class<?> arrayClazz = 
			JavaCase.of( "C", dom,  VarContext.of( "type", char[].class, "value", unicodeArray ) ).loadClass();
		
		char[] resolvedArr = (char[])Reflect.getStaticFieldValue( arrayClazz, "F" );
		for( int i = 0; i < resolvedArr.length; i++ )
		{
			assertEquals( unicode, resolved );
		}
		
	}
	
	public void testEscapeStringsCharsByTailoring()
	{
		String unicode = '\u00EA'+ "";
		
		//create the code of a class (NOTE: $lit[]() is an alias the PrintLiteral function with array notation
		Dom dom = CodeML.compile(
			"public class C { public static final {+type+} F = {+$lit[](value)+}; }" );
		
		Class<?> clazz = 
			JavaCase.of( "C", dom, VarContext.of( "type", Object.class, "value", unicode ) ).loadClass();
		
		Object resolved = Reflect.getStaticFieldValue( clazz, "F" );
		assertEquals( unicode, resolved );
		
		//array with some unicode
		Object[] unicodeArray = new Object[]{ "\u00ea", '\u00f1', '\u00fc' };
		
		Class<?> arrayClazz = 
			JavaCase.of( "C", dom,  VarContext.of( "type", Object[].class, "value", unicodeArray ) ).loadClass();
		
		Object[] resolvedArr = (Object[])Reflect.getStaticFieldValue( arrayClazz, "F" );
		for( int i = 0; i < resolvedArr.length; i++ )
		{
			assertEquals( unicode, resolved );
		}		
	}
	
	public void testArrays()
	{
		assertEquals( "\"A\", \"B\"", PrintAsLiteral.printAsLiteral( new String[]{ "A", "B" } ) );
		assertEquals( "1, 2", PrintAsLiteral.printAsLiteral( new int[]{1,2} ) );
		assertEquals( "1.0f, 2.0f", PrintAsLiteral.printAsLiteral( new float[]{1.0f, 2.0f} ) );
		assertEquals( "3.14d, 'a', \"F\", true", PrintAsLiteral.printAsLiteral( new Object[]{3.14d, 'a', "F", true} ) );
		assertEquals( "true, false", PrintAsLiteral.printAsLiteral( new boolean[]{ true, false } ) );		
	}
	
	
	public void testCollections()
	{
		TreeSet<String>hs = new TreeSet<String>();
		hs.add( "A" );
		hs.add( "B" );
		
		assertEquals( "\"A\", \"B\"", PrintAsLiteral.printAsLiteral( hs ) );
	
		List<Integer> l = new ArrayList<Integer>();
		l.add(1);
		l.add(2);
		
		assertEquals( "1, 2", PrintAsLiteral.printAsLiteral( l ) );
		
	//	assertEquals( "1.0f, 2.0f", PrintAsLiteral.printAsLiteral( new float[]{1.0f, 2.0f} ) );
		//assertEquals( "3.14d, 'a', \"F\", true", PrintAsLiteral.printAsLiteral( new Object[]{3.14d, 'a', "F", true} ) );
		//assertEquals( "true, false", PrintAsLiteral.printAsLiteral( new boolean[]{ true, false } ) );		
	}
}
