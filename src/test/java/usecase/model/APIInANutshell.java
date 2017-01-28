package usecase.model;

import java.util.UUID;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.model._class;

/**
 *
 * Simple things should be simple, 
 * complex things should be possible --Alan Kay 
 */
public class APIInANutshell    
{
    public static void main( String[] args )
    {
        buildModelAuthorCompileUseAndExportAdHocCode();
        loadModelModifyCompileUseAndExportAdHocCode();
    }
    
    public static void buildModelAuthorCompileUseAndExportAdHocCode()
    {
        //model a class
        _class _model = _class.of( "package quickstart;",
            "public class Model" )            
            .field( "public int count = 100;" )
            .method( "public String toString()",
                "return \"Hello World\" + count;" );
        
        //write the .java source "quickstart.Model.java" to a file
        Export.dir( "C:\\temp\\java\\" ).toFile( _model );
        
        //compile the "quickstart.Model.java" to "quickstart.Model.class", 
        //& create a new instance
        Object aModel = _model.instance( );
        
        //write "quickstart.Model.class" to a toFile 
        Export.dir( "C:\\temp\\classes\\" ).toFile( aModel.getClass() );
        
        //call a method on the adHoc instance (msg = "Hello World!")
        String msg = (String)Java.call( aModel, "toString" );   
        
        System.out.println( msg );        
    }
    
    /**
     * This Class will have it's source code: 
     * <UL>
     *  <LI> read in at runtime 
     *  <LI> converted to an AST
     *  <LI> converted the AST into a _class (meta model)
     * </UL>
     */
    public class ExistingClass
    {
        //we are just putting a field her which will be modelled
        public String existing = "BLAH";
        
    }
    
    /**
     * "Tailoring" means (at runtime)
     * 1) load the source of an existing class as a _model (_class, _enum, ...)
     * 2) change the _model 
     * 3) author & export the .java source from the _model 
     * 4) (optionally) compiling / loading and or exporting the .java source
     * 5) (optionally) calling methods on the code
     */
    public static void loadModelModifyCompileUseAndExportAdHocCode()
    {
        // 1) load a _class by reading the Java source of an existing class 
        // (even an inner class)
        _class _tailor = Java._classFrom( ExistingClass.class );
        
        // 2) change name & package, add an import, field, & method
        _tailor.setName( "Tailored" )
            .packageName( "ex.load.then.tailor" )
            .field( "public static final int ID = 100;" )
            .imports( UUID.class )
            .method( "public String toString()",
                "return existing + UUID.randomUUID().toString();" );
        
        // 3) author & write "ex.load.then.tailor.Tailored.java" source
        Export.dir( "C:\\myprog\\src\\main\\java\\" ).toFile(_tailor );
        
        // 4) compile & create a new instance
        Object tailored = _tailor.instance(  );
        
        System.out.println( tailored );        
        
        // 5) write "ex.load.then.tailor.Tailored.class" bytecode to a toFile
        Export.dir("C:\\myprog\\target\\classes\\").toFile( tailored.getClass() );
    }
}
