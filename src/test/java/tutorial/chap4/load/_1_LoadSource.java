package tutorial.chap4.load;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.java.load.BaseSourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 * Loading the source 
 */
public class _1_LoadSource
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( _1_LoadSource.class );
    
    /** 
     * You can load the textual source for .java files
     * that are on the classpath
     */
    public void testLoadJavaSource()
    {
        SourceStream ss = 
           BaseSourceLoader.INSTANCE.sourceStream( VarException.class );
        
        LOG.debug( ss.describe() );
        LOG.debug( ss.asString() );                
    }
    
    /** 
     * You can also load any other resource files using the BaseSourceLoader.
     * Here we load a pom.xml as text from the classpath
     * 
     * NOTE: fails if you dont have pom.xml in the root of your classpath 
     */
    public void testLoadMavenPomSource( )
    {
        SourceStream ss = 
            BaseSourceLoader.INSTANCE.sourceStream( "pom.xml" );
        
        LOG.debug( ss.describe() );
        LOG.debug( ss.asString() );        
    } 
}
