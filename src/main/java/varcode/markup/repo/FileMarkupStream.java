package varcode.markup.repo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public String getMarkupId()
    {
        return markupId;
    }

    public String describe()
    {
        return "File :\"" + fileName + "\"";
    }
}