package tutorial.varcode.chapx.appendix;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.lang._class;

/**
 *
 * @author Eric DeFazio
 */
public class _3_CompileAndRunAuthoredInstance 
    extends TestCase
{

    static JavaCase _GuidPrefix = _class.of( "tutorial.varcode.c1.author",
        "public class GuidPrefix implements Serializable" )
        .imports( UUID.class, Date.class, Serializable.class )
        .field( "private final String prefix;" )
        .field( "private static transient final Date authorDate = new Date();" )  
        .constructor( "public GuidPrefix( String prefix )",
            "this.prefix = prefix;" )
        .method( "public String createGuid()",
            "return this.prefix + UUID.randomUUID().toString();" )
        .toJavaCase();
            
    public void testCompileNewInstanceAndRun()
    {
        Object instance = _GuidPrefix.instance( "prefix" );
        String guid = (String)Java.invoke( instance, "createGuid" );
        assertTrue( guid.startsWith( "prefix" ) );
    }
    
    public static void main( String[] args )
    {
        new _3_CompileAndRunAuthoredInstance().testCompileNewInstanceAndRun();        
    }
    
    
    //concepts:
    // 1) a _class can signify it implements an interface or extends another 
    //   class by passing in
    //   "... implements XXXX, YYYY" 
    //   -or- 
    //   "... extends ZZZZ" in the signature.
    // 2) .field will parse the input "private final String prefix;" to create 
    //    the _field model and add it to the _class model
    // 3) .field will create a static or instance fields on the _class model
    // 4) fields can be pre-initialized "authorDate = new Date();"
    // 5) .constructor will build a _constructor model for the _class, 
    //    containing a signature : "public GuidPrefix( String prefix )" 
    //    and a body : "this.prefix = prefix;"
    // 6) JavaCase has an .instance( ... ) method for constructing instances of
    //    authored classes.  It will:
    //        compose "tutorial.varcode.c1.author.GuidPrefix.java"
    //        compile "tutorial.varcode.c1.author.GuidPrefix.class" using javac 
    //        load "tutorial.varcode.c1.author.GuidPrefix.class" in a new AdHocClassLoader
    //        call the ...GuidPrefix constructor with( "prefix" ) args; returning a new instance
    // 7) Java .invoke(...) method calls instance methods with any args    
}