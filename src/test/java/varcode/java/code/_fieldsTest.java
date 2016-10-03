/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.lib.text.Prefix;
import varcode.java.code._fields._field;

/**
 *
 * @author eric
 */
public class _fieldsTest
    extends TestCase
{
    public void testBindInField()
    {
        _field f = _field.of( "public {+type+} {+name+}" );
        f.bindIn(VarContext.of( 
            "type", String.class, "name", "firstName") );
        assertEquals( "public String firstName;", f.toString() );
        
        f = _field.of( "public {+type+} {+name+}" );
        f.annotate("@{+ann+}");
        f.bindIn(VarContext.of( 
            "ann", "Deprecated", "type", String.class, "name", "firstName") );
        assertEquals( 
            "@Deprecated" + N +
            "public String firstName;", f.toString() );        
    }
    
    public void testBindInFields()
    {
        _fields fs = new _fields();
        fs.addFields( 
            _field.of("public int A"), 
            _field.of("public String {+name+}") );
        
        assertEquals( 
            "public int A;" + N +
            "public String {+name+};" + N, fs.toString() );       
        
        fs.bindIn( VarContext.of( "name", "idNumber" ) );
        
        assertEquals( 
            "public int A;" + N +
            "public String idNumber;" + N, fs.toString() );       
        
    }
    
    public void testAnnotate()
    {
        _field f = _field.of( "public int f;" ).annotate( "@Persistent" );
        System.out.println( f );
        assertEquals( 
            "@Persistent" + N +
            "public int f;", f.toString() );
    }
    
    public void testEmpty()
    {
        _fields f = _fields.of( );
        assertEquals( 0, f.count() );
        assertEquals( null, f.getByName( "Name" ) );
        
        assertEquals( "", f.toString() );        
    }
    
    public void testOne()
    {
        _fields f = _fields.of( "public int a;" );
        assertEquals(1, f.count());
        assertEquals( "public int a;", f.getByName( "a" ).toString() );
        assertFalse( f.canAddFieldName( "a" ) );
        assertEquals( "public int a;", f.bind( VarContext.of() ) );        
    }
    
    public void testParameterized()
    {
        _fields f = _fields.of( "public int {+nme|vn+};");
        
        assertEquals( "public int {+nme|vn+};", f.author( ).trim() );
        
        assertEquals( "public int vn;", f.bind( VarContext.of() ) );        
        
        assertEquals( "public int theName;", f.bind( 
            VarContext.of( "nme", "theName" ) ) );        
    }
     
    public void testBadModifiers()
	{
		try
		{
			_fields.of( "public strictfp float myfloat;" );
			fail( "expected exception for bad modifier" );
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
           +"    public int b;", f.author( Prefix.INDENT_4_SPACES ) );
        
        f = _fields.of("public static int a;", "public int b;" );
        
        assertEquals(
            "public static int a;" + System.lineSeparator()
           +"public int b;"+ System.lineSeparator(), f.author( ) );
        
        assertEquals(
            "    public static int a;" + System.lineSeparator()
           +"    public int b;", f.author( Prefix.INDENT_4_SPACES ) );
        
        assertNotNull( f.getByName("a") );       
    }
}
