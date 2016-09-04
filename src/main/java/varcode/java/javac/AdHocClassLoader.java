package varcode.java.javac;

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

    /** Adds/Loads the inMemoryClass to the Loaded Classes Map */
    public void introduce( AdHocClassFile adHocClass ) 
    {
    	if( LOG.isDebugEnabled() ) 
    	{ LOG.debug( "Introducing Class \"" + adHocClass.getName() + "\" to classLoader" ); }
    	
        classNameToAdHocClass.put( adHocClass.getName(), adHocClass );
    }

    /** 
     * className to InMemoryClass Mapping for classes loaded in Memory
     */
    public Map<String, AdHocClassFile>getAdHocClassMap()
    {
    	return classNameToAdHocClass;
    }
    
    /**
     * Loads the class if need be and returns
     * (Unlike loadClass, throws a RuntimeException and not a 
     * CheckedException)
     * 
     * @param className the fully qualified class name to resolve 
     * (i.e. "ex.varcode.MyAuthored")
     * @return the class (Or null if the class is not found in this classLoader 
     * or the parent ClassLoader
     * @throws VarException 
     */
    public Class<?> findClass( String className ) 
        throws VarException 
    {
    	try
    	{            
    		if( LOG.isTraceEnabled() )
    		{    LOG.trace( "finding \"" + className + "\"" ); }
            
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
    	catch( ClassNotFoundException e )
    	{
    		throw new VarException( 
    			"Couldn't find class \"" + className + "\"", e );
    	}
    }
}
