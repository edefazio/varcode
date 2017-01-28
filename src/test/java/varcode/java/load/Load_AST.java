package varcode.java.load;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java.Java;

/**
 * Different options for loading 
 * @author Eric
 */
public class Load_AST 
    extends TestCase
{
    public void testLoadASTFromString()
    {
        CompilationUnit astRoot = Java.astFrom(
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
        CompilationUnit astRoot = Java.astFrom(Load_AST.class );

        assertEquals( "howto.java_metalang", 
            astRoot.getPackage().getName().toString());        
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
            Java.astTypeDeclarationFrom( NestedClass.class );
        
        // verify that the TypeDeclaration Node loaded contains the
        // 
        assertTrue( 
            typeDecl.getJavaDoc().getContent().contains( 
                "The Nested Class Comment" ) );
        
        //verify the class is public static
        assertEquals( Modifier.PUBLIC | Modifier.STATIC, typeDecl.getModifiers() );
    }
}
