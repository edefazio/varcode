package tutorial.chap4.load;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._methods._method;
import varcode.java.load._java;
import varcode.load.SourceLoader.SourceStream;

/**
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class KnowThyself
    extends TestCase
{
    /** Load the Source that makes up this class at runtime */
    public void testLoadSource()
    {        
        SourceStream ss = _java.sourceFrom( KnowThyself.class );
        //System.out.println( ss.asString() );
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
        CompilationUnit astRoot = _java.astFrom( KnowThyself.class );
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
            _java.astDeclarationFrom( KnowThyself.class );
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
    public void testLoad_LangModel()
    {   
        //Load a _LangModel based on the code (from the AST)
        _class _c = _java._classFrom( KnowThyself.class );
        assertEquals( "KnowThyself", _c.getName() );
        assertEquals( "tutorial.chap4.load", _c.getClassPackage().getName() );
        assertTrue( _c.getModifiers().contains( "public" ) );
        
        _method _m = _c.getMethodNamed( "testLoad_LangModel" );
        assertNotNull( _m );
        
        assertTrue( _m.getModifiers().contains( "public" ) );
        assertEquals( "void", _m.getReturnType() );
        
        
        assertEquals( "TestCase",  _c.getSignature().getExtends().getAt( 0 ) );
        assertTrue( _c.getImports().contains( TestCase.class ) );
        assertTrue( _c.getImports().contains( CompilationUnit.class ) );
        assertTrue( _c.getImports().contains( TypeDeclaration.class ) );
        assertTrue( _c.getImports().contains( _class.class ) );
        assertTrue( _c.getImports().contains( _java.class ) );
        assertTrue( _c.getImports().contains( SourceStream.class ) );
    }
}
