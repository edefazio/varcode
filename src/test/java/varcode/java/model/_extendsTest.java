/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._extends;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _extendsTest
    extends TestCase
{
    public void testExtNone()
    {
        _extends _e = _extends.of();
        assertEquals( "", _e.author() );
    }
    
    public void testExtendOne()
    {
        _extends _e = _extends.of( "A" );
        assertEquals( _e.author(), System.lineSeparator()+"    extends A" );
    }
    
}
