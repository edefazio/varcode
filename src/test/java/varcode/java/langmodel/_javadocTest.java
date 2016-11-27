/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.langmodel;

import varcode.java.langmodel._javadoc;
import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _javadocTest
     extends TestCase
{
    
    static String N = "\r\n";
    
    public void testBindIn()
    {
        _javadoc j = _javadoc.of( "A comment {+name+}" );
        j.bind( VarContext.of( "name", "Eric" ) );
        System.out.println( j );
        assertEquals(
        "/**" + N + 
        " * A comment Eric" + N + 
        " */", j.toString().trim() );
    }
    
    public void testJavadoc()
    {
        _javadoc j = _javadoc.of();
        assertEquals("", j.toString());
        
        assertEquals( "", j.getComment());
        assertTrue( j.isEmpty() );
        
        assertEquals("", j.replace("A", "Z").toString() );
    }
    
    public void testAppendReplace()
    {
        _javadoc j = _javadoc.of("A");
        assertEquals("/**" + System.lineSeparator()+
                " * A" + System.lineSeparator()+ 
                " */", j.toString().trim() );
        j.append( "B");
        
        System.out.println( j.toString() );
        
        assertEquals(
            "/**" + System.lineSeparator()+
            " * A" + System.lineSeparator()+
            " * B" + System.lineSeparator()+
            " */", j.toString().trim() );
        
        j.replace("B", "C");
        
        assertEquals(
            "/**" + System.lineSeparator()+
            " * A" + System.lineSeparator()+
            " * C" + System.lineSeparator()+
            " */", j.toString().trim() );
    }
    
    public void testBind()
    {
        _javadoc j = _javadoc.of("this is a comment with {+mark+}");
        String res = j.bind( VarContext.of( "mark", "TheMark" ) ).author( );
        assertEquals(  
            "/**" + System.lineSeparator()+
            " * this is a comment with TheMark" + System.lineSeparator()+
            " */", res.trim() );        
        
    }
}
