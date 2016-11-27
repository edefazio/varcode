package tutorial.varcode.chapx.appendix;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.langmodel._class;

/**
 *
 * @author Eric DeFazio
 */
public class _2_CompileLoadAndRunAuthoredStaticMethod
    extends TestCase
{
    public static void main( String[] args )
    {
        new _2_CompileLoadAndRunAuthoredStaticMethod()
            .testAuthorCompileLoadAndRunClass();
    }
    
    public void testAuthorCompileLoadAndRunClass()
    {
        Class authoredClass = _class.of( "tutorial.varcode.c1.author", 
            "public class Authored" )
            .method( "public static String createId()",
                "return UUID.randomUUID().toString();")
            .imports( UUID.class )
            .loadClass(); //compile & load class w/o writing the .java file
        
        String id = (String)Java.invoke( authoredClass, "createId" );
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
    //       AdHocClassLoader.  
    // 3) Java.invoke( authoredClass, "createId" ) will call .createId() static 
    //   method on "...Authored.class" and return result as an Object
}
