package varcode.markup.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import varcode.VarException;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * {@code MarkupStream} for a File
 * 
 * @author M. Eric DeFazio eric@varcode.io 
 */
public class FileMarkupStream
    implements MarkupStream
{
	/** the InputStream for the File*/
    protected final FileInputStream inputStream;

    protected final String markupId;

    protected final String fileName;

    public FileMarkupStream( String markupId, File file )
    {
        this( markupId, file.getAbsolutePath() );
    }
    
    public FileMarkupStream( String markupId, String fileName )
    {
        this.markupId = markupId;
        this.fileName = fileName;
        try
        {
            this.inputStream = new FileInputStream( fileName );
        }
        catch( FileNotFoundException fnfe )
        {
            throw new VarException( 
                "Could not load file \"" + fileName + "\" for markupId \"" 
               + markupId + "\"", fnfe );
        }
    }

    @Override
    public InputStream getInputStream()
    {
        return inputStream;
    }

    @Override
    public String getMarkupId()
    {
        return markupId;
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