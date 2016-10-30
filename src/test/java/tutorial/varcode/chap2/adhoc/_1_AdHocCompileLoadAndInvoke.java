package tutorial.varcode.chap2.adhoc;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 * Java AdHoc  
 * 
 * @author Eric DeFazio
 */
public class _1_AdHocCompileLoadAndInvoke
    extends TestCase
{
    public static final _class _withStaticMethod =
        _class.of( "tutorial.varcode.chap1.model", 
            "public final class StaticCreateId" )
            .method( "public static String doCreateId()",
                "return UUID.randomUUID().toString();" )
            .imports( UUID.class );
    //MACLI 
    // Model the _class entity 
    // Author the (.java) source code from the _class model
    // Compile the (.java) source code to a (.class)
    // Load the (.class) in a new AdHocClassLoader 
    // Invoke the static method "doCreate()" on the AdHocClass 
    public void testAdHocInvokeStaticMethod()
    {
        Class adHocClass = 
            _withStaticMethod.loadClass(); //author .java code, then compile &
        
        String id = 
            (String)Java.invoke( adHocClass, "doCreateId" );
        
        assertNotNull( id );
        
        System.out.println( id ); 
    }
    
    public static _class _withInstanceMethod = _class.of( "tutorial.varcode.chap1.model", 
        "public final class PrefixCreateId" )
        .field( "public final String prefix;" )
        .constructor("public PrefixCreateId( String prefix )",
            "this.prefix = prefix;" )
        .method( "public String createId()",
            "return this.prefix + UUID.randomUUID().toString();" )
        .imports( UUID.class );
    
    //MACLII
    // Model the _class entity 
    // Author the (.java) source code from the _class model
    // Compile the (.java) source code to a (.class)
    // Load the (.class) in a new AdHocClassLoader 
    // Instatiate a new instance of the AdHocClass
    // Invoke the static method "doCreate()" on the AdHocClass  
    public void testAdHocInvokeInstanceMethod()
    {        
        Object instance = //author, compile, load & instantiate a new AdHocClass
            _withInstanceMethod.instance( "pre" );//pass in "pre" to constructor
                                    
        String id = //invoke "createId" method on the "AdHoc" instance
            (String)Java.invoke( instance, "createId" );
        
        assertTrue( id.startsWith( "pre" ) );
        System.out.println( id ); 
    }
    // concepts:
    // 1) when creating a _class you can pass in the packageName as the 
    //    first parameter ("ex.varcode.tutorial")
    // 2) calling .loadClass() on the _class model will: 
    //    a) compose the .java code for the model into a JavaCase
    //    b) call the Java compiler (javac) on the JavaCase (.java code) 
    //       to generate "ex.varcode.tutorial.Authored.class"
    //    c) load "ex.varcode.tutorial.Authored.class" in a new 
    //    "child" ClassLoader.  
    // 3) Java.invoke( authoredClass, "createId" ) will call .createId() static 
    //   method .createId() on "Authored.class" and return result as an Object
}
