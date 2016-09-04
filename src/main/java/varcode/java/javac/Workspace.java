/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.javac;

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

/**
 * A collection of AdHocJavaFiles (java source code) to be compiled.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Workspace
    extends ForwardingJavaFileManager<JavaFileManager>         
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( Workspace.class );
    
    /** A handle to the Runtime Java Compiler */
    public static final JavaCompiler JAVAC = 
        ToolProvider.getSystemJavaCompiler(); 
    
    public static Workspace of( JavaCase...javaCases )
	{
		return of( null, javaCases );
	}
	
	public static Workspace of( String name, JavaCase... javaCases )
	{
		Workspace sw = new Workspace(  );
		sw.addCode( javaCases );
		return sw;
	}
    
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
        return ws.compileC( );
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
        return ws.compileC( compilerOptions );
    }        
    
    /** ClassLoader for loading Java Code that exists in memory 
     * (as a byte array) vs from a file */
    private final AdHocClassLoader adHocClassLoader;
    
    /** (optional) Name of the workspace*/
	public String name;
		
	/** Java source files of the Workspace */
	private final Map<String, AdHocJavaFile> classNameToAdHocCodeMap = 
		new HashMap<String, AdHocJavaFile>();
    
    public Workspace()
    {
        this( new AdHocClassLoader() );
    }
    
    public Workspace( AdHocClassLoader adHocClassLoader )
    {
        this( adHocClassLoader, new AdHocJavaFile[ 0 ] );
    }
    
    public Workspace( AdHocClassLoader adHocClassLoader, AdHocJavaFile...adHocCode )
    {
        this( 
            JAVAC.getStandardFileManager( 
                null, //use default DiagnosticListener
                null, //use default Locale
                null ),
            adHocClassLoader,
            "AdHoc",
            adHocCode );//use default CharSet     	
    }        
    
	public Workspace(
        StandardJavaFileManager fileManager,    
        AdHocClassLoader adHocClassLoader, 
        String workspaceName, 
        AdHocJavaFile...adHocJavaFiles )
	{
        super( fileManager );
        
        this.adHocClassLoader = adHocClassLoader;
		this.name = workspaceName;			     
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
		AdHocJavaFile adHocCode = 
			new AdHocJavaFile( className, code );
		addCode( adHocCode );
		return this;
	}
		
	public final Workspace addCode( JavaCase...javaCase )
	{
        for( int i = 0; i < javaCase.length; i++ )
		{
			AdHocJavaFile javaCode = javaCase[ i ].javaCode();
			classNameToAdHocCodeMap.put( javaCode.getClassName(), javaCode );
		}
		return this;
	}
	
    /** adds a java code to the Workspace and returns the Workspace */
	public final Workspace addCode( AdHocJavaFile... javaCode )
	{
        for( int i = 0; i < javaCode.length; i++ )
		{
            if( this.adHocClassLoader.getAdHocClassMap().containsKey(
                javaCode[ i ].getClassName() ) )
            {
                throw new VarException( 
                    "Failed Adding Class \""+javaCode[ i ].getClassName()
                   +"\" to workspace; a Class by this name already exists "
                  + "in this workspace" );
            }
            LOG.debug( "Adding code \"" + javaCode[ i ].getClassName() + "\" to workspace" );
			classNameToAdHocCodeMap.put( 
                javaCode[ i ].getClassName(), javaCode[ i ] );           
		}
		return this;
	}
    
    //TODO Move this to a static inner class that 
    // implements ForwardingFileManager
    //  as to avoid putting it on the public Workspace API
    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location, 
        String className, 
        JavaFileObject.Kind kind, 
        FileObject sibling ) 
        throws IOException 
    {    	
    	AdHocClassFile adHocClass =
    		this.adHocClassLoader.getAdHocClassMap().get( className );	
        
    	if( adHocClass != null )
    	{
    		return adHocClass;
    	}
    	try
    	{
    		adHocClass = new AdHocClassFile( className );
    		this.adHocClassLoader.introduce(adHocClass );
    		return adHocClass;
    	}
    	catch( Exception e )
    	{
    		throw new VarException( 
                "Unable to create output class for class \"" + className + "\"" );
    	}
    }
    
    public AdHocClassLoader compileC( 
        JavacOptions.CompilerOption...compilerOptions )
    {
        Iterable<String> options = JavacOptions.optionsFrom( compilerOptions );
			
		DiagnosticCollector<JavaFileObject> diagnostics = 
			new DiagnosticCollector<JavaFileObject>();
			
		JavaCompiler.CompilationTask task = 
            JAVAC.getTask(
                null, //use System.err if the tool fails 
                this, 
                diagnostics, 
                options, 
			null, // NO annotation processors classes (at this time) 
		 	this.classNameToAdHocCodeMap.values() );
			
		boolean compiledNoErrors = task.call();
			
	    if( !compiledNoErrors )
	    {
            throw new JavacException( 
                this.name, 
	        	this.classNameToAdHocCodeMap.values(),  
	        	diagnostics );
	    }	        	
        try
        {
        	this.close();
        }
        catch( IOException ioe )
        {
            //LOG.warn( "Error closing BaseFileManager", ioe);            	
        }
        return this.adHocClassLoader;
    }
    
    public Map<String, Class<?>> compile(
		JavacOptions.CompilerOption...compilerOptions )
    {
        compileC( compilerOptions );
        
        //return this.adHocClassLoader.loadClass(name)
	    Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
	    String[] adHocClassNames = 
            this.adHocClassLoader.getAdHocClassMap().keySet().toArray( 
                new String[ 0 ] );
	    
        for( int i = 0; i < adHocClassNames.length; i++ )
	    {
            Class<?> adHocClass = 
	        	this.adHocClassLoader.findClass( adHocClassNames[ i ] );
	        	
	        loadedClasses.put( adHocClassNames[ i ], adHocClass );  
		}
        return loadedClasses;
    }     
}
