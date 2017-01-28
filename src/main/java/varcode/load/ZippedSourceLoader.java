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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import varcode.LoadException;
import varcode.load.Source.SourceFromClassLoader;
import varcode.load.Source.SourceInputStream;
import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;

/**
 * Reads Source files from within a Jar or Zip
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ZippedSourceLoader
    implements SourceLoader, SourceFromClassLoader
{
    private final String pathToZipFile;

    private ZipFile zipFile;

    /**
     * Statically 
     * @param pathToJarFile
     * @return 
     */
    public static ZippedSourceLoader of( String pathToJarFile )
    {
        return new ZippedSourceLoader( pathToJarFile );
    }
    
    public ZippedSourceLoader( String pathToZipFile )
    {
        this.pathToZipFile = pathToZipFile;
        zipFile = null;
        try
        {
            this.zipFile = new ZipFile( pathToZipFile );            
            Enumeration it = zipFile.entries();
            while( it.hasMoreElements() )
            {
                ZipEntry ze = (ZipEntry)it.nextElement();
                System.out.println( ze );
            }            
        }
        catch( IOException ioe )
        {
            System.err.println("cannot load jar/zip file " + pathToZipFile );
        }
    }

    @Override
    public SourceStream sourceStream( final String sourceId )
    {
        final ZipEntry ze = zipFile.getEntry( 
            SourceLanguages.resolvePath( sourceId ) );
        //System.out.println( "Found zip entry  " + ze );
        if( ze != null )
        {            
            try
            {
                return new ZipEntrySourceStream(
                    zipFile.getInputStream( ze ), sourceId, pathToZipFile );
            }
            catch( IOException ioe )
            {
                System.err.println( "ARG " + ioe );
            }
        }
        return null;
    }

    public static class ZipEntrySourceStream
        implements SourceStream
    {
        private final InputStream is;
        private final String sourceId;
        private final String pathToJar;

        public ZipEntrySourceStream(
            InputStream is, String sourceId, String pathToJar )
        {
            this.is = is;
            this.sourceId = sourceId;
            this.pathToJar = pathToJar;
        }

        @Override
        public InputStream getInputStream()
        {
            return is;
        }

        @Override
        public String getSourceId()
        {
            return sourceId;
        }

        @Override
        public String describe()
        {
            return "[Jar " + pathToJar + "] : " + sourceId;
        }

        @Override
        public String toString()
        {
            return describe();
        }
        
        @Override
        public String asString()
        {
            try
            {
                return SourceInputStream.streamAsString( is );
            }
            catch( IOException ioe )
            {
                throw new LoadException( ioe );
            }
        }
    }

    @Override
    public String describe()
    {
        return "[Jar]: " + pathToZipFile;
    }

    @Override
    public String toString()
    {
        return describe();
    }
    
    @Override
    public SourceStream sourceOf( Class clazz )
    {
        return sourceStream( 
            clazz.getCanonicalName().replace( ".", File.separator ) + ".java" );
    }
}
