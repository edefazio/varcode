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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;

/**
 * Implementation of a {@code SourceLoader} that is a "strategy" for locating and
 * returning {@code SourceStream}s for
 *
 * Synonymous with to the <B>sourcepath</B> when using the javac compiler. (but
 * is not restricted to looking for Markup Files in Directories, (Repos can load
 * files from remote servers)
 * <PRE>
 * java io.varcode.ml.codeml.Compiler
 *   -Dvarcode.=C:\\workspace\\mycompany\\varcode;D:\\projects\\java\\varcode\\;
 *   -Dvarcode.d=C:\\temp
 * </PRE>
 *
 * The {@code SourcePath} COULD be multiple places (not necessarily just on the
 * local directory) and use different mechanisms (other than simply reading from
 * a local file) to load the varcode source.
 *
 * <UL>
 * <LI>a local Directory
 * <LI>a zip file
 * <LI>a Version Control System
 * <LI>a online site (GitHub)
 * </UL>
 *
 * NOTE: just like an entry SourcePath, the Repository follows a hierarchy.
 * Basically Repositories can be nested INSIDE one another, and
 * <B>the First One found WINS</B>: or to put it another way, the
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SourcePath
    implements SourceLoader
{
    /**
     * Defines a Path (of many Directories, Jar or Zip Files) like:
     * <PRE>
     * "D:\Temp\java;D:\myApp\lib\log4j1.2.7-src.jar;D:\Files\resources.zip;"
     * \____________/\______________________________/\____________________/
     *       |                    |                            |
     * source directory     path to jar                 path to zip file
     *   containing          containing                   containing
     *   .java files        .java files                   .java files
     * </PRE>
     *
     * for "loading" the Markup.
     * @param pathDescription the paths to files/ jars, etc.
     * @return 
     */
    public SourcePath of( String pathDescription )
    {
        List<SourceLoader> allRepos = new ArrayList<SourceLoader>();

        String[] eachPath = pathDescription.split( ";" );
        for( int i = 0; i < eachPath.length; i++ )
        {
            allRepos.add( DirectorySourceLoader.of( eachPath[ i ] ) );            
        }
        return new SourcePath( allRepos );
    }

    /**
     * Multiple VarCode repositories for reading (_*.java) sources
     * <UL>
     * <LI>from the existing project source folder
     * <LI>from the resources
     * <LI>from a remote source (i.e. GitHub)
     * </UL>
     * NOTE: the "ORDER" of the varcode repositories is important, it is a First
     * come / First serve (if the varcode source would be resolved by MORE THAN
     * ONE varcode repositories, the {@code Repository} that is first in the
     * list wins.
     */
    private final List<SourceLoader> sourceLoaders;

    public void add( SourceLoader... sourceLoaders )
    {
        this.sourceLoaders.addAll(Arrays.asList(sourceLoaders ) );
    }

    /**
     * Create an empty (mutable) Path
     */
    public SourcePath()
    {
        this.sourceLoaders = new ArrayList<SourceLoader>();
    }

    /**
     * Create a Path with a single SourceLoader
     *
     * @param sourceLoader the only repository for varcode source files
     */
    public SourcePath( SourceLoader sourceLoader )
    {
        this.sourceLoaders = new ArrayList<SourceLoader>();
        this.sourceLoaders.add( sourceLoader );
    }

    public SourcePath( List<SourceLoader> sourceLoaders )
    {
        this.sourceLoaders = sourceLoaders;
    }

    public SourcePath( SourceLoader... sourceLoaders )
    {
        this.sourceLoaders = new ArrayList<SourceLoader>();
        this.sourceLoaders.addAll(Arrays.asList( sourceLoaders ) );
    }

    @Override
    public SourceStream sourceStream( String sourceId )
    {
        for( int i = 0; i < sourceLoaders.size(); i++ )
        {
            SourceStream is = sourceLoaders.get( i ).sourceStream( sourceId );
            if( is != null )
            {
                return is;
            }
        }
        return null;
    }

    @Override
    public String describe()
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < sourceLoaders.size(); i++ )
        {
            sb.append( sourceLoaders.get( i ).describe() );
            sb.append( "\r\n" );
        }
        return sb.toString();
    }
}
