package varcode.java.javac;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.tools.SimpleJavaFileObject;

/** 
 * Implementation of a {@code SimpleFileObject} that maintains the Java 
 * bytecode (binary) for a class "In-Memory" as a local 
 * {@code ByteArrayOutputStream} 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InMemoryJavaClass 
    extends SimpleJavaFileObject
{	
    /** Binary in-memory representation of the Class' bytecodes */
    private ByteArrayOutputStream inMemoryBytecode = 
        new ByteArrayOutputStream();

    /**
     * Initialize 
     * @param className
     * @throws Exception
     */
    public InMemoryJavaClass( String className ) 
        throws IllegalArgumentException, URISyntaxException 
    {
        super( new URI( className ), Kind.CLASS );
    }

    @Override
    /** The"FileManager"/"ClassLoader" writes the class' bytecodes to the local 
     * {@code ByteArrayOutputStream}) {@code inMemoryClassBytes}
     */
    public OutputStream openOutputStream() 
        throws IOException 
    {
        return inMemoryBytecode;
    }

    /** gets the Class bytecodes as an array of Bytes */
    public byte[] toByteArray() 
    {
        return inMemoryBytecode.toByteArray();
    }

    /*
    public Lang getLanguage()
    {
        return Lang.JAVA;
    }
    */
}