package howto.java.load;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.load.DirectorySourceLoader;
import varcode.load.SourceLoader.SourceStream;

public class LoadSource 
    extends TestCase
{
    /**
     * This will load source code from the "normal" locations that
     * Java source code appears.
     * 
     * under the covers it will load using the strategy defined in:
     * {@code varcode.java.load.JavaSourceLoader}
     */
    public void testLoadSourceFromDefaultLoader()
    {
        SourceStream ss = _Java.sourceFrom(LoadSource.class );
        String theSourceAsAString = ss.asString();
        assertTrue( theSourceAsAString.contains( "theSourceAsAString" ) );
    }
    
    public void testLoadSourceFromCustomLoader()
    {
        SourceStream ss = _Java.sourceFrom(new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" ),    
            LoadSource.class );
    }
}
