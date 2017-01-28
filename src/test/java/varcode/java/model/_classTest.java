package varcode.java.model;

import varcode.java.model._class;
import varcode.java.model._imports;
import java.io.Serializable;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.lang.RefRenamer;
import varcode.java.model._annotations._annotation;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class _classTest
    extends TestCase
{
    
    public void testClassEqualsHashCode()
    {
        _class _c = _class.of( "public class A");
        _class _ccopy = _class.of( "public class A");
        
        assertTrue( _c.equals( _ccopy ) );
        assertTrue( _ccopy.equals( _c ) );
        
        assertEquals( _c.hashCode(), _ccopy.hashCode() );
        
        _ccopy.setName( "A1" );
        assertFalse( _c.equals( _ccopy ) );
        assertFalse( _ccopy.equals( _c ) );
        
        assertTrue( _c.hashCode() != _ccopy.hashCode() );            
    }
    
    public void testClassEQHashCode()
    {
        _field _f = _field.of( "public static final int count;" );
        _field _f2 = _field.of( "public static final int count;" );
        
        assertEquals( _f, _f2 );
    }
    
        
    
    
    public void testClassEqualsHashcode2()
    {
        _class _c = Java._classFrom( ExampleClass.class );
        _class _s = _class.of( "@Deprecated",
            "/** Javadoc */",
            "package varcode.java.model;",
            _imports.of( UUID.class ),
            "public class ExampleClass")
            .add( 
                _field.of( _annotation.of( "@Deprecated"), 
                    "public static final int count = 1;" ),
                _method.of( "@Deprecated", 
                    "public String getStuff( int count )",
                    "System.out.println( \"Hi\" );",
                    "return \"STUFF\";"),
                _method.of( "@Override", "@Deprecated",
                    "public String toString()",                    
                    "return \"TOSTRING\";" )
            );
        System.out.println( _s );
        System.out.println( _c.hashCode() );
        System.out.println( _s.hashCode() );
        _c.equals( _s );
        assertEquals( _c, _s );
        assertEquals( _c.hashCode(), _s.hashCode() );
    }
    
    public void testRenameBug()
    {
        _class _c = _class.of("public class c").field( "class f = Javac.class");
        _c.setName( "BLAH" ); 
        
        assertEquals( " = Javac.class", _c.getFieldByName( "f" ).getInit().toString() );
    }
    
    public void testJRR()
    {
        _class _c = _class.of( "public class c" )
            .field( "class f = Javac.class")
            .field( "class b =c.class" )
            .field( "class c = c.class" )
            .field( "class d = c.class " );
        String s = _c.author();
        String XXX = RefRenamer.apply( s, "c", "XXXX" );
        System.out.println( XXX );
        
        XXX = RefRenamer.apply( " a aa aaa", "a", "XXX" );
        System.out.println( XXX );
        
        
        //change none
        XXX = RefRenamer.apply( "a", "c", "XXX" );
        assertEquals( XXX, "a" );
        
        //change one
        XXX = RefRenamer.apply( "a", "a", "XXX" );
        assertEquals( XXX, "XXX" );
        
        //change nothing
        XXX = RefRenamer.apply( "a aa aaa", "c", "XXX" );
        //System.out.println( XXX );
        assertEquals( XXX, "a aa aaa" );
        
        
        XXX = RefRenamer.apply( "a aa aaa", "a", "XXX" );
        //System.out.println( XXX );
        assertEquals( XXX, "XXX aa aaa" );
        
        
        //first AND last
        XXX = RefRenamer.apply( "a aa aaa a", "a", "XXX" );
        //System.out.println( XXX );
        assertEquals( XXX, "XXX aa aaa XXX" );
        
        XXX = RefRenamer.apply( "a.class aa aaa (a)", "a", "XXX" );
        //System.out.println( XXX );
        assertEquals( XXX, "XXX.class aa aaa (XXX)" );
        
        
        //lets create a big class with a bunch of stuff
        _class _ic = _class.of(
            "/* i is the greatest */",
            "@issues(\"license to ill\")",
            "public class c extends illogical implements focii" )
            .field( "public int i = 100;" );
        
        _ic.replace( "i", "XXX" );
        
        System.out.println( _ic );
    }
    
    
}
