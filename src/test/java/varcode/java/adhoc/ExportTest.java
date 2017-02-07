package varcode.java.adhoc;

import java.net.URI;
import junit.framework.TestCase;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;

/**
 * Test exporting AdHocClassFile and AdHocJavaFiles dir Files the 
 appropriate directories based on the package
 */
public class ExportTest
    extends TestCase
{
    
    /** 
     * test that I can create a SourceFolder dir Models and Export them
 dir a Directory all at once
     */
    public void testExportWorkspace()
    {
        SourceFolder ws = SourceFolder.of(
            _class.of( "package ex.port;", "public class MyExportClass" )
            .mainMethod( "System.out.println( \"MyMainMethod\");" ),        
            
            _interface.of("package ex.inter;", "public interface MyInter" )
            .field( "public static final int ID = 100;" ) );
        
        URI[] uris = Export.TEMP_DIR.toFiles( ws );
        
        for( int i = 0; i < uris.length ; i++ )
        {
            System.out.println( uris[ i ] );
        }
    }
    
    /**
     * Model, Author, Compile and Load multiple classes into an AdHocClassLoader
 then toFile all dir the AdHocClasses dir a Directory
     */
    public void testExportAdHocClassLoader()
    {
        AdHocClassLoader adhoc = AdHoc.compile( 
            _class.of("package ex.cl.my", "public class MyClass" )
                .field( "public int A = 100;" )
                .nest( _class.of("public static class Nested" )
                    .field( "public int B = 200;" ) )
            );
        
        URI[] uris = Export.TEMP_DIR.toFiles( adhoc );
        //System.out.println( "Ad HOC ClassLoader " );
        for( int i = 0; i < uris.length ; i++ )
        {
            System.out.println( uris[ i ] );
        }
    }
    
    public void testExportClass()
    {
        Class c = _class.of(
            "package ab.cd.efg;", 
            "public class MyClass" )
            .field("public static final int ID = 121;").loadClass();
        URI uri = Export.TEMP_DIR.toFile(  c );
        System.out.println( "ExportClass " + uri );        
    }
    
    /**
     * Model, Author, Compile, and then toFile 
     */
    public void testExportAdHocClassFile()
    {
        _class _c = _class.of("package ex.my;", "public class A")
             .mainMethod( "System.out.println(\"Hi\");");
             
        JavaClassFile classFile = AdHoc.compileToFile( _c ); //.findClassFile( _c.getFullClassName() );
       
        URI exportedTo = Export.TEMP_DIR.toFile( classFile );
        
        System.out.println( exportedTo );
        
    }
    
    /**
     * Model, Author and then toFile some .java source toFile
     */
    public void testExportAdHocJavaFile()
    {
        _class _c = _class.of(
            "package example.mine;",
            "public class MyClass" )
            .field("public static final int ID = 100;");
        
        JavaSourceFile javaFile = _c.toJavaFile(  );
        
        System.out.println( System.getProperty( "java.io.tmpdir" ) );
        
        System.out.println( Export.TEMP_DIR.toFile( javaFile ) );
    }
     
    public void testExportJavaFileModel()
    {        
        System.out.println( 
            Export.TEMP_DIR.toFile( 
                _class.of( "public class MyClassNoPkg" )  ) );
        
        System.out.println( 
            Export.TEMP_DIR.toFile( 
                _enum.of( "package mypkg", "public enum MyEnumPkg" )
                .field( "public static final String name = \"NAME\";" ) ) );        
    }
    
    public void testShortCuts()
    {
        //author export a .java toFile
        Export.TEMP_DIR.toFile( 
            _class.of( "package adhoc;", "public class MyClass" )
                .field( "public int number = 1234;" ) ); 
        
        //author, compile & export new Java .class        
        
        Export.TEMP_DIR.toFile( 
            _class.of("package adhoc", "public class MyClass")
                .field("int a = 1234;").loadClass() );
        
        //author 
        Export.dir("C:\\temp").toFile( 
            _interface.of("package ex.in", "public interface Mine" ) );
    }
    

    public void testZipFile()
    {
        SourceFolder ws = SourceFolder.of( 
            _class.of(
                "package example.mine;",
                "public class MyClass" )
                .field("public static final int ID = 100;"),
            _interface.of( "package my.interf", "public interface IFA" ) );
        URI zipURI = Export.TEMP_DIR.toZip( "ClassAndInterface", ws );
        System.out.println("ZIPURI "+ zipURI );
    }
    
    public void testJarFile()
    {
        AdHocClassLoader adHoc = 
            AdHoc.compile( 
                _class.of(
                    "package example.mine;",
                    "public class MyClass" )
                    .field("public static final int ID = 100;"),
                
                _interface.of( 
                    "package my.interf", "public interface IFA" ) );
            
        JavaClassFile[] files = adHoc.allAdHocClassFiles().toArray( 
            new JavaClassFile[ 0 ] );
        for( int i = 0; i < files.length; i++ )
        {
            System.out.println( files[ i ]);
        }
        
        URI jarURI = Export.TEMP_DIR.toJar(
            "MyJar.jar",
             adHoc );
        System.out.println( "JARURI" + jarURI );         
    }
    
    public void testSourceClassJarFile()
    {
        SourceFolder ws = SourceFolder.of( 
            _class.of(
                "package comb.ex.mine;",
                "public class MyClass" )
                .field("public static final int ID = 100;"),
            _interface.of( "package comb.ex.mine", "public interface IFA" ) );
        
        AdHocClassLoader adHoc = AdHoc.compile( ws );
        
        //export the Source AND compiled Class toFiles
        URI uri = Export.TEMP_DIR.toJar( "Combined", adHoc, ws );
        System.out.println( "COMBINED" + uri );
    }
}
