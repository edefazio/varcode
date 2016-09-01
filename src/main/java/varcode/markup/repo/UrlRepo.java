package varcode.markup.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;

public class UrlRepo
	implements MarkupRepo
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( UrlRepo.class );

    
	/** defines what these URLS represent, i.e. "GitHub", "BitBucket", ...*/
	public final String name;
	
	public final Map<String, URL> markupIdToUrl;
	
	public UrlRepo( String name )
	{
		this( name, new HashMap<String, URL>() );		
	}
	
	public UrlRepo( String name, Map<String, URL> markupIdToUrl )
	{
		this.name = name;
		this.markupIdToUrl = markupIdToUrl;
	}
	
	public MarkupStream markupStream( String markupId ) 
	{
		URL url = markupIdToUrl.get( markupId );
		if( url != null )
		{
			return new UrlMarkupStream( markupId, url );
		}
		return null;
	}

	public UrlRepo addMarkupUrl( String markupId, String markupUrl )
	{
		try 
		{
			URL url = new URL( markupUrl );
			addMarkupUrl( markupId, url );
			return this;
		} 
		catch( MalformedURLException e ) 
		{
			throw new VarException( 
				"Invalid URL \"" + markupUrl + "\" for markupId \"" + markupId + "\"", e );
		}
	}
	
	public UrlRepo addMarkupUrl( String markupId, URL markupUrl )
	{
		if( this.markupIdToUrl.containsKey( markupId ) )
		{
			LOG.info( "replacing markup URL for \""+markupId+"\" to \"" + markupUrl + "\"" );
		}
		this.markupIdToUrl.put( markupId, markupUrl );
		return this;
	}
	
	public String describe() 
	{
		return "[URL \"" + name + "\"]";
	}

	public static class UrlMarkupStream
		implements MarkupStream
	{
		public final URL url;
		public final String markupId;
		
		public UrlMarkupStream( String markupId, URL url )
		{
			this.url = url;
			this.markupId = markupId;
		}
		
		public InputStream getInputStream() 
		{
			try
			{
				return url.openStream();
			}
			catch( IOException ioe )
			{
				throw new VarException(
					"Unable to load input stream to \"" + url  +"\" for markupId \"" 
				   + markupId + "\"", ioe );
			}
		}

		public String getMarkupId() 
		{
			return markupId;
		}

		public String describe() 
		{
			return "[URL] " + url;
		}
		
	}
}
