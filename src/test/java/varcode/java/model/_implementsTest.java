/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._implements;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _implementsTest
    extends TestCase
{
    public void testNone()
    {
        _implements _impls= new _implements(); 
        assertEquals( "", _impls.author() );
    }
}
