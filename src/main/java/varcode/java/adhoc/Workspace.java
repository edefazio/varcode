package varcode.java.adhoc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.java.JavaCase;
import varcode.java.JavaCase.JavaCaseAuthor;

/**
 * Collection of {@code AdHocJavaFiles} (java source code) to be compiled 
 * (via Javac) and loaded into an {@code AdHocClassLoader}.
 * 
 * {@code Workspace} represents and simplifies the client view of pushing one or 
 * more in memory Java source files to the Javac compiler at runtime 
 * and organizing the compilers result (the derived .class files) as 
 * {@code AdHocClassFile}s, and interacting with the {@code AdHocClassLoader}.
 * 
 * This greatly simplifies the client view of the process of 
 * preparing, compiling, and loading Java source files at runtime.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Workspace
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( Workspace.class );
    
    /** A handle to the Runtime Java Compiler */
    public static final JavaCompiler JAVAC = 
        ToolProvider.getSystemJavaCompiler(); 
    
    /** 
     * Build a Workspace containing the Java source output from the 
     * {@code JavaCaseAuthor}s
     * 
     * @param caseAuthors authors of Java Cases that contain Java code
     * @return a Workspace containing the JavaFiles that are
     */
    public static Workspace ofAuthors( JavaCaseAuthor...caseAuthors )
    {
        JavaCase[] cases = new JavaCase[ caseAuthors.length ];
        
        for( int i = 0; i < caseAuthors.length; i++ )
        {
            cases[ i ] = caseAuthors[ i ].toJavaCase( );
        }
        return of( cases );
    }
    
    /** 
     * Creates a Workspace given the JavaCases <PRE>
     * Workspace ws = 
     *    Workspace.of( 
     *        _interface.of("interface Count").toJavaCase(), 
     *        _enum.of("enum MyEnum").value("ONE").toJavaCase() );
     * </PRE>
     * @param javaCases java Cases to be compiled into the workspace
     */
    public static Workspace of( JavaCase...javaCases )
	{
        Workspace workspace = new Workspace( );
		workspace.addCases( javaCases );
		return workspace;
	}
    
    /**
     * Authors {@code JavaCase}s and then compiles each of the JavaCases
     * and returns a ClassLoader loaded with the compiled Classes.
     * 
     * Workspace ws = 
     *    Workspace.of( 
     *        "MyWorkspace" 
     *        _interface.of("interface Count"), 
     *        _enum.of("enum MyEnum").value("ONE") );
     * 
     * @param caseAuthors authors of JavaCases
     * @return  an AdHocClassLoader loaded with compiled Classes
     */
    public static AdHocClassLoader compileNow( JavaCaseAuthor...caseAuthors )
    {
        JavaCase[] cases = new JavaCase[ caseAuthors.length ];
        for( int i = 0; i < caseAuthors.length; i++ )
        {
            cases[ i ] = caseAuthors[ i ].toJavaCase( );
        }
        return compileNow( cases );
    }
    /**
     * 
     * <PRE>
     * Workspace ws = 
     *    Workspace.of( 
     *        "MyWorkspace" 
     *        _interface.of("interface Count").toJavaCase(), 
     *        _enum.of("enum MyEnum").value("ONE").toJavaCase() );
     * </PRE>
     * 
     * @param javaCases authored java code to be compiled and loaded
     * @return ClassLoader containing the compiled Java classes
     */
    public static AdHocClassLoader compileNow( JavaCase...javaCases )
    {
        AdHocJavaFile[] files = new AdHocJavaFile[ javaCases.length ];
        for( int i = 0; i < files.length; i++ )
        {
            files[ i ] = javaCases[ i ].javaCode(); 
        }
        return compileNow( files );
    }
    
    /**
     * Creates a new AdHocClassLoader, and compiles the javaFiles into the
     * AdHocClassLoader and returns it.
     * 
     * @param javaFiles Java code to be compiled
     * @return an AdHocClassLoader containing the compiled .classes 
     */
    public static AdHocClassLoader compileNow( AdHocJavaFile...javaFiles )
    {
        return compileNow( new AdHocClassLoader(), javaFiles );
    }
    
    /**
     * Creates a Workspace, adds the Java Files, compiles to Java Files
     * and loads the classes into the {@code adHocClassLoader} and returns 
     * the updated {@code AdHocClassLoader}
     * @param adHocClassLoader the classLoader where files are to be loaded
     * @param javaFiles the files to compile and load
     * @return the updated AdHocClassLoader
     */
    public static AdHocClassLoader compileNow( 
        AdHocClassLoader adHocClassLoader,
        AdHocJavaFile... javaFiles)
    {
        Workspace ws = new Workspace( adHocClassLoader, javaFiles );
        return ws.compile( );
    }        
    
    public static AdHocClassLoader compileNow(
        List<AdHocJavaFile>javaFiles,
        JavacOptions.CompilerOption...compilerOptions )
    {
        return compileNow( new AdHocClassLoader(), javaFiles, compilerOptions );
    }
    
    public static AdHocClassLoader compileNow(
        AdHocClassLoader adHocClassLoader,
        List<AdHocJavaFile>javaFiles,
        JavacOptions.CompilerOption...compilerOptions )
    {
        Workspace ws = new Workspace( adHocClassLoader );
        ws.addCode( javaFiles.toArray( new AdHocJavaFile[ 0 ] ) );
        return ws.compile( compilerOptions );
    }        
    
    /** ClassLoader for loading ad hoc Java Code that exists in memory 
     * at runtime */
    private final AdHocClassLoader adHocClassLoader;
    	
	/** Java source files of the Workspace */
	private final Map<String, AdHocJavaFile> classNameToAdHocJavaFileMap = 
		new HashMap<String, AdHocJavaFile>();
    
    private final WorkspaceFileManager workspaceFileManager;
    
    /**
     * Construct a new Workspace with a new AdHocClassLoader
     */
    public Workspace()
    {
        this( new AdHocClassLoader() );
    }
    
    /**
     * Construct a new Workspace with a the AdHocClassLoader
     * @param adHocClassLoader 
     */
    public Workspace( AdHocClassLoader adHocClassLoader )
    {
        this( adHocClassLoader, new AdHocJavaFile[ 0 ] );
    }
    
    /**
     * Construct a Workspace with the ClassLoader and AdHocJavaFiles
     * 
     * @param adHocClassLoader the loader that will contain the compile classes
     * @param adHocJavaFiles Java source files that are to be compiled to classes
     */
    public Workspace( 
        AdHocClassLoader adHocClassLoader, AdHocJavaFile...adHocJavaFiles )
    {
        
        this( JAVAC.getStandardFileManager( 
                null, //use default DiagnosticListener
                null, //use default Locale
                null ), //use default CharSet    
            adHocClassLoader,
            adHocJavaFiles ); 	
    }        
    
    /**
     * Creates a Workspace that delegates to the fileManager for class resolution
     * and compiles & loads classes with the adHocClassLoader
     * 
     * @param fileManager file Manager for resolving classes
     * @param adHocClassLoader classLoader to contain the compiled .classes
     * @param adHocJavaFiles source .java files to be compiled and loaded
     */
	public Workspace(
        StandardJavaFileManager fileManager,    
        AdHocClassLoader adHocClassLoader, 
        AdHocJavaFile...adHocJavaFiles )
	{
        this.workspaceFileManager = 
            new WorkspaceFileManager( fileManager, adHocClassLoader );
        //super( fileManager );
        
        this.adHocClassLoader = adHocClassLoader;     
	    for( int i = 0; i < adHocJavaFiles.length; i++ )
		{
	    	addCode( adHocJavaFiles[ i ] );								
        }        
	}
    
    /**
	 * Adds a class with code to the workspace
	 * @param className the fully qualified class name:
	 * (i.e. "java.util.HashMap") 
	 * @param code the text of the source code
     * @return the updated workspace
	 */
	public final Workspace addCode( String className, String code )
	{
		AdHocJavaFile adHocJavaFile = 
			new AdHocJavaFile( className, code );
        
		addCode( adHocJavaFile );
		return this;
	}
	
    /**
     * Adds one of more JavaCases containing (.java) source code
     * to be compiled within the workspace
     * 
     * @param javaCases one or more Java cases to be compiled together
     * @return the Workspace a workspace managing 
     */
	public final Workspace addCases( JavaCase...javaCases )
	{
        for( int i = 0; i < javaCases.length; i++ )
		{
			AdHocJavaFile javaCode = javaCases[ i ].javaCode();
            addCode( javaCode );
		}
		return this;
	}
	
    /** 
     * adds java code to the Workspace and returns the Workspace
     * @param javaCode
     * @return  the Workspace
     */
	public final Workspace addCode( AdHocJavaFile... javaCode )
	{        
        for( int i = 0; i < javaCode.length; i++ )
		{
            if( this.adHocClassLoader.getAdHocClassMap().containsKey(
                javaCode[ i ].getClassName() ) )
            {
                throw new VarException( 
                    "Failed Adding Class \"" + javaCode[ i ].getClassName()
                   +"\" to workspace; a Class by this name already exists "
                  + "in this workspace" );
            }
            if( LOG.isTraceEnabled() ){ LOG.trace( "Adding code \"" + javaCode[ i ].getClassName() + "\" to workspace" ); }
			classNameToAdHocJavaFileMap.put( 
                javaCode[ i ].getClassName(), javaCode[ i ] );           
		}
		return this;
	}
    
    /**
     * File Manager for the AdHoc Workspace
     * (note this is private b/c I dont want the internals leaking onto
     * the Workspace API)
     */
    private static class WorkspaceFileManager
        extends ForwardingJavaFileManager<JavaFileManager>      
    {
        private final AdHocClassLoader adHocClassLoader;
        
        public WorkspaceFileManager(  
            StandardJavaFileManager fileManager,    
            AdHocClassLoader adHocClassLoader )
        {
            super( fileManager );
            this.adHocClassLoader = adHocClassLoader;
        }
                
        @Override
        public JavaFileObject getJavaFileForOutput(
            JavaFileManager.Location location, 
            String className, 
            JavaFileObject.Kind kind, 
            FileObject sibling ) 
        {    	
            // check if we already loaded this class
            AdHocClassFile adHocClass =
                this.adHocClassLoader.getAdHocClassMap().get( className );	
        
            if( adHocClass != null )
            {   // return the already-loaded class
                return adHocClass;
            }
            try
            {   // create a "home" for the compiled bytes
                adHocClass = new AdHocClassFile( this.adHocClassLoader, className );
                return adHocClass;
            }
            catch( Exception e )
            {
                throw new VarException( 
                    "Unable to create output class for class \"" + className + "\"" );
            }
        }
    }
    
    public AdHocClassLoader compile( 
        JavacOptions.CompilerOption...compilerOptions )
    {
        Iterable<String> options = JavacOptions.optionsFrom( compilerOptions );
			
		DiagnosticCollector<JavaFileObject> diagnostics = 
			new DiagnosticCollector<JavaFileObject>();
			
		JavaCompiler.CompilationTask task = 
            JAVAC.getTask(
                null, //use System.err if the tool fails 
                this.workspaceFileManager,
                diagnostics, 
                options, 
			null, // NO annotation processors classes (at this time) 
            this.classNameToAdHocJavaFileMap.values() );
			
		boolean compiledNoErrors = task.call();
			
	    if( !compiledNoErrors )
	    {
            throw new JavacException( 
	        	this.classNameToAdHocJavaFileMap.values(),  
	        	diagnostics );
	    }	        	
        try
        {
        	this.workspaceFileManager.close();
        }
        catch( IOException ioe )
        {
            //LOG.warn( "Error closing BaseFileManager", ioe);            	
        }
        return this.adHocClassLoader;
    }
}
