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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import varcode.LoadException;
import varcode.load.Source.SourceStream;
import varcode.load.Source.SourceLoader;

public class UrlSourceLoader
    implements SourceLoader
{
    //private static final Logger LOG
    //    = LoggerFactory.getLogger( UrlSourceLoader.class );

    /**
     * defines what these URLS represent, i.e. "GitHub", "BitBucket", ...
     */
    public final String name;

    public final Map<String, URL> sourceIdToUrl;

    public UrlSourceLoader( String name )
    {
        this( name, new HashMap<String, URL>() );
    }

    public UrlSourceLoader( String name, Map<String, URL> sourceIdToUrl )
    {
        this.name = name;
        this.sourceIdToUrl = sourceIdToUrl;
    }

    @Override
    public SourceStream sourceStream( String sourceId )
    {
        URL url = sourceIdToUrl.get( sourceId );
        if( url != null )
        {
            return new UrlSourceStream( sourceId, url );
        }
        return null;
    }

    public UrlSourceLoader addSourceUrl( String sourceId, String sourceUrl )
    {
        try
        {
            URL url = new URL( sourceUrl );
            addSourceUrl( sourceId, url );
            return this;
        }
        catch( MalformedURLException e )
        {
            throw new LoadException(
                "Invalid URL \"" + sourceUrl + "\" for sourceId \"" + sourceId + "\"", e );
        }
    }

    public UrlSourceLoader addSourceUrl( String sourceId, URL sourceUrl )
    {        
        this.sourceIdToUrl.put( sourceId, sourceUrl );
        return this;
    }

    @Override
    public String describe()
    {
        return "[URL \"" + name + "\"]";
    }

    public static class UrlSourceStream
        implements SourceStream
    {
        public final URL url;
        public final String sourceId;

        public UrlSourceStream( String sourceId, URL url )
        {
            this.url = url;
            this.sourceId = sourceId;
        }

        @Override
        public InputStream getInputStream()
        {
            try
            {
                return url.openStream();
            }
            catch( IOException ioe )
            {
                throw new LoadException(
                    "Unable to load input stream to \"" + url + "\" for sourceId \""
                    + sourceId + "\"", ioe );
            }
        }

        @Override
        public String getSourceId()
        {
            return sourceId;
        }

        @Override
        public String describe()
        {
            return "[URL] " + url;
        }

        @Override
        public String asString()
        {
            try
            {
                return getFileContent( getInputStream(), "UTF-8" );
            }
            catch( IOException ioe )
            {
                throw new LoadException(
                    "unable to read to a String ", ioe );
            }
        }

        public static String getFileContent( InputStream fis, String encoding )
            throws IOException
        {
            try
            {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader( fis, encoding ) );

                StringBuilder sb = new StringBuilder();
                String line;
                while( (line = br.readLine()) != null )
                {
                    sb.append( line );
                    sb.append( '\n' );
                }
                return sb.toString();
            }
            catch( IOException ioe )
            {
                throw new LoadException( "Unable to get the String from the Stream" );
            }
        }
    }
}
