/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._anonClass;
import varcode.java.model._literal;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class _anonClassTest
    extends TestCase
{
    
    public void testSimple()
    {
        new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println( "Hi" );
            }
            
        };
        _anonClass ac = new _anonClass( Runnable.class )
            .method( 
                "public void run()",
                "System.out.println( \"Hi\");" );
        
        System.out.println( ac );
    }
    
    public void  testExample()
    {
        _anonClass _ac = new _anonClass( "Comparator<String>" )
            .method( 
                "@Override",
                "public int compare(String a, String b)",
                "return a.length() - b.length();" );
        System.out.println( _ac );        
    }
    
    public void testWithFieldsAndParams()
    {
        _anonClass _ac = _anonClass.of( "AbsClass<String>")
            .args( _literal.of("A"), 1 )
            .fields( "protected String a;",
                "protected int b;" )
            .method( "public void doIt()",
                "System.out.println(\"Hello\");" );
        
        assertEquals( 2, _ac.getArgs().count() );
        assertEquals( 2, _ac.getFields().count() );
        assertEquals( 1, _ac.getMethods().count() );
        assertEquals( "AbsClass<String>", _ac.getImplType() );
        
        System.out.println( _ac );
    }
}
