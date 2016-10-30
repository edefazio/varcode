package tutorial.varcode.chap3.loader;

import junit.framework.TestCase;
import varcode.source.BaseSourceLoader;
import varcode.source.SourceLoader.SourceStream;

/**
 *
 * @author eric
 */
public class _1_LoadSourceForPom
    extends TestCase
{
    /** NOTE this Test will fail if you */
    public void testLoadSource( )
    {
        SourceStream ss = 
            BaseSourceLoader.INSTANCE.sourceStream( "pom.xml" );
        
        System.out.println( ss.asString() );
        
    }
    
}
