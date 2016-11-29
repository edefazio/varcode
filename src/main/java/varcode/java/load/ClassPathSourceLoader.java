package varcode.java.load;

import java.io.File;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Lang;
import varcode.load.SourceLoader;

/**
 * Finds source files (.java files OR any other language source files)
 * if they exist somewhere on the classpath)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ClassPathSourceLoader
    implements SourceLoader
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( ClassPathSourceLoader.class );
    
    /** 
     * find the source ".java" file for the runtime class {@code markupClass}
     * 
     * @param runtimeClass the runtime class
     * @return a SourceStream 
     */
    public SourceStream sourceStream( Class runtimeClass )
    {
        Class topLevelClass = runtimeClass;
        if( runtimeClass.isMemberClass() )
        {
            topLevelClass = runtimeClass.getDeclaringClass();
        }
        SourceStream ms = sourceStream( 
            topLevelClass.getCanonicalName()
                .replace( ".", File.separator ) + ".java" ); 
        
        if( topLevelClass == runtimeClass )
        {   //the class asked for is the main file
            return ms;
        }
        LOG.info( runtimeClass.getName()+ 
            " is a nested class, returning Stream for the Declaring Class" );
        return ms;
    }
    
    @Override
    public SourceStream sourceStream( String sourceId )
    {
        String relativePath = 
	    File.separatorChar + Lang.fromCodeId( sourceId ).resolvePath( sourceId );
        
        relativePath = relativePath.replace( File.separatorChar, '/' );
        
        URL url = getClass().getResource( relativePath );
        if( url != null )
        {
            LOG.debug( "Found resource on classpath at:" + url.toString() );
            
            return new FileInJarSourceStream( 
                url.toString(), 
                sourceId, 
                getClass().getResourceAsStream( relativePath ) );
        }
        LOG.debug( "Could not find resource on classpath:" +  relativePath );
        return null;        
    }

    
    @Override
    public String describe()
    {
        return "[Classpath]";
    }
    
}
