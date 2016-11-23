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
package varcode.java.load;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import junit.framework.TestCase;

/**
 *
 * @author Eric DeFazio
 */
public class JavaASTParserTest
    extends TestCase
{
    public void testSimpleClassAST() 
        throws ParseException
    {
        CompilationUnit cu = 
           JavaASTParser.astFrom( "class A {}" );
        assertEquals( null, cu.getPackage());
        assertEquals( 1, cu.getTypes().size() );
        assertEquals( 0, cu.getComments().size() );
        assertEquals( 0, cu.getAllContainedComments().size() );
       
        TypeDeclaration td = 
           JavaASTParser.findTypeDeclaration( cu, "A" );
       
        ClassOrInterfaceDeclaration cd = 
           JavaASTParser.findClassDeclaration( cu, "A" );
        assertNotNull( td ); 
        
        assertNotNull( cd );
        assertEquals( "A", cd.getName() );
        assertEquals( 0, cd.getAllContainedComments().size() );
        assertEquals( 0, cd.getAnnotations().size() );
        assertEquals( 0, cd.getExtends().size() );
        assertEquals( 0, cd.getImplements().size() );
        assertNull( cd.getJavaDoc() );
        assertEquals( 0, cd.getMembers().size() );
        
        assertEquals( 0, cd.getModifiers() );
        assertEquals( 0, cd.getOrphanComments().size() );                
    }
    
    public void testSimpleInterfaceAST() 
        throws ParseException
    {
       CompilationUnit cu = 
           JavaASTParser.astFrom( "interface A {}" );
       assertEquals( null, cu.getPackage());
       assertEquals( 1, cu.getTypes().size() );
       assertEquals( 0, cu.getComments().size() );
       assertEquals( 0, cu.getAllContainedComments().size() );
       
        TypeDeclaration td = 
           JavaASTParser.findTypeDeclaration( cu, "A" );
        
        ClassOrInterfaceDeclaration cd = 
           JavaASTParser.findInterfaceDeclaration( cu, "A" );
        
        assertNotNull( td ); 
        assertNotNull( cd );
        assertEquals( "A", cd.getName() );
        assertEquals( 0, cd.getAllContainedComments().size() );
        assertEquals( 0, cd.getAnnotations().size() );
        assertEquals( 0, cd.getExtends().size() );
        assertEquals( 0, cd.getImplements().size() );
        assertNull( cd.getJavaDoc() );
        assertEquals( 0, cd.getMembers().size() );
        
        assertEquals( 0, cd.getModifiers() );
        assertEquals( 0, cd.getOrphanComments().size() );
    }
    
    public void testSimpleEnumAST() 
        throws ParseException
    {
       CompilationUnit cu = 
           JavaASTParser.astFrom( "enum A {}" );
       assertEquals( null, cu.getPackage());
       assertEquals( 1, cu.getTypes().size() );
       assertEquals( 0, cu.getComments().size() );
       assertEquals( 0, cu.getAllContainedComments().size() );
       
        TypeDeclaration td = 
           JavaASTParser.findTypeDeclaration( cu, "A" );
        
        EnumDeclaration cd = 
           JavaASTParser.findEnumDeclaration( cu, "A" );
        
        assertNotNull( td ); 
        
        assertNotNull( cd );
        assertEquals( "A", cd.getName() );
        assertEquals( 0, cd.getAllContainedComments().size() );
        assertEquals( 0, cd.getAnnotations().size() );
        assertEquals( 0, cd.getImplements().size() );
        assertNull( cd.getJavaDoc() );
        assertEquals( 0, cd.getMembers().size() );
        
        assertEquals( 0, cd.getModifiers() );
        assertEquals( 0, cd.getOrphanComments().size() );
    }
}
