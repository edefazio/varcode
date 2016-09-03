package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.Java;

/**
 * Illustrates using varcode to dynamically 
 * author (and call) custom source at runtime
 */
public class FluentTest
    extends TestCase        
{
    public void testAuthorMeta()
    {
        System.out.println ( 
            Java.invoke(
                _class.of( "public class UUIDFactory" )
                    .method( "public static String genUUID()", 
                        "return java.util.UUID.randomUUID().toString();" )
                    .toJavaCase( ).loadClass( ), 
                "genUUID" ) );
    }
    
}
