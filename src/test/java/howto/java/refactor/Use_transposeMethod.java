package howto.java.refactor;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._code;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._imports;
import varcode.java.metalang.macro._methodTranspose;
import varcode.java.metalang._methods._method;

/**
 *
 * @author Eric
 */
public class Use_transposeMethod 
    extends TestCase
{

    _methodTranspose _GET_PREFIX_ID = 
        _methodTranspose.of( 
            _method.of( "public String getId()", 
                    _code.of( "return prefix + UUID.randomUUID().toString();") ) )
            .addRequiredFields( _field.of( "public String prefix;" ) ) 
            .addRequiredImports( _imports.of( UUID.class ) );
    
    public void testTransposeToEmpty_class()
    {    
        _class _simple = _class.of(
            "howto.java.refeactor", "public class EmptyClass" );
        
        //write the method and any dependencies into the _simple method
        _simple = _GET_PREFIX_ID.transposeTo( _simple );
        
        //System.out.println( _simple );
        Object instance = _simple.instance();
        _Java.setFieldValue( instance, "prefix", "PREFIX" );
        
        String prefix = (String)_Java.invoke( instance, "getId" );
        assertTrue( prefix.startsWith( "PREFIX" ) );
        
        
    }
}
