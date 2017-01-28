/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.markup;

import varcode.markup.FillInTheBlanks;
import junit.framework.TestCase;
import varcode.markup.FillInTheBlanks.BlankBinding;

/**
 *
 * @author Eric
 */
public class FillInTheBlanksBindingTest
     extends TestCase
{
    public void testIt()
    {
        BlankBinding fitb = FillInTheBlanks.of();
        assertEquals("", fitb.bind(  ) );
        
        fitb = FillInTheBlanks.of("abcdefg");
        assertEquals( "abcdefg", fitb.bind( ) );        
        
        //the Blank is Inferred BETWEEN the two static strings
        fitb = FillInTheBlanks.of("abcdefg", "hijklmnop");
        
        //null into the blank is an empty string
        assertEquals( "abcdefghijklmnop", fitb.bind( null ) );
        
        // insert something into the blank
        assertEquals( "abcdefg***hijklmnop", fitb.bind( "***" ) );        
        
        fitb = FillInTheBlanks.of( "a", null, "b" );
        assertEquals( "ab", fitb.bind( null ) );     
        
        // (3) blanks spelled out by nulls
        // putting null in the first position means prefix null
        fitb = FillInTheBlanks.of( null, "a", null, "b", null );
        assertEquals( "1a2b3", fitb.bind( 1,2,3 ) );
        
        //Only nulls (1) null
        fitb = FillInTheBlanks.of(  null );
        assertEquals("1" , fitb.bind( 1 ) );
        
        // ONly blanks (3) blanks
        fitb = FillInTheBlanks.of(  null, null, null );
        assertEquals( "123" , fitb.bind( 1,2, 3 ) );
        
    }
}
