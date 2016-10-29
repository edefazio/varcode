/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._modifiers;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _modifiersTest
    extends TestCase
{
    public void testAllModifiers()
    {
        _modifiers m = _modifiers.of(
           "public",
		   "static",
		   "synchronized",
		   "native",
		   "transient",
		   "volatile",
		   "strictfp",
		   "default");     
        
        assertTrue( m.containsAll(
            Modifier.PUBLIC, 
            Modifier.STATIC, 
            Modifier.SYNCHRONIZED, 
            Modifier.NATIVE, 
            Modifier.TRANSIENT, 
            Modifier.VOLATILE, 
            Modifier.STRICT ) ); 
            
        assertTrue( m.containsAll(
            Modifier.PUBLIC, 
            Modifier.STATIC, 
            Modifier.SYNCHRONIZED, 
            Modifier.NATIVE, 
            Modifier.TRANSIENT, 
            Modifier.VOLATILE, 
            Modifier.STRICT, 
            _modifiers._mod.INTERFACE_DEFAULT.getBitValue() ) );
        
        
        m = _modifiers.of(
           "public",
		   "abstract",
		   "transient",
		   "volatile",
		   "strictfp",
		   "default");  
        
        assertTrue( m.containsAll(
            Modifier.PUBLIC, 
            Modifier.ABSTRACT, 
            Modifier.TRANSIENT, 
            Modifier.VOLATILE, 
            Modifier.STRICT, 
            _modifiers._mod.INTERFACE_DEFAULT.getBitValue() ) );
                
        m = _modifiers.of(
           "protected",
		   "static",
           "final",
		   "synchronized",
		   "native",
		   "transient",
		   "volatile",
		   "strictfp",
		   "default");     
        
        m = _modifiers.of(
           "protected",
		   "native",
		   "transient",
		   "volatile",
		   "strictfp",
		   "default");  
           
        m = _modifiers.of(
		   "private",
		   "static",
		   "synchronized",
		   "native",
		   "transient",
		   "volatile",
		   "strictfp",
		   "default");
        
        m = _modifiers.of(
		   "private",
		   "abstract", 
		   "transient",
		   "volatile",
		   "strictfp",
		   "default"); 
    }
    public void testEmpty()
    {
        _modifiers m = new _modifiers();
        assertEquals( "", m.author( ) );
        assertEquals( "", m.bind( VarContext.of() ).author() );
        assertFalse( m.contains( Modifier.ABSTRACT ) );
        assertFalse( m.containsAny( "public" ) );
        assertFalse( m.containsAll( "public", "static" ) );
        assertEquals( 0, m.getBits() );
        assertEquals( 0, m.count() );
        assertTrue( m.isEmpty() );        
        m.validate( 0 );
    }
    
    public void testOne()
    {
        _modifiers m = new _modifiers();
        m.set( "public" );
        assertEquals(1, m.count() );
        assertFalse( m.isEmpty() );
        
        assertTrue( m.contains( "public" ) );
        assertFalse( m.contains( "blah" ) );
        assertFalse( m.contains( "static" ) );
        
        assertTrue( m.containsAny( "public" ) );
        assertTrue( m.contains( Modifier.PUBLIC ) );
        assertTrue( m.containsAll( Modifier.PUBLIC ) );
        assertTrue( m.containsAny( Modifier.PUBLIC ) );
        
        assertFalse( m.containsAll( Modifier.STATIC ) );
        assertFalse( m.containsAny( Modifier.FINAL ) );
        assertTrue( m.containsAny( "protected", "public" ) );
        assertTrue( m.containsAll( "public" ) );

        assertEquals( "public ",  m.author( ) );
        assertEquals( "public ", m.bind( VarContext.of() ).author() );
    }
    
    public void testCombo()
    {
        
    }
}
