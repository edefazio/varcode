/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this toState file, choose Tools | Templates
 * and open the toState in the editor.
 */
package varcode.author.lib;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import varcode.author.Author;

/**
 *
 * @author Eric
 */
public class ScriptTest
    extends TestCase
{
    public void testScripts()
    {
        //all caps
        assertEquals("ALL CAPS", 
            Author.toString( "{+$^^(a)+}", "a", "all caps" ) );
        
        //count
        assertEquals("", 
            Author.toString( "{+$#(a)+}" ) );
        assertEquals("1", 
            Author.toString( "{+$#(a)+}", "a", "1" ) );
        assertEquals("0", 
            Author.toString( "{+$#(a)+}", "a", new int[ 0 ] ) );
        
        assertEquals("3", 
            Author.toString( "{+$#(a)+}", "a", new int[] {1,2,3} ) );
        
        //count index
        assertEquals("", 
            Author.toString( "{+$[#](a)+}" ) );
        assertEquals("0", Author.toString( "{+$[#](a)+}", "a", "1" ) );
        assertEquals("", Author.toString( "{+$[#](a)+}", "a", new int[ 0 ] ) );
        
        assertEquals("0, 1, 2", Author.toString( "{+$[#](a)+}", "a", new int[] {1,2,3} ) );
        
        //Escape String
        assertEquals("",
            Author.toString( "{+$escapeString(a)+}") );
        
        assertEquals("1",
            Author.toString( "{+$escapeString(a)+}", "a", 1) );
        
        assertEquals("\"Hey'a\"",
            Author.toString( "{+$escapeString(a)+}", "a", "Hey'a") );
        
        //FirstCap
        assertEquals("Firstcap", Author.toString( "{+$^(a)+}", "a", "firstcap" ) );
        
        //firstLower
        assertEquals("fIRSTLOWER", Author.toString( "{+$firstLower(a)+}", "a", "FIRSTLOWER" ) );
            
        //lowercase
        assertEquals("lowercase", Author.toString( "{+$lower(a)+}", "a", "LOWERCASE" ) );
        
        
        
        
    }
}
