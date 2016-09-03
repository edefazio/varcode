package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.Java;

/**
 * Illustrates using varcode to dynamically 
 * author (and call) custom source at runtime
 */
public class InLinerTest
    extends TestCase
{
    /**
     * <UL>
     * <LI>"authors" a new .java source file for a class
     * <LI>javac compiles the authored .java file to a .class. 
     * <LI>loads the  authored class (bytecode) into a new ClassLoader
     * <LI>creates/returns a new instance of the authored class
     * </UL>
     */
    public static final Object AuthoredUUIDFactory = 
        _class.of( "public class MyUUIDFactory" )
            .method( "public static String uuid()",
                "return java.util.UUID.randomUUID().toString()" )
            .toJavaCase( ).instance( );
    
    public void testAuthoredUUID()
    {
        //call the .uuid() method on the authored a number of times
        for( int i = 0; i < 100; i++ )
        {
            String aUUID = (String)Java.invoke( AuthoredUUIDFactory, "uuid" );
            assertTrue( aUUID.length() == 36 );
        }
    }
}
