/*
 * Copyright 2017 Eric.
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
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

/**
 * Forwards calls to a given file manager.  Subclasses of this class
 * might override some of these methods and might also provide
 * additional fields and methods.
 *
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public class AdHocFileManager 
    implements JavaFileManager 
{
      
    /**
     * The file manager which all methods are delegated to.
     */
    protected final StandardJavaFileManager fileManager;
    
    /** Loader for AdHocClasses */
    private final AdHocClassLoader adHocClassLoader;
    
    
    public AdHocFileManager( AdHocClassLoader adHocClassLoader )
    {
        this( Javac.STANDARD_FILE_MANAGER, adHocClassLoader );
    }
    
    /**
     * Creates a new instance of ForwardingJavaFileManager.
     * @param fileManager delegate to this file manager
     */
    protected AdHocFileManager( 
        StandardJavaFileManager fileManager, 
        AdHocClassLoader adHocClassLoader ) 
    {
        fileManager.getClass(); // null check
        this.fileManager = fileManager;
        this.adHocClassLoader = adHocClassLoader;
    }

    /**
     * @throws SecurityException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public ClassLoader getClassLoader( JavaFileManager.Location location ) 
    {
        //System.out.println( "getting ClassLoader for "+ location );
        return this.adHocClassLoader;
        //return fileManager.getClassLoader(location);
    }

    /**
     * @throws IOException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public Iterable<JavaFileObject> list( JavaFileManager.Location location, 
        String packageName, Set<Kind> kinds, boolean recurse )
        throws IOException
    {
        //System.out.println( "Listing " + location + " " + packageName + " " + recurse );
        
        
        List<JavaFileObject>files = new ArrayList<JavaFileObject>();
        
        if( kinds.contains( Kind.CLASS ) )
        {
            files.addAll( this.adHocClassLoader.adHocClassFilesByPackage( packageName ) );
            if( files.size() > 0 )
            {
                //System.out.println("+++++FOUND FILES "+ files );
            }
        }
        Iterable<JavaFileObject> parentFiles = 
            fileManager.list( location, packageName, kinds, recurse );
        Iterator<JavaFileObject> it = parentFiles.iterator();
        while( it.hasNext() )
        {
            files.add( it.next() );
        }
        return files;
    }

    /**
     * @throws IllegalStateException {@inheritDoc}
     */
    public String inferBinaryName( 
        JavaFileManager.Location location, JavaFileObject file) 
    {
        //System.out.println( "inferring binary name of "+ location +" "+ file );  
        return fileManager.inferBinaryName(location, file);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public boolean isSameFile( FileObject a, FileObject b ) 
    {
        //System.out.println( "is same file "+ a +" "+ b );  
        return fileManager.isSameFile(a, b);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public boolean handleOption( String current, Iterator<String> remaining ) 
    {
        //System.out.println( "handleOption "+ current + " :: "+ remaining );  
        return fileManager.handleOption(current, remaining);
    }

    public boolean hasLocation( JavaFileManager.Location location ) 
    {
        //System.out.println( "hasLocation "+ location );  
        return fileManager.hasLocation( location );
    }

    public int isSupportedOption( String option ) 
    {
        //System.out.println( "isSupportedOption "+ option );  
        return fileManager.isSupportedOption( option );
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public JavaFileObject getJavaFileForInput( 
        JavaFileManager.Location location, String className, Kind kind )
        throws IOException
    {
        //System.out.println( "getJavaFileForInput "+ location+ " "+className+" "+ kind + " "  );         
        return fileManager.getJavaFileForInput(location, className, kind);
    }

    
    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location,
        String className,
        JavaFileObject.Kind kind,
        FileObject sibling )
    {
        // check if we already loaded this class
        JavaClassFile adHocClass
            = this.adHocClassLoader.classMap().get( className );

        if( adHocClass != null )
        {   // return the already-loaded class
            return adHocClass;
        }
        try
        {   // create a "home" for the compiled bytes
            adHocClass = new JavaClassFile( className );
            this.adHocClassLoader.load( adHocClass );
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
    
    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     
    public JavaFileObject getJavaFileForOutput( JavaFileManager.Location location, 
        String className, Kind kind, FileObject sibling )
        throws IOException
    {
        System.out.println( "getJavaFileForOutput "+className+" "+ kind + " "+ sibling  ); 
        return fileManager.getJavaFileForOutput(location, className, kind, sibling);
    }
    */

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public FileObject getFileForInput(JavaFileManager.Location location,
                                      String packageName,
                                      String relativeName)
        throws IOException
    {
        //System.out.println( "getFileForInput "+location+" "+ packageName + " "+ relativeName  ); 
        return fileManager.getFileForInput(location, packageName, relativeName);
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    public FileObject getFileForOutput(JavaFileManager.Location location,
        String packageName, String relativeName, FileObject sibling )
        throws IOException
    {
        //System.out.println( "getFileForOutput "+location+" "+ packageName + " "+ relativeName+ "  "+ sibling ); 
        return fileManager.getFileForOutput(location, packageName, relativeName, sibling);
    }

    public void flush() 
        throws IOException 
    {
        //System.out.println( "FLUSHING" );
        fileManager.flush();
    }

    public void close() 
        throws IOException 
    {
        //System.out.println( "CLOSING" );
        fileManager.close();
    }
}
