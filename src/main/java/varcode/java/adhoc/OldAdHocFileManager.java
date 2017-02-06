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
package varcode.java.adhoc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * {@link JavaFileManager} implementation for use within the Javac Compiler 
 * that manages the handling of {@code JavaFileObject}s when calling the 
 * Javac compiler at runtime
 * <UL>
 *   <LI>{@linkplain javax.tools.JavaFileObject.Kind.SOURCE}
 *   <LI>{@linkplain javax.tools.JavaFileObject.Kind.CLASS}
 * </UL>
 * we use the following implementations:
 * <UL>
 *   <LI>{@link AdHocClassFile} ( for {@link Kind.SOURCE} )
 *   <LI>{@link AdHocJavaFile} ( for {@link Kind.CLASS} )
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class OldAdHocFileManager
    extends ForwardingJavaFileManager<JavaFileManager>
{
    private final AdHocClassLoader adHocClassLoader;

    public OldAdHocFileManager(
        StandardJavaFileManager fileManager,
        AdHocClassLoader adHocClassLoader )
    {
        super( fileManager );
        this.adHocClassLoader = adHocClassLoader;
    }

    /**
     * @param location
     * @return 
     * @throws SecurityException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public ClassLoader getClassLoader( JavaFileManager.Location location )
    {
        System.err.println( "getting class Loader for "+ location ); 
        return this.adHocClassLoader; //fileManager.getClassLoader(location);
    }

    //MED Added
    /**
     * @param location
     * @param className
     * @param kind
     * @return 
     * @throws java.io.IOException 
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public JavaFileObject getJavaFileForInput( 
        JavaFileManager.Location location,
        String className,
        JavaFileObject.Kind kind )
        throws IOException
    {
        System.err.println( "*** getting file for "+ className ); 
        return fileManager.getJavaFileForInput( location, className, kind );
    }

    /**
     * @param location
     * @param packageName
     * @param kinds
     * @param recurse
     * @return 
     * @throws IOException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public Iterable<JavaFileObject> list( 
        JavaFileManager.Location location,
        String packageName,
        Set<JavaFileObject.Kind> kinds,
        boolean recurse )
        throws IOException
    {
        //LOG.debug( "****** LISTING ****** " + location );
        System.err.println( "*** LISTING getting file for "+ location + " package \"" + packageName +"\""); 
        Iterable<JavaFileObject> theList
            = fileManager.list( location, packageName, kinds, recurse );
        
        System.err.println( " --> theFirstList "+ theList );
        if( "CLASS_PATH".equals( location.getName() )
            && kinds.contains( JavaFileObject.Kind.CLASS ) )
        {
            System.err.println( "Trying to get AdHocClassFiles by package "+ packageName );
            
            System.out.println( this.adHocClassLoader );
            List<JavaFileObject> adHocClassFilesInPackage = 
                this.adHocClassLoader.adHocClassFilesByPackage( packageName );
            Iterator<JavaFileObject> it = theList.iterator();
            while( it.hasNext() )
            {    
                adHocClassFilesInPackage.add( it.next() );
            }
            return adHocClassFilesInPackage;
        }
        /*
        if( "CLASS_PATH".equals( location.getName() )
            && kinds.contains( JavaFileObject.Kind.CLASS )
            && recurse )
        {   
            System.err.println( " !! Checking the AdHocClassLoader !! ");
            //if they want the classpath, then Include the 
            List<JavaFileObject> javaFiles = new ArrayList<JavaFileObject>();
            
            //adds all the Class files in the AdHocClassLoader
            javaFiles.addAll( this.adHocClassLoader.classMap().values() );
            System.out.println( "ADDING FILES " + javaFiles );
            Iterator<JavaFileObject> it = theList.iterator();
            while( it.hasNext() )
            {
                javaFiles.add( it.next() );
            }

            //for( int i = 0; i < )
            //javaFiles.addAll( theList. );
            return javaFiles;
        }
        */
        return theList;
    }

    /**
     * @param location
     * @param packageName
     * @param relativeName
     * @return 
     * @throws java.io.IOException 
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public FileObject getFileForInput( 
        JavaFileManager.Location location,
        String packageName,
        String relativeName )
        throws IOException
    {
        return fileManager.getFileForInput( location, packageName, relativeName );
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location,
        String className,
        JavaFileObject.Kind kind,
        FileObject sibling )
    {
        // check if we already loaded this class
        AdHocClassFile adHocClass
            = this.adHocClassLoader.classMap().get( className );

        if( adHocClass != null )
        {   // return the already-loaded class
            return adHocClass;
        }
        try
        {   // create a "home" for the compiled bytes
            adHocClass = new AdHocClassFile( this.adHocClassLoader, className );
            return adHocClass;
        }
        catch( IllegalArgumentException e ) 
        {
            throw new AdHocException(
                "Unable to create output class for class \""
                + className + "\"", e );
        }
        catch( URISyntaxException e )
        {
            throw new AdHocException(
                "Unable to create output class for class \""
                + className + "\"", e );
        }
    }
}
