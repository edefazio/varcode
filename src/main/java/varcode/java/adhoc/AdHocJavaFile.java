package varcode.java.adhoc;

import java.io.IOException;
import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

import varcode.VarException;
import varcode.java.JavaNaming;

/**
 * Encapsulates a compile-able AdHoc Java unit of source code (a ".java" file)
 * for integrating with the {@code SimpleJavaFileObject} to be "fed" into
 * the Javac compiler Tool at Runtime (to convert from source to a class 
 * bytecodes)
 * 
 * NOTE: a single AdHocCodeFile can contain MANY Classes 
 * (Inner Classes, Anonymous Classes
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocJavaFile
    extends SimpleJavaFileObject
{    
    /** the fully qualified name of the Source created (i.e. "ex.varcode.MyClass") */
    private final String className;
    
    /** the "actual" source code content of the class */
    private final String code;

    /**
     * returns the content of the class
     * @param ignoreEncodingErrors
     * @return 
     */
    public CharSequence getCharContent( boolean ignoreEncodingErrors ) 
    {
        return code;
    }
    
    /**
     * creates and returns an AdHocJavaCode representing the javaFileObject
     * @param javaFileObject object representing a Java File (class)
     * @return the generated AdHocJavaCode
     */
    public static AdHocJavaFile of( JavaFileObject javaFileObject )
    {
    	String code = null;
    	try
    	{
    		code = javaFileObject.getCharContent( true ).toString();
    	}
    	catch( IOException ioe )
    	{
    		throw new VarException(
                "Unable to read code from JavaFileObject \"" + javaFileObject 
              + "\"", ioe );
    	}
        //the className does not have the 
        String fullName = null;
        if( javaFileObject.getName().endsWith( ".java" ) )
        {
            fullName = 
                javaFileObject.getName().substring( 
                    0, javaFileObject.getName().lastIndexOf( ".java" ) );
        }
        else
        {
            fullName = javaFileObject.getName();
        }
    	fullName = fullName.replace( "\\", "." );
    	fullName = fullName.replace( "/", "." );
        
    	if( fullName.contains( "." ) )
    	{
    		String className = 
                fullName.substring( fullName.lastIndexOf( "." ) + 1 );
    		String packageName = 
                fullName.substring( 0, fullName.lastIndexOf( "." ) );
    		return new AdHocJavaFile( packageName, className, code );
    	}
    	else
    	{
    		return new AdHocJavaFile( fullName, code );
    	}
    }
    
    public AdHocJavaFile( String className, String code )
    {
        super( 
            URI.create( 
                "string:///" + className.replace('.', '/') + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        
        this.className = className;
        this.code = code;
    }
    
    public AdHocJavaFile( 
        String packageName, String className, String code )
    {
        super( 
            URI.create( 
                "string:///" + ( packageName + "." + className ).replace( '.', '/' ) 
                + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        this.className = JavaNaming.ClassName.toFullClassName( packageName, className );
        this.code = code;
    }
    
    public String getClassName()
    {
        return className;
    }

    public String getCode()
    {
        return code;
    }

    @Override
    public String toString()
    {
        return code;
    }
}
