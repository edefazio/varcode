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
package varcode.java.load.langmodel;

import varcode.java.load.langmodel.Java_LangModelCompiler;
import varcode.java.ast.JavaASTParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import junit.framework.TestCase;
import varcode.java.langmodel._class;
import varcode.java.langmodel._enum;
import varcode.java.langmodel._interface;
import varcode.java.load.BaseSourceLoader;
import varcode.java.load.LargeTopLevelClass;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author Eric DeFazio
 */
public class Java_LangModelCompilerTest
    extends TestCase
{
    
    public void testClass() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "class A {}" );
        
        _class _c = Java_LangModelCompiler._classFrom( 
            cu, 
            JavaASTParser.findClassDeclaration( cu, "A" ) );
        
        assertEquals( "A", _c.getName() );        
    }
    
    
    public void testBigClass() throws ParseException
    {
        //Load the Source for the Class
        SourceStream ss = 
            BaseSourceLoader.INSTANCE.sourceStream( LargeTopLevelClass.class );
        
        //Create the AST root from the Source File
        CompilationUnit astRoot = 
            JavaASTParser.astFrom( ss.getInputStream() );
        
        //find the class AST declaration within the rootAST
        ClassOrInterfaceDeclaration astClass = 
            JavaASTParser.findClassDeclaration( 
                astRoot, LargeTopLevelClass.class );
        
        //create the _class model
        _class _c = Java_LangModelCompiler._classFrom( astRoot, astClass );
        
        System.out.println( _c.author() );
    }
    
    public void testInterface() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "interface A {}" );
        
        _interface _i = Java_LangModelCompiler._interfaceFrom( 
            cu, 
            JavaASTParser.findInterfaceDeclaration( cu, "A" ) );
        
        assertEquals( "A", _i.getName() );        
    }
    
    public void testEnum() throws ParseException
    {
        CompilationUnit cu = 
            JavaASTParser.astFrom( "enum A {}" );
        
        _enum _e = Java_LangModelCompiler._enumFrom( 
            cu, 
            JavaASTParser.findEnumDeclaration( cu, "A" ) );
        
        assertEquals( "A", _e.getName() );        
    }
}
