/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.ast;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import junit.framework.TestCase;
import varcode.java.ast.JavaAst;

/**
 *
 * @author Eric
 */
public class JavaAstTest
    extends TestCase
{
    public void testAst() 
         throws ParseException
    {
        CompilationUnit astRoot = 
            JavaAst.astFrom( "public class A { int ID = 100; }" );
        
    }
}
