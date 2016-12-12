package howto.java.author.detail;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._constructors._constructor;
import varcode.java.lang._enum;
import varcode.java.lang._fields._field;
import varcode.java.lang._methods._method;

/**
 * the varcode API wants to make developers productive and
 * provide a flexible and consistent API for readable code.
 * 
 * Ideally, we should be able to define (_class, _enum, and _interface) 
 * abstractions in a single "fluent-style" initializing statement 
 * (to avoid having to unnecessarily create static initializer blocks)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class APIShortcuts
    extends TestCase
{    
    /** 
     * you can just pass in the Annotation Class and we will know
     * it's an annotation
     */
    public void testAnnotationShortcut()
    {
        _class _c = _class.of( 
            Deprecated.class, //pass in an Annotation class   
            "public class MyClass",             
            _method.of( Override.class, //pass in an Annotation Class
                "public String toString()", 
                "return \"Hi\";" )
            );
        
        assertEquals( 
            _c.getAnnotations().author().trim(), "@java.lang.Deprecated" );
        
        assertEquals( _c.getMethodNamed("toString").getAnnotations().author().trim(), 
               "@java.lang.Override" );
        
        //System.out.println( _c.getAnnotations().getAt( 0 ) );
        System.out.println( _c );
        
        _c.instance( );
    }
    
    /**
     * varcode tries to discern what the tokens you pass in "mean"<BR><PRE>
     * ... String args that start with "package " are package declarations
     * ... String args that start with "/*" are javadocs
     * ... String args that start with "@" are annotations
     * 
     * ... Also, passing in any _facets (_fields, _methods, _constructors)
     * will add them to the _class model
     * </PRE>
     */
    public static _class _apiShort = _class.of(
        "package howto.java.author.detail;",      
        "/* API Shortcuts to create a _class */", 
        "@Deprecated",                            
        "@CustomAnnotation{a=100}",               
        
        "public class APIShortcut " +             
            "extends BaseClass implements Serializable, AnotherInterface",
        _field.of( "/* a number */",  //add a field w/ javadoc
            "@Deprecated",    
            "public final int count = 100;" ),
        _constructor.of( 
            "/* multi-line constructor comment " + System.lineSeparator() +
            "@param count the number" + System.lineSeparator() +        
            "@throws IOException" +        
            "*/",    
                
            "@Deprecated",    
            "public APIShortcut( int count ) throws IOException, CustomException",
            "this.count = count;" ),
        _method.of(  
            "/* method comment */", 
            "@Deprecated",    
            "public int getCount() throws MyException",
            "return this.count;" ),    
        
        _enum.of( //add a nexted enum to this class
            "/* Enum Javadoc */",    
            "@CustomAnnotation",    
            "public enum Nested" )
            .constant( "INSTANCE" )
            .imports( UUID.class )
            .field("public static final String ID = UUID.randomUUID().toString();")            
        );
    
    public void testIt()
    {
        
        assertEquals("APIShortcut", _apiShort.getName());
        assertEquals(" API Shortcuts to create a _class ", _apiShort.getJavadoc().getComment() );
        assertEquals(2, _apiShort.getAnnotations().count());
        assertEquals(1, _apiShort.getExtends().count());
        assertEquals(2, _apiShort.getImplements().count());
        
        
        _enum _e = (_enum)_apiShort.getNestedAt( 0 );
        assertEquals( " Enum Javadoc ", _e.getJavadoc().getComment() );
        assertEquals( 1, _e.getAnnotations().count() );
        assertEquals( "Nested", _e.getName() );
        assertTrue( _e.getModifiers().contains( "public" ) );
        assertEquals( "INSTANCE", _e.getConstants().getAt( 0 ).getName() );
        assertEquals( 0, _e.getConstants().getAt( 0 ).getArguments().count() );
        
        _method _m = _apiShort.getMethodNamed("getCount" );
        assertEquals(1, _m.getAnnotations().count());
        assertEquals( " method comment ", _m.getJavadoc().getComment() );
        assertEquals( 1, _m.getThrows().count() );
        assertEquals( "return this.count;", _m.getBody().toString() );
        
        _constructor _ctor = _apiShort.getConstructors().getAt( 0 );
        assertNotNull( _ctor.getJavadoc().getComment() );
        assertEquals( 1, _ctor.getAnnotations().count() );
        assertEquals( "APIShortcut", _ctor.getName());
        assertEquals( "int", _ctor.getParameters().getAt(0).getType());
        assertEquals( "count", _ctor.getParameters().getAt(0).getName());
        
        assertEquals( 2, _ctor.getThrows().count() );
        assertEquals( "this.count = count;", _ctor.getBody().toString() );
        
        _field _f = _apiShort.getField("count");
        assertEquals( "int", _f.getType() );
        assertEquals( " 100", _f.getInit().getCode() );
        assertEquals( " a number ", _f.getJavadoc().getComment() );
        assertEquals(1, _f.getAnnotations().count());
        assertTrue( _f.getModifiers().containsAll( "public", "final" ) );
        
                        
    }
}
