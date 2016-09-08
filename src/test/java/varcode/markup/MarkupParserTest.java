package varcode.markup;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import junit.framework.TestCase;

public class MarkupParserTest
    extends TestCase
{
    public static final String N = "\r\n";

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
            //System.out.println ( e );
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

}
