/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.macro.auto;

import varcode.java.macro.auto._auto;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._constructors._constructor;

/**
 *
 * @author Eric
 */
public class _autoMethodsTest
    extends TestCase
{
    
    public void testStringEquals()
    {
        assertTrue( java.util.Objects.equals( "eric", "eric" ) );
        
    }
    public void testDataClassImmutable()
    {
        _class _c = _auto.dataClass( 
            "ex.varcode.auto.Immutable", "final String name", "final int count" );
        System.out.println( _c );
        assertEquals( "ex.varcode.auto", _c.getPackageName() );
        Class autoClass = _c.loadClass();
        
        //the auto generated constructor has (2) fields because  
        //there are (2) final fields that are not initialized
        assertEquals( 2, _c.getConstructor( 0 ).getParameters().count() );   
        Object eric1A = Java.instance( autoClass, "eric", 1 );
        Object eric1B = Java.instance( autoClass, "eric", 1 );
        
        assertTrue( (Boolean)Java.call( eric1A, "equals", eric1A ) );
        assertTrue( (Boolean)Java.call( eric1B, "equals", eric1B ) );
        assertTrue( (Boolean)Java.call( eric1A, "equals", eric1B ) );
        
        
        assertEquals( "String", _c.getMethod( "getName" ).getReturnType() );
        assertEquals( "int", _c.getMethod( "getCount" ).getReturnType() );
        
        //ensure the fluent setter methods return the instance
        //assertEquals( eric1A, Java.call( eric1A, "setCount", 10101 ) );
        //assertEquals( 10101, Java.call( eric1A, "getCount") );        
    }
    
    public void testDataClass()
    {
        _class _c = _auto.dataClass( "Mutable", "String name", "int count" );
        System.out.println( _c );
        Class autoClass = _c.loadClass();
        
        Object dc1 = Java.instance( autoClass );
        Object dc2 = Java.instance( autoClass );
        
        assertEquals( dc1, Java.call( dc1, "setName", "eric" ) );
        assertEquals( dc2, Java.call( dc2, "setName", "eric" ) );
        
        assertEquals( dc1, Java.call( dc1, "setCount", 101 ) );
        assertEquals( dc2, Java.call( dc2, "setCount", 101 ) );
        
        assertTrue( (Boolean)Java.call( dc1, "equals", dc1 ) );
        assertTrue( (Boolean)Java.call( dc1, "equals", dc2 ) );
        assertTrue( (Boolean)Java.call( dc2, "equals", dc2 ) );
        assertTrue( (Boolean)Java.call( dc2, "equals", dc1 ) );
        
        
    }

    
    
    
    public void testNoSetterForFinalFields()
    {
        _class _c = _class.of( "MyDto").properties( "public final int a;", "static final int b;" );        
        assertEquals( 2, _c.getMethods().count() ); //only the getter methods
        assertEquals( "getA", _c.getMethods().getAt( 0 ).getName() );
        assertEquals( "getB", _c.getMethods().getAt( 1 ).getName() );        
    }
    
    public void testEquals()
    {
        _class _c = 
            _auto.equalsTo(
                _auto.constructorTo( 
                    _class.of( "A" ).properties( "final int a;", "final int b;" ) ) );
        
        Class eqClass = _c.loadClass();
        
        Object oneTwoA = Java.instance( eqClass, 1,2 );
        Object oneTwoB = Java.instance( eqClass, 1,2 );
        
        Object twoOneA = Java.instance( eqClass, 2,1 );
        Object twoOneB = Java.instance( eqClass, 2,1 );
        
        assertTrue( (Boolean)Java.call( oneTwoA, "equals", oneTwoB ) ); 
        assertTrue( (Boolean)Java.call( oneTwoA, "equals", oneTwoA ) ); 
        assertTrue( (Boolean)Java.call( oneTwoB, "equals", oneTwoB ) ); 
        
        assertTrue( (Boolean)Java.call( twoOneA, "equals", twoOneB ) ); 
        
        assertFalse( (Boolean)Java.call( twoOneA, "equals", oneTwoB ) ); 
        
    }
    
    
    public void testCtorNone()
    {
        _constructor _ctor = _auto.constructor( _class.of( "A" ) );
        assertEquals( "A", _ctor.getName() );
        assertTrue( _ctor.getModifiers().contains( "public" ) );
        assertEquals(0, _ctor.getParameters().count());        
        
        _ctor = _auto.constructor( _class.of( "A" ).field( "public int count;") );
        assertEquals( "A", _ctor.getName() );
        assertTrue( _ctor.getModifiers().contains( "public" ) );
        assertEquals( 0, _ctor.getParameters().count());        
    }
    
    public void testCtorOne()
    {
        _constructor _ctor = 
            _auto.constructor( _class.of( "A" ).field( "public final int count;") );
        assertEquals( "A", _ctor.getName() );
        assertTrue( _ctor.getModifiers().contains( "public" ) );
        assertEquals(1, _ctor.getParameters().count());        
        
        _ctor = _auto.constructor( _class.of( "A" )
            .field( "public final int count;")
            .field( "public final String name = \"Eric\";" ) ); //has initializer     
        
        assertEquals( "A", _ctor.getName() );
        assertTrue( _ctor.getModifiers().contains( "public" ) );
        assertEquals(1, _ctor.getParameters().count());     
        assertEquals( "count", _ctor.getParameters().getAt( 0 ).getName() );
        assertEquals( "int", _ctor.getParameters().getAt( 0 ).getType() );
        System.out.println( _ctor.getBody() );
    }
    
    public void testCtorMore()
    {
        _constructor _ctor = 
            _auto.constructor( _class.of( "A" )
                .field( "public final int count;" )
                .field( "public final String name;" )
                .field( "public final Map<String,Integer> map;" )
            );
        
        assertEquals( 3, _ctor.getParameters().count() );
        assertEquals( "count", _ctor.getParameters().getAt(0).getName() );
        assertEquals( "int", _ctor.getParameters().getAt(0).getType() );
        
        assertEquals( "name", _ctor.getParameters().getAt(1).getName() );
        assertEquals( "String", _ctor.getParameters().getAt(1).getType() );
        
        assertEquals( "map", _ctor.getParameters().getAt(2).getName() );
        assertEquals( "Map<String,Integer>", _ctor.getParameters().getAt(2).getType() );
        
          
    }
    
    
}
