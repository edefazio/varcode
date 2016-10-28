package varcode.markup.mark;

import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.context.eval.Eval_JavaScript;
import varcode.markup.MarkupException;
import junit.framework.TestCase;

public class AddVarOneOfTest
	extends TestCase
{

	public void testAddVarOneOfStringQuotes()
	{    
		String varName= "vowel";
		int lineNumber = 100;
		String arrayDescription =  "[\"A\", \"E\", \"I\", \"O\", \"U\"]";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		

		
		String text = "{+" + varName + arrayDescription + "+}";
		boolean isRequired = false;
		String defaultValue = null;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		assertEquals( null, aoo.derive( VarContext.of( ) ) );
		assertEquals( "A", aoo.derive( VarContext.of( "vowel", "A") ) );
		//assertEquals( 'A', aoo.derive( VarContext.of( "vowel", 'A') ) );
		assertEquals( "E", aoo.derive( VarContext.of( "vowel", "E") ) );
		assertEquals( "I", aoo.derive( VarContext.of( "vowel", "I") ) );
		assertEquals( "O", aoo.derive( VarContext.of( "vowel", "O") ) );
		assertEquals( "U", aoo.derive( VarContext.of( "vowel", "U") ) );
		
		try
		{
			aoo.derive(VarContext.of( "vowel", "X" ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
	
	public void testAddVarOneOfString()
	{    
		String varName= "vowel";
		int lineNumber = 100;
		String arrayDescription =  "['A', 'E', 'I', 'O', 'U']";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		

		
		String text = "{+" + varName + arrayDescription + "+}";
		boolean isRequired = false;
		String defaultValue = null;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		assertEquals( null, aoo.derive( VarContext.of( ) ) );
		assertEquals( "A", aoo.derive( VarContext.of( "vowel", "A") ) );
		//assertEquals( 'A', aoo.derive( VarContext.of( "vowel", 'A') ) );
		assertEquals( "E", aoo.derive( VarContext.of( "vowel", "E") ) );
		assertEquals( "I", aoo.derive( VarContext.of( "vowel", "I") ) );
		assertEquals( "O", aoo.derive( VarContext.of( "vowel", "O") ) );
		assertEquals( "U", aoo.derive( VarContext.of( "vowel", "U") ) );
		
		try
		{
			aoo.derive(VarContext.of( "vowel", "X" ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
	
	
	public void testAddVarOneOfStringRequired()
	{    
		String varName= "vowel";
		int lineNumber = 100;
		String arrayDescription =  "['A', 'E', 'I', 'O', 'U']";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		
		String text = "{+" + varName + arrayDescription + "*+}";
		boolean isRequired = true;
		String defaultValue = null;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		try
		{
			assertEquals( null, aoo.derive( VarContext.of( ) ) );
		}
		catch( NullVar vbe )
		{
			//expected
		}
		try
		{
			aoo.derive(VarContext.of( "vowel", "X" ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
	
	public void testRequiredDefault()
	{
		String varName= "vowel";
		String defaultValue = "A"; //"'A'"; //TODO if I have a Default, Look it up in Array and toString it
		int lineNumber = 100;
		String arrayDescription =  "['A', 'E', 'I', 'O', 'U']";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		
		String text = "{+" + varName + arrayDescription + "|" + defaultValue + "*+}";
		boolean isRequired = true;
		
		try
		{
			new AddVarOneOf( 
					text, 
					lineNumber,
					varName, 
					array,
					arrayDescription,
					isRequired,
					defaultValue );
			fail( "Expected Markup Exception cant be required AND have default" );
		}
		catch( MarkupException e )
		{
			//expected 
		}
	}
	public void testAddVarOneOfStringDefault()
	{    
		String varName= "vowel";
		String defaultValue = "A"; //"'A'"; //TODO if I have a Default, Look it up in Array and toString it
		int lineNumber = 100;
		String arrayDescription =  "['A', 'E', 'I', 'O', 'U']";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		
		String text = "{+" + varName + arrayDescription + "|" + defaultValue + "+}";
		boolean isRequired = false;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		assertEquals( "A", aoo.derive( VarContext.of( ) ) );

		try
		{
			aoo.derive(VarContext.of( "vowel", "X" ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
	
	public void testAddVarOneOfInteger()
	{    
		String varName= "odd";
		int lineNumber = 100;
		String arrayDescription =  "[1, 3, 5, 7, 9]";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		
		String text = "{+" + varName + arrayDescription + "+}";
		boolean isRequired = false;
		String defaultValue = null;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		assertEquals( null, aoo.derive( VarContext.of( ) ) );
		assertEquals( 1, aoo.derive( VarContext.of( "odd", 1 ) ) );
		//assertEquals( 'A', aoo.derive( VarContext.of( "vowel", 'A') ) );
		assertEquals( 3, aoo.derive( VarContext.of( "odd", 3 ) ) );
		assertEquals( 5, aoo.derive( VarContext.of( "odd", 5 ) ) );
		assertEquals( 7, aoo.derive( VarContext.of( "odd", 7 ) ) );
		assertEquals( 9, aoo.derive( VarContext.of( "odd", 9 ) ) );
		
		try
		{
			aoo.derive(VarContext.of( "odd", 50 ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
	
	//the Type doesnt 
	public void testAddVarOneOfStringAndInteger()
	{    
		String varName= "arr";
		int lineNumber = 100;
		String arrayDescription =  "['A', 1, 'B', 3, 'C', 5, 'D', 7, 'E', 9]";		
		Object[] array = 
			Eval_JavaScript.getJSArrayAsObjectArray( 
				Eval_JavaScript.INSTANCE.evaluate( 
					VarContext.of(), arrayDescription ) );
		
		String text = "{+" + varName + arrayDescription + "+}";
		boolean isRequired = false;
		String defaultValue = null;
		
		AddVarOneOf aoo = new AddVarOneOf( 
			text, 
			lineNumber,
			varName, 
			array,
			arrayDescription,
			isRequired,
			defaultValue );
		
		assertEquals( null, aoo.derive( VarContext.of( ) ) );
		assertEquals( 1, aoo.derive( VarContext.of( "arr", 1 ) ) );
		assertEquals( 1, aoo.derive( VarContext.of( "arr", new Integer( 1 ) ) ) );
		
		//assertEquals( 1, aoo.derive( VarContext.of( "arr", new Long( 1 ) ) ) );
		//assertEquals( 'A', aoo.derive( VarContext.of( "vowel", 'A') ) );
		assertEquals( 3, aoo.derive( VarContext.of( "arr", 3 ) ) );
		assertEquals( 5, aoo.derive( VarContext.of( "arr", 5 ) ) );
		assertEquals( 7, aoo.derive( VarContext.of( "arr", 7 ) ) );
		assertEquals( 9, aoo.derive( VarContext.of( "arr", 9 ) ) );
		
		assertEquals( "A", aoo.derive( VarContext.of( "arr", "A" ) ) );
		//assertEquals( 'A', aoo.derive( VarContext.of( "vowel", 'A') ) );
		assertEquals( "B", aoo.derive( VarContext.of( "arr", "B" ) ) );
		assertEquals( "C", aoo.derive( VarContext.of( "arr", "C" ) ) );
		assertEquals( "D", aoo.derive( VarContext.of( "arr", "D" ) ) );
		assertEquals( "E", aoo.derive( VarContext.of( "arr", "E" ) ) );
		
		
		try
		{
			aoo.derive(VarContext.of( "arr", 50 ) );
			fail( "Expected Binding Exception" );
		}
		catch( VarBindException e)
		{
			//expected
		}
	}
}
