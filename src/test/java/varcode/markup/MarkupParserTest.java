package varcode.markup;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import junit.framework.TestCase;

public class MarkupParserTest
    extends TestCase
{
    public static final String N = System.lineSeparator();
    
    /*
    public void testGetExcerpts()
    {
        String s = "package com.geico.ibu.sales.someproj;";
        List<String>excerpts = Text.Tokenize.getExcerpts( s, "package", ";" );
        assertTrue( excerpts.size() == 1 );
        String first = excerpts.get( 0 );
        //verify that I can get the thing
        assertEquals( " com.geico.ibu.sales.someproj", first );
        
    }
    
    public void testNoExceprtsOpenNoClose()
    {
        String s = "  sdfklasd;klfasd package";
        List<String>excerpts = Text.Tokenize.getExcerpts( s, "package", ";" );
        assertTrue( excerpts.size() == 0 );
    }
    
    public void testClosesNoOpens()
    {
        String s = " ;; ; ; ; ;;;   ;;; ;  ;";
        List<String>excerpts = Text.Tokenize.getExcerpts( s, "package", ";" );
        assertTrue( excerpts.size() == 0 );
    }
    
    public void testEmptyExcerpts()
    {
        String s = "()()()()()()()()()";
        List<String>excerpts = Text.Tokenize.getExcerpts( s, "(", ")" );
        assertTrue( excerpts.size() == 9 );
        assertEquals( "", excerpts.get( 0 ) );
    }
    
    public void testManyExcerpts()
    {
        String s = "(0)(1)(2)(3)(4)(5)(6)(7)(8)";
        List<String>excerpts = Text.Tokenize.getExcerpts( s, "(", ")" );
        assertTrue( excerpts.size() == 9 );
        assertEquals( "0", excerpts.get( 0 ) );
        assertEquals( "1", excerpts.get( 1 ) );
        assertEquals( "2", excerpts.get( 2 ) );
        assertEquals( "3", excerpts.get( 3 ) );
        assertEquals( "4", excerpts.get( 4 ) );
        assertEquals( "5", excerpts.get( 5 ) );
        assertEquals( "6", excerpts.get( 6 ) );
        assertEquals( "7", excerpts.get( 7 ) );
        assertEquals( "8", excerpts.get( 8 ) );
    }
    */
    public void testProperties()
    {
        String props = "a=1" + N + "b=2" + N + "c=3" + N + "d=\"Mack the knife\"";
        
        Properties p = new Properties();
        try
        {
            p.load( new ByteArrayInputStream( props.getBytes()) );
            //System.out.println( p );
            //System.out.println(  p.get( "a" ) );
        }
        catch( Exception e )
        {
            System.out.println ( e );
        }
    }
    /*
    public void testReplaceComment()
    {
        String commentOpen = "/+*"; 
        String commentClose = "*+/";
        
        assertTrue( Text.replaceComment( commentOpen ).equals( "/*" ) );
        assertTrue( Text.replaceComment( commentClose ).equals( "*"+"/" ) );
    }
    */
 
    
    public void testTokenizeNull()
    {
        String[] tokens = MarkupParser.Tokenize.byChar( null, ',' );
        assertTrue( tokens.length == 0 ); 
    }
    
    public void testTokenizeEmpty()
    {
        String[] tokens = MarkupParser.Tokenize.byChar( "", ',' );
        assertTrue( tokens.length == 0 );
    }
    
    public void testHardTokenize()
    {
        
    }
    public void testTokenizeNoSeparator()
    {
        String[] noTokens = MarkupParser.Tokenize.byChar( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", ',' );
        assertTrue( noTokens.length == 1 );
        assertEquals( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", noTokens[ 0 ] );
    }
    
    public void testTokenizeOnlySeparators()
    {
        String[] arr = MarkupParser.Tokenize.byChar( ",,,,,,", ',' );
        assertTrue( arr.length == 0 );        
    }
    
   
    /*
    public void testCountLinesToCharIndex()
    {
        assertTrue( 0 == Text.Lines.countToCharIndex( "", 1 ) );
        assertTrue( 1 == Text.Lines.countToCharIndex( " ", 1 ) );
        assertTrue( 1 == Text.Lines.countToCharIndex( N + N + " ", 1 ) );
        assertTrue( 2 == Text.Lines.countToCharIndex( N + N + " ", N.length() * 2 ) );
        assertTrue( 3 == Text.Lines.countToCharIndex( N + N + " ", N.length() * 2 + 1 ) );
    }
    */
    
    
}
