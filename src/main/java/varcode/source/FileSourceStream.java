package varcode.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import varcode.VarException;
import varcode.source.SourceLoader.SourceStream;

/**
 * Source from a File
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public class FileSourceStream
    implements SourceStream
{
	/** the InputStream for the File*/
    protected final FileInputStream inputStream;

    protected final String sourceId;

    protected final String fileName;

    public FileSourceStream( String markupId, File file )
    {
        this( markupId, file.getAbsolutePath() );
    }
    
    public FileSourceStream( String sourceId, String fileName )
    {
        this.sourceId = sourceId;
        this.fileName = fileName;
        try
        {
            this.inputStream = new FileInputStream( fileName );
        }
        catch( FileNotFoundException fnfe )
        {
            throw new VarException( 
                "Could not load file \"" + fileName + "\" for sourceId \"" 
               + sourceId + "\"", fnfe );
        }
    }

    @Override
    public InputStream getInputStream()
    {
        return inputStream;
    }

    @Override
    public String getSourceId()
    {
        return sourceId;
    }

    @Override
    public String describe()
    {
        return "File :\"" + fileName + "\"";
    }
    
    @Override
    public String asString()
    {
        try
        {
            return getFileContent( inputStream, "UTF-8" );
        }
        catch( IOException ioe )
        {
            throw new VarException( 
                " unable to read to a String ", ioe );
        }
    }
    
    public static String getFileContent( FileInputStream fis, String encoding ) 
        throws IOException
    {
        try
        {
            BufferedReader br = new BufferedReader( 
                new InputStreamReader( fis, encoding ) );
            StringBuilder sb = new StringBuilder();
            String line;
            while(( line = br.readLine()) != null ) 
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