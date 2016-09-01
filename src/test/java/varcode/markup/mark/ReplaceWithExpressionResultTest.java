package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.markup.mark.ReplaceWithExpressionResult;
import junit.framework.TestCase;

public class ReplaceWithExpressionResultTest
    extends TestCase
{
	
    public void testSimple()
    {
        String codeMLMark = "/*{+((3+4))*/something/*+}*/";        
        ReplaceWithExpressionResult rwe = 
        	new ReplaceWithExpressionResult( codeMLMark, 
        	        23, 
        	        "3+4", 
        	        "something" );
        
        assertEquals( "3+4", rwe.getExpression() );
        assertEquals( "something", rwe.getWrappedText() );
        
        assertEquals( 7, rwe.derive( VarContext.of() ) );        
    }
    
}
