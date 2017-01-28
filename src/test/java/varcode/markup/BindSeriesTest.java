/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.markup;

import junit.framework.TestCase;
import varcode.context.VarBindException;

/**
 *
 * @author Eric
 */
public class BindSeriesTest
    extends TestCase
{
    public void testNothing()
    {
        //I'm binding nothing
        BindSeries svb = BindSeries.of( );
        try
        {
            svb.resolve( "blah" );
            fail("expected Exception");
        }
        catch(VarBindException e )
        {
            //expected
        }        
    }
    public void testOneCap()
    {
        BindSeries svb = BindSeries.of( "aname" );
        assertEquals( "aname", svb.resolve( "varone" ) );
        assertEquals( "aname", svb.resolve( "varone" ) );
        assertEquals( "aname", svb.resolve( "varone" ) );
        
        assertEquals( "Aname", svb.resolve( "Varone" ) );
        assertEquals( "ANAME", svb.resolve( "VARONE" ) );
        
        try
        {
            svb.resolve( "fail" );
            fail("expected VarBindException");
        }
        catch( VarBindException e )
        {
            //expected
        }        
    }
    
    public void testMulti()
    {
        BindSeries svb = new BindSeries( "aa", "bb", "cc" );
        
        
        assertEquals("aa", svb.resolve( "first" ) );
        assertEquals("Aa", svb.resolve( "First" ) );
        assertEquals("AA", svb.resolve( "FIRST" ) );
        
        assertEquals("Bb", svb.resolve( "Second" ) );
        assertEquals("bb", svb.resolve( "second" ) );
        
        assertEquals("BB", svb.resolve( "SECOND" ) );
        
        assertEquals("cc", svb.resolve( "third" ) );
        assertEquals("Cc", svb.resolve( "Third" ) );
        assertEquals("CC", svb.resolve( "THIRD" ) );
        
        assertEquals("aa", svb.resolve( "first" ) );
        assertEquals("Aa", svb.resolve( "First" ) );
        assertEquals("AA", svb.resolve( "FIRST" ) );
        
        assertEquals("bb", svb.resolve( "second" ) );
        assertEquals("Bb", svb.resolve( "Second" ) );
        assertEquals("BB", svb.resolve( "SECOND" ) );                
    }    
}
