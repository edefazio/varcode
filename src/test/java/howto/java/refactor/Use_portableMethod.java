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
import varcode.java.macro._portMethod;

/**
 *
 * @author Eric
 */
public class Use_portableMethod 
    extends TestCase
{
    _portableMethod _GET_PREFIX_ID = 
        _portableMethod.of( 
            _method.of( "public String getId()", 
                _code.of( "return prefix + UUID.randomUUID().toString();") ) )
            .addRequiredFields( _field.of( "public String prefix;" ) ) 
            .addRequiredImports( _imports.of( UUID.class ) );
    
    public void testTransposeToEmpty_class()
    {    
        _class _simple = _class.of(
            "howto.java.refeactor", "public class EmptyClass" );
        
        //write the method and any dependencies into the _simple method
        _simple = _portMethod.portTo( _GET_PREFIX_ID, _simple );
        
        //System.out.println( _simple );
        Object instance = _simple.instance();
        _Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        String prefix = (String)_Java.invoke( instance, "getId" );
        assertTrue( prefix.startsWith( "PREFIX" ) );
        
        
    }
}
