package varcode.markup.mark;

import java.util.UUID;

import varcode.VarException;
import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.markup.mark.AddExpressionResult;
import junit.framework.TestCase;

/**
 * Tests I can call expressions that are evaluated by Nashorn/Rhino
 * (Java embedded engine) and return results 
 * 
 * @author eric
 *
 */
public class AddExpressionResultTest 
	extends TestCase
{
	
	public static class StaticFieldsBean
	{
		public static final String T = "1000";
		public static final int Z = 1;
		
		public static String together()
		{
			return T + Z;
		}
		
	}
	
	public static class PublicMemberFieldsBean
	{
		public int a = 100;
		public String b = "HEY";		
	}
	
	public static class MethodBean
	{
		private String local;
		
		public MethodBean( String value )
		{
			this.local = value;
		}
		
		public void mutate()
		{
			System.out.println( "AT FIRST I WAS \""+local+"\"" );
			this.local = UUID.randomUUID().toString();
			System.out.println( "MUTATED TO \""+this.local+"\"" );
		}
		
		public String logReturn()
		{
			System.out.println( "I LOGGED \""+local +"\"" );
			return local;
		}
		
		public void set(String value )
		{
			this.local = value;
		}
		
	}
	
	/** check If I can access properties of a Bean in an expression */
	public void testExpressionBeanProperties()
	{
		VarContext c = VarContext.of( "bean", new PublicMemberFieldsBean() );
		String expressionText = "bean.a";		
		String text = "/*{+((" + expressionText + "))+}*/";
		
		AddExpressionResult aer = new AddExpressionResult(
				text, 
				0 ,
				expressionText );
			
		assertEquals( 100, aer.derive( c ) );
		
		expressionText = "bean.b";
		aer = new AddExpressionResult(
				"/*{+((" + expressionText + "))+}*/", 
				0 ,
				expressionText );
		assertEquals( "HEY", aer.derive( c ) );	
	}
	
	public void testExpressionCallJavaMethods()
	{
		//call a method (no re
		String AVALUE = "BLAH";
		MethodBean mb = new MethodBean( AVALUE );
		VarContext vc = VarContext.of( "bean", mb );
		String expressionText = "bean.mutate()";		
		String text = "/*{+((" + expressionText + "))+}*/";
		
		assertEquals ( mb.local, AVALUE );
		AddExpressionResult aer = new AddExpressionResult(
				text, 
				0 ,
				expressionText );
		assertEquals( null, aer.derive( vc ) );
		assertTrue( !AVALUE.equals( mb.local  ) );
		
		
		expressionText = "bean.logReturn()";		
		text = "/*{+((" + expressionText + "))+}*/";
		aer = new AddExpressionResult(
				text, 
				0 ,
				expressionText );
		
		assertEquals( mb.local, aer.derive( vc ) );			
	}
	
	/** verify that I can pass parameters in Javascript to Java Methods*/
	public void testExpressionPassLiteralParameter()
	{
		String INIT = "INIT";
		MethodBean mb = new MethodBean( INIT );
		VarContext vc = VarContext.of( "bean", mb );
		
		String expressionText = "bean.set('hi')"; //pass a literal parameter		
		String text = "/*{+((" + expressionText + "))+}*/";
		
		assertEquals ( mb.local, INIT );
		AddExpressionResult aer = new AddExpressionResult(
				text, 
				0 ,
				expressionText );
		assertEquals( null, aer.derive( vc ) );
		assertEquals( mb.local, "hi" );
		
	}
	
	/** verify that I can pass parameters in Javascript to Java Methods */
	public void testExpressionPassVarParameter()
	{
		String INIT = "INIT";
		MethodBean mb = new MethodBean( INIT );
		VarContext vc = VarContext.of( "bean", mb, "va", "HEYO" );
		
		String expressionText = "bean.set(va)"; //pass a var parameter		
		String text = "/*{+((" + expressionText + "))+}*/";
		
		assertEquals ( mb.local, INIT );
		AddExpressionResult aer = new AddExpressionResult(
				text, 
				0 ,
				expressionText );
		
		assertEquals( null, aer.derive( vc ) );
		//this doesnt work
		assertEquals( mb.local, "HEYO" );		
	}
	
	public void testExpressionWithTempVariables()
	{
		VarContext c = VarContext.of( "bean", new MethodBean( "D" ) );
		String expressionText = "aCount = 'A'; bCount = 'B'; aCount < bean.logReturn() && bCount < bean.logReturn()";
		AddExpressionResult aer = new AddExpressionResult(
				"/*{+((" + expressionText + "))+}*/", 
				0 ,
				expressionText );
		System.out.println( aer.derive( c ) );
	}
	
	
	public void testExpressionAccessStaticFields()
	{
		//expressionText = "bean.T";
		//expressionText = "bean.T";
		//Java.type("java.lang.Math").PI
		VarContext c = VarContext.of( "bean", new StaticFieldsBean() );
		//String expressionText = "bean.a";		
		String expressionText = "Java.type(\"java.lang.Math\").PI";
		String mark = "/*{+((" + expressionText + "))+}*/";
		
		
		
		AddExpressionResult aer = new AddExpressionResult(
				mark, 
				0 ,
				expressionText );
		System.out.println(aer.derive( c ) );
		
		//expressionText = "bean.Z";
		
		//access a static field of a class
		//I could have a Script that 
		// generates the appropriate (javascript) text 
		// for accessing the static field
		
		//i.e. staticField( bean, Z )
		//i.e. staticMethod( bean, 
		expressionText = "Java.type(\""+ StaticFieldsBean.class.getCanonicalName() +"\" ).Z";
		aer = new AddExpressionResult(
				"/*{+((" + expressionText + "))+}*/", 
				0 ,
				expressionText );
		System.out.println(aer.derive( c ) );
		
	}
	
	public void testConstant()
	{
		String expressionText = "4";
		
		String text = "/*{+((" + expressionText + "))+}*/"; 
        int lineNumber = 0;
        
        
		AddExpressionResult aer = new AddExpressionResult(
			text, 
			lineNumber ,
			expressionText );
		
		assertEquals( 4, aer.derive( VarContext.of( ) ) );			
	}
	
	public void testUseVars()
	{
		String expressionText = "a + b";
		
		String text = "/*{+((" + expressionText + "))+}*/"; 
        int lineNumber = 0;
        
        
		AddExpressionResult aer = new AddExpressionResult(
			text, 
			lineNumber ,
			expressionText );
		
		assertEquals( 101.0, aer.derive( VarContext.of("a", 1.0, "b", 100.0 ) ) );			
	}
	
	
	public void testUseVarsMaskFloatingPoint()
	{
		String expressionText = "a + b | 0";
		
		String text = "/*{+((" + expressionText + "))+}*/"; 
        int lineNumber = 0;
        
        
		AddExpressionResult aer = new AddExpressionResult(
			text, 
			lineNumber ,
			expressionText );
		
		assertEquals( 10001, aer.derive( VarContext.of("a", 1, "b", 10000 ) ) );			
	}

	public void testUseFunction()
	{
		String expressionText = "Math.sqrt( a * a + b * b )";
		
		String text = "/*{+((" + expressionText + "))+}*/"; 
        int lineNumber = 0;
        
        
		AddExpressionResult aer = new AddExpressionResult(
			text, 
			lineNumber ,
			expressionText );
		
		assertEquals( 5.0, aer.derive( VarContext.of("a", 3, "b", 4 ) ) );		
		
		TranslateBuffer tb = new TranslateBuffer();
		aer.fill(VarContext.of("a", 3, "b", 4 ), tb );		
		assertEquals( "5.0", tb.toString() );
	}
	
	public void testFunctionVarNotFound()
	{
		String expressionText = "Math.sqrt( a * a + b * b )";
		
		String text = "/*{+((" + expressionText + "))+}*/"; 
        int lineNumber = 0;
        
        
		AddExpressionResult aer = new AddExpressionResult(
			text, 
			lineNumber ,
			expressionText );
		
		try
		{
			aer.derive( VarContext.of("a", 3 ) );
		}
		catch( VarException e )
		{			
			//make sure the Exception thrown has the Refernce error (for b)
			//signified
			Throwable cause = e.getCause();
			assertTrue( cause.getMessage().contains( "ReferenceError: \"b\"" ) );
		}
	}
}
