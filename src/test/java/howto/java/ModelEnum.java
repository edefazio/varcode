package howto.java;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.model._enum;

public class ModelEnum
    extends TestCase
{
    static _enum _ModelOfEnum = _enum.of(
        "package usecase.model;",
        "import java.util.UUID;",
        "import java.io.Serializable",
        "/** Enum Javadoc comment */",
        "@Deprecated",
        "public enum ModelOfEnum implements Serializable" )
        .staticBlock( "System.out.println( \"In Enum Static Block\" );" )
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
        .mainMethod( "System.out.println( \"Hello Enum Main Method\");" )
        .constructor(
            "/** constructor Javadoc */",
            "@Deprecated",
            "private ModelOfEnum( int fieldValue )",
            "this.memberField = fieldValue;" )
        .constant( "A", 1 )
        .constant( "B", 2 );
    
    public static void main(String[] args )
    {
        //Export.dir("C:\\myApp\\src\\main\\java" ).toFile(_ModelOfEnum ); 
        //Class clazz = _ModelOfEnum.loadClass();
        //Object[] constants = clazz.getEnumConstants();
        
        System.out.println( _ModelOfEnum );
        
        //Java.callMain( constants[0] );
        //Java.call( constants[0], "getId", "somePrefix" );
        
        //Export.TEMP_DIR.toFile( clazz ); //export the .class to a file
    }
    
    public void testUseEnum()
    {
        String javaSource = _ModelOfEnum.author(); //author the .java source to a String
        System.out.println( javaSource ); //write to the 
        Export.TEMP_DIR.toFile(_ModelOfEnum ); //export .java source to a file

        
        Class clazz = _ModelOfEnum.loadClass();
        Object[] constants = clazz.getEnumConstants();
        
        assertEquals( 2, constants.length );
        
        Java.callMain( constants[0] );
        Java.call( constants[0], "getId", "somePrefix" );

        Export.TEMP_DIR.toFile( clazz ); //export the .class to a file
    }   
    
    
}
