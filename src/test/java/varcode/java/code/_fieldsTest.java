/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import junit.framework.TestCase;
import varcode.CodeAuthor;
import varcode.VarException;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _fieldsTest
    extends TestCase
{
    public void testEmpty()
    {
        _fields f = _fields.of( );
        assertEquals( 0, f.count() );
        assertEquals( null, f.byName("Name") );
        assertEquals( 0, f.fieldMap().size() );        
        assertEquals( "", f.toString() );        
    }
    
    public void testOne()
    {
        _fields f = _fields.of( "public int a;");
        assertEquals(1, f.count());
        assertEquals("public int a;", f.byName("a").toString() );
        assertTrue( f.fieldMap().containsKey( "a" ) );
        assertEquals( "public int a;", f.bind( VarContext.of() ) );        
    }
    
    public void testParameterized()
    {
        _fields f = _fields.of( "public int {+nme|vn+};");
        
        assertEquals("public int {+nme|vn+};", f.author( ).trim() );
        
        assertEquals( "public int vn;", f.bind( VarContext.of() ) );        
        
        assertEquals( "public int theName;", f.bind( 
            VarContext.of( "nme", "theName" ) ) );        
    }
     
    public void testBadModifiers()
	{
		try
		{
			_fields.of("public strictfp float myfloat;");
			fail("expected exception for bad modifier");
		}
		catch( VarException ve )
		{
			
		}
		
		try
		{
			_fields.of( "private volatile static final int y = 100;");
		}
		catch( VarException e)
		{
			//expected
		}
	}
	
	public static String N = System.lineSeparator();
	
	public void testField()
	{
		_fields._field f = _fields._field.of( "int x;" );		
		assertEquals( "int x;", f.toString() );
		
		f = _fields._field.of("doc", "int x;" );
		assertEquals( 
			"/**" + N +
			" * doc" + N +
			" */" + N +
			"int x;", f.toString() );
		
		f = _fields._field.of("int x = 100;");
		
		assertEquals( f.toString(), "int x = 100;" );
		
		
		f = _fields._field.of( "doc", "int x = 100;" );
		
		assertEquals( 
			"/**" + N +
			" * doc" + N +
			" */" + N +
			"int x = 100;", f.toString() );
        
        System.out.println("asdfasdKK");
        
	}
    
    public void testFields ()
    {
        _fields f = _fields.of("public int a;", "public int b;" );
        
        assertEquals(
            "public int a;" + System.lineSeparator()
           +"public int b;"+ System.lineSeparator(), f.author( ) );
        
        assertEquals(
            "    public int a;" + System.lineSeparator()
           +"    public int b;", f.author( CodeAuthor.INDENT ) );
        
        f = _fields.of("public static int a;", "public int b;" );
        
        assertEquals(
            "public static int a;" + System.lineSeparator()
           +"public int b;"+ System.lineSeparator(), f.author( ) );
        
        assertEquals(
            "    public static int a;" + System.lineSeparator()
           +"    public int b;", f.author( CodeAuthor.INDENT ) );
        
        assertNotNull( f.byName("a") );       
    }
}
