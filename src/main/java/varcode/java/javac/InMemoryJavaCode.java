package varcode.java.javac;

import java.io.IOException;
import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

import varcode.VarException;
import varcode.java.JavaNaming;

/**
 * Encapsulates a compile-able Java unit of source code (a ".java" file)
 * for integrating with the {@code SimpleJavaFileObject} to be "fed" into
 * the Javac compiler Tool at Runtime (to convert from source to a class 
 * bytecodes)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InMemoryJavaCode
    extends SimpleJavaFileObject
{    
    /** the fully qualified name of the Source created */
    private final String className;
    
    /** the "actual" source code text that was tailored */
    private final String code;

    public CharSequence getCharContent( boolean ignoreEncodingErrors ) 
    {
        return code;
    }
    
    public static InMemoryJavaCode of ( JavaFileObject jfo )
    {
    	String code = null;
    	try
    	{
    		code = jfo.getCharContent( true ).toString();
    	}
    	catch( IOException ioe )
    	{
    		throw new VarException("Unable to read codwe from JavaFileObject \"" + jfo+"\"" );
    	}
    	String fullName = jfo.getName().replace(".java", ""); 
    	fullName = fullName.replace("\\", ".");
    	fullName = fullName.replace("/", ".");
    	String packageName = null;
    	String className = null;
    	if( fullName.contains( "." ) )
    	{
    		className = fullName.substring( fullName.lastIndexOf(".") + 1 );
    		packageName = fullName.substring( 0, fullName.lastIndexOf(".") );
    		return new InMemoryJavaCode( packageName, className, code );
    	}
    	else
    	{
    		className = fullName;
    		return new InMemoryJavaCode( className, code );
    	}
    }
    
    public InMemoryJavaCode( String className, String code )
    {
        super( 
            URI.create( "string:///" + className.replace('.', '/') + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        
        
        this.className = className;
        this.code = code;
        //System.out.println( "setting \""+ className+"\" to "+ uri );
    }
    
    public InMemoryJavaCode( String packageName, String className, String sourceCode )
    {
        super( 
            URI.create( 
                "string:///" + ( packageName + "." + className ).replace( '.', '/' ) 
                + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        this.className = JavaNaming.ClassName.toFullClassName( packageName, className );
        this.code = sourceCode;
    }
    
    public String getClassName()
    {
        return className;
    }

    public String getCode()
    {
        return code;
    }

    /** 
     * Gets the relative File Path that this code would reside 
     * based on the package hierarchy of the fully qualified class name.
     */
    public String getRelativeFilePath()
    {
        return JavaNaming.ClassName.toSourcePath( this.className );
    }

    public String getCodeId()
    {
        return className + ".java";
    }
    
    public String toString()
    {
        return code;
    }
}
