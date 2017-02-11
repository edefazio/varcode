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
package howto.java.adhoc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import junit.framework.TestCase;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Export;
import varcode.java.adhoc.Javac;
import varcode.java.adhoc.JavaSourceFolder;
import varcode.java.model._class;

/**
 * So this  use case, we want to illustrate
 * 1) create a model for an (abstract base) class
 * 2) AdHoc compile the class (dynamically at runtime)
 * 3) AdHoc export the class to a jar (baseClass.jar)
 * 4) create a model for a derived class 
 *    (extending from the dyanmically exported base class)
 * 5) compile the derived class (by updating the classpath of the AdHocCompiler
 *    to include the baseClass.jar
 * 
 * @author Eric
 */
public class CreateBaseClass_ExportJar_UpdClassPath_Compile
    extends TestCase    
{
    public static final String JAVA_CLASS_PATH = "java.class.path";
    
    public void testbuildcompileupdatebuildcompile( )
    {
        
        AdHocClassLoader adHocBase = AdHoc.compile( 
            _class.of( "package exam;", "public abstract class A1" )
                .field( "public static final int VALUE = 100;" )  );
        
        //Exports the contents of the creates a jar file, 
        //returns the URI of the jar file 
        URI uri = Export.dir( "C:\\temp" ).toJar( "A10.jar", adHocBase );
        //File adHocJarFile = 
        //    new File( uri );
        
        //if( adHocJarFile.canRead() )
        //{
        //    System.out.println( "I CAN READ" );
        //}
        
        //adHocJarFile.deleteOnExit();
        
        //get the "existing" classpath
        String classPath = System.getProperty( JAVA_CLASS_PATH );
        
        //PREPEND the path to the AdHoc jar to the ClassPath
        String updatedClassPath = classPath + "C:\\temp\\A10.jar;";
            //adHocJarFile.getAbsolutePath() + File.pathSeparator + classPath;
        
        System.out.println( updatedClassPath );
        
        _class _derived = 
            _class.of( "package exam;", "public class B" )
            .constructor( "public B()",
                "System.out.println( exam.A1.class );" );
        
        AdHocClassLoader loaded = AdHoc.compile(JavaSourceFolder.of( _derived ), 
            Javac.JavacOptions.ClassPath.of( updatedClassPath ) );
        
        assertNotNull( loaded.findClass( _derived ) );
        
        
    }
    
    /**
     * Add a directory to the class path for compiling.  This can be required with custom
     *
     * @param dir to add.
     * @return whether the directory was found, if not it is not added either.
     */
    public static boolean addDirToClassPath( String dir ) 
    {
        File file = new File( dir );
        if( file.exists() ) 
        {
            String path;
            try 
            {
                path = file.getCanonicalPath();
            } 
            catch( IOException ignored ) 
            {
                path = file.getAbsolutePath();
            }
            if( !Arrays.asList( System.getProperty( JAVA_CLASS_PATH ).split( File.pathSeparator ) ).contains( path ) )
                System.setProperty( JAVA_CLASS_PATH, System.getProperty( JAVA_CLASS_PATH ) + File.pathSeparator + path );
        } 
        else 
        {
            return false;
        }
        //reset();
        return true;
    }
    
}
