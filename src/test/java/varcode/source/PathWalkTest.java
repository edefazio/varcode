package varcode.source;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import varcode.source.PathWalk;
import junit.framework.TestCase;

public class PathWalkTest
    extends TestCase
{
    public void testUserDir()
    {        
        //System.out.print( System.getProperties() );
        PathWalk pw = new PathWalk( System.getProperty( "user.dir", "." ) );
        List<Path> candidates = 
        	pw.getCandidates( "LangToScriptEvaluator.java" );        
        System.out.println( candidates.size() );
    }
    
    public static PathWalk getWorkspacePathWalk()
    {
        String userDirectoryName = System.getProperty( "user.dir" );
        File userDir = new File( userDirectoryName );
        if( userDir.exists() && userDir.isDirectory() && userDir.canRead() )
        {
            File parentFile = userDir.getParentFile();
            if( parentFile.exists() && parentFile.isDirectory() && parentFile.canRead() )
            {   //this is "exactly" what we wanted
                return new PathWalk( parentFile.getAbsolutePath() );
            }
            else
            {
                return new PathWalk( userDir.getAbsolutePath() );
            }
        }
        return new PathWalk( "." );
        
    }
    
    public void testUserDirParent()
    {
        PathWalk pw = getWorkspacePathWalk();
        List<Path> paths = pw.getCandidates( "Lang.java" );
        System.out.println( paths.size() );
    }
    
    /*
    public void testJavaIOTempDir()
    {
        java.io.tmpdir
    }
    */
}
