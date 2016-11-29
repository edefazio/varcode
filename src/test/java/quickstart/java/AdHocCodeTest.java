package quickstart.java;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;

/**
 * varcode makes generating Java source code easy with the metalang API.
 * (code in varcode.java.metalang)
 * 
 * Meta Language models like {@code _class, _enum, and _interface} can also be 
 * compiled (using Javac), loaded (in a new {@code AdHocClassLoader}) and used 
 * in an ad-hoc manner <B>at runtime</B>.
 * 
 * NOTE: to compile / load / instantiate new instances, a JDK (not just a JRE) 
 * must be used at runtime, or {@code .instance()} will fail. Writing the source 
 * with {@code author()} will work with any JRE.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocCodeTest 
    extends TestCase
{
    public void testAdHocClass()
    {
        /* create a _class (langmodel) for the code */
        _class _c = _class.of( "public class AdHoc" )
            .method( "public String toString()", 
                "return \"Hello AdHoc\";" );
        
        /* write the code as a String (Optional) */
        System.out.println( _c.author() );
        
        /* compile, load and instantiate an new AdHoc instance */ 
        Object adHocInstance = _c.instance( );
        
        //invoke a method on the adHoc instance
        assertEquals( "Hello AdHoc", 
            _Java.invoke( adHocInstance, "toString" ) );
    }
}
