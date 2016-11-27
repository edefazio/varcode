package varcode.java.langmodel.auto;

import varcode.java.langmodel.auto._autoGetter;
import junit.framework.TestCase;
import varcode.java.langmodel._fields._field;
import varcode.java.langmodel._methods._method;
import static varcode.java.langmodel.auto._autoGetterTest.N;

/**
 *
 * @author eric
 */
public class _autoGetterTest
    extends TestCase
{
    public static String N = "\r\n";
    
    public void testA()
    {        
        _method m = _autoGetter.of( _field.of( "public String name;" ) );
        
        assertEquals( 
            "public String getName(  )" + N +
            "{" + N +     
            "    return this.name;" + N + 
            "}", 
            m.toString() );
    }
    
    public void testNameType()
    {
        _method m = _autoGetter.of( int.class,  "count" );
        
         assertEquals( 
            "public int getCount(  )" + N +
            "{" + N +     
            "    return this.count;" + N + 
            "}", 
            m.toString() );        
    }
}
