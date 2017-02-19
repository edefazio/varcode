/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class _methodTest
    extends TestCase
{
    public void testMethodThrowsMultiple()
    {
        _method _m = _method.of("public String doX( int i) throws A" );
        
        assertEquals( 1, _m.getThrows().count() );
        assertEquals("A", _m.getThrows().getAt( 0 ) );
        
        System.out.println( _m );
        
        _m = _method.of("public String doX( int i) throws A, B" );
        
        assertEquals( 2, _m.getThrows().count() );
        assertEquals("A", _m.getThrows().getAt( 0 ) );
        assertEquals("B", _m.getThrows().getAt( 1 ) );
    }
    
    public void testMethodSignature()
    {
        _method._signature _s = _method._signature.of( "public String doX( int i) throws A" );
        
    }
    public class MHolder{
        public String getStuff( int count )
        {
            System.out.println( "Hi" );
            return "STUFF";
        }
    }
    
    public void testM()
    {
        _method _m = _method.of( //"@Deprecated", 
            "public String getStuff( int count )",
            "System.out.println( \"Hi\" );",
            "return \"STUFF\";");
        
        
        _method _mh = Java._classFrom( MHolder.class )
            .getMethodsNamed( "getStuff" ).get( 0 );
        
        _m.getBody().equals( _mh.getBody() );
        
        assertEquals( _m.getBody(), _mh.getBody() );
        
        System.out.println( _mh.hashCode() );
        System.out.println( _m.hashCode() );
        System.out.println( _mh.equals( _m ) );
        assertTrue( _mh.equals( _m ) );
        assertEquals( _mh, _m );
        
        
        _method _m2 = _method.of( //"@Deprecated", 
            "public String getStuff( int count )",
            "System.out.println( \"Hi\" );",
            "return \"STUFF\";");
         
        assertEquals( _m, _m2 );
        
        _m2 = _method.of( //"@Deprecated", 
            "public String getStuff( int count )")
            .body( "System.out.println( \"Hi\" );");
        _m2.add( "return \"STUFF\";");
        
        assertEquals( _m, _m2 );
    }
}
