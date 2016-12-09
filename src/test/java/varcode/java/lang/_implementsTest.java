/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.lang;

import varcode.java.lang._implements;
import java.io.Serializable;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _implementsTest
    extends TestCase
{
    
    public void testImplsEmpty()
    {
        _implements imps = new _implements();
        assertEquals( "", imps.toString() );
        
        assertEquals( 0, imps.count() );
        
        try
        {
            imps.getAt( 1 );
            fail("expected VarException for trying to acccess implements at bad index");
        }
        catch( VarException ve )
        {
            //expected 
        }
    }
    
    public void testImplsAdd()
    {
        _implements impls = _implements.of( "MyInterface" );
        assertEquals( 1, impls.count() ); 
        assertEquals( "MyInterface", impls.getAt( 0 ) ); 
        assertEquals( "implements MyInterface", impls.toString().trim() );
        
        impls.implement( Serializable.class );
        
        assertEquals( "implements MyInterface, java.io.Serializable", 
            impls.toString().trim() );
        
    }
    
    public void testImplsParam()
    {
        _implements impls = _implements.of( "{+inter*+}" );
        assertEquals( "implements MahInter", 
            impls.bind( VarContext.of( "inter", "MahInter" ) ).author().trim()  );
    }
    
}
