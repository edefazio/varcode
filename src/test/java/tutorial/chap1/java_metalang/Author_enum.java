package tutorial.chap1.java_metalang;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._enum;

public class Author_enum
    extends TestCase
{
    public void testAuthor()
    {
        _enum _e = _enum.of( "public enum AdHocEnum" )
            .method( "public static String sayHello()",
                "return \"Hello\";" );
        
        System.out.println( _e.author() );        
        
        System.out.println( //invoke static method on the enum Class
            _Java.invoke( _e.loadClass(), "sayHello" ) );         
    }
}
