package varcode.markup.repo;

import java.io.File;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Lang;

/**
 * Finds source files (.java files OR any other language source files)
 * if they exist somewhere on the classpath)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ClassPathScannerRepo
    implements MarkupRepo
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( ClassPathScannerRepo.class );
    
    /** 
     * find the source ".java" file for the runtime class {@code markupClass}
     * 
     * @param markupClass the markup class
     * @return a MarkupStream 
     */
    public MarkupStream loadMarkup( Class markupClass )
    {
        Class topLevelClass = markupClass;
        if( markupClass.isMemberClass() )
        {
            topLevelClass = markupClass.getDeclaringClass();
        }
        MarkupStream ms = markupStream( 
            topLevelClass.getCanonicalName()
                .replace( ".", File.separator ) + ".java" ); 
        if( topLevelClass == markupClass )
        {   //the class asked for is the main file
            return ms;
        }
        LOG.warn(
            markupClass.getName()+ " is a member class, returning Stream for the Declaring Class" );
        return ms;
    }
    
    @Override
    public MarkupStream markupStream( String markupId )
    {
        String relativePath = 
	        File.separatorChar + Lang.fromCodeId( markupId ).resolvePath( markupId );
        
        relativePath = relativePath.replace( File.separatorChar, '/' );
        
        URL url = getClass().getResource( relativePath );
        if( url != null )
        {
            LOG.debug( "Found resource on classpath at:" + url.toString() );
            
            return new FileInJarMarkupStream( 
                url.toString(), 
                markupId, 
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
