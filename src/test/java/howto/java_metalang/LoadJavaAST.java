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
package howto.java_metalang;

import com.github.javaparser.ast.CompilationUnit;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.load.JavaSourceLoader;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class LoadJavaAST 
    extends TestCase
{
     /**
     * This will load Java AST (using the JavaParser API from the "normal" 
     * locations that Java source code appears.
     * 
     * under the covers it will load using the strategy defined in:
     * {@code varcode.java.load.JavaSourceLoader}
     */
    public void testLoadASTDefaultLoader()
    {
        CompilationUnit astRoot = _Java.astFrom( LoadJavaAST.class );        
    }
    
    public void testLoadASTSpecificLoader()
    {
        //Load the 
        CompilationUnit astRoot = _Java.astFrom( 
            JavaSourceLoader.BaseJavaSourceLoader.SRC_TEST_JAVA_DIRECTORY,
                LoadJavaAST.class );        
    }
    
    /**
     * To specify EXACTLY where to Load the Source From
     * you can pass in a SourceLoader instance.
     * 
     * In this case we pass in a DirectorySourceLoader instance
     * Starting in the src/test/java directory (under the user.dir)
     * and will look for the source file: 
     * "${user/dir}/src/test/java/howto/java_metalang.LoadAST.java"
     * 
     * In this will search for the 
     */
    public void testLoadASTCustomLoader()
    {
        CompilationUnit astRoot = _Java.astFrom( 
           new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" ),
                howto.java_metalang.LoadJavaAST.class);
    }    
}
