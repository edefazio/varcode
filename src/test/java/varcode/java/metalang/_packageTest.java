/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.metalang;

import varcode.java.metalang._package;
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
    
    public void testBindIn()
    {
        _package p = _package.of( null );
        p.bind(VarContext.of() );
        
        _package pt = _package.of( "i.o.c" );
        assertEquals( "package i.o.c;", pt.toString().trim() );
        p.bind( VarContext.of() );
        assertEquals( "package i.o.c;", pt.toString().trim() );
        
        _package pa = _package.of( "i.o.{+a*+}" );
        
        assertEquals( "package i.o.{+a*+};", pa.toString().trim() );
        
        pa.bind( VarContext.of("a", "aaaa" ) );
        
        assertEquals( "package i.o.aaaa;", pa.toString().trim() );                
    }
    
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
        assertEquals( "", p.bind( VarContext.of() ).author( ) );
        
        p = _package.of( "my.pack" );
        String s = p.bind( VarContext.of( ) ).author( ).trim();
        
        assertEquals( "package my.pack;", s );
        
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
        p = _package.of( "start.{+part*+}.p");
        assertEquals( "package start.part.p;", p.bind( 
                VarContext.of("part", "part") ).author().trim() );
        
    }
   
}
