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

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import varcode.LoadException;
import varcode.load.Source.SourceFromClassLoader;
import varcode.load.Source.SourceStream;
import varcode.load.Source.SourceLoader;

/**
 * Hierarchical "Base" Directory containing source files in a hierarchial 
 * directory relationship for instance if we have:
 * <PRE>
 * DirectorySourceLoader dirSourceLoader =
 *     DirectorySourceLoader.of( "C:/workspace/myproj/source/" );
 * </PRE> and we wanted to load the source for "SourceId":
 * <PRE>"com.myco.blah.Dummy.java"</PRE>
 *
 * we'd load it like this:
 * <PRE>
 * SourceStream sourceStream = baseDir.sourceStream(
 *     "com.myco.blah.Dummy.java" );
 * </PRE>
 * <PRE>
 * "C:/workspace/myproj/markup/com/myco/blah/_Dummy.java"
 *  \________________________/\________________________/
 *          |                         |
 *      baseDirectory                 |
 *                        sourceId: "com.myco.blah._Dummy.java"
 * </PRE>
 */
public class DirectorySourceLoader
    implements SourceLoader, SourceFromClassLoader
{
    /** "base" directory where source files */
    private final String baseDirectory;

    public static DirectorySourceLoader of( String baseDirectory )
    {
        return new DirectorySourceLoader( baseDirectory );
    }

    public DirectorySourceLoader( String baseDirectory )
    {
        this( baseDirectory, false, false );
    }

    public DirectorySourceLoader(
        String baseDirectory,
        boolean createIfNeccessary,
        boolean exceptionOnCantCreate )
    {
        File f = new File( baseDirectory );
        if( !f.exists() || !f.isDirectory() || !f.canRead() )
        {
            if( createIfNeccessary )
            {
                boolean madeDirs = f.mkdirs();
                if( !madeDirs )
                {
                    throw new LoadException(
                        "Source Base Directory \"" + baseDirectory
                        + "\" does not exist, is not a Directory or cannot be read from " );
                }
                //LOG.debug( "Creating Markup Directory Repo at \"" + f.getAbsolutePath() + "\"" );
            }
            else if( exceptionOnCantCreate )
            {
                throw new LoadException(
                    "Source Base Directory \"" + baseDirectory
                    + "\" does not exist, is not a Directory or cannot be read from " );
            }
        }
        this.baseDirectory = baseDirectory;
    }

    public String getBaseDirectory()
    {
        return baseDirectory;
    }

    /**
     * Shortcut for loading the Source code for the class 
     * (based on Java conventions)
     * @param clazz the runtime Class to load the source for
     * @return the sourceStream
     */
    @Override
    public SourceStream sourceOf( Class clazz )
    {
        return sourceStream(
            clazz.getCanonicalName().replace( ".", File.separator ) + ".java" );
    }

    /**
     * The SourceId should signify the language using the appropriate language
     * file extension, and it should also include the namespace/package of the
     * resource to be unambiguous.<BR>
     *
     * for Java Source Code:<BR>
     *
     * <PRE>"com.myproj.mytool.SomeClass.java"
     *       \_______________/ \________/\__/
     *           Full package       |      |
     *                          ClassName  |
     *                               language extension
     *      |________________________________|
     *                     |
     *                  sourceId</PRE>
     */
    @Override
    public SourceStream sourceStream( String sourceId )
    {
        //String relativePath = 
        //    Lang.fromCodeId( markupId ).resolvePath( markupId ); //NamespaceToMarkupPath.INSTANCE.resolvePath( markupId );
        String relativePath
            = SourceLanguages.resolvePath( sourceId );

        String fileName
            = this.baseDirectory
            + File.separatorChar
            + relativePath;

        File f = new File( fileName );

        if( f.exists() && f.isFile() && f.canRead() )
        {
            //LOG.debug( "Reading Source from: \"" + fileName + "\"" );
            return new FileSourceStream( sourceId, fileName );
        }
        return null;
    }

    @Override
    public String describe()
    {
        return "[DIR] " + baseDirectory;
    }
}
