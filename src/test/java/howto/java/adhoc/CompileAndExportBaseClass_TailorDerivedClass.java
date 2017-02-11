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

import static howto.java.adhoc.CreateBaseClass_ExportJar_UpdClassPath_Compile.JAVA_CLASS_PATH;
import java.net.URI;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertNotNull;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Export;
import varcode.java.adhoc.Javac;
import varcode.java.adhoc.JavaSourceFolder;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class CompileAndExportBaseClass_TailorDerivedClass
    extends TestCase
{
    public void testCreateAndExportBaseClass()
    {
        _class _c = _class.of("package mypackage.blah;", "public class MyClass")
            .field("public int ID = 10001;");
        AdHocClassLoader adHocClassLoader = AdHoc.compile( _c );
        
        Export.dir( "C:\\temp\\adhoc\\aaa\\" ).toFiles( adHocClassLoader );
        
        
        
        //get the "existing" classpath
        String classPath = System.getProperty( JAVA_CLASS_PATH );
        
        //PREPEND the path to the AdHoc jar to the ClassPath
        String updatedClassPath = classPath + "C:\\temp\\adhoc\\aaa\\;";
            //adHocJarFile.getAbsolutePath() + File.pathSeparator + classPath;
        
        System.out.println( updatedClassPath );
        
        _class _derived = 
            _class.of( "package mypackage.blah;", "public class B" )
            .imports( "mypackage.blah.MyClass" )    
            .constructor( "public B()",
                "System.out.println( mypackage.blah.MyClass.class );" );
        
        AdHocClassLoader loaded = AdHoc.compile(JavaSourceFolder.of( _derived ), 
            Javac.JavacOptions.ClassPath.of( updatedClassPath ) );
        
        assertNotNull( loaded.findClass( _derived ) );
        
    }
}
