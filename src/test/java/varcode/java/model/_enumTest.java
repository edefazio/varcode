/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._annotationType;
import varcode.java.model._enum;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._enum._constants._constant;
import varcode.java.model._fields._field;

/**
 *
 * @author Eric
 */
public class _enumTest
    extends TestCase
{
    
    
    public void testEnumImplementsInterface()
    {
        _enum._signature _es = 
            _enum._signature.of( "public enum MyEnum implements MyInterface" );
        
        assertEquals( "MyInterface", 
            _es.getImplements().getAt( 0 ).toString() );
            
        _enum _e = _enum.of( "public enum MyEnum implements MyInterface" );
        
        
        assertEquals( "MyInterface", 
            _e.getSignature().getImplements().getAt( 0 ).toString() );
    }
    
    @interface Generated {}
    
    @Generated
    enum Rochambo
    {
        @Generated
        ROCK("theRock")
        {
            public int count = 100;
            
            @Override
            public String toString()
            {
                return count + "";
            }
        },
        PAPER("thePaper"),
        SCISSORS("theScissors");
        
        private final String name;
          
        private Rochambo( String name )
        {
            this.name = name;
        }        
    }
         
    public void testReadComplexEnum()
    {
        _enum _e = Java._enumFrom( Rochambo.class );
        assertNotNull( _e.getAnnotation( Generated.class ) );
        //System.out.println( "CONST \"" + _e.getConstants().get( "ROCK" ).getArguments().getAt( 0 ) + "\"" );
        
        System.out.println( _e );
        //_e.getConstants().get( "ROCK" )
        assertTrue( 
            _e.getConstants().get( "ROCK" ).getArguments().getAt( 0 ).toString().equals( "\"theRock\"") );
        
        assertNotNull( 
            _e.getConstants().get( "ROCK" ).getAnnotation( Generated.class ) );
    }

    public void testSimpleThingsSimple()
    {
        _enum _e = _enum.of( "E" );
        
        assertEquals( "E", _e.getName() );
        assertEquals( Modifier.PUBLIC, _e.getModifiers().getBits() );
    }
    
    public void testNest()
    {
        _enum _e = _enum.of("E")
            .nest(_annotationType.of( "@interface blah") );
        
        System.out.println( _e );
    }
    
    public void testMultiNest()
    {
        _enum _e = _enum.of("E")
            .nest(
                _annotationType.of( "@interface blah1"),
                _annotationType.of( "@interface blah2")
                );
        System.out.println( _e );
    }
    public void testConstantBody()
    {
        _enum _e = _enum.of( "public enum E" )
            .constant( 
                _constant.of( "Rock" )
                    .fields( "public int count = 100;" )
                    .method( "@Override", 
                        "public String toString()", 
                        "return \"\" + count;" ) 
                );
        
        System.out.println( _e );
    }
    
}
