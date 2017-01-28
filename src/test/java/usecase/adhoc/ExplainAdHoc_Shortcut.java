package usecase.adhoc;

import varcode.java.Java;
import varcode.java.model._class;

/**
 * varcode can compile load and use new code dynamically. 
 * 
 * Here we show the <B>Long form, Step By step instructions</B> for authoring
 * compiling, loading, resolving, instantiating and calling methods on an
 * Ad Hoc class at runtime.
 * 
 * NOTE: the API provides many shortcuts
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ExplainAdHoc_Shortcut
{
    public static void main(String[] args)
    {        
        /**
         * 1) create a _class model representing a class
         *    author the _class model to .java code
         *    compile the .java code to an AdHocJavaClass
         *    load the Class into a new AdHocClassLoader
         *    create and return a new instance of the Class
         */
        Object adHocInstance = Java.instance( 
            _class.of( "public class AnAdHocClass" )
            .method( "public String createId()",
                "return UUID.randomUUID().toString();" ) );
        
        
        //** 2) invoke a method on the adHocInstance
        String id = (String)Java.call( adHocInstance, "createId" );
        
        System.out.println( "Id created : " + id );
    }    
    
}
