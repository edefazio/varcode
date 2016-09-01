package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.markup.mark.CutIfExpression;
import junit.framework.TestCase;

public class CutIfTest
    extends TestCase
{
    private static final String EXPRESSION = 
        //NOTE the === (triple =) is intentional (equal in value and type)           
        "typeof debug==='undefined' || debug == false || debug.equals( 'false' )";
    
    private static final String CONTENT = 
        "System.out.println(\"Created new source code export directory\");"; 
    
    private static final String MARK = 
       "/*{-?((" + EXPRESSION + ")):*/" 
     + CONTENT
     + "/*-}*/";
    
    public void testCutIfSimple()
    {
        CutIfExpression cu = new CutIfExpression( MARK, -1, EXPRESSION, CONTENT );
        
        assertEquals( EXPRESSION,  cu.getExpression() );
        assertEquals( CONTENT,  cu.getConditionalText() );
        assertEquals( MARK, cu.getText() );
        
        //verify that if the evaluation FAILS (no variables i.e debug bound) the
        // content is NOT cut
        //HERE ARE THE (3) Scenarios where we "CUT"
        assertEquals( null, cu.derive( VarContext.of(  ) ) ); //there is no "debug" var
        assertEquals( null, cu.derive( VarContext.of("debug", false  ) ) ); //debug exists and it's a boolean false)
        assertEquals( null, cu.derive( VarContext.of("debug", "false"  ) ) ); //debug exists and its a String false
        
        //IF debug exists, and is ANYTHING but false or 'false' then DON'T CUT
        assertEquals( CONTENT, cu.derive( VarContext.of("debug", "true"  ) ) );
    }
    
    /** this is an example*/
    public static void main( String[] args )
    {        
        /*{-?(debug!=true):*/
        System.out.println("Created new source code export directory");
        /*-}*/
    }
}
