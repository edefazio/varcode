/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template toFile, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.source;

import varcode.load.Source;
import varcode.load.ZippedSourceLoader;
import junit.framework.TestCase;
import varcode.java.adhoc.Export;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class ZippedSourceLoaderTest
    extends TestCase
{
    
    public void testLoadZip( )
    {        
        Export.dir( "C:\\temp" ).toJar( "MySource.jar", 
            _class.of( "A" ),
            _class.of( "B" ).packageName( "ex.subpkg" ) );
        
        ZippedSourceLoader zipSourceLoader = 
            new ZippedSourceLoader( "C:\\temp\\MySource.jar" );
        
        //I can Load
        Source.SourceStream ss = zipSourceLoader.sourceStream( "A.java" );
        assertNotNull( ss.asString() );
        
        ss = zipSourceLoader.sourceStream( "ex\\subpkg\\B.java" );
        assertNotNull( ss.asString() );
    }
    
}
