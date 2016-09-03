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
public class InMemoryJavaClassLoader
    extends ClassLoader
{
    private static final Logger LOG = 
         LoggerFactory.getLogger( InMemoryJavaClassLoader.class );
    
    /** Maps the class name to the InMemoryClass */
    private final Map<String, InMemoryJavaClass> classNameToInMemoryClass;
       
    public InMemoryJavaClassLoader()
    {
        this( ClassLoader.getSystemClassLoader() );
    }
    
    public InMemoryJavaClassLoader( ClassLoader parent ) 
    {
        super( parent );
        classNameToInMemoryClass
          = new HashMap<String, InMemoryJavaClass>();
    }

    /** Adds/Loads the inMemoryClass to the Loaded Classes Map */
    public void introduce( InMemoryJavaClass inMemoryClass ) 
    {
    	if( LOG.isDebugEnabled() ) 
    	{ LOG.debug( "Introducing Class \"" + inMemoryClass.getName() + "\" to classLoader" ); }
    	
        classNameToInMemoryClass.put( inMemoryClass.getName(), inMemoryClass );
    }

    /** className to InMemoryClass Mapping for classes loaded in Memory
     */
    public Map<String, InMemoryJavaClass>getInMemoryClassMap()
    {
    	return classNameToInMemoryClass;
    }
    
    /**
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
    		{    LOG.trace("ClassLoader trying to load \"" + className + "\"" ); }
    		InMemoryJavaClass inMemClass = classNameToInMemoryClass.get(className );
    		if( inMemClass == null ) 
    		{
    			if( LOG.isTraceEnabled() )
        		{    LOG.trace( "Class Is Not in Memory, checking parent ClassLoader for \"" + className + "\"" ); }
    			return super.findClass(className );
    		}
    		if( LOG.isTraceEnabled() )
    		{    LOG.trace("Found Class \"" + className + "\" in memory" ); }
    		byte[] byteCode = inMemClass.toByteArray();
    		return defineClass(className, byteCode, 0, byteCode.length );
    	}
    	catch( ClassNotFoundException e )
    	{
    		throw new VarException( 
    			"Couldn't find class \"" + className + "\"", e );
    	}
    }
}
