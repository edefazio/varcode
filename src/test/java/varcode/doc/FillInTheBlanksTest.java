package varcode.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import varcode.doc.translate.TranslateBuffer;
import varcode.doc.FillInTheBlanks;
import varcode.doc.FillInTheBlanks.Builder;
import varcode.doc.FillInTheBlanks.FillTemplate;

public class FillInTheBlanksTest
    extends TestCase
{
	private static final Logger LOG = 
	    LoggerFactory.getLogger( FillInTheBlanksTest.class );
	
	
    public static String N = System.lineSeparator();

    public void testMerge()
    {
    	FillTemplate f1 = FillInTheBlanks.of( "1", "3", "5" );
    	FillTemplate f2 = FillInTheBlanks.of( "6", "8", "10" );
    	
    	assertEquals( "12345", f1.fill( 2, 4 ) );
    	assertEquals( "678910", f2.fill( 7,9 ) );
    	
    	FillTemplate f3 = FillInTheBlanks.merge( f1, f2 );
    	
    	assertEquals( f3.getBlanksCount(), f1.getBlanksCount() + f2.getBlanksCount() );    	
    	assertEquals( f3.getStaticText(), f1.getStaticText() + f2.getStaticText() );
    	
    	assertEquals( "12345678910", f3.fill( 2, 4, 7, 9 ) );
    }
    
    public void testMergeBlanksAtEdges()
    {
    	FillTemplate f1 = FillInTheBlanks.of( null, "2", "4", null );
    	FillTemplate f2 = FillInTheBlanks.of( null, "7", "9", null );
    	
    	assertEquals( "12345", f1.fill( 1, 3, 5 ) );
    	assertEquals( "678910", f2.fill( 6, 8, 10 ) );
    	
    	FillTemplate f3 = FillInTheBlanks.merge(f1,f2);
    	
    	assertEquals( f3.getBlanksCount(), f1.getBlanksCount() + f2.getBlanksCount() );    	
    	assertEquals( f3.getStaticText(), f1.getStaticText() + f2.getStaticText() );
    	assertEquals( "12345678910", f3.fill( 1, 3, 5, 6, 8, 10 ) );
    	
    }
    public void testOfBetweenStrings()
    {
    	FillTemplate ft = FillInTheBlanks.of( "A", "C", "E", "G" );
    	assertEquals( ft.getStaticText(), "ACEG" );
    	assertEquals( "ABCDEFG", ft.fill( "B", "D", "F" ) );
    	assertEquals( 3, ft.getBlanks().cardinality() );
    	
    	assertTrue( ft.getBlanks().get( 1 ) );
    	assertTrue( ft.getBlanks().get( 3 ) );
    	assertTrue( ft.getBlanks().get( 5 ) );
    	
    	assertEquals( 3, ft.getBlanksCount() );
    	
    	assertEquals( ft.getTextAfterBlank( 0 ), "C" );
    	assertEquals( ft.getTextAfterBlank( 1 ), "E" );
    	assertEquals( ft.getTextAfterBlank( 2 ), "G" );
    	
    	assertEquals( ft.getTextBeforeBlank( 0 ), "A" );
    	assertEquals( ft.getTextBeforeBlank( 1 ), "C" );
    	assertEquals( ft.getTextBeforeBlank( 2 ), "E" );    	
    }
    
    public void testOfDoubleBlanks()
    {
    	//to create multiple (N) blanks in a row, add (N) nulls in a row
    	FillTemplate ft = FillInTheBlanks.of( "A", null, null, "D", "F" );
    	assertEquals( ft.getStaticText(), "ADF" );
    	assertEquals( "ABCDEF", ft.fill( "B", "C", "E" ) );
    	assertEquals( 3, ft.getBlanks().cardinality() );
    	
    	assertTrue( ft.getBlanks().get( 1 ) );
    	assertTrue( ft.getBlanks().get( 2 ) );
    	assertTrue( ft.getBlanks().get( 4 ) );
    	
    	assertEquals( 3, ft.getBlanksCount() );
    	
    	assertEquals( ft.getTextAfterBlank( 0 ), "" );
    	assertEquals( ft.getTextAfterBlank( 1 ), "D" );
    	assertEquals( ft.getTextAfterBlank( 2 ), "F" );
    	
    	assertEquals( ft.getTextBeforeBlank( 0 ), "A" );
    	assertEquals( ft.getTextBeforeBlank( 1 ), "" );
    	assertEquals( ft.getTextBeforeBlank( 2 ), "D" );    	
    }
    
    public void testOfAllBlanks()
    {
    	FillTemplate ft = FillInTheBlanks.of( null,null,null );
    	assertEquals( 3, ft.getBlanksCount() );
    	assertEquals( "", ft.getStaticText() );
    	assertEquals("ABC", ft.fill("A", "B", "C" ) );
    	
    	assertTrue( ft.getBlanks().get( 0 ) );
    	assertTrue( ft.getBlanks().get( 1 ) );
    	assertTrue( ft.getBlanks().get( 2 ) );    	
    }
    
    public void testOfPrefixBlanks()
    {
    	FillTemplate ft = FillInTheBlanks.of( null, null, "345", null );
    	assertEquals( "123456", ft.fill( 1,2,6 ) );
    	
    	ft = FillInTheBlanks.of( null, null, "3", "5", null );
    	assertEquals( "123456", ft.fill( 1,2,4,6 ) );    	
    }
    
    public void testOfTrailingBlanks()
    {
    	FillTemplate ft = FillInTheBlanks.of( "12", null, null, null );
    	assertEquals( "12345", ft.fill( 3,4,5 ) );
    	    	
    }
    
    /**
     * Verify that: 
     * <UL>
     * <LI>comment "open tags" "/+*" are converted to "/*"
     * <LI>comment close tags "*+/" are converted to "* /"
     * </UL> 
     */
    public void testReplaceComment()
    {
        String comment = "/+**+/";
        FillTemplate replaceComment = 
            new FillInTheBlanks.Builder().text( comment ).compile();
        assertTrue( replaceComment.getBlanksCount() == 0 );
        //assertTrue( replaceComment.getStaticText().equals( "/**/" ) );

        replaceComment.fill( 
            new TranslateBuffer(), new Object[ 0 ]);
        
        comment = "/+*/+**+/*+/";
        replaceComment = 
            new FillInTheBlanks.Builder().text( comment ).compile();
        assertTrue( replaceComment.getBlanksCount() == 0 );
        //assertTrue( replaceComment.getStaticText().equals( "/*/**/*/" ) );
        
        comment = "/+** javadoc comment *+/";
        FillTemplate replaceJdocComment = 
            new FillInTheBlanks.Builder().text( comment ).compile();
        assertTrue( replaceJdocComment.getBlanksCount() == 0 );
        //assertTrue( replaceJdocComment.getStaticText().equals( "/** javadoc comment */" ) );
    }
    
    public void testNoBlanks()
    {
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        
        
        FillTemplate noBlanks = 
            new FillInTheBlanks.Builder().text( alpha ).compile();
        
        assertTrue( noBlanks.getBlanksCount() == 0 );
        
        assertTrue( noBlanks.getStaticText().equals( alpha ) );                
    }
    
    public void testNoBlanksBuilder()
    {
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        Builder b = new Builder();
        b.text( alpha );
        FillTemplate fo = b.compile();
        assertTrue( fo.getStaticText().equals( alpha ) );
    }
    
    public void testGetTextAfterBlanks()
    {
        FillTemplate empty = 
            new Builder()
                .blank().blank().blank().blank().compile();
        
        assertTrue( empty.getTextAfterBlank( -1 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 0 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 1 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 2 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 3 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 4 ).equals( "" ) );
        assertTrue( empty.getTextAfterBlank( 5 ).equals( "" ) );

        FillTemplate fo = new Builder()
            .text( "0123" ).blank().text( "567" ).blank()
            .text( "901" ).blank().text( "3456" ).blank() 
            .text( "890" ).blank().text( "23" ).blank().compile();
            //( "0123_567_901_3456_890_23_", "_" );

        assertTrue( fo.getTextAfterBlank( -1 ).equals( "" ) );
        
        assertTrue( fo.getTextAfterBlank( 0 ).equals( "567" ) );
        assertTrue( fo.getTextAfterBlank( 1 ).equals( "901" ) );
        assertTrue( fo.getTextAfterBlank( 2 ).equals( "3456" ) );
        assertTrue( fo.getTextAfterBlank( 3 ).equals( "890" ) );
        assertTrue( fo.getTextAfterBlank( 4 ).equals( "23" ) );
        assertTrue( fo.getTextAfterBlank( 5 ).equals( "" ) );
    }

    public void testGetTextBeforeBlanks()
    {
        FillTemplate empty = 
            new Builder().blank().blank().blank().blank().compile();
        assertTrue( empty.getTextBeforeBlank( -1 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 0 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 1 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 2 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 3 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 4 ).equals( "" ) );
        assertTrue( empty.getTextBeforeBlank( 5 ).equals( "" ) );

        FillTemplate fo = 
           new Builder().text("0123").blank().text( "567").blank().text( "901" )
           .blank().text( "3456" ).blank().text( "890" ).blank() .text( "23")
           .blank().compile();

        assertTrue( fo.getTextBeforeBlank( -1 ).equals( "" ) );

        assertTrue( fo.getTextBeforeBlank( 0 ).equals( "0123" ) );
        assertTrue( fo.getTextBeforeBlank( 1 ).equals( "567" ) );
        assertTrue( fo.getTextBeforeBlank( 2 ).equals( "901" ) );
        assertTrue( fo.getTextBeforeBlank( 3 ).equals( "3456" ) );
        assertTrue( fo.getTextBeforeBlank( 4 ).equals( "890" ) );
        assertTrue( fo.getTextBeforeBlank( 5 ).equals( "23" ) );
        assertTrue( fo.getTextBeforeBlank( 6 ).equals( "" ) );
    }

    public void testGetCharIndexOfBlank()
    {

        //FillOrder fo = ;
        assertTrue( new Builder().blank().text( " B" ).compile()
            .getCharIndexOfBlank( 0 ) == 0 );
        assertTrue( new Builder().text("A").blank().text( "B" ).compile()
            .getCharIndexOfBlank( 0 ) == 1 );
        assertTrue( new Builder().text( "AB" ).blank().compile()
            .getCharIndexOfBlank( 0 ) == 2 );

        assertTrue( new Builder().blank().text( "B" ).blank().compile()
            .getCharIndexOfBlank( 0 ) == 0 );
        
        //System.out.println( FillInTheBlanks.parse( "__B__" ).getCharIndexOfBlank( 1 ) );
        FillTemplate fo = 
            new Builder().blank().text( "B" ).blank().compile();
        //System.out.println( fo );
        //System.out.println( fo.getCharIndexOfBlank( 1 ) );
        assertTrue( fo.getCharIndexOfBlank( 1 ) == 1 );

        fo = new Builder().blank().blank().compile();
        assertTrue( fo.getCharIndexOfBlank( 0 ) == 0 );
        assertTrue( fo.getCharIndexOfBlank( 1 ) == 0 );

        fo = new Builder()
               .text( "0123" ).blank()
               .text( "567" ).blank()
               .text( "901" ).blank()
               .text( "3456" ).blank()
               .text( "890" ).blank()
               .text( "23" ).blank().compile();
            //"0123_567_901_3456_890_23_", "_" );

        LOG.debug( fo.getStaticText() ); //0123567901345689023

        assertTrue( fo.getCharIndexOfBlank( 0 ) == 4 ); //0123_567901345689023
        assertTrue( fo.getCharIndexOfBlank( 1 ) == 7 ); //0123567_901345689023
        assertTrue( fo.getCharIndexOfBlank( 2 ) == 10 ); //0123567901_345689023 
        assertTrue( fo.getCharIndexOfBlank( 3 ) == 14 ); //01235679013456_89023       
        assertTrue( fo.getCharIndexOfBlank( 4 ) == 17 ); //01235679013456890_23
        assertTrue( fo.getCharIndexOfBlank( 5 ) == 19 ); //0123567901345689023_	    
    }

    /**
     * Test that things look right when I toString a FillInTheBlanks.FillOrder
     */
    public void testGetTextAfterBlank()
    {
        /*
        FillOrder a = 
            FillInTheBlanks.parse( 
                "public class __" + N
                + "    extends __" + N 
                + "    implements __" + N
                + "{" + N
                + "    __" + N 
                + "}"
            );
        */

        FillTemplate fillOrder = new Builder().text( "a" ).compile();
        assertTrue( fillOrder.getTextAfterBlank( 0 ).equals( "" ) );
        assertTrue( fillOrder.getTextAfterBlank( 1 ).equals( "" ) );

        //fillOrder = new Builder().blank().compile();
        //assertTrue( fillOrder.getTextAfterBlank( 0 ).equals( ", b" ) );
        //assertTrue( fillOrder.getTextAfterBlank( 1 ).equals( "" ) );

        //fillOrder = FillInTheBlanks.parse( "__, b, __" );

        //System.out.println( fillOrder.getTextAfterBlank( 0 ) );
        //assertTrue( fillOrder.getTextAfterBlank( 0 ).equals( ", b, " ) );

        //System.out.println( "\"" + fillOrder.getTextAfterBlank( 1 ) + "\"" );
        //assertTrue( fillOrder.getTextAfterBlank( 1 ).equals( "" ) );
    }

    public void testBlanks2()
    {
        FillTemplate f2 = 
            new Builder()
            .text( "A" ).blank()
            .text( "C" ).blank()
            .text( "E" ).blank()
            .text( "G" ).blank() 
            .text( "I" ).compile();
        assertTrue( f2.getTextAfterBlank( 0 ).equals( "C" ) );
        LOG.debug( "(1)" + f2.getTextAfterBlank( 1 ) );
        assertTrue( f2.getTextAfterBlank( 1 ).equals( "E" ) );

        assertTrue( f2.getTextAfterBlank( 2 ).equals( "G" ) );
        assertTrue( f2.getTextAfterBlank( 3 ).equals( "I" ) );
        assertTrue( f2.getTextAfterBlank( 4 ).equals( "" ) );
    }

    /**
     * Test that things look right when I toString a FillInTheBlanks.FillOrder
     
    public void testToString()
    {
        FillOrder a = FillInTheBlanks.parse( "public class __" + N + "    extends __" + N
            + "    implements __" + N + "{" + N + "    __" + N + "}" );

        System.out.println( a );

        a = FillInTheBlanks.parse( "____ __ __ __ & __ __" + N + "    is __" );

        System.out.println( a );

        //String[] orderedNames = new String[] 
        //  { "${1}","${2}", "${3}", "${4}", "${5}", "${6}", "${7}", "${8}", "${9}" };

        //System.out.println(  a.fill( (Object[])orderedNames ) );

        //System.out.println( a );
    }
    */
    //here is the conventional use, first parsing the template, then filling it in
    public void testSimpleUse()
    {
        assertEquals( 
            new Builder().text( "I have a ").blank().text( " dog and a ").blank()
            .text( " house" ).compile()
            .fill( "black", "beige" ), 
            "I have a black dog and a beige house" );
    }

    /**
     * FillInTheBlanks is dumb, but adding an abstraction layer on top can make it
     * 
     
    public void testFormatted()
    {
        FillOrder classDef = 
            new Builder().text( "public class " ).blank().blank()
            __" + "__" + "__" + "extends __"
            + "__" + "__" + "implements __" + "__" + "{" + "__" + "}" );
        String N = System.lineSeparator();
        String T = "    ";
        String theClassDef =
            classDef.fill( "TheClass", N, T, "TheBaseClass", N, T, "TheInterface", N, N );

        assertTrue( theClassDef.equals( "public class TheClass" + N + "    extends TheBaseClass" + N
            + "    implements TheInterface" + N + "{" + N + "}" ) );

    }
    */

    /*
    // verify that (for a FillInTheBlanks) I want to compact so I dont get multiple 
    // newlines
    public void testCompressNewLines()
    {
        String _ = null;
        FillOrder a = FillInTheBlanks.of( "A", N, N, N, N, _, N, N, N, N, "C " );
        System.out.println( a.fill( "B" ) );
    }
    */

    /*
    public void testNewLines()
    {
        String _ = null;
        FillOrder a = new Builder().text( "A"), N, _, "C " );
        System.out.println( a.fill( "B" ) );

        FillInTheBlanks.Builder f = new FillInTheBlanks.Builder();
        f.text( "A" );
        f.text( N );
        f.blank();
        f.text( "C" );

        FillOrder d = f.compile();
        System.out.println( d.fill( "B" ) );
    }
    */

    /** Simple Usage examples of FillInTheBlank 
    public static void main( String[] args )
    {
        String _ = null;

        FillOrder a = FillInTheBlanks.of( "A", _, "C " );
        System.out.println( a.fill( "B" ) );

        //4 blanks
        FillOrder c = FillInTheBlanks.of( _, _, _, _ );

        //you can use the "compiled" document 
        //to Fill in the blank many times (and share a document across threads)
        System.out.println( c.fill( "A", "B", "C", "D" ) ); //"ABCD"		
        System.out.println( c.fill( "Don't", " disturb", " the", " beast" ) ); //"Don't disturb the beast"

        //blanks intermingled with text (starts with blank)
        c = FillInTheBlanks.of( _, "B", _, "D", _ );
        System.out.println( "\"" + c.fill( "A", "C", "E" ) + "\"" ); //"ABCDE"

        //blanks intermingled with text (starts with text)
        c = FillInTheBlanks.of( "A", _, "C", _, "E" );
        System.out.println( "\"" + c.fill( "B", "D" ) + "\"" ); //"ABCDE"

        c = FillInTheBlanks.of( "A", _, "C", _, "E", _, "G" );
        System.out.println( "\"" + c.fill( "B", "D", "F" ) + "\"" ); //"ABCDEF"

        //multiple blanks in a row
        c = FillInTheBlanks.of( "A", _, "C", _, "E", _, _, _ );
        System.out.println( "\"" + c.fill( "B", "D", "F", "G", "H" ) + "\"" ); //"ABCDEFGH"		
    }
    */
}
