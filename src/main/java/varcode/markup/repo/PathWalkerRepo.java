package varcode.markup.repo;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import varcode.Lang;
import varcode.VarException;

/**
 * Deeply/Recursively scans the sub-directories within a Directory (workspace) 
 * for all source files matching the sourceId. 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PathWalkerRepo
    implements MarkupRepo
{
    /** the base Directory of the Workspace*/
    private final String workspaceDirPath;
    
    /** walks all directory paths to try and find all source files matching the sourceId */ 
    private final PathWalk pathWalk;
    
    public PathWalkerRepo( String workspaceDirPath )
    {
        this.workspaceDirPath = workspaceDirPath;
        this.pathWalk = new PathWalk( workspaceDirPath );
    }

    public MarkupStream markupStream( String markupId )
    {
        //here's the full (hierarchial) path. 
        String path =
        	Lang.fromCodeId( markupId ).resolvePath( markupId );	
        	//LangNamespaceToMarkupPath.INSTANCE.resolvePath( markupId );
        
        File f = new File( path );
        String name = f.getName();
        
        List<Path> paths = pathWalk.getCandidates( name );
        if( paths.size() == 1 )
        {
            MarkupStream ss = 
            	new FileMarkupStream( markupId, paths.get( 0 ).toString() );
            return ss;
        }
        else
        {
            //there is more than One candidate, find the most appropriate one
            //String userDirectory = System.getProperty( "user.dir" );
            //TODO FIX THIS
            throw new VarException(
                "NOT IMPLEMENTED YET more than one candidate for \"" + markupId + "\"");
        }
    }

    public String describe()
    {
        return "[WORKSPACE]: \"" + workspaceDirPath + "\"";
    }
}
