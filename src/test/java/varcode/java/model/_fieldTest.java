/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.model._fields._field;

/**
 *
 * @author Eric
 */
public class _fieldTest
    extends TestCase
{
    public void testFieldInit()
    {
        _field _f = _field.of("public int count = 100;");
        _field _f2 = _field.of("public int count =100;");
        
        assertEquals( _f, _f2 );
    }
}
