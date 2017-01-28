package howto.java.tailor;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 * "Authoring" Java code in varcode is building the .java source code
 * using langmodels (_class, _enum, _interface) classes found in 
 * <CODE>varcode.java.langmodel</CODE>).
 * 
 * <B>*1*</B> Use the fluent _class API to build a <CODE>_class</CODE> 
 * (langmodel) representing the .java source code for a class.  
 * 
 * <B>*2</B>
 * Convert the _class model to .java source code by calling the 
 * <CODE>author();</CODE> method.
 * 
 * <B>*3*</B>
 * The _class langmodel can compile and load an "AdHoc" Class based on the 
 * _class model at runtime by calling the {@code loadClass()} method. 
 * It will :
 * <OL>
 *  <LI>author the .java code from the _class 
 *  <LI>compile the .java code to a .class <CODE>AdHocClass</CODE> using Javac
 *  <LI>create a new <CODE>AdHocClassLoader</CODE> and load the <CODE>AdHocClass</CODE>
 *  <LI>return a runtime reference to the loaded <CODE>AdHocClass</CODE>.
 * </OL>
 * **NOTE: this requires a JDK (not a JRE) to be in the classpath at runtime**
 * 
 * <B>*4*</B>
 * invoke (static) methods on the <CODE>AdHocClass</CODE>, with: 
 * <CODE>Java.invoke( adHocClass, "methodName", parameters... );</CODE>
 * ...it returns the result of invoking the static method.
 * 
 * <B>*5*</B>
 * To create a new "AdHoc" instance of an <CODE>AdHocClass</CODE> 
 * at runtime, call the <CODE>_class.instance(...)</CODE> method. 
 * (passing in any constructor parameters)
 * It will :
 * <OL>
 *  <LI>author the .java code from the _class 
 *  <LI>compile the .java code to a new <CODE>AdHocClass</CODE> using Javac
 *  <LI>create a new <CODE>AdHocClassLoader</CODE> and load the <CODE>AdHocClass</CODE>
 *  <LI>create and return a new instance of the <CODE>AdHocClass</CODE>.
 * </OL>
 *
 * <B>*5*</B>
 * To invoke an instance method on the <CODE>AdHocInstance</CODE>, use 
 * <CODE>Java.invoke( adHocInstance, "methodName", parameters... );</CODE>.
 * it will return the result of the method call.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AuthorNewCode_Describe
    extends TestCase
{    
    public void testAuthorUseAdHocCode()
    {
        /*1: build _class langmodel representing java source code  */
        _class _c = _class.of( "use.java.langmodel", 
            "public class Authored" )    
            .field( "public static final int ID = 100;" )
            .method( "public static int staticMethod()", 
                "return ID;" )
            .method( "public String instanceMethod( String param )", 
                "return param + ID;" );
            
        /*2: author .java source for this _class langmodel  */
        String adHocJavaCode = _c.author();
        System.out.println( adHocJavaCode );
        
        /*3: compile & load an adHocClass based on the _class */  
        Class adHocClass = _c.loadClass();
        
        /*4: call a static method on an AdHocClass */ 
        assertEquals( 100, Java.call( adHocClass, "staticMethod" ) );
        
        /*5: create a new adHocInstance of a _class */
        Object adHocInstance = _c.instance( );
        
        /*6: invoke an instance method */
        assertEquals( "ParamValue100", 
            Java.call( adHocInstance, "instanceMethod", "ParamValue" ) ); 
    }    
}
