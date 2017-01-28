package varcode.java.model;

import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _modifiersTest
    extends TestCase
{
    
    public void testOfDefault()
    {
        _modifiers _mods = _modifiers.of( "default" );
        
        assertEquals( "default ", _mods.toString() );        
    }
    
    public void testSetDefault()
    {
        _modifiers _mods = new _modifiers();
        
        
        _mods.set( "default" );
        assertEquals( "default ", _mods.toString() );        
        
        _mods.set( "public" );        
        assertEquals( "public default ", _mods.toString() );        
        
    }
    
    
    
}
