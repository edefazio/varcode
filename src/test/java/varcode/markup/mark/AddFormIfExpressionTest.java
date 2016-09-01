package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.form.Form;
import varcode.markup.forml.ForML;
import junit.framework.TestCase;

public class AddFormIfExpressionTest
	extends TestCase
{

	public void testSimple()
	{
		String formText = "LOG.debug(\" the value is {+a*+} \");"; 
		Form form = ForML.compile( formText );
		String expression = " a > 100 ";
		String bindMLText = "{{+?((" + expression + ")):" + formText + "+}}";
		//String codeMLText = "/*{{+?((" + expression + ")):" + formText + "+}}*/";
		AddFormIfExpression afie = 
			new AddFormIfExpression( bindMLText, 0, expression, form );
		
		assertEquals("LOG.debug(\" the value is 101 \");", afie.derive( VarContext.of( "a", 101 ) ) );
		
		//not equal
		assertEquals( null , afie.derive( VarContext.of( "a", 100 ) ) );
		
		//expression fails (dont print)
		assertEquals( null , afie.derive( VarContext.of( "a", "JELLO" ) ) );
	}
}
