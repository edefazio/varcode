package varcode.load;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*{-?(log==false):*/import org.slf4j.Logger;
import org.slf4j.LoggerFactory;/*}*/

/**
 * Given a Base Directory Path, 
 * Walks it to find a specific Markup File (with a given MarkupId)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PathWalk
{   
    /*{-?(log==false):*/private static final Logger LOG = 
        LoggerFactory.getLogger( PathWalk.class );/*}*/
    
    protected final FileSystem fs;
    
    protected final Path rootDirectory;
    
    protected final String rootDirectoryName;
    
    public PathWalk( String rootDirectoryName )
    {
        this.fs = FileSystems.getDefault();
        this.rootDirectoryName = rootDirectoryName;
        this.rootDirectory = Paths.get( rootDirectoryName );        
    }
        
    public List<Path> getCandidates( String fileName )
    {
        final PathMatcher fileMatcher = 
            fs.getPathMatcher( "glob:" + fileName );
        final AtomicInteger count = new AtomicInteger( 0 );
        final List<Path> candidates = new ArrayList<Path>();
        
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() 
        {
            @Override
            public FileVisitResult visitFile( 
                Path file, BasicFileAttributes attribs ) 
            {                
                Path name = file.getFileName();                
                count.incrementAndGet();
                if( fileMatcher.matches( name ) ) 
                {      
                    candidates.add( file );
                }
                return FileVisitResult.CONTINUE;                
            }
        };
        try
        {
            Files.walkFileTree( rootDirectory, matcherVisitor );            
            /*{-?(log==false):*/
            LOG.info( "Scanned (" + count + ") files; found (" + candidates.size() + ")" );
            /*-}*/ 
            return candidates;
        }
        catch( IOException e )
        {
            return null;
        }
    }
}
