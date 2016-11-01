package tutorial.varcode.chap3.load;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.source.BaseSourceLoader;
import varcode.source.SourceLoader.SourceStream;

/**
 * Loading the source 
 */
public class _1_LoadSource
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( _1_LoadSource.class );
    
    public void testLoadJavaSource()
    {
        SourceStream ss = 
           BaseSourceLoader.INSTANCE.sourceStream( VarException.class );
        
        LOG.debug( ss.describe() );
        LOG.debug( ss.asString() );                
    }
    
    /** 
     * Load a pom.xml as text from the classpath
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
