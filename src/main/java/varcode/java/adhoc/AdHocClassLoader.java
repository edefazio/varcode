package varcode.java.adhoc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

/**
 * A ClassLoader that caches {@code InMemoryJavaClass}es (containers
 * for Java class bytecodes) and delegates to the parent ClassLoader
 * when resolving classes by name.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocClassLoader
    extends ClassLoader
{
    private static final Logger LOG = 
         LoggerFactory.getLogger( AdHocClassLoader.class );
    
    /** Maps the class name to the AdHocJavaClass */
    private final Map<String, AdHocClassFile> classNameToAdHocClass;
       
    public AdHocClassLoader()
    {
        this( ClassLoader.getSystemClassLoader() );
    }
    
    public AdHocClassLoader( ClassLoader parent ) 
    {
        super( parent );
        classNameToAdHocClass = new HashMap<String, AdHocClassFile>();
    }

    /** 
     * Adds the adHocMemoryClass to the Loaded Classes Map
     * @param adHocClass 
     */
    public void introduce( AdHocClassFile adHocClass ) 
    {
    	if( LOG.isTraceEnabled() ) 
    	{ LOG.trace( "Introducing Class \"" + adHocClass.getName() + "\" to classLoader" ); }
    	
        classNameToAdHocClass.put( adHocClass.getName(), adHocClass );
    }

    /** 
     * className to AdHocClassFile Mapping for classes loaded in Memory
     * @return the Map of classname to AdHocClassFile
     */
    public Map<String, AdHocClassFile>getAdHocClassMap()
    {
    	return classNameToAdHocClass;
    }
    
    /**
     * Loads the class if need be and returns
     * (Unlike loadClass, throws an UNCHECKED VarException and not a 
     * CheckedException if the class is not found)
     * 
     * @param className the fully qualified class name to resolve 
     * (i.e. "ex.varcode.MyAuthored")
     * @return the class (Or null if the class is not found in this classLoader 
     * or the parent ClassLoader
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public Class<?> findClass( String className ) 
        throws ClassNotFoundException 
    {
        if( LOG.isTraceEnabled() )
    	{    LOG.trace( "finding \"" + className + "\"" ); }
            
        //Why am I checking this first??? --
        Class loadedClass = this.findLoadedClass( className );
        if( loadedClass != null )
        {
            if( LOG.isTraceEnabled() )
        	{    LOG.trace( "found loaded \"" + className + "\"" ); }    
            return loadedClass;
        }
    	AdHocClassFile adHocClass = classNameToAdHocClass.get( className );
    	if( adHocClass == null ) 
    	{
    		if( LOG.isTraceEnabled() )
        	{    LOG.trace( className + " Not an AdHoc class, checking parent ClassLoader"); }
    		return super.findClass(className );
    	}
    	if( LOG.isTraceEnabled() )
    	{    LOG.trace( "Defining unloaded AdHocClass \"" + className + "\" " ); }
    	byte[] byteCode = adHocClass.toByteArray();
    	return defineClass( className, byteCode, 0, byteCode.length );    	
    }

    /**
     * A convenient method use in leu of {@code findClass()} or {@code loadClass()}
     * which throws a VarException (RuntimeException) if the classLoader
     * cannot resolve a class (verses a ClassNotFound CheckedException).  
     * 
     * @param className the name of the class to resolve
     * @return the Class
     * @throws VarException (wrapping a Checked RuntimeException) if the class cannot
     * be found
     */
    public Class<?> find( String className )
    {
        try 
        {
            return findClass( className );
        }
        catch( ClassNotFoundException ex ) 
        {
            throw new VarException(
                "Could not resolve class \"" + className + "\"", ex);
        }
    }
        
    /**
     * Remove ALL adHoc Classes from this Class Loader
     */
    public void unloadAll()
    {
        //What I COULD do, is keep the names in the Map
        // so that I could RELOAD the classes (PERHAPS)
        this.classNameToAdHocClass.clear();
    }    
}
