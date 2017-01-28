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
package varcode.java;

import java.util.ArrayList;
import java.util.List;
import varcode.load.ClassPathSourceLoader;
import varcode.load.DirectorySourceLoader;
import varcode.load.Source;
import varcode.load.SourcePath;
import varcode.load.ZippedSourceLoader;

/**
 * The Base implementation of a JavaSourceLoader (can load Java source code, as
 * well as other source code/resources from the classpath and "common locations"
 * relative to the root directory based off of the ${user.dir} System property
 * set within the editor
 */
public enum BaseJavaSourceLoader
    implements JavaSourceLoader
{
    INSTANCE;//singleton enum idiom

    /**
     * A "path" (grouping of directories and .zip of jar files separated by ";" i.e.<PRE>
     * source.path=C:\Users\Me\AppData\Temp;C:\Users\Me\AppData\Source\MySource.jar
     * </PRE> The System Property Name for a System property (to add a directory
     * for the BaseJavaSourceLoader to read from)
     */
    public static final String SOURCE_PATH_SYS_PROP = "source.path";

    public static final DirectorySourceLoader USER_DIR
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) );

    public static final DirectorySourceLoader VARCODE_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/varcode/" );

    public static final DirectorySourceLoader SRC_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/src/" );

    public static final DirectorySourceLoader SRC_MAIN_JAVA_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/src/main/java/" );

    public static final DirectorySourceLoader SRC_MAIN_RESOURCES_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/src/main/resources/" );

    public static final DirectorySourceLoader TEST_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/test/" );

    public static final DirectorySourceLoader SRC_TEST_JAVA_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/src/test/java/" );

    public static final DirectorySourceLoader SRC_TEST_RESOURCES_DIRECTORY
        = new DirectorySourceLoader(
            System.getProperty( "user.dir" ) + "/src/test/resources/" );

    public static final ClassPathSourceLoader CLASSPATH_REPO
        = new ClassPathSourceLoader();

    /**
     * Where to look for Java Source that Corresponds to a specific class
     */
    public static final SourcePath SOURCE_PATH
        = new SourcePath(
            VARCODE_DIRECTORY,
            SRC_DIRECTORY,
            SRC_MAIN_JAVA_DIRECTORY,
            SRC_MAIN_RESOURCES_DIRECTORY,
            TEST_DIRECTORY,
            SRC_TEST_JAVA_DIRECTORY,
            SRC_TEST_RESOURCES_DIRECTORY,
            USER_DIR,
            CLASSPATH_REPO
        );

    private static Source.SourceStream pathFromSystemProperty(
        String systemProperty, String sourceId )
    {
        String paths = System.getProperty( systemProperty );

        if( paths != null )
        {
            List<Source.SourceLoader> pathSourceLoaders = 
                new ArrayList<Source.SourceLoader>();
            String[] eachPath = paths.split( ";" );

            for( int i = 0; i < eachPath.length; i++ )
            {
                if( eachPath[ i ].endsWith( ".jar" ) )
                {
                    pathSourceLoaders.add( ZippedSourceLoader.of( eachPath[ i ] ) );
                }
                else if( eachPath[ i ].endsWith( ".zip" ) )
                {
                    pathSourceLoaders.add( ZippedSourceLoader.of( eachPath[ i ] ) );
                }
                else
                {
                    pathSourceLoaders.add( DirectorySourceLoader.of( eachPath[ i ] ) );
                }
            }
            SourcePath sp = new SourcePath( pathSourceLoaders );
            return sp.sourceStream( sourceId );
        }
        return null;
    }

    /**
     *
     * @param sourceId the Id of the Markup source to load
     * @return MarkupStream for reading the input
     */
    @Override
    public Source.SourceStream sourceStream( String sourceId )
    {
        Source.SourceStream ms = SOURCE_PATH.sourceStream( sourceId );
        if( ms != null )
        {
            return ms;
        }
        return pathFromSystemProperty( SOURCE_PATH_SYS_PROP, sourceId );
    }

    /**
     * given the Class looks in the "usual places" on the Path to return the
     * Source markup Stream
     *
     * @param runtimeClass the local Class
     * @return the markupStream
     */
    @Override
    public Source.SourceStream sourceStream( Class<?> runtimeClass )
    {
        Class topLevelClass = JavaReflection.getTopLevelClass( runtimeClass );

        //LOG.debug("Reading source for TOP LEVEL class" + topLevelClass );
        Source.SourceStream topLevelClassStream = sourceStream(
            topLevelClass.getCanonicalName() + ".java" );

        return topLevelClassStream;
    }
 
    @Override
    public String describe()
    {
        return SOURCE_PATH.describe();
    }
}
