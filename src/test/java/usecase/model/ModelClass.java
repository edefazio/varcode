package usecase.model;

import java.io.Serializable;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.model._class;

/**
 */
public class ModelClass
    extends TestCase
    implements Serializable
{
    
    static _class _ModelOfClass = _class.of(
        "package usecase.model;",
        "import java.util.UUID;",
        "import java.io.Serializable",
        "/** Class Javadoc comment */",
        "@Deprecated",
        "public class ModelOfClass<K,V> implements Serializable" )
        .staticBlock( "System.out.println( \"In Static Block\" );" )
        .field( "private static final long serialVersionUID = 42L;" )
        .property(
            "/** Field JavaDoc */",
            "@Deprecated",
            "public final int memberField;" )
        .method(
            "/** Method Javadoc*/",
            "@Deprecated",
            "public String getId( String prefix )",
            "return prefix + UUID.randomUUID().toString();" )
        .mainMethod( "System.out.println( \"Hello World\");" )
        .constructor(
            "/** constructor Javadoc */",
            "@Deprecated",
            "public ModelOfClass( int fieldValue )",
            "this.memberField = fieldValue;" );

    public static void main( String[] args )
    {
        Export.dir("C:\\MyApp\\src\\main\\java\\").toFile(_ModelOfClass );

        //create an instance, pass 100 in as argument
        Object instance = _ModelOfClass.instance( 100 );

        Java.callMain( instance ); //prints "Hello World"
        Java.call( instance, "getId", "somePrefix" );

        System.out.println( _ModelOfClass );
        
        Export.dir( "C:\\MyApp\\target\\classes\\" ).toFile( instance.getClass() ); 
    }

    
    public static _class _Abstract = _class.of( 
        "public abstract class BaseClass" );
    
    public static _class _ExtendsImplements = _class.of( 
        "public class Extends extends BaseClass implements I1, I2");
    
    public static _class _Nest = _class.of( "public class Top" )
        .nest( _class.of( "public static class Nest1" )
            .nest( _class.of("public static class Nest2" ) )
        );
}
