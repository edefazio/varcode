/*
 * Copyright 2016 Eric DeFazio.
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
package varcode.java.metalang;

import varcode.java.metalang.JavaMetaLangCompiler;
import varcode.java.ast.JavaASTParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;
import varcode.java.load.JavaSourceLoader;
import varcode.java.load.LargeTopLevelClass;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author Eric DeFazio
 */
public class JavaMetaLangCompilerTest
    extends TestCase
{
    
    public void testClass() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "class A {}" );
        
        _class _c = JavaMetaLangCompiler._classFrom( 
            cu, 
            JavaASTParser.findClassDeclaration( cu, "A" ) );
        
        assertEquals( "A", _c.getName() );        
    }
    
    
    public void testBigClass() throws ParseException
    {
        //Load the Source for the Class
        SourceStream ss = 
            JavaSourceLoader.INSTANCE.sourceStream( LargeTopLevelClass.class );
        
        //Create the AST root from the Source File
        CompilationUnit astRoot = 
            JavaASTParser.astFrom( ss.getInputStream() );
        
        //find the class AST declaration within the rootAST
        ClassOrInterfaceDeclaration astClass = 
            JavaASTParser.findClassDeclaration( 
                astRoot, LargeTopLevelClass.class );
        
        //create the _class model
        _class _c = JavaMetaLangCompiler._classFrom( astRoot, astClass );
        
        System.out.println( _c.author() );
    }
    
    public void testInterface() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "interface A {}" );
        
        _interface _i = JavaMetaLangCompiler._interfaceFrom( 
            cu, 
            JavaASTParser.findInterfaceDeclaration( cu, "A" ) );
        
        assertEquals( "A", _i.getName() );        
    }
    
    public void testEnum() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "enum A {}" );
        
        _enum _e = JavaMetaLangCompiler._enumFrom( 
            cu, 
            JavaASTParser.findEnumDeclaration( cu, "A" ) );
        
        assertEquals( "A", _e.getName() );        
    }
}
