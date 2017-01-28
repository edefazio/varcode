package usecase.model;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.model._interface;

public class ModelInterface
    extends TestCase
{
    static _interface _ModelOfInterface = _interface.of(
        "package usecase.model;",
        "import java.util.UUID;",
        "import java.io.Serializable",
        "/** Interface Javadoc comment */",
        "@Deprecated",
        "public interface ModelOfInterface<P> extends Serializable" )
        .field( "public static final long serialVersionUID = 4L;" )        
        .field(
            "/** Field JavaDoc */",
            "@Deprecated",
            "public static final int staticField = 101;" )
        .method(  "/** Javadoc */",
            "@Deprecated",
            "public void soundOff();")
        .method(
            "/** Default Method Javadoc*/",
            "@Deprecated",
            "public default String getId( String prefix )",
            "return prefix + UUID.randomUUID().toString();" );
    
    public static void main(String[] args )
    {
        Export.dir( "C:\\temp\\java\\" ).toFile( _ModelOfInterface );
        Class interfaceClass = _ModelOfInterface.loadClass();
        
        //to class file "C:/temp/classes/usecase.model.ModelOfInterface.class"
        Export.dir( "C:\\temp\\classes\\").toFile( interfaceClass );
    }
    public void testUseInterfaceModel()
    {
        String javaSource = _ModelOfInterface.author();
        System.out.println( javaSource );
        
        Export.dir( "C:\\temp\\java\\" ).toFile( _ModelOfInterface );
        
        Class interfaceClass = _ModelOfInterface.loadClass();
        
        assertEquals( 101, Java.get( interfaceClass, "staticField" ) );        
    }        
}
