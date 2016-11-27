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
package varcode.java.load;

import com.github.javaparser.ast.CompilationUnit;
import junit.framework.TestCase;
import varcode.java._Java;

/**
 *
 * @author Eric
 */
public class FormatCodeTest 
    extends TestCase
{

    public void testFormat()
    {
        String code = "class A { int f; void foo(int p) { return 'z'; }}";
        //CompilationUnit cu = JavaParser.parse(code);
        //_java.astTypeDeclarationFrom( FormatCodeTest.class );
        CompilationUnit cu = 
            _Java.astFrom( "class A { int f; void foo(int p) { return 'z'; }}" );
        
        _Java._classFrom( "public class A {}" );
        
        //String source = JavaASTToCodeVisitor.print( cu );
        //System.out.println( source );
    }
}
