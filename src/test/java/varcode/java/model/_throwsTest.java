/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _throwsTest
    extends TestCase
{
    public void testThrowsNothing()
    {
        _throws t = _throws.of();
        assertEquals("", t.author() );
    }
    
    public void testThrows1()
    {
        _throws t = _throws.of( "A" );
        assertEquals( System.lineSeparator() + "    throws A", t.author() );
    }
    
    public void testThrows2()
    {
        _throws t = _throws.of( "A", "B" );
        assertEquals( System.lineSeparator() + "    throws A, B", t.author() );
    }
    
}
