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
public class JavaClassFile 
    extends SimpleJavaFileObject 
    implements ClassNameQualified
{	        
    private final CacheBytesOutputStream cacheBytesOutputStream;
    
    /** the name of the Class */
    private final String className;
    
    /**
     * Initialize an in memory Java Class for a given class Name
     * @param className the full class name "io.varcode.MyValueObject"
     * @throws IllegalArgumentException
     * @throws URISyntaxException
     */
    public JavaClassFile( String className ) 
        throws IllegalArgumentException, URISyntaxException 
    {
        super( new URI( className ), Kind.CLASS );
        this.className = className;
        this.cacheBytesOutputStream = new CacheBytesOutputStream( uri );       
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
        
    /** The"FileManager"/"ClassLoader" writes the class' bytecodes to the local 
     * {@code ByteArrayOutputStream}) {@code inMemoryClassBytes}
     * @return OutputStream
     * @throws java.io.IOException
     */
    @Override
    public OutputStream openOutputStream() 
        throws IOException 
    {
        return cacheBytesOutputStream;
    }

    /** gets the Class bytecodes as an array of bytes
     * @return the byte array
     */
    public byte[] toByteArray() 
    {        
        return this.cacheBytesOutputStream.toByteArray();
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
    public static class CacheBytesOutputStream 
        extends OutputStream
    {
        /** 
         * Binary in-memory representation of the Class' bytecodes 
         * This Stream is used the First 
         */
        private final ByteArrayOutputStream initialOutputStream;
        
        /** Has the initialInputStream been completely written to? */
        private final AtomicBoolean isWritten;
        
        /** The Cached bytes that are stored locally (representing the Class bytecodes) */ 
        private byte[] bytes;
            
        /** the time in millis when the class was last modified */
        protected long lastModifiedMillis;
        
        /** the URI for the class file to be written */
        private final URI uri;
        
        public CacheBytesOutputStream( URI uri ) //AdHocJavaClassFile adHocClassFile )
        {   //this is the underlying OutputStream 
            this.uri = uri;
            initialOutputStream = new ByteArrayOutputStream();            
            this.isWritten = new AtomicBoolean( false );
            this.lastModifiedMillis = System.currentTimeMillis();            
        }
        
        @Override
        public void write( int b ) 
            throws IOException
        {
            initialOutputStream.write( b );
            this.lastModifiedMillis = System.currentTimeMillis();
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
        public void write( byte b[] ) 
            throws IOException 
        {
            initialOutputStream.write( b, 0, b.length );
            this.lastModifiedMillis = System.currentTimeMillis();
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
        public void write( byte b[], int off, int len ) 
            throws IOException 
        {
            initialOutputStream.write( b, off, len );
            this.lastModifiedMillis = System.currentTimeMillis();
        }
        
        public byte[] toByteArray()
        {
            //todo guard this
            if( this.isWritten.get() )
            {
                return bytes;
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
            super.flush();
            //now it's just bytes
            this.bytes = this.initialOutputStream.toByteArray();            
            super.close();
            this.isWritten.set( true );
            this.lastModifiedMillis = System.currentTimeMillis();
            //LOG.trace( "finshed writing bytes to class " + uri );
        }        
    }
}