/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.macro;

import varcode.java.macro._auto;
import varcode.java.macro._autoBuilder;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Publisher;
import varcode.java.model._class;

import static varcode.java.macro._auto.*;
/**
 *
 * @author Eric
 */
public class _autoBuilderTest
    extends TestCase
{
    public void testNoFields()
    {
        
        //_class _t2 = macro( CONSTRUCTOR, GETTERS )
        //    .to( _class.of( "Immutable") );
        
        _class _t2 = _class.of( "Immutable" );
        _auto.builderTo( _t2 );
        
        //Since there are NO fields, we dont actually add the builder
        assertEquals( 0, _t2.getMethods().count() );
        assertEquals( 0, _t2.getNests().count() );
        assertEquals( 0, _t2.getNests().count() );
        
        System.out.println( _t2 );
    }
    
    public void testOneConstructor()
    {
        _class _immut = 
            _class.of( "package adhoc;", "Immutable" )
                .field( "private final int count;" );
        
        _auto.to( _immut ).apply( CONSTRUCTOR, BUILDER, EQUALS );
        //_auto.to( _immut ).macro( CONSTRUCTOR, GETTERS, BUILDER, EQUALS, HASHCODE );
        
        
        System.out.println( _immut );
        //assertNotNull( _immut.getMethod( "getCount" ) );
        //assertNotNull( _immut.getConstructor( 0 ) );
        assertNotNull( _immut.getNest( "ImmutableBuilder" ) );
        
        //to create an instance by calling the constructor (NOT the builder);
        Object instance = _immut.instance( 102 );
        assertNotNull( instance );
        
        
        //now 
        Object theBuilder = Java.call( instance, "builder" );        
        
        Object theModBuilder = Java.call( theBuilder, "count", 102 );
        assertEquals( theBuilder, theModBuilder );
        
        Object theBuiltInstance = Java.call( theBuilder, "build" );
        
        //verify that the instance built with the builder is equal to the one created via the
        // constructor
        assertTrue( (Boolean)Java.call(theBuiltInstance, "equals", instance ) );
    }
    
    
    public static void main( String[] args ) 
        throws ClassNotFoundException
    {
        _class _t = _class.of( "package ex;", "ImmutableTarget" )
            .field( "final int a;" )
            .field( "final String b;" )
            .field( "final String[] names;" );
        _auto.constructorTo( _t );
        _auto.gettersTo( _t );
        
        
        _autoBuilder.to( _t );
        
        System.out.println( _t ); 
        
        AdHocClassLoader adHocCL = AdHoc.compile( _t );
        //Class immutableTarget = _t.loadClass();
        
        Publisher.publishToParent( adHocCL );
        
        //Class c = adHocCL.findClass(_t.getQualifiedName() );
        Package.getPackage( _t.getPackageName() );
        
        Class c = Class.forName( _t.getQualifiedName() );
        Object builderInstance = 
            Java.call( c, "getBuilder" );
        
        /*
        Snippet s = Snippet.of( _imports.of( 
            _t.getQualifiedName(), 
            _t.getQualifiedName()+"$ImmutableTargetBuilder" ), 
            "( ImmutableTargetBuilder builder, int a, String b, String[] names)"
          + "{ builder.a(a).b(b).names(names).build(); }" );
        
        Object target1 = s.call( builderInstance, 1100, "eric", new String[]{"a", "b"} );
        System.out.println( target1.getClass().getSimpleName() ) ;
        */
        
        Java.call( builderInstance, "a", 1100 );
        Java.call( builderInstance, "b", "eric" );
        Java.call( builderInstance, "names", (Object)new String[]{"a", "b"} );
        
        //call the "build" method on the builder to return the target instance
        Object target2 = Java.call( builderInstance, "build" );
        
        System.out.println( target2.getClass().getSimpleName() ) ;
        
    }
}
