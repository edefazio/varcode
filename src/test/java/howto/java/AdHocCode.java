package howto.java;

import varcode.java.model._class;

public class AdHocCode 
{
    public static void main( String[] args )
    {
        _class _c = _class.of( "public class AdHoc" )
            .method( "public String toString()", 
                "return \"Hello AdHoc\";" );
        
        System.out.println( _c.author() ); //write the Java code
        Object adHocInstance = _c.instance( ); //create a new instance
        System.out.println( adHocInstance ); //prints "Hello AdHoc"
    }
}
