/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._interface;
import varcode.java.model._annotationType;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _interfaceTest
    extends TestCase
{
    public void testSimpleThingsSimple()
    {   
        _interface _i = _interface.of( "I" );
        assertEquals( "I", _i.getName() );
    }
    
    public void testMultiNest()
    {
        _interface _i = _interface.of("I")
            .nest(
                _annotationType.of( "@interface blah1"),
                _annotationType.of( "@interface blah2")
                );
        System.out.println( _i );
    }
    
}
