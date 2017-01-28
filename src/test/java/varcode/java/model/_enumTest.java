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
import varcode.java.model._enum._constants._constant;
import varcode.java.model._fields._field;

/**
 *
 * @author Eric
 */
public class _enumTest
    extends TestCase
{
    
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
