/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.model._fields._init;

/**
 *
 * @author Eric
 */
public class _initTest
    extends TestCase
{
    public void testInit()
    {
        _init _a = _init.of( null );
        _init _b = _init.of( null );
        
        assertEquals( _a, _b );
        
        _a = new _init( "=100;");
        _b = new _init( " =100;");
        
        assertEquals( _a, _b);
        
        _a = new _init( " = 100;");
        _b = new _init( " =100;");
        
        assertEquals( _a, _b);
    }
    
}
