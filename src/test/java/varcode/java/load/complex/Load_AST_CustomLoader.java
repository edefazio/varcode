/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.load.complex;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import varcode.java.load.Load_AST;
import java.lang.reflect.Modifier;
import static junit.framework.TestCase.assertEquals;
import varcode.java.Java;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class Load_AST_CustomLoader
{
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
        CompilationUnit astRoot = Java.astFrom(
            new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" ),
                Load_AST.class);
        
        assertEquals( "howto.java_metalang", 
            astRoot.getPackage().getName().toString());
        
        //under the AST CompilationUnit node is the TypeDeclaration of the class
        TypeDeclaration typeDec = Java.astTypeDeclarationFrom(astRoot,  
            Load_AST.class );
        
        assertEquals( Modifier.PUBLIC, typeDec.getModifiers() );
        assertEquals(Load_AST.class.getSimpleName(), 
            typeDec.getName() );
        
    }    
    
    public void testLoadClassASTSourcePath()
    {
        
        CompilationUnit astRoot = Java.astFrom( Load_AST.class);
        
        assertEquals( "howto.java_metalang", 
            astRoot.getPackage().getName().toString());
        
        //under the AST CompilationUnit node is the TypeDeclaration of the class
        TypeDeclaration typeDec = Java.astTypeDeclarationFrom(astRoot,  
            Load_AST.class );
        
        assertEquals( Modifier.PUBLIC, typeDec.getModifiers() );
        assertEquals(Load_AST.class.getSimpleName(), 
            typeDec.getName() );
    }
}
