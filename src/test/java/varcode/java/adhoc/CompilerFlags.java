/*
 * Copyright 2016 Eric.
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

import junit.framework.TestCase;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Javac.JavacOptions;
import varcode.java.adhoc.JavacException;
import varcode.java.adhoc.JavaSourceFolder;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class CompilerFlags
    extends TestCase
{
    public void testCompilerFlags()
    {
        _class _c = _class.of( "public class A" );
        _c.annotate( "@Deprecated" );
        
        //create a 
        JavaSourceFolder ws = JavaSourceFolder.of( _c );
        
        //compiling the workspace normally should work just fine
        AdHocClassLoader adHocClassLoader = AdHoc.compile( ws );
        assertNotNull( adHocClassLoader.findClass( _c ) );
        
        try
        {
            //compiling with Java1.3 source flag w/ annotations will fail
            AdHoc.compile( ws,
                JavacOptions.SourceVersion.MajorVersion._1_3 );
            fail( "expected a Javac Exception when compiling annotation in Java 1.3" );
        }
        catch( JavacException javacE )
        {
            //expected
        }
        
        //remove the annotation
        _c.getAnnotations().removeAt( 0 );
        
        //we need to re-add the class to the workspace (since it changed)
        ws = JavaSourceFolder.of( _c );
        
        //compiling with Java1.3 source flag w/o annotations will succeed
        AdHoc.compile( ws, JavacOptions.SourceVersion.MajorVersion._1_3 );
    }
 } 
