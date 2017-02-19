/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.macro.auto;

import varcode.java.macro.auto._auto;
import varcode.java.macro.auto._autoGetters;
import java.lang.reflect.AnnotatedType;
import java.util.Map;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class autoGetTest
    extends TestCase
{
    public void testField()
    {
        //_method _m = autoGet.of( _field.of("public String a;" ) );
        _method _m = _autoGetters.of( _field.of( "public String a;" ) );
        assertEquals( "getA", _m.getName( ) );        
        assertEquals( "String", _m.getReturnType() );
        
    }
    
    public void testGetMethod()
    {
        _method _gm = _auto.getter( int.class, "count" );
        System.out.println( _gm );
        assertEquals( "int", _gm.getReturnType() );
        assertEquals( "getCount", _gm.getName() );        
    }
    
    public void testGetMethodGeneric()
    {
        _method _gm = _auto.getter( "Map<Integer, String>", "count" );
        System.out.println( _gm );
        assertEquals( "Map<Integer, String>", _gm.getReturnType() );
        assertEquals( "getCount", _gm.getName() );        
    }
    
    public static class MyClass
    {
        public static final Map<Integer,String> theMap = null;
    }
    
    /**
     * Pass in an "annoted type" as the (type) object 
     * @throws NoSuchFieldException 
     */
    public void testGetMethodGenericOfType() 
        throws NoSuchFieldException
    {
        AnnotatedType at = 
            MyClass.class.getField( "theMap" ).getAnnotatedType();
        _method _gm = _auto.getter( at, "count" );
        System.out.println( _gm );
        assertEquals( "java.util.Map<java.lang.Integer, java.lang.String>", _gm.getReturnType() );
        assertEquals( "getCount", _gm.getName() );        
    }
    
}
