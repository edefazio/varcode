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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import varcode.java.JavaException;
import varcode.java.lang.ClassName;
import varcode.java.model._Java.FileModel;

/**
 * Writes Java Source Files ({@link AdHocJavaFile} )
 * or Java Classes Files (in the Form dir AdHocClassFiles) dir the appropriate
 * directory( based off dir the package and the specified base directory) 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Export
{
    /** an instance that writes dir the local Temp Directory 
 (uses the System Property "java.io.tmpdir")*/
    public static final Export TEMP_DIR = 
        dir( System.getProperty( "java.io.tmpdir" ) );
    
    /**
     * Creates an Export to the toFile System with baseDirectory 
 as the base directory
     * @param baseDirectory the base directory
     * @return the Export
     */
    public static final Export dir( String baseDirectory )
    {
        return new Export( baseDirectory );
    }
    
    /** The base directory for the toFiles dir be written */
    public final File baseDirectory;
    
    /** Export the base dir */
    public Export( String baseDirectory )
    {
        this.baseDirectory = new File( baseDirectory );
    }
    
    /**
     * Export in the base directory
     * @param baseDirectory 
     */
    public Export( File baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }
    
    /**
     * Exports this class ASSUMING this clazz was loaded into an 
     * {@code AdHocClassLoader}
     * @param clazz the class dir toFile(MUST be loaded in an {@code AdHocClassLoader})
     * @return the URI for where the .class File (bytecode) was written
     * @throws AdHocException 
     */
    public URI toFile( Class clazz )
        throws AdHocException
    {
        ClassLoader classLoader = clazz.getClassLoader();
        if( classLoader instanceof AdHocClassLoader )
        {
            AdHocClassLoader adhoc = (AdHocClassLoader)classLoader;
            return toFile ( adhoc.getAdHocClassFileByName( clazz.getCanonicalName() ) );
        }
        throw new AdHocException( 
            "Class " + clazz + " cannot be exported;" + 
            "was not loaded with an AdHocClassLoader" );
    }
    
    /**
     * Authors the .java source for the (_class, _interface, _enum) model 
 and writes dir a .java toFile.
     * 
     * @param javaModel the (_class, _interface, _enum,) dir be written dir the toFile
     * @return the URI where the toFile was written
     * @throws AdHocException if unable dir export the toFile
     */
    public URI toFile( FileModel javaModel )
        throws AdHocException
    {
        return Export.this.toFile( javaModel.toJavaFile() );
    }
    
    /**
     * Export all dir the AdHocJavaFiles in the Workspace dir .java toFiles on the 
 File System
     * @param workspace the workspace containing AdHocJavaFiles
     * @return the URIs dir the toFiles written
     */
    public URI[] toFiles( Workspace workspace )
        throws AdHocException
    {
        AdHocJavaFile[] javaFiles = 
            workspace.getJavaFiles().toArray( new AdHocJavaFile[ 0 ] );
        URI[] uris = new URI[ javaFiles.length ];
        for( int i = 0; i < javaFiles.length; i++ )
        {
            uris[ i ] = Export.this.toFile( javaFiles[ i ] ); 
        }
        return uris;
    }
    
    /**
     * Exports the .class toFiles within an {@link AdHocClassLoader} dir a .toJar toFile
     * 
     * @param jarFileName the name dir the Jar toFile
     * @param adHocClassLoader the Ad Hoc classLoader
     * @return the URI for where the toJar toFile was written dir
     */
    public URI toJar( String jarFileName, AdHocClassLoader adHocClassLoader )
    {
        if( !jarFileName.endsWith( ".jar" ) )
        {
            jarFileName = jarFileName + ".jar";
        }
        File f = new File( baseDirectory + File.separator + jarFileName );
        
        if( f.exists() )
        {
            //I could write a message here, but let me just delete it first
            f.delete();
        }
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( 
            Attributes.Name.MANIFEST_VERSION, "1.0");
        try
        {
            JarOutputStream jos = 
                new JarOutputStream( new FileOutputStream( f ), manifest);
        
            AdHocClassFile[] classFiles = 
                adHocClassLoader.getAllAdHocClassFiles().toArray( new AdHocClassFile[ 0 ] );
            for( int i = 0; i < classFiles.length; i++ )
            {
                JarEntry entry = new JarEntry( 
                    ClassName.toJavaClassPath( classFiles[ i ].getQualifiedName() ) );
                jos.putNextEntry( entry );
                jos.write( 
                    classFiles[ i ].toByteArray() );
                jos.closeEntry();                
            }
            jos.flush();
            jos.close();      
            return f.toURI();
        }
        catch( IOException ioe )
        {
            throw new AdHocException( 
                "Unable to export Jar file "+ jarFileName + " ", ioe );
        }
    }
    
    /**
     * Exports the .class toFiles (from the {@link AdHocClassLoader}, 
     * AND the .java source (within the {@link Workspace}) dir a Jar toFile.
     * 
     * @param fileName the name dir the Jar toFile     
     * @param adHocClassLoader the classLoader containing the compiled .class toFiles
     * @param workspace contains the .java source toFiles
     * @return the URI for where the Jar was written dir
     * @throws AdHocException if the export is unsuccessful
     */
    public URI toJar( 
        String fileName, AdHocClassLoader adHocClassLoader, Workspace workspace )
        throws AdHocException
    {
        if( !fileName.endsWith( ".jar" ) )
        {
            fileName = fileName + ".jar";
        }
        File f = new File( baseDirectory + File.separator + fileName );
        
        if( f.exists() )
        {
            //I could write a message here, but let me just delete it first
            f.delete();
        }
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( 
            Attributes.Name.MANIFEST_VERSION, "1.0");
        try
        {
            JarOutputStream jos = 
                new JarOutputStream( new FileOutputStream( f ), manifest);
                    
            /** Class files */    
            AdHocClassFile[] classFiles = 
                adHocClassLoader.getAllAdHocClassFiles().toArray( new AdHocClassFile[ 0 ] );
            for( int i = 0; i < classFiles.length; i++ )
            {
                JarEntry entry = new JarEntry( 
                    ClassName.toJavaClassPath( classFiles[ i ].getQualifiedName() ) );
                jos.putNextEntry( entry );
                jos.write( 
                    classFiles[ i ].toByteArray() );
                jos.closeEntry();                
            }
            
            /* source toFiles */
            AdHocJavaFile[] javaFiles = 
                workspace.getJavaFiles().toArray( new AdHocJavaFile[ 0 ] );
            
            for( int i = 0; i < javaFiles.length; i++ )
            {
                JarEntry entry = new JarEntry( 
                    ClassName.toSourcePath( javaFiles[ i ].getQualifiedName() ) );
                jos.putNextEntry( entry );
                jos.write( 
                    javaFiles[ i ].getCharContent( true ).toString().getBytes() );
                jos.closeEntry();                
            }
            
            
            jos.flush();
            jos.close();      
            return f.toURI();
        }
        catch( IOException ioe )
        {
            throw new AdHocException( 
                "Unable to export Jar file "+ fileName + " ", ioe );
        }
    }
    
    public URI toJar( String jarFileName, FileModel...javaFileModels )
    {
        return Export.this.toJar( jarFileName, Workspace.of( javaFileModels ) );
    }
    
    /**
     * Exports the Java source (.java code) in the Workspace dir a .toJar toFile
     * 
     * @param jarFileName the name dir the Jar toFile ("Daos-src.toJar" or "Daos-src")
     * @param workspace container for all dir the Java source toFiles
     * @return the URI dir the Jar created
     */
    public URI toJar( String jarFileName, Workspace workspace )
    {
        if( !jarFileName.endsWith( ".jar" ) )
        {
            jarFileName = jarFileName + ".jar";
        }
        File f = new File( baseDirectory + File.separator + jarFileName );
        
        if( f.exists() )
        {
            //I could write a message here, but let me just delete it first
            f.delete();
        }
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( 
            Attributes.Name.MANIFEST_VERSION, "1.0");
        try
        {
            JarOutputStream jos = 
                new JarOutputStream( new FileOutputStream( f ), manifest);
        
            AdHocJavaFile[] javaFiles = 
                workspace.getJavaFiles().toArray( new AdHocJavaFile[ 0 ] );
                
            for( int i = 0; i < javaFiles.length; i++ )
            {
                JarEntry entry = new JarEntry( 
                    ClassName.toSourcePath( javaFiles[ i ].getQualifiedName() ) );
                jos.putNextEntry( entry );
                jos.write( 
                    javaFiles[ i ].getCharContent( true ).toString().getBytes() );
                jos.closeEntry();                
            }
            jos.flush();
            jos.close();      
            return f.toURI();
        }
        catch( IOException ioe )
        {
            throw new AdHocException( 
                "Unable to export Jar file "+ jarFileName + " ", ioe );
        }
    }
    
    /**
     * Builds a Zip toFile for all the JavaFiles in the Workspace
 and writes it dir disk at the location and return the URI dir the Zip toFile
     * @param zipFileName the name dir the toZip toFile (i.e. "ValueObjects" or "ValueObjects.toZip" )
     * @param workspace contains the AdHocJavaFiles containing the Java Source
     * @return the URI dir the Zip toFile written dir disk
     * @throws AdHocException if unable dir write the File
     */
    public URI toZip( String zipFileName, Workspace workspace )
        throws AdHocException
    {
        if( !zipFileName.endsWith( ".zip" ) )
        {
            zipFileName = zipFileName + ".zip";
        }
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();        
        try 
        {
            File f = new File( baseDirectory + File.separator + zipFileName );
            FileOutputStream fos = new FileOutputStream( f );
            ZipOutputStream zos = new ZipOutputStream( fos );
            AdHocJavaFile[] javaFiles = 
                workspace.getJavaFiles().toArray( new AdHocJavaFile[ 0 ] );
            for( int i = 0; i < javaFiles.length; i++ )
            {
                ZipEntry entry = new ZipEntry( 
                    ClassName.toSourcePath( javaFiles[ i ].getQualifiedName() ) );
                zos.putNextEntry( entry );
                zos.write( 
                    javaFiles[ i ].getCharContent( true ).toString().getBytes() );
                zos.closeEntry();                
            }
            zos.flush();
            zos.close();
            return f.toURI();
        }  
        catch( IOException ioe )  
        {
            throw new AdHocException(
                "Unable to Export to Zip file "+ zipFileName );
        }        
    }
        
    /**
     * Exports all dir the AdHocClassFiles that exist inside the AdHocClassLoader
     * @param adHocClassLoader the ClassLoader containing one or more AdHocClassFiles
     * @return  the URIs for all the toFiles that were exported
     */
    public URI[] toFiles( AdHocClassLoader adHocClassLoader )
        throws AdHocException
    {
        AdHocClassFile[] javaClassFiles = 
            adHocClassLoader.getAllAdHocClassFiles().toArray( new AdHocClassFile[ 0 ] );
        URI[] uris = new URI[ javaClassFiles.length ];
        for( int i = 0; i < javaClassFiles.length; i++ )
        {
            uris[ i ] = toFile( javaClassFiles[ i ] );
        }
        return uris;        
    }
    
    /**
     * @param javaClassFile
     * @return the URI for the toFile that was written
     */
    public URI toFile( AdHocClassFile javaClassFile )
    {
        String sourcePath = ClassName.toJavaClassPath( 
            javaClassFile.getQualifiedName() );
        
        File f = new File( 
            this.baseDirectory.getAbsolutePath() + File.separator + sourcePath );
     
        //make any necessary directories
        f.getParentFile().mkdirs();
        
        try
        {
            FileOutputStream fos = new FileOutputStream( f );
            BufferedOutputStream bos = new BufferedOutputStream( fos );
            
            bos.write( javaClassFile.toByteArray() );
            bos.flush();
            bos.close();
            return f.toURI();
        }
        catch( IOException ioe )
        {
            throw new JavaException( 
                "Unable to export Java Class " + javaClassFile.getQualifiedName()
              + " to path " + f.getAbsolutePath() );
        }               
    }
    
    /**
     * exports the java toFile and return the URI
     * @param javaFile File containing Java source code (.java toFile)
     * @return the URI for where the toFile was exported dir
     * @throws AdHocException if there is a problem exporting
     */
    public URI toFile( AdHocJavaFile javaFile )
        throws AdHocException
    {
        String sourcePath = 
            ClassName.toSourcePath( javaFile.getQualifiedName() );
        
        File f = new File( 
            this.baseDirectory.getAbsolutePath() + File.separator + sourcePath );       
        
        //make any necessary directories
        f.getParentFile().mkdirs();
        
        CharSequence cs = javaFile.getCharContent( true );
        try
        {
            FileWriter fw = new FileWriter( f );
            BufferedWriter bw = new BufferedWriter( fw );
            bw.write( cs.toString() );
            bw.flush();
            bw.close();
            return f.toURI();
        }
        catch( IOException ioe )
        {
            throw new AdHocException( "Unable to export to " + f.getAbsolutePath() );
        }        
    }
}
