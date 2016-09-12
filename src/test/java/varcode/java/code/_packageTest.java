/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _packageTest
     extends TestCase
{
    public void testPackageNull()
    {
        _package p = _package.of( null );
        assertEquals( "", p.author( ) );
    }
    
    public void testReplace()
    {
        
    }
    public void testPackageEmpty()
    {
        _package p = _package.of( "" );
        assertEquals( "", p.author( ) );
    }
    
    public void testLazyBind()
    {
        _package p = _package.of( "" );
        assertEquals( "", p.bind( VarContext.of() ) );
        
        p = _package.of( "my.pack" );
        System.out.println (p.bind (VarContext.of( )) );
        
        assertEquals( "package my.pack;", p.bind( VarContext.of() ).trim() );
        
        p = _package.of( "start.{+part*+}.p");
        
        assertEquals( "package start.{+part*+}.p;", p.author( ).trim() );
        
        try
        {
            p.bind( VarContext.of( ) );
            fail("expected Exception for unbound required var {+part*+}");
        }
        catch( VarException ve )
        {
            //expwetced
        }
        assertEquals( "package start.part.p;", p.bind( 
                VarContext.of("part", "part") ).trim() );
        
    }
   
}
