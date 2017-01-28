/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model.auto;

import varcode.java.model.auto._autoHashCode;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
//import varcode.java.meta.auto._autoHashCode;

/**
 *
 * @author Eric
 */
public class autoHashCodeTest
    extends TestCase
{
    public void testNoFields()
    {
        _method _hc = _autoHashCode.of(  );
        assertTrue( _hc.getAnnotations().contains( "@Override" ) );
        assertEquals( "hashCode", _hc.getName() );        
        
        
    }
    public void testOneField()
    {
        _method _hc = _autoHashCode.of( _fields.of( "int count" ) );
        assertTrue( _hc.getAnnotations().contains( "@Override" ) );
        assertEquals( "hashCode", _hc.getName() );        
    }
    
    public void testTwoField()
    {
        _methods._method _hc = 
            _autoHashCode.of( _fields.of( "int count", "String name" ) );
        assertTrue( _hc.getAnnotations().contains( "@Override" ) );
        assertEquals( "hashCode", _hc.getName() );                
    }
    
    public void testClassHc()
    {
        _class _c = _class.of("public class A")
            .field("public int a=1").field("public String name=\"Eric\"");
        
        _c.add(_autoHashCode.of( _c ) );
        
        System.out.println( _c );
        
        Class cClass = _c.loadClass();
        Object instance1 = Java.instance( cClass );
        Object instance2 = Java.instance( cClass );
        
        //verify that if the values are equal 
        assertEquals( Java.call( instance1, "hashCode"),
            Java.call( instance2, "hashCode")
            );
        
        //change a value
        Java.set( instance1, "a", 100 );
        
        assertTrue( Java.call( instance1, "hashCode") !=
            Java.call( instance2, "hashCode")
            );
    }
    
}
