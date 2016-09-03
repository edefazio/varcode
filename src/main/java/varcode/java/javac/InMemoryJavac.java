package varcode.java.javac;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
 * Compiles Java Code that exists (as a Strings) in a "Workspace" 
 * {@code InMemoryJavaCode} abstraction.
 * 
 * A <B>JDK</B> (not just a <B>JRE</B>) Must be in the classpath to use 
 * the Javac tool (since this class uses methods in "javax.tools" package)
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public enum InMemoryJavac
{
    INSTANCE; //singleton enum idiom
	
    private static final Logger LOG = 
        LoggerFactory.getLogger( InMemoryJavac.class );
    
	/** static reference to the Javac Compiler Tool (at Runtime)*/
    public final JavaCompiler JAVAC; 
    
	private InMemoryJavac()
	{		
		JAVAC = ToolProvider.getSystemJavaCompiler(); 
	}
	
    public static Map<String, Class<?>> compileLoadClasses(
        InMemoryJavaClassLoader inMemoryClassLoader, 
        List<InMemoryJavaCode> javaCode, 
        JavacOptions.CompilerOption... compilerOptions )         
    {
        compile( inMemoryClassLoader, javaCode, compilerOptions );
        
        Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
        for( int i = 0; i < javaCode.size(); i++) 
        {
        	loadedClasses.put(
        		javaCode.get( i ).getClassName(), 
        		inMemoryClassLoader.findClass( javaCode.get( i ).getClassName() ) );        	
        }
        return loadedClasses;       
    }
    
    /**
     * Compile a Single Java Source File Workspace and return the Class
     * 
     * @param inMemoryClassLoader classLoader
     * @param javaCode the JavaCode to compile
     * @param compilerOptions options for compiler
     * @return the bytecode (Java class)
     * @throws VarException
     
    public static InMemoryJavaClass compile(
    	InMemoryJavaClassLoader inMemoryClassLoader, 
        InMemoryJavaCode javaCode, 
        JavacOptions.CompilerOption... compilerOptions )
        throws VarException
    {
    	List<InMemoryJavaCode> codeList = new ArrayList<InMemoryJavaCode>();
    	codeList.add( javaCode );
    	List<InMemoryJavaClass>retList = 
    		compile( inMemoryClassLoader, codeList, compilerOptions );
    	return retList.get( 0 );
    }
    */
    /*
    public static Map<String, InMemoryJavaClass> compile(
        InMemoryJavaClassLoader inMemoryClassLoader, 
        List<InMemoryJavaCode> listOfJavaCode, 
        JavacOptions.CompilerOption... compilerOptions )
    	throws JavacException
    {
        
    }
    */
    /**
     * Compiles the Tailored Java Code and returns a {@code TailoreClass} 
     * representing the bytes of the compiled class
     * 
     * @param inMemoryClassLoader the classLoader
     * @param listOfJavaCode the Code to be compiled
     * @param compilerOptions all of the options for calling javac at runtime
     * @return the Class representing the compiled 
     */
    public static Map<String, InMemoryJavaClass> compile( 
        InMemoryJavaClassLoader inMemoryClassLoader, 
        List<InMemoryJavaCode> listOfJavaCode, 
        JavacOptions.CompilerOption... compilerOptions )
    	throws JavacException
    {   
    	StringBuilder javaSourceNames = new StringBuilder();
    	if( LOG.isDebugEnabled() )
    	{
    		for( int i = 0; i < listOfJavaCode.size(); i++ )
    		{
    			if (i > 0 )
    			{
    				javaSourceNames.append( ", " );
    			}
    			javaSourceNames.append( listOfJavaCode.get( i ).getClassName() );
    		}
    		LOG.debug( "Compiling ["+ listOfJavaCode.size()+"] Java classes {"+ javaSourceNames.toString() + "}");
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

        /*
        List<InMemoryJavaClass> tailorClassTargets = 
        	new ArrayList<InMemoryJavaClass>();
        
        for( int i = 0; i < listOfJavaCode.size(); i++ )
        {	
        	try	 
			{
        		tailorClassTargets.add(
        			new InMemoryJavaClass( listOfJavaCode.get( i ).getClassName() ) );
			} 
			catch( IllegalArgumentException e ) 
			{
				throw new JavacException( 
					"Could not create (In Memory) Java Class for \"" 
					+ listOfJavaCode.get( i ).getClassName() + "\"", e );
			} 
			catch( URISyntaxException e ) 
			{
				throw new JavacException( 
					"Could not create (In Memory) Java Class for \"" 
					+ listOfJavaCode.get( i ).getClassName() + "\"", e );
			}
        } 
        */
        InMemoryJavaWorkspace workspace = 
            new InMemoryJavaWorkspace(
                baseFileManager, 
                //tailorClassTargets, 
                inMemoryClassLoader );
       
        DiagnosticCollector<JavaFileObject> diagnostics = 
            new DiagnosticCollector<JavaFileObject>();
         
        Iterable<String>javacOptions = JavacOptions.optionsFrom( compilerOptions );

        JavaCompiler.CompilationTask task = 
            INSTANCE.JAVAC.getTask(null, //use System.err for "additional" output from the compiler
                workspace, 
                diagnostics, 
                javacOptions, 
                null, //classes
                listOfJavaCode );
        
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
        		"AdHoc", listOfJavaCode, diagnostics );
        	
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
        LOG.debug( "Done Compiling {"+ javaSourceNames + "}" );
        
        return inMemoryClassLoader.getInMemoryClassMap();
        //return tailorClassTargets;            
    }
}
