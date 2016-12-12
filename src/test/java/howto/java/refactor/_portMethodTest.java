package howto.java.refactor;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.lang._class;
import varcode.java.lang._code;
import varcode.java.lang._fields._field;
import varcode.java.lang._imports;
import varcode.java.macro._portableMethod;
import varcode.java.lang._methods._method;
import varcode.java.macro._Port;

/**
 *
 * @author Eric
 */
public class _portMethodTest 
    extends TestCase
{
    
    /**
     * 
     */
    _portableMethod _PORT_PREFIX_ID_METHOD = 
        _portableMethod.of( 
            _method.of( "public String getId()", 
                _code.of( "return prefix + UUID.randomUUID().toString();" ) ) )
            .addRequiredFields( _field.of( "public String prefix;" ) ) 
            .addRequiredImports( _imports.of( UUID.class ) );
    
    /**
     * Port a method to an "empty class"
     * 
     * verify that the _class is modified and can compile afterward
     */
    public void testPortMethodToEmpty_class()
    {    
        _class _simple = _class.of(
            "howto.java.refeactor", "public class EmptyClass" );
        
        //write the method and any dependencies into the _simple method
        _simple = _Port.portForce( _PORT_PREFIX_ID_METHOD, _simple );
         
        //System.out.println( _simple );
        Object instance = _simple.instance();
        _Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        String prefix = (String)_Java.invoke( instance, "getId" );
        assertTrue( prefix.startsWith( "PREFIX" ) );        
    }
    
    /**
     * Port a _method to an _class that already has one of the 
     * required fields ( prefix ) defined
     * (in this case the field is a static field)
     */
    public void testPortMethodAlreadyDefinedField()
    {
        _class _hasField = _class.of(
            "howto.java.refeactor", 
            "public class EmptyClass",
            _field.of("public static final String prefix = \"THEPREFIX\";") 
        );
        
        //write the method and any dependencies into the _simple method
        _hasField = _Port.portForce( _PORT_PREFIX_ID_METHOD, _hasField );
        
        //System.out.println( _simple );
        Object instance = _hasField.instance();
        //_Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        String prefix = (String)_Java.invoke( instance, "getId" );
        assertTrue( prefix.startsWith( "THEPREFIX" ) ); 
    }

    /**
     * Port a method ot a model that already has an abstract method
     * (i.e. replace the abstract method with the "ported" method.
     */
    public void testPortMethodAbstractMethod()
    {
        _class _hasAbstractMethod = _class.of(
            "howto.java.refactor", 
            "public abstract class EmptyClass",
            _method.of( "public abstract String getId()" )             
        );
        
        //write the method and any dependencies into the _simple method
        _hasAbstractMethod = 
            _Port.portForce( _PORT_PREFIX_ID_METHOD, _hasAbstractMethod );
        
        
        //get rid of abstract
        _hasAbstractMethod.setModifiers( "public" );
        
        //System.out.println( _simple );
        Object instance = _hasAbstractMethod.instance();
        //_Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        _Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        String prefix = (String)_Java.invoke( instance, "getId" );
        assertTrue( prefix.startsWith( "PREFIX" ) );        
    }    
}
