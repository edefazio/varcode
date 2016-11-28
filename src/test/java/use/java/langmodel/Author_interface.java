package use.java.langmodel;

import junit.framework.TestCase;
import varcode.java.langmodel._interface;

/**
 *
 * @author Eric
 */
public class Author_interface 
    extends TestCase
{
    public void testAuthor()
    {
        _interface _i = 
            _interface.of( "public interface GenId" )
                .method( "public String genId()" );
        
        //load the class for the interface
        Class adHocInterface = _i.loadClass();
        
        assertEquals( "AdHocClassLoader", 
            adHocInterface.getClassLoader().getClass().getSimpleName() );
        
    }
    
}
