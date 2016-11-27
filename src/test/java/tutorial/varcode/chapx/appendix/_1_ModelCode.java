
package tutorial.varcode.chapx.appendix;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.JavaCase;
import varcode.java.langmodel._class;

/**
 * "Authoring" means "generating" .java source code from scratch.  
 * varcode has "models" for java entities (_class, _interface, _enum )
 * These models are in the "varcode.java.code" package.
 * 
 * _class, _enum, _interface objects model entities at the source code level.  
 * They provide APIs for manipulating and building source code (Instead
 * of maintaining a large in memory String to represent the source code).  
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class _1_ModelCode
    extends TestCase
{
    public static void main( String[] args )
    {
        new _1_ModelCode().testModelCode();        
    }
    
    public void testModelCode()
    {
        JavaCase javaCase = _class.of( "public class Authored" )
            .method( "public static String createId()",
                "return UUID.randomUUID().toString();" )
            .imports( UUID.class )
            .toJavaCase();
        
        System.out.println( javaCase );
    }
    
    //concepts:
    // 1) JavaCase is the source code of a _class/_interface/_enum model
    // 2) _class represents the code model for a Java class, the string passed 
    //    in the constructor("public class Authored") is parsed, and the _class 
    //    _signature is created with the name and modifiers. 
    // 3) .method adds a _method to the _class model, it parses the _method
    //    signature from the first line: "public static String createId()": 
    //     (the modifiers, returnType, name, parameters)
    //    any lines after the first are the "body" of the method.
    // 4) .imports(), will add import statement(s) to the _class model
    // 5) calling System.out.println( javacase ); will print the .java code 
    
}
