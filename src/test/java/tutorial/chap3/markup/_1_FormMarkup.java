package tutorial.chap3.markup;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.doc.form.Form;
import varcode.markup.forml.ForML;

/**
 * Forms are documents that containing text and "Marks" to specify 
 * how data can be used to fill in "blanks". 
 * Forms need to be run through the ForML "compiler" which 
 * differentiates the static text from the dynamic "marks".
 * 
 * @author Eric DeFazio
 */
public class _1_FormMarkup
    extends TestCase
{
    public void testAddMarks()
    {
        Form ADD_MARKS = ForML.compile( 
            "{+add+} {+addOrDefault|default+} {+addRequired*+}" );
        
        assertEquals( "+ default 1234", ADD_MARKS.compose( 
            "add", "+", 
            "addOrDefault", null,
            "addRequired", 1234 ) );
        
        assertEquals( "+ value 1234", ADD_MARKS.compose( 
            "add", "+", 
            "addOrDefault", "value",
            "addRequired", 1234 ) );
        
        try
        {
            ADD_MARKS.compose(  );
            fail( 
                "expected exception for missing required field \"addRequired\"" );
        }
        catch( VarException ve )
        {
            //expected
        }
    }
    
    public void testAddScriptResultMarks()
    {
        Form ADD_SCRIPT_RESULT = ForML.compile(
            "{+$>(param)+} " + //indent value of param 4 spaces
            "{+$^(param)+} " + //first cap value of param
            "{+$^^(param)+} " + //all caps value of param
            "{+$#(param)+}" ); //count number of param (the cardinality)
        
        assertEquals(
            "    param1 " + 
            "Param1 " +
            "PARAM1 " +
            "1", 
            ADD_SCRIPT_RESULT.compose( "param", "param1" ) );
    }
    
    public static final Form ADD_EXPRESSION_RESULT = ForML.compile(
        "{+(( 1 + 2 | 0 ))+} {+(( p1 + p2 ))+} {+(( p1 / 3 ))+}" );
    
    public void testAddExpressionResult()
    {
        String res = ADD_EXPRESSION_RESULT.compose( 
            "p1", 3,
            "p2", 3.14159d );
        
        System.out.println( res );
        
        assertEquals( "3 6.14159 1.0", res );
    }
    
    public static final Form IF_CONDITIONAL = ForML.compile(
        "{+?log:LOG.debug( \"got here\");+}" );
    
    public void testAddIfCondition()
    {
        assertEquals( "", IF_CONDITIONAL.compose( ) );
        
        assertEquals( "LOG.debug( \"got here\");", 
            IF_CONDITIONAL.compose( "log", true ) );
    }
}
