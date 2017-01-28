/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.adhoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.tools.SimpleJavaFileObject;
import varcode.java.ClassNameQualified;

/** 
 * Container to hold a Java (.class file) (bytecodes) in memory
 * 
 * Implementation of a {@code SimpleFileObject} that maintains the Java 
 * bytecode (binary) for a class "In-Memory" as a local 
 * {@code ByteArrayOutputStream} 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocClassFile 
    extends SimpleJavaFileObject 
    implements ClassNameQualified
{	        
    /** 
     * Specific OutputStream used to register the 
     * Class when the JAVAC process has completed writing to the
     * in-memory Stream at runtime. (Delays registering a Class in
     * the AdHocClassLoader until after it has 
     */
    private final RegisterOnCloseOutputStream adHocClassBytecode;
    
    /** the name of the Class */
    private final String className;
    
    /** the Class Loader */
    private final AdHocClassLoader classLoader;
    
    /**
     * Initialize an in memory Java Class for a given class Name
     * @param classLoader
     * @param className the full class name "io.varcode.MyValueObject"
     * @throws IllegalArgumentException
     * @throws URISyntaxException
     */
    public AdHocClassFile( AdHocClassLoader classLoader, String className ) 
        throws IllegalArgumentException, URISyntaxException 
    {
        super( new URI( className ), Kind.CLASS );
        this.className = className;
        this.adHocClassBytecode = new RegisterOnCloseOutputStream( this );
        this.classLoader = classLoader;
    }

    /**
     * The toString 
     * @return 
     */
    @Override
    public String toString()
    {
        return getQualifiedName() + ".class : AdHocClassFile@" + Integer.toHexString( hashCode() );
    }
    
    /** returns the className of the class 
    public String getClassName()
    {
        return this.className;
    }
    */
    
    /** The"FileManager"/"ClassLoader" writes the class' bytecodes to the local 
     * {@code ByteArrayOutputStream}) {@code inMemoryClassBytes}
     * @return OutputStream
     * @throws java.io.IOException
     */
    @Override
    public OutputStream openOutputStream() 
        throws IOException 
    {
        return adHocClassBytecode;
    }

    /** gets the Class bytecodes as an array of bytes
     * @return the byte array
     */
    public byte[] toByteArray() 
    {
        return adHocClassBytecode.toByteArray();
    }

    @Override
    public String getQualifiedName()
    {
        return this.className;
    }
    
    /**
     * This class exists to delay the registering of a Class
     * in the ClassLoader until AFTER the compiler has compiled it
     * and written the compiled bytecodes into the AdHocJavaClass and closed 
     * the stream.
     * 
     * so that between the time in which Javac (the runtime Java
     * compiler)attempts to load a class 
     * 
     * that has not been loaded, and when the class
     * has finally been read in, compiled and the finalized
     */
    public static class RegisterOnCloseOutputStream 
        extends OutputStream
    {
        /** Binary in-memory representation of the Class' bytecodes */
        private final ByteArrayOutputStream classBytecodeOutputStream;
        
        private final AdHocClassFile adHocClassFile;
        
        private final AtomicBoolean isWritten;
        
        public RegisterOnCloseOutputStream( AdHocClassFile adHocClassFile )
        {   //this is the underlying OutputStream 
            classBytecodeOutputStream = new ByteArrayOutputStream();
            //this.classLoader = classLoader;
            this.adHocClassFile = adHocClassFile;
            this.isWritten = new AtomicBoolean( false );
        }
        
        @Override
        public void write( int b ) 
            throws IOException
        {
            classBytecodeOutputStream.write( b );
        }
        
        /**
        * Writes <code>b.length</code> bytes from the specified byte array
        * to this output stream. The general contract for <code>write(b)</code>
        * is that it should have exactly the same effect as the call
        * <code>write(b, 0, b.length)</code>.
        *
        * @param      b   the data.
        * @exception  IOException  if an I/O error occurs.
        * @see        java.io.OutputStream#write(byte[], int, int)
        */
        @Override
        public void write(byte b[]) throws IOException 
        {
            classBytecodeOutputStream.write( b, 0, b.length );
        }

        /**
        * Writes <code>len</code> bytes from the specified byte array
        * starting at offset <code>off</code> to this output stream.
        * The general contract for <code>write(b, off, len)</code> is that
        * some of the bytes in the array <code>b</code> are written to the
        * output stream in order; element <code>b[off]</code> is the first
        * byte written and <code>b[off+len-1]</code> is the last byte written
        * by this operation.
        * <p>
        * The <code>write</code> method of <code>OutputStream</code> calls
        * the write method of one argument on each of the bytes to be
        * written out. Subclasses are encouraged to override this method and
        * provide a more efficient implementation.
        * <p>
        * If <code>b</code> is <code>null</code>, a
        * <code>NullPointerException</code> is thrown.
        * <p>
        * If <code>off</code> is negative, or <code>len</code> is negative, or
        * <code>off+len</code> is greater than the length of the array
        * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
        *
        * @param      b     the data.
        * @param      off   the start offset in the data.
        * @param      len   the number of bytes to write.
        * @exception  IOException  if an I/O error occurs. In particular,
        *             an <code>IOException</code> is thrown if the output
        *             stream is closed.
        */
        @Override
        public void write(byte b[], int off, int len) 
            throws IOException 
        {
            classBytecodeOutputStream.write( b, off, len );
        }
        
        public byte[] toByteArray()
        {
            //todo guard this
            if( this.isWritten.get() )
            {
                return classBytecodeOutputStream.toByteArray();
            }
            throw new AdHocException( "the class has not been fully loaded yet" );
        }
        
        /**
         * Closes the stream and registers the Class with the ClassLoader
         * @throws IOException 
         */
        @Override
        public void close()
            throws IOException
        {
            //this is saying that I finished writing data to the class
            // so NOW i should 
            super.close();
            //here is where I should register the class with the 
            adHocClassFile.classLoader.loadAdHocClass( adHocClassFile );
            this.isWritten.set( true );
            //this.classLoader.loadAdHocClass( this );            
        }
        
    }
}