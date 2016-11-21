package quickstart.java;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.lang._class;

/**
 * "Authoring" Java code in varcode is building the .java source code
 * using "LangModels" (classes in  <CODE>varcode.java.lang</CODE>).
 * 
 * <B>*1*</B> To build source code for a Java class, we build a 
 * <CODE>_class</CODE> (model). We can convert the <CODE>_class</CODE>
 * to a String representing the .java source code by calling the 
 * <CODE>author();</CODE> method.
 * 
 * <B>*2*</B>
 * You can load a Class based on the _class model by calling loadClass(). 
 * It will :
 * <OL>
 *  <LI>author the .java code from the _class 
 *  <LI>compile the .java code to a .class using Javac
 *  <LI>create a new <CODE>AdHocClassLoader</CODE> and load the .class
 *  <LI>return a runtime reference to the loaded .class
 * </OL>
 * 
 * <B>*3*</B>
 * You can create a new instance of a Class based on the _class model 
 * by calling the <CODE>instance(...)</CODE> method. 
 * It will :
 * <OL>
 *  <LI>author the .java code from the _class 
 *  <LI>compile the .java code to a .class using Javac
 *  <LI>create a new <CODE>AdHocClassLoader</CODE> and load the .class
 *  <LI>create a new instance of the AdHocClass passing in any arguments
 * </OL>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _1_AuthorCode
    extends TestCase
{
    /*1: Author .java source code */
    public void testAuthorCode()
    {
        String code = _class.of( "quickstart.java", 
            "public class Authored" )            
            .method( "public static int getCount()", 
                "return 100;" )
            .author();
        
        System.out.println( code );
    }
    
    /*2: Author load & invoke method on a dynamic Java Class */ 
    public void testDynamicClass()
    {
        Class dynamicClass = _class.of( "quickstart.java", 
            "public class Authored" )
            .method( "public static int getCount()", 
                "return 100;" )
            .loadClass();
        //invoke a static mehod on a dynamically authored class
        assertEquals( Java.invoke( dynamicClass, "getCount" ), 100 );
    }
    
    /*3: Author load & create new instance of a dynamic Java Class */ 
    public void testDynamicInstance()
    {
        //create a new instance of a dynamically authored class
        Object dynamicInstance = _class.of( "quickstart.java", 
            "public class Authored" )
            .method( "public String toString()", 
                "return \"Hello World!\";" )
            .instance();
       
       assertEquals( "Hello World!", dynamicInstance.toString() );
    }
}
