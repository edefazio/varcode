package varcode.java.adhoc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.java.metalang._javaComponent;

/**
 * A ClassLoader that maintains a cache Map of {@code InMemoryJavaClass}es by name
 * and delegates to the parent ClassLoader when resolving classes by name.
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
    	{ 
            LOG.trace( "Introducing Class \"" + adHocClass.getName() + "\" to classLoader" ); 
        }        
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
     * returns the count of Declared Classes (loaded directly
     * in this ClassLoader)
     * @return the number of Loaded Classes in this classLoader
     */
    public int countLoadedClasses()
    {
        return this.classNameToAdHocClass.size();
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
    	{    
            LOG.trace( "finding \"" + className + "\"" ); 
        }
            
        //Why am I checking this first??? -- BECAUSE overlead of loading
        Class loadedClass = this.findLoadedClass( className );
        if( loadedClass != null )
        {
            if( LOG.isTraceEnabled() )
            {    
                LOG.trace( "found loaded \"" + className + "\"" ); 
            }    
            return loadedClass;
        }
    	AdHocClassFile adHocClass = classNameToAdHocClass.get( className );
    	if( adHocClass == null ) 
    	{
            if( LOG.isTraceEnabled() )
            {    
                LOG.trace( className + " Not an AdHoc class, checking parent ClassLoader"); 
            }
    		return super.findClass(className );
    	}
    	if( LOG.isTraceEnabled() )
    	{    
            LOG.trace( "Defining unloaded AdHocClass \"" + className + "\" " ); 
        }
    	byte[] byteCode = adHocClass.toByteArray();
    	return defineClass( className, byteCode, 0, byteCode.length );    	
    }

    public Class<?> find( _javaComponent component )
    {
        return findBySimpleName( component.getName() );
    }
    
    /**
     * Finds the first class loaded within THIS classLoader that has this
     * simple name (i.e. no .'s )
     * 
     * DO:
     * Class c = findBySimpleName( "Map" ); 
     * 
     * DON'T: 
     * Class c = findBySimpleName( "java.util.Map" );
     * 
     * NOTE, this method Does NOT check the parent classLoader for this class
     * NOR does it differentiate between two classes that have the same SimpleName 
     * but are in different packages, so USE AT YOUR OWN RISK.
     * 
     * @param simpleName the simple name of the class
     * @return the Class with this name or null if it is not found
     * @throws VarException if there is no class found with this simple name
     */
    public Class<?> findBySimpleName( String simpleName )
        throws VarException
    {
        String[] classNames = 
            this.classNameToAdHocClass.keySet().toArray( new String[ 0 ] );
        
        for( int i = 0; i < classNames.length; i++ )
        {
            String cn = classNames[ i ];
            int start = cn.lastIndexOf( '.' );
            if( start < 0 )
            {
                start = 0;
            }
            else
            {
                start ++;
            }
            cn = cn.substring( start );
            if( cn.equals( simpleName ) )
            {
                try
                {
                    return findClass( classNames[ i ] );
                }
                catch( ClassNotFoundException cnfe )
                {
                    throw new VarException(
                        "Unable to find class by simple name " + simpleName , cnfe );
                }
            }
        }
        throw new VarException(
            "Unable to find class by simple name " + simpleName );
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
        this.classNameToAdHocClass.clear();
    }    
}
