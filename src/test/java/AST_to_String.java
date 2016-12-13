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


import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.ast.JavaAst;
import varcode.java.lang._if;

/**
 *
 * @author Eric
 */
public class AST_to_String 
    extends TestCase
{
    public static class MethodWithIf
    {
        public static void methodWithIf()
        {            
            if( ( System.currentTimeMillis() & 1L ) == 1 )
            {
                System.out.println( "Odd" );
            }
        }
    }
    public void testThing( )
    {
        TypeDeclaration astTypeDecl = 
            _Java.astTypeDeclarationFrom( MethodWithIf.class ); //MethodWithSomeCode.class );
        
        MethodDeclaration astMethod = 
            JavaAst.findAllMethods( astTypeDecl )[ 0 ];
        
        
        
    }
}
