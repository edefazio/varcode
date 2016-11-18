package varcode.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a {@code MarkupRepo} that is a "strategy" for locating and 
 * returning {@code MarkupStream}s for 
 * 
 * Synonymous with to the <B>sourcepath</B> when using the javac compiler. 
 * (but is not restricted to looking for Markup Files in Directories, 
 * (Repos can load files from remote servers)
 * <PRE>
 * java io.varcode.ml.codeml.Compiler 
 *   -Dvarcode.=C:\\workspace\\mycompany\\varcode;D:\\projects\\java\\varcode\\;
 *   -Dvarcode.d=C:\\temp
 * </PRE>
 * 
 * The {@code MarkupPath} COULD be multiple places 
 * (not necessarily just on the local directory) and use different mechanisms
 * (other than simply reading from a local file) to load the varcode source. 
 * 
 * <UL>
 *  <LI>a local Directory
 *  <LI>a zip file
 *  <LI>a Version Control System
 *  <LI>a online site (GitHub)
 * </UL>
 * 
 * NOTE: just like an entry SourcePath, the Repository follows a hierarchy.  
 * Basically Repositories can be nested INSIDE one another, and 
 * <B>the First One found WINS</B>: or to put it another way, the 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SourcePath
    implements SourceLoader
{
	/**
	 * Defines a Path (of many Directories, Jar or Zip Files)
	 * like:  
	 * <PRE>
	 * "D:\Temp\java;D:\myApp\lib\log4j1.2.7-src.jar;D:\Files\resources.zip;"
	 * \____________/\______________________________/\____________________/
	 *       |                    |                            |
	 * source directory     path to jar                 path to zip file
	 *   containing          containing                   containing  
	 *   .java files        .java files                   .java files
	 * </PRE>  
	 * 
	 * for "loading" the Markup.
	 */
	public SourcePath of( String pathDescription )
	{
		List<SourceLoader> allRepos = new ArrayList<SourceLoader>();
		
		String[] eachPath = pathDescription.split( ";" );
		for( int i = 0; i < eachPath.length; i++ )
		{   /*
			if( eachPath[ i ].endsWith( ".jar" ) )
			{
				allRepos.add( JarRepo.of( eachPath[ i ] ) );
			}
			else if( eachPath[ i ].endsWith( ".zip" ) )
			{
				allRepos.add( ZipRepo.of( eachPath[ i ] ) );
			}
			
			else
			
			{*/
			allRepos.add( DirectorySourceLoader.of( eachPath[ i ] ) );
			//}			
		}
		return new SourcePath( allRepos );		
	}
	
	
    /** 
     * Multiple VarCode repositories for reading (_*.java) sources
     * <UL>
     *  <LI>from the existing project source folder
     *  <LI>from the resources
     *  <LI>from a remote source (i.e. GitHub)
     * </UL>   
     * NOTE: the "ORDER" of the varcode repositories is important,
     * it is a First come / First serve (if the varcode source would be resolved
     * by MORE THAN ONE varcode repositories, the {@code Repository}
     * that is first in the list wins. 
     */
    private final List<SourceLoader> repos;
    
    public void add( SourceLoader... repos )
    {
        this.repos.addAll( Arrays.asList( repos ) );
    }
    
    /**
     * Create an empty (mutable) Path
     */
    public SourcePath()
    {
        this.repos = new ArrayList<SourceLoader>();
    }
    
    /**
     * Create a Path with a single Repo
     * @param repo the only repository for varcode source files
     */
    public SourcePath( SourceLoader repo )
    {
        this.repos = new ArrayList<SourceLoader>();
        this.repos.add( repo );        
    }
    
    public SourcePath( List<SourceLoader> repos )
    {
        this.repos = repos;
    }
    
    public SourcePath( SourceLoader...repos )
    {
        this.repos = new ArrayList<SourceLoader>();
        this.repos.addAll( Arrays.asList( repos ) );
    }

    public SourceStream sourceStream( String markupId )
    {
        for( int i = 0; i < repos.size(); i++ )
        {
            SourceStream is = repos.get( i ).sourceStream( markupId );
            if( is != null )
            {
                return is;
            }
        }
        return null;
    }

    public String describe()
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < repos.size(); i++ )
        {
            sb.append(  repos.get( i ).describe() );
            sb.append( "\r\n" );
        }
        return sb.toString();
    }
}
