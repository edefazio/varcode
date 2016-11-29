package varcode.java.lang.auto;

import varcode.java.metalang.auto._autoGetter;
import junit.framework.TestCase;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;
import static varcode.java.lang.auto._autoGetterTest.N;

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
