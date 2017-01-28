package varcode.java.load;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.load.DirectorySourceLoader;
import varcode.load.Source.SourceStream;

public class Load_JavaSource 
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
        SourceStream ss = Java.sourceFrom(Load_JavaSource.class );
        String theSourceAsAString = ss.asString();
        assertTrue( theSourceAsAString.contains( "theSourceAsAString" ) );
    }
    
    public void testLoadSourceFromCustomLoader()
    {
        SourceStream ss = Java.sourceFrom(new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" ),    
            Load_JavaSource.class );
    }
}
