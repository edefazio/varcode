package varcode.form;

import junit.framework.TestCase;
import varcode.form.BetweenTokens;
import varcode.form.BetweenTokens.BaseBetweenTokens;

public class BetweenTokensTest
    extends TestCase
{
    public static final BaseBetweenTokens b = 
        BetweenTokens.BaseBetweenTokens.INSTANCE;
    
    public void testBetween()
    {        
    	//logical short curcuit
        assertEquals( "||", b.endsWithToken( "||" ) ); // a || b || c       
        assertEquals( "&&", b.endsWithToken( "&&" )); // a && b && c
        
        //bitwise
        assertEquals( "|", b.endsWithToken( "|" ) ); // a | b | c
        assertEquals( "&", b.endsWithToken(  "&" )); // a & b & c
        assertEquals( "^", b.endsWithToken(  "^" )); // a ^ b ^ c
        
        //parameterized
        assertEquals( ",", b.endsWithToken( "," ) ); // a, b, c
        
        
        assertEquals( "+", b.endsWithToken( "+" )); // a + b + c
        assertEquals( "-", b.endsWithToken( "-" )); // a - b - c            
        assertEquals( "*", b.endsWithToken( "*" )); // a * b * c
        //assertEquals( "/", b.endsWithToken(  "/" )); // a / b / c
        //assertEquals( "%", b.endsWithToken(  "%" )); // a % b % c                              
        
         
       
        //assertEquals( "=", b.endsWithToken(  "=" ));
        //assertEquals( ">>", b.endsWithToken( ">>" ));
        //assertEquals( ">>>", b.endsWithToken(  ">>>" ));
        //assertEquals( "<<", b.endsWithToken( "<<" ));
      
        //assertEquals( "==", b.endsWithToken(  "==" ));
    
        //assertEquals( "+=", b.endsWithToken(  "+=" ));
        //assertEquals( "/=", b.endsWithToken( "/=" ));
        //assertEquals( "*=", b.endsWithToken( "*=" ));
        //assertEquals( "-=", b.endsWithToken( "-=" ));
        //assertEquals( "%=", b.endsWithToken( "%=" ));
    
        //assertEquals( "<<=", b.endsWithToken( "<<=" ));
        //assertEquals( ">>=", b.endsWithToken( ">>=" ));
        //assertEquals( "&=", b.endsWithToken( "&=" ));
        //assertEquals( "^=", b.endsWithToken( "^=" ));
        //assertEquals( "|=", b.endsWithToken( "|=" ));
            
        //assertEquals( "!=", b.endsWithToken( "!=" ));
        //assertEquals( ">", b.endsWithToken( ">" ));
        //assertEquals( "<", b.endsWithToken( "<" ));
        //assertEquals( ">=", b.endsWithToken( ">=" ));
        //assertEquals( "<=", b.endsWithToken( "<=" ));
         
             
    }
    
    
    public void testTail()
    {
        assertEquals( null, b.endsWithToken( "" ) );
        assertEquals( null, b.endsWithToken( null ) );
        //assertEquals( "+=", b.endsWithToken( "much text here and+=      " ) );
        //assertEquals( ">>>", b.endsWithToken( "much text here and>>>    " ) );
        
        //assertEquals( ">>", b.endsWithToken( "much text here and> >>        " ) );
        assertEquals( "&", b.endsWithToken( "much text here and& &" ) );
    }
    
    public void testNotBetween()
    {
        assertNull( b.endsWithToken( "<< | ! )" ) );
    }
}
