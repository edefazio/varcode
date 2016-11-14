package varcode.java.model.load;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.Serializable;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.model._fields._field;
import varcode.java.model._interface;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.source.SourceLoader.SourceStream;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _LoadTest
    extends TestCase
{
 
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

    static final _Load LOADER = _Load.INSTANCE;
    public void testLoadMemberClass()
    {
        SourceStream source = 
            LOADER.sourceOf( MemberClass.class );

        System.out.println( source.asString() );
        
        TypeDeclaration dec = LOADER.astNodeOf( MemberClass.class );
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
            LOADER.sourceOf( MemberInterface.class );
        
        System.out.println( source );
        
        TypeDeclaration dec = LOADER.astNodeOf( MemberInterface.class );
        assertTrue( dec instanceof ClassOrInterfaceDeclaration );
        assertTrue( ((ClassOrInterfaceDeclaration)dec).isInterface() );
        
        _interface _i = LOADER._interfaceOf( MemberInterface.class );
        
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
            LOADER.sourceOf( MemberEnum.class );
        
        System.out.println( source );
        
        TypeDeclaration dec = LOADER.astNodeOf( MemberEnum.class );
        assertTrue( dec instanceof EnumDeclaration );
    }
        
}
