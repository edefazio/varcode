package varcode.java.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.JavaReflection;
import varcode.load.DirectorySourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourcePath;

/**
 * Where/How to FIND the Java Source Code (text) this abstraction .
 * 
 * This provides some "conventional" places where the source code might be
 * (as it relates to System Properties, specifically "user.dir" and "markup.dir")
 *  
 * this uses "conventional" places where java source code exists
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaSourceLoader
    extends SourceLoader
{
    
    /** 
     * load the sourceStream from the runtime Java Class 
     * @param clazz runtime Java class to load .java source
     * @return the SourceStream for this class
     */
    public SourceStream sourceStream( Class<?> clazz );
        
    public static final BaseJavaSourceLoader INSTANCE = 
        BaseJavaSourceLoader.I;
    
    /**
     * The Base implementation of a JavaSourceLoader
     * (can load Java source code, as well as other source code/resources
     * from the classpath and "common locations" relative to the root directory
     */
    public enum BaseJavaSourceLoader
        implements JavaSourceLoader
    {
        I;//singleton enum idiom
    
        public static final Logger LOG = 
            LoggerFactory.getLogger( BaseJavaSourceLoader.class );
        
        public static final DirectorySourceLoader USER_DIR = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) );    
    
        public static final DirectorySourceLoader VARCODE_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/varcode/" );
    
        public static final DirectorySourceLoader SRC_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/" );
    
        public static final DirectorySourceLoader SRC_MAIN_JAVA_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/main/java/" );
    
        public static final DirectorySourceLoader SRC_MAIN_RESOURCES_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/main/resources/" );
        
        public static final DirectorySourceLoader TEST_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/test/" );
    
        public static final DirectorySourceLoader SRC_TEST_JAVA_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" );
    
        public static final DirectorySourceLoader SRC_TEST_RESOURCES_DIRECTORY = 
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/resources/" );
    
        public static final ClassPathSourceLoader CLASSPATH_REPO = 
            new ClassPathSourceLoader();    
        
        /**
        * Where to look for Java Source that Corresponds to a specific class 
        */
        public static final SourcePath SOURCE_PATH = 
            new SourcePath( 
                VARCODE_DIRECTORY, 
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
         * @param runtimeClass the local Class
         * @return the markupStream
         */
        @Override
        public SourceStream sourceStream( Class<?> runtimeClass )
        {
            Class topLevelClass = JavaReflection.getTopLevelClass( runtimeClass );
            
            //LOG.debug("Reading source for TOP LEVEL class" + topLevelClass );
            SourceStream topLevelClassStream  = sourceStream( 
                topLevelClass.getCanonicalName() + ".java" ); 
            
            return topLevelClassStream; 
        }
        
        @Override
        public String describe()
        {
            return SOURCE_PATH.describe();
        } 
    }
}
