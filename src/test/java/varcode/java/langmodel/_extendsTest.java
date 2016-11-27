/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.langmodel;

import varcode.java.langmodel._extends;
import java.util.Map;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _extendsTest
    extends TestCase
{
    public void testExtendsAddBind()
    {
        _extends e = _extends.of( );
        assertEquals( "", e.toString() );
        
        e = _extends.of( "{+Yada+}" );        
        
        
        assertEquals("{+Yada+}", e.getAt( 0 ) );
        
        assertEquals( 1, e.count() );
        
        assertEquals(
            "extends A",
            e.bind( VarContext.of("Yada", "A") ).author().trim() );    
        
        e = _extends.of( "{+Yada+}" );        
        e.addExtends( Map.class );
        
        assertEquals(
            "extends A, java.util.Map",
            e.bind( VarContext.of("Yada", "A") ).author().trim() );    
        
    }
    
    public void testSimple()
	{
		_extends exs = new _extends();
		assertEquals( "", exs.toString() );
		
		exs = _extends.of( "Serializable" );
		assertEquals( 
			System.lineSeparator() + "    extends Serializable", 
			exs.toString() );
		
		exs = _extends.of( "Serializable", "Externalizable" );
		
		assertEquals( 
			System.lineSeparator() + "    extends Serializable, Externalizable", 
			exs.toString() );
		
	}
}
