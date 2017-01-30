package howto.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import junit.framework.TestCase;
import varcode.java.adhoc.Export;
import varcode.java.model._annotationType;

public class ModelAnnotationType
    extends TestCase
{
    static _annotationType _ModelOfAnnotation = _annotationType.of(
        "package usecase.model;",
        "import java.lang.annotation.Retention;",
        "import java.lang.annotation.RetentionPolicy;",
        "/** Annotation Type Javadoc comment */",
        "@Retention(RetentionPolicy.RUNTIME)",
        "public @interface ModelOfAnnotation" )
        
        .property( "float", "price" )
        .property( "boolean", "isOpen" )
        //properties with defaults
        .property( "int", "count", 1 )
        .property( String.class, "property", "\"defaultValue\"" )        
        .property( String[].class, "arrayString", "{\"A\", \"B\"}" );
        
    /** Annotation Type Javadoc comment */   
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModelOfAnnotation
    {        
        float price();
        boolean isOpen();
        
        int count() default 1;
        String property() default "defaultValue";
        String[] arrayString() default {"A", "B"};        
    }
    
    public static void main(String[] args)
    {
        System.out.println( _ModelOfAnnotation );
        Export.dir("C:\\MyApp\\src\\main\\java\\").toFile(_ModelOfAnnotation ); 
        
        Class clazz = _ModelOfAnnotation.loadClass();
        
        //Export "C:\\MyApp\\target\\classes\\usecase.model.ModelAnnotation.class"
        Export.dir("C:\\MyApp\\target\\classes\\").toFile( clazz );
    }
    
    public void testUseAnnotation()
    {
        String javaSource = _ModelOfAnnotation.author(); //author the .java source to a String
        System.out.println( javaSource ); //write to the 
        Export.TEMP_DIR.toFile(_ModelOfAnnotation ); //export .java source to a file

        
        Class clazz = _ModelOfAnnotation.loadClass();
        
        assertTrue( clazz.isAnnotation() );
        

        Export.TEMP_DIR.toFile( clazz ); //export the .class to a file
    }   
    
    
}
