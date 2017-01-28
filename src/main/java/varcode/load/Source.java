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
package varcode.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import varcode.LoadException;

/**
 * Documents / Configuration files / Code that is Data to be 
 * found, and read in for the purposes of templating, auditing, 
 * meta-programming, etc.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Source
{
    /** */
    public interface SourcePathResolver
    {
        /**
         * resolve the relative path to the sourceId: Note, depending on the
         * sourceId "extension", this can change:
         *
         * @param sourceId
         * @return
         */
        public String pathTo( String sourceId );

        public enum DotPathResolver
            implements SourcePathResolver
        {
            INSTANCE;

            @Override
            public String pathTo( String sourceId )
            {
                return sourceId.replace( '.', File.separatorChar );
            }
        }

        public enum FlatPathResolver
            implements SourcePathResolver
        {
            INSTANCE;

            @Override
            public String pathTo( String sourceId )
            {
                return sourceId;
            }
        }
    }

    public interface LanguageResolver
    {
        /**
         * Resolve and return the Language from the Source ID (based off of the
         * file extension, return the Language of the source)
         * <PRE>
         * i.e. "someFile.c" returns "C"
         * i.e. "com.java.somepackage.Blahde.java" returns "Java"
         * </PRE>
         */
        public String languageOf( String sourceId );
    }

    public static class SourceInputStream
    {
        /**
         * Reads the source input Stream as a String using the default 
         * UTF-8 encoding
         * @param is the inoputStream to read from
         * @return the String content within the inputStream
         * @throws IOException 
         */
        public static String streamAsString( InputStream is )
            throws IOException
        {
            return streamAsString( is, "UTF-8" );
        }
        
        /**
         * returns the contents of the InputStream as a String
         * @param is the inputStream
         * @param encoding the encoding of the inputStream
         * @return a String representation of the contents of the inputStream
         * @throws IOException if uable to read the Stream
         */
        public static String streamAsString( 
            InputStream is, String encoding ) 
            throws IOException
        {
            try
            {
                System.out.println( "ENCODING "+ encoding );
                BufferedReader br = new BufferedReader( 
                    new InputStreamReader( is, encoding ) );
                StringBuilder sb = new StringBuilder();
                String line;
                while(( line = br.readLine()) != null ) 
                {
                    sb.append( line );
                    sb.append( '\n' );                
                }
                //is.reset();
                is.close();
                return sb.toString();
            }
            catch( IOException ioe )
            {
                throw new LoadException( 
                    "Unable to get the String from the Stream", ioe );
            }        
        }
    }
    /**
     * Input Stream containing the Source being read in
     */
    public interface SourceStream
    {
        /**
         * An InputStream to Source
         *
         * @return the inputStream for this source
         */
        InputStream getInputStream();

        /**
         * the identity of the source to be loaded
         *
         * @return the sourceId for this stream
         */
        String getSourceId();

        /**
         * describe the nature of the Stream (a File, a (remote Server, etc.)
         *
         * @return description of this source
         */
        String describe();

        /**
         * Return the contents of the InputStream as a String
         *
         * @return the Textual representation of the source stream
         */
        String asString();
    }

    /**
     * VERY *Similar* to <B>a single entry in the -sourcepath</B> when using
     * javac.
     *
     * Defines a path containing varcode source (i.e. _*.java files). These
     * files COULD be:
     * <UL>
     * <LI>inline with existing project source code
     * <LI>in a zip file
     * <LI>in a separate source folder
     * <LI>in a compressed resource bundle/archive
     * <LI>stored on a remote server
     * <LI>in a version control system
     * <LI>...
     * </UL>
     *
     * Instead of using a ClassLoader to return a specific .class file, which
     * could either be returns a {@code Markup}.
     */
    public interface SourceLoader
    {
        /**
         * @param sourceId the name of the source to be loaded (i.e. for Java
         * the fully qualified Java class name like:
         * <BLOCKQUOTE>"java.io.InputStream.java"</BLOCKQUOTE>
         *
         * @return a {@code MarkupStream} for streaming in the text of the
         * markup
         * <B>null</B> if a no MarkupStream is found in this Loader with
         * "sourceId".
         */
        public SourceStream sourceStream( String sourceId );

        /**
         * Each element of the Path should be able to describe itself (most of
         * the time the "repo" will be a path on the fileSystem) so I can print
         * out the order/strategy that was used to resolve a specific source
         * file.
         *
         * (I want to be able to print out the VarSourcePath in text if the
         * VarSource for a given class is not found)
         *
         * @return a String that describes a repository (a file path, a jar
         * file, URI, etc.)
         */
        public String describe();

    }
    
    /**
     * Given a Runtime Class, loads the (.java) Source for the Class
     * Using java conventions: 
     * <PRE>
     * a class will qualified name : "my.pack.age.SomeClass.class" 
     * ...would have the sourceId  : "my.pack.age.SomeClass.java"
     * </PRE>
     */
    public interface SourceFromClassLoader
    {
        public SourceStream sourceOf( Class clazz );
    }
}
