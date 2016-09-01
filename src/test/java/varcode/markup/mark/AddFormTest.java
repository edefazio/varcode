package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.form.Form;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddForm;
import junit.framework.TestCase;

public class AddFormTest
	extends TestCase
{

	public static final String AB_REQUIRED_FORM_TEXT = "{+a*+}{+b*+}, ";
	
	public static final String AB_REQUIRED_MARK = "/*{{+:" + AB_REQUIRED_FORM_TEXT + "+}}*/";
	
	public void testMissingRequired()
	{
		Form form = ForMLCompiler.INSTANCE.compile( AB_REQUIRED_FORM_TEXT );
		
		AddForm af = 
			new AddForm( AB_REQUIRED_MARK, -1, false, form );
		
		try
		{
			assertEquals( "", af.derive( VarContext.of( ) ));
			fail("Expected Exception for Missing Required Var");
			
		}
		catch( VarException ve ) 
		{
			//expected
		}
	}
	
	public void testAddSingleForm()
	{			
		Form form = ForMLCompiler.INSTANCE.compile( AB_REQUIRED_FORM_TEXT );
		
		AddForm af = 
			new AddForm( AB_REQUIRED_MARK, -1, false, form );
		
		assertEquals( "AB", af.derive( VarContext.of( "a", "A", "b", "B" ) ) );
	}
	
	public void testAddMultipleForms ()
	{			
		Form form = ForMLCompiler.INSTANCE.compile( AB_REQUIRED_FORM_TEXT );
		
		AddForm af = 
			new AddForm( AB_REQUIRED_MARK, -1, false, form );
		
		assertEquals( "AB, 12", af.derive( VarContext.of( "a", new String[]{"A", "1"}, "b", new String[]{"B", "2"} ) ) );
	}
	
}
