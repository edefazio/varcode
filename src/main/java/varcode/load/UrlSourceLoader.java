package varcode.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

public class UrlSourceLoader
	implements SourceLoader
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( UrlSourceLoader.class );

    
	/** defines what these URLS represent, i.e. "GitHub", "BitBucket", ...*/
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
		URL url = sourceIdToUrl.get(sourceId );
		if( url != null )
		{
			return new UrlSourceStream( sourceId, url );
		}
		return null;
	}

	public UrlSourceLoader addMarkupUrl( String sourceId, String sourceUrl )
	{
		try 
		{
			URL url = new URL( sourceUrl );
			addMarkupUrl(sourceId, url );
			return this;
		} 
		catch( MalformedURLException e ) 
		{
			throw new VarException( 
				"Invalid URL \"" + sourceUrl + "\" for sourceId \"" + sourceId + "\"", e );
		}
	}
	
	public UrlSourceLoader addMarkupUrl( String sourceId, URL sourceUrl )
	{
		if( this.sourceIdToUrl.containsKey( sourceId ) )
		{
			LOG.info( "replacing source URL for \"" + sourceId + 
                "\" to \"" + sourceUrl + "\"" );
		}
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
				throw new VarException(
					"Unable to load input stream to \"" + url  +"\" for sourceId \"" 
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
                throw new VarException( 
                    " unable to read to a String ", ioe );
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
                while( ( line = br.readLine() ) != null ) 
                {
                    sb.append( line );
                    sb.append( '\n' );                
                }
                return sb.toString();
            }
            catch( IOException ioe )
            {
                throw new VarException( "Unable to get the String from the Stream" );
            }        
        }		
	}
}
