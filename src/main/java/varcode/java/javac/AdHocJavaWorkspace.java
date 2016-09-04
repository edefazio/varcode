package varcode.java.javac;
import java.io.IOException;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import varcode.VarException;

/**
 * A FileManager that "fronts"/"spoofs" a normal file System
 * and instead reads Java Classes from stored/in-memory byte arrays and writes
 * Java Classes to in memory byte arrays (rather than to disk as a .class file). 
 * 
 * (Any classes not found in the "extended" File Manager are "forwarded"
 * to the "parent" fileManager {@see ForwardingJavaFileManager} 
 * 
 * The idea is to give the illusion that Tailored classes 
 * (stored in memory as a bytes) are treated the same as  
 * ".class" files Loaded from the File System or on the classpath. 
 * 
 * This DOES incur extra memory cost (of keeping the Class in memory)
 * but does not require classes to be created and written to disk before
 * being Loaded by the ClassLaoder {@see AdHocJavaClassLoader} 
 * 
 * This is adapted from: 
 * <A HREF="https://github.com/trung/InMemoryJavaCompiler">InMemoryJavaCompiler</A>
 * Created by trung on 5/3/15.
 */
public class AdHocJavaWorkspace 
    extends ForwardingJavaFileManager<JavaFileManager> 
{
    /** ClassLoader for loading Java Code that exists in memory 
     * (as a byte array) vs from a file */
    private final AdHocClassLoader adHocClassLoader;

    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     * @param adHocClassLoader
     */
    public AdHocJavaWorkspace(
        JavaFileManager fileManager, 
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
        throws IOException 
    {    	
    	AdHocClassFile imjc =
    		this.adHocClassLoader.getAdHocClassMap().get( className );	
    	if( imjc != null )
    	{
    		return imjc;
    	}
    	try
    	{
    		imjc = new AdHocClassFile( className );
    		this.adHocClassLoader.introduce( imjc );
    		return imjc;
    	}
    	catch( Exception e )
    	{
    		throw new VarException( 
                "Unable to create output class for class \""+className+"\"" );
    	}
    }

    public ClassLoader getClassLoader( 
        JavaFileManager.Location location ) 
    {
        return adHocClassLoader;
    }
}