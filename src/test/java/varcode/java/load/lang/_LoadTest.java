package varcode.java.load.lang;

//import varcode.java.ast.JavaASTParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.Serializable;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._interface;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;
import varcode.java.load.JavaSourceLoader;
import varcode.java._Java;
import varcode.java.ast.JavaASTParser;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _LoadTest
    extends TestCase
{
 
    /**
     * Marker Annotation to represent the Context
     * @author eric
     */
    public @interface Context
    {
    
    }

    /**
     * Member Class Comment
     */
    @Context
    public static class MemberClass
    {
        public int field;
        
        public void method( String str )
        {
            System.out.println( str );
        }
    }

    //static final _JavaLoader LOADER = _Load.INSTANCE;
    public void testLoadMemberClass() throws ParseException
    {
        //SourceStream source = 
        //    BaseSourceLoader.INSTANCE.sourceStream( MemberClass.class );
            //LOADER.sourceOf( MemberClass.class );

        SourceStream ss = _Java.sourceFrom( MemberClass.class );
        
        System.out.println( ss.asString() );
        
        CompilationUnit astRoot = _Java.astFrom( _LoadTest.class );        
        TypeDeclaration dec = JavaASTParser.findClassDeclaration(
            astRoot,
            MemberClass.class );
        
        assertTrue( dec instanceof ClassOrInterfaceDeclaration );
        assertFalse( ((ClassOrInterfaceDeclaration)dec).isInterface() );        
    }
    
    /**
     * Member Interface Comment
     */
    @Context
    public interface MemberInterface
        extends Serializable
    {
        public static final String id = UUID.randomUUID().toString();
        
        public String getName();
        
        int getCount();
    }
    
    public void testLoadMemberInterface()
    {
        SourceStream source = 
            JavaSourceLoader.INSTANCE.sourceStream( MemberInterface.class );
            //LOADER.sourceOf( MemberInterface.class );
        
        System.out.println( source );
        
        TypeDeclaration dec = _Java.astTypeDeclarationFrom( MemberInterface.class );
        assertTrue( dec instanceof ClassOrInterfaceDeclaration );
        assertTrue( ((ClassOrInterfaceDeclaration)dec).isInterface() );
        
        _interface _i = _Java._interfaceFrom( MemberInterface.class );
        
        assertEquals( _i.getName(), "MemberInterface" );
        assertEquals( _i.getSignature().getExtends().getAt( 0 ), "Serializable" );
        _field f = _i.getFields().getByName( "id" );
        assertEquals( f.getType(),"String" );
        _methods ms = _i.getMethods();
        _method m = ms.getByName( "getName" ).get( 0 );
        assertTrue( m.getBody().isEmpty() );
        
        System.out.println( m );
        
            
    }
    
    /**
     * Member Enum Comment
     */
    @Context
    public enum MemberEnum
        implements MemberInterface
    {
        A("A");
        
        private final String name;
        
        private MemberEnum( String name )
        {
            this.name = name;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        public int getCount()
        {
            return 1;
        }
    }
    
    public void testLoadMemberEnum()
    {
        SourceStream source = 
            _Java.sourceFrom( MemberEnum.class );
        
        System.out.println( source );
        
        TypeDeclaration dec = _Java.astTypeDeclarationFrom( MemberEnum.class );
        assertTrue( dec instanceof EnumDeclaration );
    }
        
}
