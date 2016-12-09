package howto.java.author;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.lang._class;

public class Author_class
    extends TestCase
{
    public void testAuthor()
    {
        _class _c = _class.of( "public class AdHocClass" )
            .method( "public String toString()",
                "return \"Hello World\";" );
        
        System.out.println( _c.author() ); //print the source code      
        
        Object adHocInstance = _c.instance(); //create a new instance
        
        System.out.println( //call toString() on the 
            _Java.invoke( adHocInstance, "toString" ) ); 
    }
}
