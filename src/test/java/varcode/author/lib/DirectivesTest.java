/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this toState file, choose Tools | Templates
 * and open the toState in the editor.
 */
package varcode.author.lib;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.author.Author;

/**
 *
 * @author Eric
 */
public class DirectivesTest
    extends TestCase
{
    public static final String N = System.lineSeparator();
    
    public void testDirs()
    {
        assertEquals("",  
            Author.toString( 
                N+N+N+N+"{$$condenseMultipleBlankLines$$}"+N+N+N+N ) );
        
        assertEquals("a" + N,  
            Author.toString( 
                "a"+N+N+N+N+"{$$condenseMultipleBlankLines$$}"+N+N+N+N ) );

        assertEquals("Z", Author.toString( "{+a+}", 
            VarContext.of( "a", 1 ), 
            new PostReplace("1", "Z") ) );
        
        assertEquals("ZZZ", Author.toString( "1{+a+}1", 
            VarContext.of( "a", 1 ),  
            new PostReplace("1", "Z") ) );    
        
        System.out.println( Author.toString( 
            "A"+ N + "B" + N, 
            VarContext.of(), 
            PrefixWithLineNumber.INSTANCE ) );
        
        assertEquals("a 1" + N + "c 3",
            Author.toString( "a 1" + N + "b 2" + N + "c 3", VarContext.of( ), 
                new RemoveAllLinesWith( "b" ) ) );
        
        assertEquals("a 1" + N + "c 3",
            Author.toString( "a 1" + N + "b 2" + N + "c 3", VarContext.of( ), 
                new RemoveAllLinesWith( "2" ) ) );
    }
}
