package tutorial.chap3.markup;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;

/**
 * BindML contains all of the 
 * @author Eric DeFazio
 */
public class _2_BindML
    extends TestCase
{
    static Dom SimpleMarks = BindML.compile( 
        "{+a+}" //print value of var a (or "" if a is null)                
      + "{+a*+}" //print required* var a (or throw MarkupException if a is null)
      + "{+b|default+}" //print b if non-null, otherwise print "default"                           
                
      + "{+$>(a)+}" //print result of calling ">" (indent) script with a
                
      + "{+?a:some text+}" //if( a is non null ) print "some text"
      + "{+?a:$>(a)+}" //if( a is non null) print result of calling ">" indent with a 
                
      + "{+?a==1:a is 1+}" //if a == 1, print "a is 1"          
      + "{+?a==1:$>(a)+}" //if a == 1, result of calling ">" (indent) script with a
                
      + "{+(( 5 + 3 ))+}" //evaluate the expression ( 5 + 3 ) and print  
      + "{+(( \" a = \" + a ))+}" //evaluate the experession using value of a      
    );
    
    public void testSimpleMarkUseCases()
    {
        String simple = Compose.asString( SimpleMarks, 
            "a", 1, 
            "b", 2 );
        
        System.out.println( simple );
        
        String useDefault = Compose.asString( SimpleMarks, 
            "a", 1  );
        
        System.out.println( useDefault );
        
    }
    
    static Dom FormMarks = BindML.compile( 
        "{{+:{+type+} {+name+}, +}}" //form a repeating patttern for ALL types and names               )
    );
}
