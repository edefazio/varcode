package varcode.java.load.lang;

import varcode.java.load.JavaMetaLangLoader;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.UUID;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import varcode.Lang;
import varcode.Model;
import varcode.VarException;
import varcode.java.metalang._class;
import varcode.java.metalang._constructors;
import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._enum;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._imports;
import varcode.java.metalang._interface;
import varcode.java.metalang._javadoc;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;
import varcode.java.metalang._parameters;

/**
 * 
 * @author eric
 */
public class JavaMetaLangLoaderTest
    extends TestCase
{
    public void testLoad_interface()
    {
        _interface _i = JavaMetaLangLoader._Interface.from( Model.class );
        
        assertEquals( "varcode", _i.getPackage().getName() );
        
        //import varcode.context.VarContext;
        //import varcode.doc.Directive;        
        _imports imports = _i.getImports();
        assertEquals( 2, imports.count() );
        assertTrue( imports.contains( "varcode.context.VarContext" ) );
        assertTrue( imports.contains( "varcode.doc.Directive" ) );        
        System.out.println( _i.getJavadoc() );        
    }
    
    public void testLoad_enum()
    {
        _enum _e = JavaMetaLangLoader._Enum.from( Lang.class );
        assertEquals( "Lang", _e.getName() );
        assertEquals( 3, _e.getFields().count() );
        _e.getValueConstructs().getByName( Lang.JAVA.getName() );
    }
    
    public interface SimpleInterface
    {
        
    }
    
    public void testLoadMemberLevelInterfaceModel()
    {
        _interface _i = 
            JavaMetaLangLoader._Interface.from( SimpleInterface.class );
        //JavaLoad._interfaceOf( SimpleInterface.class );        
    }
    
    @Deprecated
    public interface MemberInterface
        extends Serializable
    {
        @Deprecated
        public int id = 10101;
        
    }
    
    public static class BaseClass
    {
        
    }
    
    /**
     * The Javadoc For the class
     */
    @Deprecated
    public static class MemberClass
        extends BaseClass    
        implements MemberInterface
    {
        /** The Comment for the field */
        @Deprecated
        public static String NAME = "theName";
        
        /** Another field comment */
        @Deprecated
        public final int version;
        
        /** constructor comment
         * @param version */
        @Deprecated
        public MemberClass( int version )
        {
            this.version = version;            
        }
        
        /** method comment */
        @Deprecated
        public void printName()
        {
            System.out.println(NAME);
        }        
    }

    //Load the _class model for 
    public void testLoadMemberClass()
    {
        //this is what it takes to LOAD / Parse the source AST
        // and create a _class Model
        _class _c = 
            JavaMetaLangLoader._Class.from( MemberClass.class );
            //JavaLoad._classOf( MemberClass.class );
        
        //verify I modeled the annotations
        assertEquals( 1, _c.getAnnotations().count() );
        assertEquals( "@Deprecated", _c.getAnnotations().getAt( 0 ).toString() );        
        assertEquals( "MemberClass", _c.getName() );
        
        assertEquals( 1, _c.getSignature().getExtends().count() );
        assertEquals( "BaseClass", _c.getSignature().getExtends().getAt( 0 ).toString() );
        
        assertEquals( 1, _c.getSignature().getImplements().count() );                
        assertEquals( "MemberInterface", _c.getSignature().getImplements().getAt( 0 ).toString() );
        
        _constructors ctors = _c.getConstructors();
        assertEquals( 1, ctors.count() );
        
        _constructor ctor = ctors.getAt( 0 );
        assertEquals( 1, ctor.getAnnotations().count() );
        assertEquals( "@Deprecated", ctor.getAnnotations().getAt( 0 ) );        
        assertEquals( "MemberClass", ctor.getSignature().getClassName() );
        assertTrue( ctor.getSignature().getModifiers().containsAll( Modifier.PUBLIC ) );
        assertEquals( 1, ctor.getSignature().getParameters().count() );        
        assertEquals( "int", ctor.getSignature().getParameters().getAt( 0 ).getType() );
        assertEquals( "version", ctor.getSignature().getParameters().getAt( 0 ).getName() );     
        assertEquals( "this.version = version;", ctor.getBody().toString() );
        
        _fields fields = _c.getFields();
        assertEquals( 2, fields.count() );
        
        _field NAMEfield = fields.getByName( "NAME" );        
        assertEquals( "String",  NAMEfield.getType() );
        assertTrue( NAMEfield.hasInit() );
        assertTrue( NAMEfield.getModifiers().containsAll("public", "static" ) );
        assertEquals( " =\"theName\"", NAMEfield.getInit().toString() );        
        assertEquals( "int", fields.getByName( "version" ).getType() );
        assertEquals( 1, NAMEfield.getAnnotations().count() );
        assertEquals( "@Deprecated", NAMEfield.getAnnotations().getAt( 0 ).toString() );
        
        _javadoc jd = NAMEfield.getJavadoc();
        System.out.println( "JAVADOC " + jd );
        
        _field versionField = fields.getByName( "version" );        
        assertEquals( "int",  versionField.getType() );
        assertFalse( versionField.hasInit() );
        assertTrue( versionField.getModifiers().containsAll( "public", "final" ) );
        assertEquals( 1, versionField.getAnnotations().count() );
        assertEquals( "@Deprecated", versionField.getAnnotations().getAt( 0 ).toString() );  
        
        _methods methods = _c.getMethods();
        assertEquals( 1, methods.count() );
        _method m = methods.getByName( "printName" ).get( 0 );
        assertEquals( 1, m.getAnnotations().count() );
        assertEquals( "@Deprecated", m.getAnnotations().getAt( 0 ).toString() );
        assertEquals( "void", m.getSignature().getReturnType() );
        assertTrue( m.getSignature().getModifiers().containsAll("public") );
        assertEquals( "System.out.println(NAME);", m.getBody().toString() );
        
        
        
        //System.out.println( _c );
        
    }
    
    public void testLoad_class()
    {
        //load the _class model for VarException.class
        //_class c = JavaLoad._classOf( VarException.class );
        _class c = JavaMetaLangLoader._Class.from( VarException.class );
        assertEquals( "varcode.VarException",  c.getFullyQualifiedClassName() );
        assertEquals( "varcode", c.getClassPackage().getName() );
        assertEquals( 1, c.getSignature().getExtends().count() );
        assertEquals( "RuntimeException", c.getSignature().getExtends().getAt( 0 ) );
        assertEquals( 0, c.getSignature().getImplements().count() );
        assertTrue( c.getSignature().getModifiers().containsAll( "public" ) );
        
        //private static final long serialVersionUID = 4495417336149528283L;
        _fields fields = c.getFields();
        assertEquals( 1, fields.count() );
        _field f = fields.getAt( 0 );
        assertEquals( "serialVersionUID", f.getName() );
        assertTrue( 
            f.getModifiers().containsAll(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL) );
        assertEquals( "long", f.getType() );
        assertTrue( f.hasInit() );
        assertEquals( " =4495417336149528283L", f.getInit().toString() );         
    }
    
    
    /** 
     * We want to load the _class model for this class
     * then specialize it at runtime
     */
    public static class PrefixCreateId 
        implements Serializable
    {
        public final String prefix;
        
        public PrefixCreateId( String prefix )
        {
            this.prefix = prefix;
        }
        
        public String createId()
        {
            return this.prefix + UUID.randomUUID().toString();
        }
    }
    
    public void testLoadModelOfMemberClass()
    {
        //load the "model" of the PrefixCreateIdClass( from the source )
        //_class _c = 
        //    JavaLoad._classOf( PrefixCreateId.class );
        _class _c = JavaMetaLangLoader._Class.from( PrefixCreateId.class );
        //System.out.println( "JAVADOC" + _c.getJavadoc() );
        
        assertTrue( _c.getSignature().getModifiers().containsAll( "public", "static" ) );
        assertEquals( "PrefixCreateId", _c.getSignature().getName() );
        assertEquals( 1, _c.getSignature().getImplements().count() );
        assertTrue( _c.getSignature().getImplements().contains( "Serializable" ) );
        
        assertEquals( 1, _c.getFields().count() );        
        _field prefix = _c.getFields().getByName( "prefix" );
        assertTrue( prefix.getModifiers().containsAll( Modifier.PUBLIC, Modifier.FINAL ) );
        assertEquals( "String", prefix.getType() );
        
        _constructors ctors = _c.getConstructors();
        assertEquals( 1, ctors.count() );
        
        _constructor ctor = ctors.getAt( 0 );
        assertEquals( "PrefixCreateId", ctor.getSignature().getClassName() );
        assertTrue( ctor.getSignature().getModifiers().containsAll( Modifier.PUBLIC ) );
        assertEquals( 1, ctor.getSignature().getParameters().count() );
        _parameters._parameter param = ctor.getSignature().getParameters().getAt( 0 );
        assertEquals( "String", param.getType() );
        assertEquals( "prefix", param.getName() );
        
        _methods methods = _c.getMethods();
        assertEquals( 1, methods.count() );
        _method createId = _c.getMethodsByName( "createId" ).get( 0 );        
        assertEquals( "String", createId.getSignature().getReturnType() );
    }
}
