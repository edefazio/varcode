/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.model._constructors._constructor;

/**
 *
 * @author Eric
 */
public class _constructorTest
    extends TestCase
{
    public void testCtor()
    {
        _constructor _ctor = 
            _constructor.of( "public A()" ).addParameter( "String", "a" );
        
        System.out.println( _ctor );
        
        //assertEquals( 1, _ctor.getParameters().count() );
        //_ctor.addParameter( _parameter.of( this, this ) );        
    }
    public void testCtorBody()
    {
        _constructor _ctor = 
            _constructor.of( "public A()", "System.out.println( \"Hi\");");
        
        System.out.println( _ctor );
        
    }
}
