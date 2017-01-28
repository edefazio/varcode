/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._code;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _codeTest
    extends TestCase
{
    public void testCodeEqualsHashcode()
    {
        _code _c = new _code();
        _code _s = new _code();
        
        assertEquals( _c, _s );
        
        _s = _code.of("");
        _c = _code.of("");
        
        assertEquals( _c, _s );
        
        _s = _code.of(null);
        _c = _code.of(null);
        assertEquals( _c, _s );
    
        _s = _code.of( "System.out.println( \"Hi\" );", "return \"STUFF\";" );
        _c = _code.of( "System.out.println( \"Hi\" );", "return \"STUFF\";" );
        assertEquals( _c, _s );
    }
    
    
    public void testReplace()
    {
        _code _c = _code.of("//this is code");        
        assertEquals( "//this is code", _c.toString() );
        
        _c.replace( "code", "CODE" );
        assertEquals( "//this is CODE", _c.toString() );
        
        
        
    }
}
