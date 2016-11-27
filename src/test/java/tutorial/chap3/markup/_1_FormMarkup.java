package tutorial.chap3.markup;

import java.util.Date;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.doc.form.Form;
import varcode.markup.forml.ForML;

/**
 * Forms are "incomplete" documents containing text and 
 * "parameterized" blanks that can compose textual documents, 
 * Think:<PRE>
 * "I _________ ________, do solemnly swear to tell the truth"
 *    firstName lastName</PRE>
 *
 * We'd represent the above as a Form:<PRE>
 * Form declarationForm = ForML.compile(
 *   "I {+firstName*+} {+lastName*+}, do solemnly swear to tell the truth" );
 * </PRE>
 *
 * The ForML "compiler" differentiates the static text from the 
 * dynamic "marks", and prepares the Form to be "composed".
 *
 * After compilation, each form can be composed multiple times:<PRE>
 * 
 * String me = declarationForm.compose(
 *    "firstName", "Eric", "lastName", "DeFazio" ); 
 * //me = "I Eric DeFazio, do solemnly swear to tell the truth"
 *
 * String myDog = declarationForm.compose(
 *    "firstName", "Lorenzo", "lastName", "DeFazio");
 * //myDog = I Lorenzo DeFazio, do solemnly swear to tell the truth"
 * </PRE>
 *
 * @author Eric DeFazio eric@varcode.io
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
    
    public void testAddExpressionResult()
    {
        Form ADD_EXPRESSION_RESULT = ForML.compile(
            "{+(( 1 + 2 | 0 ))+} {+(( p1 + p2 ))+} {+(( p1 / 3 ))+}" );
        
        String res = ADD_EXPRESSION_RESULT.compose( 
            "p1", 3,
            "p2", 3.14159d );
        
        System.out.println( res );
        
        assertEquals( "3 6.14159 1.0", res );
    }
             
    public void testIfAnyCondition()
    {
        // IF the value of "condition" is any non-null value
        // the form will output "LOG.debug( "got here" );"      
        Form IF_ANY_CONDITIONAL = ForML.compile(
        "{+?condition:LOG.debug( \"got here\" );+}" );
        
        //condition is null, nothing is output 
        assertEquals( "", IF_ANY_CONDITIONAL.compose( ) );
        
        assertEquals( "", 
            IF_ANY_CONDITIONAL.compose( "condition", null ) );
        
        //condition is NOT null (true), output ...
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_ANY_CONDITIONAL.compose( "condition", true ) );
        
        //condition is false (which is NOT-null), output...
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_ANY_CONDITIONAL.compose( "condition", false ) );
        
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_ANY_CONDITIONAL.compose( "condition", "A" ) );
        
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_ANY_CONDITIONAL.compose( "condition", new Date() ) );
    }

    public void testIfIsCondition()
    {
        // IF the value of "condition" is "true"
        // the form will output "LOG.debug( "got here" );"
        Form IF_IS_CONDITIONAL = ForML.compile(
            "{+?condition==true:LOG.debug( \"got here\" );+}" );
        
        //condition is null =/= true, nothing is output 
        assertEquals( "", IF_IS_CONDITIONAL.compose( ) );
        
        assertEquals( "", 
            IF_IS_CONDITIONAL.compose( "condition", null ) );
        
        //condition == true, output ...
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_IS_CONDITIONAL.compose( "condition", true ) );
        
        assertEquals( "", 
            IF_IS_CONDITIONAL.compose( "condition", "true" ) );
        
        //condition is false (which is NOT-null), output...
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_IS_CONDITIONAL.compose( "condition", false ) );
        
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_IS_CONDITIONAL.compose( "condition", "A" ) );
        
        assertEquals( "LOG.debug( \"got here\" );", 
            IF_IS_CONDITIONAL.compose( "condition", new Date() ) );
    }
}