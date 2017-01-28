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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import varcode.LoadException;
import varcode.load.Source.SourceInputStream;
import varcode.load.Source.SourceStream;

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

    public FileSourceStream( String sourceId, File file )
    {
        this(sourceId, file.getAbsolutePath() );
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
            throw new LoadException( 
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
            return SourceInputStream.streamAsString( 
                inputStream, "UTF-8" );
        }
        catch( IOException ioe )
        {
            throw new LoadException( 
                " unable to read to a String ", ioe );
        }
    }
    
    
}