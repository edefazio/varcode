package varcode.java.model.auto;

import varcode.java.model.auto._autoGetter;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import static varcode.java.model.auto._autoGetterTest.N;

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
    
    public void testArrayField()
    {
        _field f = _field.of( "public int[] f;" );
        _method m = _autoGetter.ofArrayIndex( f );
        assertEquals( 
        "public int getFAt( int index )" + N + 
        "{"+ N +             
        "    if( this.f == null )"+ N + 
        "    {"+ N + 
        "        throw new IndexOutOfBoundsException( \"f is null\" );"+ N +   
        "    }" + N + 
        "    if( index < 0 || index > this.f.length  )" + N + 
        "    {" + N + 
        "        throw new IndexOutOfBoundsException(" + N + 
        "            \"index [\" + index + \"] is not in range [0...\" + f.length + \"]\" );" + N +
        "    }" + N + 
        "    return this.f[ index ];" + N +            
        "}", m.toString() );
        
        _class c = _class.of("A")
            .field( "private final int[] count = new int[]{1,2,3};" );
        
        //create and add a getter method for the count array field
        c.method( 
            _autoGetter.ofArrayIndex( c.getFields().getByName( "count" ) ) );
        
        assertEquals( 1, Java.invoke( c.instance( ), "getCountAt", 0 ) );        
    }
   
}
