/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perf.author;

import junit.framework.TestCase;
import varcode.author.Author;
import varcode.author.AuthorState;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class AuthorSimple
    extends TestCase
{
    public static final int COUNT = 100000; //100000 took 1289 ms
    
    public void testProfile()
    {
        main(new String[ 0 ] );
    }
    
    public static void main( String[] args )
    {
        Template t = BindML.compile("public class {+className+}{}");
        AuthorState as = null;
        
        long start = System.currentTimeMillis();
        for( int i = 0; i < COUNT; i++ )
        {
            as = Author.toState( t, "className", "MyClass" + i );
        }
        long end = System.currentTimeMillis();
        
        System.out.println( "TOOK "+ (end- start) );
        
    }
}
