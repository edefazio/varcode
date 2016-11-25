package quickstart.java;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.Java;
import varcode.java.lang._class;
import varcode.java.lang._fields;
import varcode.java.lang._fields._field;
import varcode.java.lang._methods._method;
import varcode.java.load._java;

/**
 *
 * @author Eric
 */
public class _1_KnowThyself
    extends TestCase
{
    /**
     * loads the {@code _class} LangModel for _1_KnowThyself (this class)
     * at runtime... the LangModel  
     */
    public void testLoad_LangModel()
    {   
        _class _c = _java._classFrom( _1_KnowThyself.class );
        verify_LangModel( _c ); //verify that the _class loaded 
        _class _modifiedClone = change_class( _c ); //create a clone and modify
        
        System.out.println( _modifiedClone );
        
        Class adHocClass = _modifiedClone.loadClass();
        
        //call a static method on the new AdHocClass
        Java.invoke( adHocClass, "logMessage", "Some Message" );
    }
    
    private _class change_class( _class _c )
    {
        _class _clone = _c.clone();
        _clone.setName( "Change_Thyself" ); //rename the class
        _clone.replace("_1_KnowThyself", "Change_ThySelf" );
        _clone.imports( Logger.class, LoggerFactory.class );
        //add a Logger
        _clone.field( 
            _field.of( "public static final Logger LOG = " + 
            "LoggerFactory.getLogger( Change_ThySelf.class ) " ) );
        
        _clone.method( "public static void logMessage( String message )", 
                "LOG.info( message );" );
        
        return _clone;
    }
            
    
    //this just validates that the _class LangModel was indeed loaded
    private boolean verify_LangModel( _class _c )
    {
        assertEquals( "_1_KnowThyself", _c.getName() );
        assertEquals( "quickstart.java", _c.getPackageName() );
        assertTrue( _c.getModifiers().contains( "public" ) );
        
        _method _thisMethod = _c.getMethodNamed( "verify_LangModel" );
        assertTrue( _thisMethod.getModifiers().contains( Modifier.PRIVATE ) );
        assertEquals( "boolean", _thisMethod.getReturnType() );                
        assertEquals( 1, _thisMethod.getParameters().count() );
        assertEquals( "_c", _thisMethod.getParameters().getAt(0).getName() );
        assertEquals( "_class", _thisMethod.getParameters().getAt(0).getType() );
        
        
        _method _m = _c.getMethodNamed( "testLoad_LangModel" );
        assertNotNull( _m );
        
        assertTrue( _m.getModifiers().contains( "public" ) );
        assertEquals( "void", _m.getReturnType() );
        
        
        assertEquals( "TestCase",  _c.getSignature().getExtends().getAt( 0 ) );
        assertTrue( _c.getImports().contains( TestCase.class ) );
        assertTrue( _c.getImports().contains( _class.class ) );
        assertTrue( _c.getImports().contains( _java.class ) );
        assertTrue( _c.getImports().contains( _method.class ) );
        return true;
    }
}
