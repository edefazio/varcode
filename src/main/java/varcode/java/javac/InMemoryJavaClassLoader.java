package varcode.java.javac;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

/**
 * A ClassLoader that caches {@code InMemoryJavaClass}es (containers
 * for Java class bytecodes) and delegates to the parent ClassLoader
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InMemoryJavaClassLoader
    extends ClassLoader
{
    private static final Logger LOG = 
         LoggerFactory.getLogger( InMemoryJavaClassLoader.class );
    
    private Map<String, InMemoryJavaClass> classNameToInMemoryClass = 
        new HashMap<String, InMemoryJavaClass>();

    public InMemoryJavaClassLoader()
    {
        this( ClassLoader.getSystemClassLoader() );
    }
    
    public InMemoryJavaClassLoader( ClassLoader parent ) 
    {
        super( parent );
    }

    /** Adds/Loads the inMemoryClass to the classLoader*/
    public void introduce( InMemoryJavaClass inMemoryClass ) 
    {
    	if( LOG.isDebugEnabled() ) 
    	{ LOG.debug( "Introducing Class \"" + inMemoryClass.getName() + "\" to classLoader" ); }
    	
        classNameToInMemoryClass.put( inMemoryClass.getName(), inMemoryClass );
    }

    /** gets the String(name) to InMemoryClass Mapping for classes loaded in Memory*/
    public Map<String, InMemoryJavaClass>getInMemoryClassMap()
    {
    	return classNameToInMemoryClass;
    }
    
    @Override
    public Class<?> findClass( String name ) 
        throws VarException 
    {
    	try
    	{
    		if( LOG.isTraceEnabled() )
    		{    LOG.trace( "ClassLoader trying to load \"" + name + "\"" ); }
    		InMemoryJavaClass inMemClass = classNameToInMemoryClass.get( name );
    		if( inMemClass == null ) 
    		{
    			if( LOG.isTraceEnabled() )
        		{    LOG.trace( "Class Is Not in Memory, checking parent ClassLoader for \"" + name + "\"" ); }
    			return super.findClass( name );
    		}
    		if( LOG.isTraceEnabled() )
    		{    LOG.trace( "Found Class \"" + name + "\" in memory" ); }
    		byte[] byteCode = inMemClass.toByteArray();
    		return defineClass( name, byteCode, 0, byteCode.length );
    	}
    	catch( ClassNotFoundException e )
    	{
    		throw new VarException( 
    			"Couldn't find class \"" + name + "\"", e );
    	}
    }
}
