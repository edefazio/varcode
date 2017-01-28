/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model.auto;

import varcode.java.model.auto._autoEquals;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import static varcode.java.model.auto._autoEquals.of;

/**
 *
 * @author Eric
 */
public class autoEqualsTest
    extends TestCase
{
    public void testEquals()
    {
        _class _c = _class.of( "package varcode.java.meta.auto;", 
            "public class A"  )
            .field( "public int a=1;" )
            .field( "public String name=\"Eric\";" );
        
        _method _m = _autoEquals.of( _c );
        System.out.println( _m );
        _c.method( _m );
        
        System.out.println( _c );
        
        Class cClass = _c.loadClass();
        Object instance1 = Java.instance( cClass );
        Object instance2 = Java.instance( cClass );
        
        //verify that if the values are equal 
        assertEquals( true, Java.call( instance1, "equals", (Object)instance1 ) );
        assertEquals( true, Java.call( instance2, "equals", (Object)instance2 ) );
        assertEquals( true, Java.call( instance1, "equals", (Object)instance2 ) );
        assertEquals( true, Java.call( instance2, "equals", (Object)instance1 ) );
        
        //change a value
        //Java.setFieldValue( instance1, "a", 100 );
        Java.set( instance1, "a", 100 );
        assertEquals( false, Java.call( instance1, "equals", instance2 ) );
        assertEquals( false, Java.call( instance2, "equals", instance1 ) );
        
    }
    
     public static void main(String[] args)
    {
        _methods._method _m = of( "myClass", 
            _fields.of( "public int count;", "public String name" ) ); 
        
        
        System.out.println( _m );
        
        _m = of( "myClass", 
            _fields.of( "public Map<Integer,String> map;", "public boolean is" ) ); 
        System.out.println( _m );
    }
}
