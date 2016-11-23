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
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;

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
