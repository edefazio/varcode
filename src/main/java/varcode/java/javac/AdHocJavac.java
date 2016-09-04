package varcode.java.javac;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

/**
 * Compiles one or more classes of AdHoc Java Code that exists (as a Strings) 
 * in a "Workspace" (does not have to read .java files from memory)
 * {@code AdHocJavaCode} abstraction.
 * 
 * A <B>JDK</B> (not just a <B>JRE</B>) Must be in the classpath to use 
 * the Javac tool (since this class uses methods in "javax.tools" package)
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public enum AdHocJavac
{
    INSTANCE; //singleton enum idiom//singleton enum idiom
	
    private static final Logger LOG = 
        LoggerFactory.getLogger( AdHocJavac.class );
    
	/** static reference to the Javac Compiler Tool (at Runtime)*/
    public final JavaCompiler JAVAC; 
    
	private AdHocJavac()
	{		
		JAVAC = ToolProvider.getSystemJavaCompiler(); 
	}
	
    public static Map<String, Class<?>> compileLoadClasses(
        AdHocClassLoader inMemoryClassLoader, 
        List<AdHocJavaFile> adHocJavaFiles, 
        JavacOptions.CompilerOption... compilerOptions )         
    {
        compile(inMemoryClassLoader, adHocJavaFiles, compilerOptions );
        
        Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
        for( int i = 0; i < adHocJavaFiles.size(); i++) 
        {
        	loadedClasses.put(adHocJavaFiles.get( i ).getClassName(), 
        		inMemoryClassLoader.findClass(adHocJavaFiles.get( i ).getClassName() ) );        	
        }
        return loadedClasses;       
    }

    /**
     * Compiles the Tailored Java Code and returns a {@code TailoreClass} 
     * representing the bytes of the compiled class
     * 
     * @param adHocClassLoader the classLoader
     * @param listOfJavaFiles the Code to be compiled
     * @param compilerOptions all of the options for calling javac at runtime
     * @return the Class representing the compiled 
     */
    public static Map<String, AdHocClassFile> compile( 
        AdHocClassLoader adHocClassLoader, 
        List<AdHocJavaFile> listOfJavaFiles, 
        JavacOptions.CompilerOption... compilerOptions )
    	throws JavacException
    {   
    	StringBuilder fileNames = new StringBuilder();
    	if( LOG.isDebugEnabled() )
    	{
    		for( int i = 0; i < listOfJavaFiles.size(); i++ )
    		{
    			if( i > 0 )
    			{
    				fileNames.append( ", " );
    			}
    			fileNames.append( listOfJavaFiles.get( i ).getClassName() );
    		}
    		LOG.debug( "Compiling [" + listOfJavaFiles.size() + "] Java classes {" + fileNames.toString() + "}");
    	}
    	StandardJavaFileManager baseFileManager = null; 
    	try
    	{
    		baseFileManager = 
    			INSTANCE.JAVAC.getStandardFileManager( 
    				null, //use default DiagnosticListener
    				null, //use default Locale
    				null );//use default CharSet 
    	}
    	catch( Exception e )
    	{
    		throw new JavacException (
    			"JDK version 1.6 or greater (NOT a JRE) MUST BE used to compile "
    		  + "Java  at Runtime, you are currently using \"" 
    		  + System.getProperty( "java.version" ) 
    		  + "\" make sure you have a JDK (NOT A JRE) running", e ); 
    	}

        AdHocJavaWorkspace workspace = 
            new AdHocJavaWorkspace(
                baseFileManager, 
                adHocClassLoader );
       
        DiagnosticCollector<JavaFileObject> diagnostics = 
            new DiagnosticCollector<JavaFileObject>();
         
        Iterable<String>javacOptions = JavacOptions.optionsFrom( compilerOptions );

        JavaCompiler.CompilationTask task = 
            INSTANCE.JAVAC.getTask(null, //use System.err for "additional" output from the compiler
                workspace, 
                diagnostics, 
                javacOptions, 
                null, //classes
                listOfJavaFiles );
        
        boolean compiledWithoutErrors = false;
        try
        {
        	compiledWithoutErrors = task.call();
        }
        catch( RuntimeException rte )
        {
        	throw new VarException(
                "Unable to compile workspace ", rte.getCause() );
        }        
        if( !compiledWithoutErrors )
        { 
        	
        	try
            {
        		baseFileManager.close();
            }
            catch( IOException ioe )
            {
            	LOG.warn( "Error closing BaseFileManager", ioe);
            }
        	
        	JavacException je = new JavacException(
        		"AdHoc", listOfJavaFiles, diagnostics );
        	
        	LOG.error( "Compilation Failed", je );
        	
        	throw je;
        }
        try
        {
        	baseFileManager.close();
        }
        catch( IOException ioe )
        {
        	LOG.warn( "Error closing BaseFileManager ", ioe);
        }
        LOG.debug("Done Compiling {"+ fileNames + "}" );
        
        return adHocClassLoader.getAdHocClassMap();            
    }
}
