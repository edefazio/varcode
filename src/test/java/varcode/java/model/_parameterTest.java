/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.model._parameters._parameter;

/**
 *
 * @author M. Eric DeFazio
 */
public class _parameterTest
    extends TestCase
{
    public void testConstruct()
    {
        _parameter _p = _parameter.of( "String", "name" );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( String.class, "name" );
        assertEquals( "java.lang.String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( "@ann", "String", "name" );
        assertEquals( "@ann ", _p.getAnnotations().author() );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( "@ann1", "@ann2", "String", "name" );
        assertEquals( "@ann1 @ann2 ", _p.getAnnotations().author() );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( _ann.of( "@ann"), "String", "name" );
        assertEquals( "@ann ", _p.getAnnotations().author() );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( _ann.of( "@ann1"), 
            _ann.of("@ann2"), "String", "name" );
        assertEquals( "@ann1 @ann2 ", _p.getAnnotations().author() );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( "final", "String", "name" );
        assertEquals( "", _p.getAnnotations().author() );
        assertEquals( true, _p.isFinal() );
        assertEquals( "String", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( "final", "String...", "name" );
        assertEquals( "", _p.getAnnotations().author() );
        assertEquals( true, _p.isFinal() );
        assertEquals( true, _p.isVararg());
        assertEquals( "String...", _p.getType() );
        assertEquals( "name", _p.getName() );
        
        _p = _parameter.of( "@ann1", "@ann2", "final", "Map<Integer,String>...", "countToName" );
        assertEquals( "@ann1 @ann2 ", _p.getAnnotations().author() );
        assertEquals( true, _p.isFinal() );
        assertEquals( true, _p.isVararg());
        assertEquals( "Map<Integer,String>...", _p.getType() );
        assertEquals( "countToName", _p.getName() );
                
    }
}
