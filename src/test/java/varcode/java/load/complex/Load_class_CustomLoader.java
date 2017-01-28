package varcode.java.load.complex;

import junit.framework.TestCase;
import varcode.java.load.Load_class;
import static junit.framework.TestCase.assertEquals;
import varcode.java.BaseJavaSourceLoader;
import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.model._class;
import varcode.load.DirectorySourceLoader;
import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;

/**
 *
 * @author Eric
 */
public class Load_class_CustomLoader
    extends TestCase
{
    public void test_classCustomSourceLoader()
    {
        //you can manually specify a Loader implementation
        SourceLoader customLoader = 
            DirectorySourceLoader.of( 
                System.getProperty( "user.dir" ) + "/src/test/java" );
        
        _class _c = Java._classFrom( 
            customLoader,
            Load_class.class );
        
        assertEquals( _c.getPackageName(), Load_class.class.getPackage().getName() );
    }
    
    public void test_SystemPropLoader()
    {
        //export a .java source toFile in the TOP directory (no package)
        Export e = Export.dir( "C:\\temp\\" );
        e.toFile( _class.of( "public class CustLoadTest" ) );
        
        e.toFile( _class.of( "public class LoadSubDirTest" ).packageName( "sub.dir" ) );
        
        //if we "register" the C:\\Temp directory It can read resources from these
        System.setProperty(  
            BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP, "C:\\temp\\" );
        
        //I can load a toFile in the top directory "C:\\temp" (no package)
        SourceStream ss = 
            BaseJavaSourceLoader.INSTANCE.sourceStream( "CustLoadTest.java" );
        
        assertNotNull( ss );
        
        //I can load a toFile that exsists in a "C:\\temp\\sub\\dir\\" sub directory (package)
        ss = 
            BaseJavaSourceLoader.INSTANCE.sourceStream( "sub.dir.LoadSubDirTest.java" );
        
        assertNotNull( ss );
        
    }
    
}
