package varcode.java.macro;

import varcode.java.macro._autoMethodGetter;
import junit.framework.TestCase;
import varcode.java.lang._fields._field;
import varcode.java.lang._methods._method;
import static varcode.java.macro._autoGetterTest.N;

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
        _method m = _autoMethodGetter.of( _field.of( "public String name;" ) );
        
        assertEquals( 
            "public String getName(  )" + N +
            "{" + N +     
            "    return this.name;" + N + 
            "}", 
            m.toString() );
    }
    
    public void testNameType()
    {
        _method m = _autoMethodGetter.of( int.class,  "count" );
        
         assertEquals( 
            "public int getCount(  )" + N +
            "{" + N +     
            "    return this.count;" + N + 
            "}", 
            m.toString() );        
    }
}
