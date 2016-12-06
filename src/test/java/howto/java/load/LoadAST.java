package howto.java.load;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.load.DirectorySourceLoader;

/**
 * Different options for loading 
 * @author Eric
 */
public class LoadAST 
    extends TestCase
{
    public void testLoadASTFromString()
    {
        CompilationUnit astRoot = _Java.astFrom(
            "package a.b.c; "
          + "public class A { public int a; }" );
        
        assertEquals( "a.b.c", astRoot.getPackage().getPackageName() );
    }
    
     /**
     * This will load Java AST (using the JavaParser API from the "normal" 
     * locations that Java source code appears.
     * 
     * under the covers it will load using the strategy defined in:
     * {@code varcode.java.load.JavaSourceLoader}
     */
    public void testLoadASTDefaultLoader()
    {
        CompilationUnit astRoot = _Java.astFrom(LoadAST.class );

        assertEquals( "howto.java_metalang", 
            astRoot.getPackage().getName().toString());        
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
        CompilationUnit astRoot = _Java.astFrom(new DirectorySourceLoader( 
                System.getProperty( "user.dir" ) + "/src/test/java/" ),
                howto.java.load.LoadAST.class);
        
        assertEquals( "howto.java_metalang", 
            astRoot.getPackage().getName().toString());
        
        //under the AST CompilationUnit node is the TypeDeclaration of the class
        TypeDeclaration typeDec = _Java.astTypeDeclarationFrom(astRoot,  
            LoadAST.class );
        
        assertEquals( Modifier.PUBLIC, typeDec.getModifiers() );
        assertEquals(LoadAST.class.getSimpleName(), 
            typeDec.getName() );
        
    }    
    
    @interface MyAnnotation
    {
        
    }
    
    /** The Nested Class Comment */
    @MyAnnotation
    public static class NestedClass
    {
        public static final int A = 100;
        
        /** Method Comment */
        public static int getA()
        {
            return A;
        }
    }
    
    /** 
     * We can Load the TypeDeclaration AST node for a Nested Class
     * it returns a TypeDeclaration Node (and not a CompilationUnit node)
     */
    public void testLoadNestedASTTypeDefinition()
    {
        TypeDeclaration typeDecl = 
            _Java.astTypeDeclarationFrom( NestedClass.class );
        
        // verify that the TypeDeclaration Node loaded contains the
        // 
        assertTrue( 
            typeDecl.getJavaDoc().getContent().contains( 
                "The Nested Class Comment" ) );
        
        //verify the class is public static
        assertEquals( Modifier.PUBLIC | Modifier.STATIC, typeDecl.getModifiers() );
    }
}
