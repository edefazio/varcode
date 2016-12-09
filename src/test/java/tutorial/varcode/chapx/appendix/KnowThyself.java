package tutorial.varcode.chapx.appendix;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._methods._method;
import varcode.java._Java;
import varcode.load.SourceLoader.SourceStream;

/**
 * Test case which does "code introspection" on this class at runtime
 * <UL>
 *   <LI>loads the source code (.java file) that is used to represent the Class at runtime
 *   <LI>loads the AST (Abstract Syntax Tree) from the source code at runtime
 *   <LI>loads the _class (JavaMetaLang) model from the AST at runtime 
 * </UL>
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class KnowThyself
    extends TestCase
{
    /** Load the Source that makes up this class at runtime */
    public void testLoadSource()
    {        
        SourceStream ss = _Java.sourceFrom( KnowThyself.class );
        //String sourceFrom = ss.describe();
        //System.out.println( sourceFrom );
        assertNotNull( ss.describe() );
        assertEquals( 
            KnowThyself.class.getCanonicalName()+".java", 
            ss.getSourceId() );
    }
    
    /** 
     * Load the AST (Abstract Syntax Tree) Model that represents 
     * this class at runtime (First reads the source, then parses out
     * the AST from the source).
     * the AST ROOT contains package, and import information AS 
     * well as the KnowThyself Type Declaration (as a CHILD NODE)
     */
    public void testLoadAST()
    {
        CompilationUnit astRoot = _Java.astFrom( KnowThyself.class );
        //System.out.println( astRoot );
        List<TypeDeclaration> types = astRoot.getTypes();
        assertEquals( 1, types.size() );
        
        //here we can ger the AST type declaration (child) from the AST root
        ClassOrInterfaceDeclaration astClass = 
            (ClassOrInterfaceDeclaration)types.get( 0 ); 
        assertEquals( "KnowThyself", astClass.getName() );
        assertEquals( java.lang.reflect.Modifier.PUBLIC, astClass.getModifiers() ); 
    }
    
    /**
     * Load the AST child node (the TypeDeclaration) that represents the
     * declaration of KnowThySelf
     */
    public void testLoadASTTypeDeclaration()
    {
        TypeDeclaration astClassDef = 
            _Java.astTypeDeclarationFrom( KnowThyself.class );
        assertEquals( "KnowThyself", astClassDef.getName() );
        assertEquals( java.lang.reflect.Modifier.PUBLIC, astClassDef.getModifiers() );        
        
        //Members are fields, methods, nested classes, enums, interfaces, static blocks...
        List<BodyDeclaration> members = astClassDef.getMembers();
        
        //get the 
        for( int i = 0;i < members.size(); i++ )
        {
            if( members.get( i ) instanceof MethodDeclaration )
            {
                MethodDeclaration astMethod 
                    = (MethodDeclaration)members.get( i );
                
                if( "testLoadASTTypeDeclaration".equals( astMethod.getName() ) )
                {
                    assertEquals( 
                        java.lang.reflect.Modifier.PUBLIC, astMethod.getModifiers() );
                    
                    assertEquals( "void", astMethod.getType().toString() );
                    assertEquals( 0, astMethod.getTypeParameters().size() );
                    assertEquals( 0, astMethod.getThrows().size());
                }
            }
        }
    }
    
    /**
     * loads the _class LangModel from the Class at runtime
     */
    public void testLoad_class()
    {   
        //Load a _LangModel based on the code (from the AST)
        _class _c = _Java._classFrom( KnowThyself.class );
        assertEquals( "KnowThyself", _c.getName() );
        assertEquals( "tutorial.chap4.load", _c.getClassPackage().getName() );
        assertTrue( _c.getModifiers().contains( "public" ) );
        
        _method _m = _c.getMethodNamed( "testLoad_class" );
        assertNotNull( _m );
        
        assertTrue( _m.getModifiers().contains( "public" ) );
        assertEquals( "void", _m.getReturnType() );
        
        
        assertEquals( "TestCase",  _c.getSignature().getExtends().getAt( 0 ) );
        assertTrue( _c.getImports().contains( TestCase.class ) );
        assertTrue( _c.getImports().contains( CompilationUnit.class ) );
        assertTrue( _c.getImports().contains( TypeDeclaration.class ) );
        assertTrue( _c.getImports().contains( _class.class ) );
        assertTrue( _c.getImports().contains( _Java.class ) );
        assertTrue( _c.getImports().contains( SourceStream.class ) );
    }
}
