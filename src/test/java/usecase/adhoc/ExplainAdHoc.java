package usecase.adhoc;

import java.util.UUID;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
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
public class ExplainAdHoc
{
    public static void main(String[] args)
    {
        /**1) build a model */
        _class _c = _class.of(
            "package example.java.adhoc;",
            "public class AnAdHocClass")
            .imports( UUID.class )
            .method( "public String createId()",
                "return UUID.randomUUID().toString();" );
        
        //**2) author an AdHocJavaFile (so we can compile it)
        AdHocJavaFile adHocJavaFile = _c.toJavaFile(  ); 
        
        //**3) Compile and Load the Java file into a new AdHocClassLoader
        AdHocClassLoader adHocClassLoader = AdHoc.compile( adHocJavaFile );
        
        //**4) return the Loaded Class 
        Class adHocClass  = adHocClassLoader.getAdHocClassBySimpleName( _c.getName() );
        
        //**5) create a new instance of the Class
        Object adHocInstance = Java.instance( adHocClass );
        
        //**6) invoke a method on the adHocInstance
        String id = (String)Java.call( adHocInstance, "createId" );
        
        System.out.println( "Id created" + id );
    }    
    
}
