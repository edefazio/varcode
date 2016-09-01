package varcode.markup.bindml;

import varcode.doc.DocState;
import varcode.markup.MarkupException;

public class BindMLFunctionalTests_DefineVar
	extends BindMLFunctionalTest
{

	public static void defines( String bindML, String varName, Object expected )	
	{
		DocState ts = BindML.tailor( bindML );
		is( expected, ts.getContext().resolveVar( varName ) );
	}
	
	public void testDefine()
	{
		//       run this   verify this varName...
		//           |       |
		//           |       |    | ...is bound to this value
		defines( "{#a:1#}", "a", "1" );
		defines( "{##a:1##}", "a", "1" );		
		defines( "{#a=1#}", "a", "1" );
		defines( "{##a=1##}", "a", "1" );
		
	}
	
	
	/** 
	 * when we "define" types, they arent always Strings...
	 * They get converted to strings with the SmartBuffer
	 * (but can remain typed in the VarContext
	 */
	public void testDefineAsScriptResult()
	{
		defines( "{#a:$count(b)#}", "a", null );
		defines( "{##a:$count(b)##}", "a", null );
		DocState ts = BindML.tailor( "{#a:$count(b)#}", "b", new String[ 0 ] );
		assertEquals( 0, ts.getContext().resolveVar( "a" ) );
		
		ts = BindML.tailor( "{##b:1##}{#a:$count(b)#}");
		assertEquals( 1, ts.getContext().resolveVar( "a" ) );
	}
	
	public void testDefineComposite()
	{
		//NOTE: when we define with {# the var values are Strings
		defines( "{#a:1#}{#b:2#}{#c:((a+b))#}", "c", "12" );
		defines( "{##a:1##}{##b:2##}{##c:((a+b))##}", "c", "12" );
	}

	public void testDefineExpressionArithmetic()
	{
		//NOTE: the JS expression Evaluator stores all number values as 
	    // 64-bit doubles, 
		defines( "{#a:(( 1 ))#}{#b:(( 2 ))#}{#c:(( a+b ))#}", "c", 3.0 );
		defines( "{##a:(( 1 ))##}{##b:(( 2 ))##}{##c:(( a+b ))##}", "c", 3.0 );
		
		//Use "|0" to cast the result expression to int
		defines( "{#a:(( 1 ))#}{#b:(( 2 ))#}{#c:(( a+b | 0 ))#}", "c", 3 );
		defines( "{##a:(( 1 ))##}{##b:(( 2 ))##}{##c:(( a+b | 0 ))##}", "c", 3 );
	}
	
	/** NOTE: the Type is int, since expressions are evaluated */
	public void testDefineAsExpression()
	{
		defines( "{#a:(( 1 ))#}", "a", 1 );
		defines( "{##a:(( 1 ))##}", "a", 1 );
		
		defines( "{##a:(( 4 ))##}", "a", 4 );		
	}
		
	public void testDefineAsExpressionUsingUtils()
	{
		defines( 
			"{#r:2#}{#circumference:(( Math.PI * r * r ))#}", 
			"circumference", 2*2*Math.PI );
	}
	
	public void testBadVarName()
	{
		try
		{
			BindML.tailor( "{#int:3#}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		
		try
		{
			BindML.tailor( "{##int:3##}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		try
		{
			BindML.tailor( "{#%a:3#}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		try
		{
			BindML.tailor( "{##%a:3##}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
	}
	
	
}
