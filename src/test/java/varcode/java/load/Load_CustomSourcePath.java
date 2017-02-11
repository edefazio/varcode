/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template toFile, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.load;

import junit.framework.TestCase;
import varcode.java.adhoc.Export;
import varcode.java.model._class;
import varcode.load.Source.SourceStream;

/**
 *
 * @author Eric
 */
public class Load_CustomSourcePath
    extends TestCase
{ 
    static String oldPath;
       
    @Override
    public void setUp()
    {
        oldPath = 
            System.getProperty(
                BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP );
        
        System.setProperty( BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP, 
            "" );
    }
    
    @Override
    public void tearDown()
    {
        System.setProperty( BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP, oldPath );
    }
    
    public void testCustomSourcePath()
    {
        setUp();
        
        //export a .java toFile to the temp dir ( that we'll load in later)
        Export.TEMP_DIR.toFile( 
             _class.of( "public class A" )
            .packageName( "ex.fileindir") );
        
        //export a .java source toFile into a toJar "C:\\temp\\MyJavaSource.toJar"
        Export.dir( "C:\\temp" ).toJar( "MyJavaSource.jar", 
            _class.of( "public class B" ).packageName( "ex.injar" ) );
        
        // VERIFY I CANNOT LOAD IT WITH THE DEFAULT LOADER
        SourceStream ss = 
            BaseJavaSourceLoader.INSTANCE.sourceStream( "ex.fileindir.A.java" );            
        assertEquals( ss, null );
        
        // VERIFY I CANNOT LOAD IT WITH THE DEFAULT LOADER
        ss = BaseJavaSourceLoader.INSTANCE.sourceStream( "ex.injar.B.java" );            
        assertEquals( ss, null );
        
        //UPDATE THE "source.path" System property to include the .toJar
        // and the Temp Dir
        System.setProperty( BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP, 
            "C:\\temp\\MyJavaSource.jar;" + 
            Export.TEMP_DIR.baseDirectory.getAbsolutePath() );
        
        // VERIFY I CAN LOAD BOTH 
        
        //THE FILE FROM THE DIRECTORY ON THE "source.path" (in the ex\\fileindir\\ directory)
        ss = BaseJavaSourceLoader.INSTANCE.sourceStream( "ex.fileindir.A.java" );            
        System.out.println( ss.toString() );
        
        //THE FILE WITHIN THE "C:\\temp\\MyJavaSource.toJar" at "ex\\injar" path
        ss = BaseJavaSourceLoader.INSTANCE.sourceStream( "ex.injar.B.java" );            
        System.out.println( ss.toString() );
        
        tearDown();        
    }
    
    
    public void testCustomSourceDir()
    {
        setUp();
        
        //first export source to the temp dir        
        String tempDir = System.getProperty( "java.io.tmpdir" );
        
        Export.dir( tempDir ).toFile( _class.of( "public class A" ) );
        
        //verify that I can';t load it
        SourceStream ss = 
            BaseJavaSourceLoader.INSTANCE.sourceStream( "A.java" );            
        assertEquals( null, ss );
        
        // SET THE SOURCE_DIR_SYS_PROP SYSTEM PROPERTY 
        // SO THE BASE SOURCE LOADER KNOWS TO READ FROM THE TEMP DIR
        System.setProperty( 
            BaseJavaSourceLoader.SOURCE_PATH_SYS_PROP, 
            tempDir );
        
        //know it'll read fine
        ss = BaseJavaSourceLoader.INSTANCE.sourceStream( "A.java" );            
        
        assertNotNull( ss );
        System.out.println( ss.asString() );
        tearDown();
    }
}
