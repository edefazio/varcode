/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._interface;
import varcode.java.model._nests;
import varcode.java.model._enum;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _nestsTest
    extends TestCase
{
    public void testAuthorManyNests()
    {
        _nests n = new _nests();
        n.add(             
            _interface.of( "I" ),
            _enum.of( "E" )
            );
        
        System.out.println( n );
    }
    
}
