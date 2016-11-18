package varcode.load;

/**
 * Where/How to FIND the Source Code (text).
 * 
 * This provides some "conventional" places where the source code might be
 * (as it relates to System Properties, specifically "user.dir" and "markup.dir")
 *  
 * this uses "conventional" places where java source code exists
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum BaseSourceLoader
    implements SourceLoader
{
    INSTANCE;
    
    public static final DirectorySourceLoader USER_DIR = 
        new DirectorySourceLoader( System.getProperty( "user.dir" ) );    
    
    public static final DirectorySourceLoader MARKUP_DIRECTORY = 
        new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/markup/" );
    
    public static final DirectorySourceLoader SRC_DIRECTORY = 
        new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/src/" );
    
    public static final DirectorySourceLoader SRC_MAIN_JAVA_DIRECTORY = 
       new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/src/main/java/" );
    
    public static final DirectorySourceLoader SRC_MAIN_RESOURCES_DIRECTORY = 
       new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/src/main/resources/" );
        
    public static final DirectorySourceLoader TEST_DIRECTORY = 
        new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/test/" );
    
    public static final DirectorySourceLoader SRC_TEST_JAVA_DIRECTORY = 
        new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/src/test/java/" );
    
    public static final DirectorySourceLoader SRC_TEST_RESOURCES_DIRECTORY = 
       new DirectorySourceLoader( System.getProperty( "user.dir" ) + "/src/test/resources/" );
    
    public static final ClassPathSourceLoader CLASSPATH_REPO = 
        new ClassPathSourceLoader();    
    /**
     * Where to look for Java Source that Corresponds to a specific class 
     */
    public static final SourcePath SOURCE_PATH = 
       new SourcePath( 
           MARKUP_DIRECTORY, 
           SRC_DIRECTORY, 
           SRC_MAIN_JAVA_DIRECTORY,
           SRC_MAIN_RESOURCES_DIRECTORY,
           TEST_DIRECTORY,
           SRC_TEST_JAVA_DIRECTORY,
           SRC_TEST_RESOURCES_DIRECTORY,
           USER_DIR,    
           CLASSPATH_REPO    
       );

    private static SourceStream fromSystemProperty( 
        String systemProperty, String sourceId )
    {
        
        String sysDir = System.getProperty( systemProperty );
        if( sysDir != null )
        {
            // Here I need to be able to parse with ";" separators
            // so I can test each path 
            // (I can set something like... Also i need to be able to
            // load from .jar files or .zip files that are directly
            // in the path (if the thing is not a directory)
            DirectorySourceLoader directLoader = 
                new DirectorySourceLoader( sysDir );
            
            SourceStream sourceStream = directLoader.sourceStream( sourceId );
            if( sourceStream != null )
            {
                return sourceStream;
            }        
        }
        return null;
    }
    /**
     * 
     * @param sourceId the Id of the Markup source to load
     * @return MarkupStream for reading the input
     */
    @Override
    public SourceStream sourceStream( String sourceId )
    {
        SourceStream ms = fromSystemProperty( "source.dir", sourceId );
        if( ms != null )
        {
            return ms;
        }
        ms = fromSystemProperty( "java.source.path", sourceId );
        if( ms != null )
        {
            return ms;
        }
        return SOURCE_PATH.sourceStream( sourceId );
    }


    /** 
     * given the Class looks in the "usual places" on the Path
     * to return the Source markup Stream 
     * @param localClass the local Class
     * @return the markupStream
     */
    public SourceStream sourceStream( Class<?> localClass )
    {
        //need to check if it's a member class 
        if ( localClass.isMemberClass() )
        {
            // at the moment any Member class just returns the Declaring Classes
            // full source code
            
            SourceStream declaringClassStream  = sourceStream( 
                localClass.getDeclaringClass().getCanonicalName() + ".java" ); 
            
            return declaringClassStream;            
            //_JavaParser.findMemberNode(cu, localClass)
        }
        else
        {
            return sourceStream( localClass.getCanonicalName() + ".java" );
        }
    }
        
    @Override
    public String describe()
    {
        return SOURCE_PATH.describe();
    } 
}
