package varcode.markup.forml;

import java.util.HashSet;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.markup.bindml.BindMLParseState;
import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddTextIfVar;
import varcode.markup.mark.AddVar;

public class ForMLParserTest
	extends TestCase
{
	public void testFirstOpenTag()
	{
		assertEquals( "{+", ForMLParser.INSTANCE.getFirstOpenTag( "{+" ) );
		assertEquals( "{+?", ForMLParser.INSTANCE.getFirstOpenTag( "{+?" ) );
		assertEquals( "{+$", ForMLParser.INSTANCE.getFirstOpenTag( "{+$" ) );
		assertEquals( "{+((", ForMLParser.INSTANCE.getFirstOpenTag( "{+((" ) );
	}
	
	public void getCloseTag()
	{
		assertEquals( "+}", ForMLParser.INSTANCE.closeTagFor( "{+" ) );
		assertEquals( "+}", ForMLParser.INSTANCE.closeTagFor( "{+?" ) );
		assertEquals( ")+}", ForMLParser.INSTANCE.closeTagFor( "{+$" ) );
		assertEquals( "))+}", ForMLParser.INSTANCE.closeTagFor( "{+((" ) );
	}
	
	public void testParseEach()
	{
		ForMLParser.INSTANCE.parseMark( VarContext.of(), "{+name+}", 0 );
		ForMLParser.INSTANCE.parseMark( VarContext.of(), "{+?a=1:name+}", 0 );
		ForMLParser.INSTANCE.parseMark( VarContext.of(), "{+?a==1:name+}", 0 );
		ForMLParser.INSTANCE.parseMark( VarContext.of(), "{+$scriptName(params)+}", 0 );
		ForMLParser.INSTANCE.parseMark( VarContext.of(), "{+((3+5))+}", 0 );		
	}
	
	public void testAddExpressionResult()
	{
		AddExpressionResult aer = 
			(AddExpressionResult)ForMLParser.INSTANCE.parseMark( 
					VarContext.of(), "{+(( 3 + 8 ))+}", 0 );
		
		assertEquals( 0, aer.getLineNumber() );
		assertEquals( " 3 + 8 ", aer.getExpression() );		
		assertEquals( "{+(( 3 + 8 ))+}", aer.getText() );
		assertEquals( 11, aer.derive(VarContext.of( )) );
		
		
		aer = (AddExpressionResult)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+(( a + b ))+}", 0 );
		
		assertEquals( 0, aer.getLineNumber() );
		assertEquals( " a + b ", aer.getExpression() );		
		assertEquals( "{+(( a + b ))+}", aer.getText() );
		
		//NOTE: the JavaScript Expression Engine converts 
		//variable numbers to 48 bit precision floats (JUST TO ANNOY PEOPLE)
		// so the result is float
		assertEquals( 4.0, aer.derive( VarContext.of( "a", 1, "b", 3 ) ) );
		
		//this is how you return an int from an expression (" | 0")
		aer = (AddExpressionResult)ForMLParser.INSTANCE.parseMark( 
				VarContext.of(), "{+(( a + b | 0 ))+}", 0 );
			
		
		assertEquals( 0, aer.getLineNumber() );
		assertEquals( " a + b | 0 ", aer.getExpression() );		
		assertEquals( "{+(( a + b | 0 ))+}", aer.getText() );
		assertEquals( 4, aer.derive( VarContext.of( "a", 1, "b", 3 ) ) );
	}
	
	public void testAddVar()
	{
		AddVar av = (AddVar)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+name+}", 0 );
		assertEquals( 0, av.getLineNumber() );
		assertEquals( null, av.getDefault() );
		assertEquals( 0, av.getLineNumber() );
		assertFalse( av.isRequired() );
		assertEquals( "{+name+}", av.getText() );
		
		HashSet<String> varNames = new HashSet<String>();
		av.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		
		//assertEquals( "name", av.getAllVarNames( VarContext.of( ) ).toArray( new String[ 0 ])[ 0 ] );
		
		assertEquals( null, av.derive( VarContext.of( ) ) );
		assertEquals( "eric", av.derive(VarContext.of( "name", "eric" )));
		
		
		av = (AddVar)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+name|default+}", 0 );
		assertEquals( 0, av.getLineNumber() );
		assertEquals( "default", av.getDefault() );
		assertEquals( 0, av.getLineNumber() );
		assertFalse( av.isRequired() );
		assertEquals( "{+name|default+}", av.getText() );
		

		varNames = new HashSet<String>();
		av.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		
		//assertEquals( "name", av.getAllVarNames(VarContext.of( ) ).toArray(new String[0])[ 0 ] );
		
		assertEquals( "default", av.derive( VarContext.of( ) ) );
		assertEquals( "eric", av.derive(VarContext.of( "name", "eric" )));
		
		av = (AddVar)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+name*+}", 0 );
		assertEquals( 0, av.getLineNumber() );
		assertEquals( null, av.getDefault() );
		assertEquals( 0, av.getLineNumber() );
		assertTrue( av.isRequired() );
		
		assertEquals( "{+name*+}", av.getText() );
		

		varNames = new HashSet<String>();
		av.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		
		//assertEquals( "name", av.getAllVarNames(VarContext.of( ) ).toArray(new String[0])[ 0 ] );
		try
		{
			av.derive( VarContext.of( ) );
			fail( "Expected exception for required field not found" );
		}
		catch ( VarException ve )
		{
			//expected Required field not found
		}
		assertEquals( "eric", av.derive(VarContext.of( "name", "eric" ) ) );
		
		//verify that on Parsing the mark it adds a blank
		BindMLParseState cs = new BindMLParseState( VarContext.of() );
		//av.onMarkParsed( cs );
		cs.reserveBlank();
		assertEquals( 1, cs.compile().getBlanksCount() );
	}
	
	public void testAddIfVar()
	{		
		AddTextIfVar aifv = (AddTextIfVar)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+?a=1:conditionalText+}", 0 );
		
		assertEquals( "conditionalText", aifv.getConditionalText() );
		assertEquals( "conditionalText", aifv.getConditionalText() );
		assertEquals( 0, aifv.getLineNumber() );
		assertEquals( "1", aifv.getTargetValue() );
		assertEquals( "{+?a=1:conditionalText+}", aifv.getText() );
		assertEquals("a", aifv.getVarName() );
		
		assertEquals( null, aifv.derive( VarContext.of( ) ) );
		assertEquals( null, aifv.derive( VarContext.of("a", "2") ) );
		assertEquals( "conditionalText", aifv.derive( VarContext.of("a", "1") ) );
		

		HashSet<String> varNames = new HashSet<String>();
		aifv.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "a" ) );
		
		//assertEquals( "a", aifv.getAllVarNames( VarContext.of( ) ).toArray( new String[0] )[0] );
		
		assertEquals( "conditionalText",  aifv.getConditionalText() );
		assertEquals( 0, aifv.getLineNumber() );
		assertEquals("1", aifv.getTargetValue() );
		
		//verify that on Parsing the mark it adds a blank
		BindMLParseState bindMLState = new BindMLParseState( VarContext.of() );
		//aifv.onMarkParsed( bindMLState );
		bindMLState.reserveBlank();
		assertEquals( 1, bindMLState.compile().getBlanksCount() );
		
		aifv = (AddTextIfVar)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+?a==1:name+}", 0 );
	}
	
	public void testAddScriptResult()
	{
		AddScriptResult asr = (AddScriptResult)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+$date(yyyy-MM-dd)+}", 0 );
		
		//a script with input
		assertEquals( 0, asr.getLineNumber() );
		assertEquals( "{+$date(yyyy-MM-dd)+}", asr.getText() );
		
		HashSet<String> varNames = new HashSet<String>();
		
		asr.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 0, varNames.size() ); 
			
		//assertEquals( 0, asr.getAllVarNames(
		//		          VarContext.of( "date", DateTime.DATE_FORMAT ) ).size() );
		assertEquals( "yyyy-MM-dd", asr.getScriptInput() );
		assertEquals( "date", asr.getScriptName() );
		assertEquals( "{+$date(yyyy-MM-dd)+}", asr.getText() );
		assertFalse( asr.isRequired() );
				
		BindMLParseState cs = new BindMLParseState( VarContext.of() );
		cs.reserveBlank();
		assertEquals( 1, cs.compile().getBlanksCount() );
		
		
		asr = (AddScriptResult)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+$count(a)+}", 0 );
		
		assertEquals( null, asr.derive( VarContext.of( ) ) );
		
		
		assertEquals( 0, asr.getLineNumber() );
		assertEquals( "{+$count(a)+}", asr.getText() );
		

		varNames = new HashSet<String>();
		asr.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "a" ) );
		
		//assertEquals( "a", asr.getAllVarNames(
		//		          VarContext.of( "a", "name" ) ).toArray( new String[ 0 ] )[ 0 ] );
		assertEquals( "a", asr.getScriptInput() );
		assertEquals( "count", asr.getScriptName() );
		assertEquals( "{+$count(a)+}", asr.getText() );
		assertFalse( asr.isRequired() );
		
		assertEquals( null, asr.derive( VarContext.of( ) ) );
		assertEquals( 0, asr.derive( VarContext.of( "a", new String[ 0 ] ) ) );
		assertEquals( 1, asr.derive( VarContext.of( "a", "name" ) ) );
		
		
		asr = (AddScriptResult)ForMLParser.INSTANCE.parseMark( 
			VarContext.of(), "{+$count(a)*+}", 0 );		
		assertTrue( asr.isRequired() );		
		try
		{
			asr.derive( VarContext.of( ) );
			fail( "expected Exception for Required not null" );
		}
		catch( VarException ve )
		{
			//expected
		}
		
		VarContext vc = VarContext.of( );
		
		//get rid of the count() script in the core library to "force" an error
		vc.getBindings( VarScope.CORE_LIBRARY ).clear();
		
		try
		{
			asr.derive( vc.set( "a", 1, VarScope.INSTANCE ) );
			fail( "expected Exception for No count Script Found" );
		}
		catch( VarException ve )
		{
			//expected
		}
		
	}
}
