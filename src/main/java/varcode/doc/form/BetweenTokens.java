package varcode.doc.form;

import java.util.HashSet;

/**
 * Ending a Form with any of these Tokens (as the last printable characters)
 * will signify that they are to be used "between" entities:
 * for instance:<PRE><CODE>
 * 
 * CodeForm argumentList = new CodeForm( "{+type*} {+fieldName}, ");
 *  //                                                         --
 *  //                                 this is the "Between tokens"  
 * String argList = argumentList.tailorAll( 
 *     Pairs.of(
 *         "type", "int",
 *         "name", "a" ) );
 *          
 *  //where argList = "int a"
 *  //** (Notice, there is no need to use the ',' in a (1) item list) ** 
 *  
 *  String argList = argumentList.tailorAll( 
 *     Pairs.of(
 *         "type", new String[]{"int", "String"},
 *         "name", new String[]{"a", "b"} ) );
 *         
 *  //argList = "int a, String b"
 *  //** (Notice, there is no need to use the ',' after "String b" since it is 
 *  the last form item in the list) **
 * </CODE></PRE>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface BetweenTokens
{
    /**
     * returns the maximum token length of a token
     * (this will determine the number of "printable characters" to retrieve 
     * from the tail of the form
     * 
     * @return the maximum Token length (in characters) of all the tokens
     */
    public int getMaxTokenLength();
    
    /**
     * Returns the largest between token that the token (or null if the
     * formTail does not end with a BetweenToken
     * 
     * @param formTail the "tail" content of the form 
     * (information after the last Blank/Fill/Mark) of the {@code Form}
     *  
     * @return the token or null
     */
    public String endsWithToken( String formTail );
    
    /**
     * These tokens are used between multiple entities
     * for example if we have three items {A B C}:
     * we signifiy the (3) items using the ',' token which 
     * logically separates the three tokens:<BR>
     * A, B, C<BR>
     * 
     * if we have a list of only (1) item, we don't need a logical separator
     * A<BR>
     */
    public static class BaseBetweenTokens
        implements BetweenTokens
    {        
        public final static HashSet<String> BETWEEN = new HashSet<String>();
        
        static
        {
        	BETWEEN.add( "||" );  
            BETWEEN.add( "&&" );
            
            BETWEEN.add( "|" );             
            BETWEEN.add( "^" );            
            BETWEEN.add( "&" );
            
            BETWEEN.add( "," );            
            BETWEEN.add( "+" ); 
            BETWEEN.add( "-" );             
            BETWEEN.add( "*" );
            
            BETWEEN.add( ">>" );
            BETWEEN.add( ">>>" );
            BETWEEN.add( "<<" );            
        }
        
        public static final BaseBetweenTokens INSTANCE 
            = new BaseBetweenTokens();
        
        @Override
        public int getMaxTokenLength()
        {
            return 3;
        }

        @Override
        public String endsWithToken( String token )
        {
            if( token == null )
            {
                return null;
            }
            String trimmed = token.trim();
            int tailCharCount = Math.min( getMaxTokenLength(), trimmed.length() );
            
            //these are the last characters
            String test = trimmed.substring( trimmed.length() - tailCharCount );
            
            for( int i = 0; i < tailCharCount; i++ )
            {
                //System.out.println(  "TRY \"" + test + "\"" );//here I might ALTERNATIVELY WANT TO PRINT OUT
                if( BETWEEN.contains( test ) )
                {
                    return test;
                }
                if ( test.length() > 0 )
                {
                    test = test.substring( 1 ); //chop off the head of the tail
                }
            }
            return null;
        }
    }
}
