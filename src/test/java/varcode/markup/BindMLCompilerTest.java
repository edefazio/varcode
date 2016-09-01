package varcode.markup.bindml;

import varcode.context.VarBindException.NullResult;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.doc.Author;
import varcode.doc.DocState;
import varcode.dom.Dom;
import varcode.eval.EvalException;
import varcode.markup.MarkupException;
import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfExpression;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddScriptResultIfExpression;
import varcode.markup.mark.AddTextIfVar;
import varcode.markup.mark.AddVarIsExpression;
import varcode.markup.mark.Cut;
import varcode.markup.mark.DefineVar;
import varcode.markup.mark.DefineVarAsExpressionResult;
import varcode.markup.mark.DefineVarAsForm;
import varcode.markup.mark.DefineVarAsScriptResult;
import varcode.markup.mark.EvalExpression;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.Mark;
import varcode.markup.mark.SetMetadata;
import varcode.markup.mark.DocDirective;
import junit.framework.TestCase;

public class BindMLCompilerTest 
	extends TestCase
{
	private static Mark getOnlyMark( Dom markup )
	{
		return markup.getMarks()[ 0 ];
	}
	
	public void testAddScriptResultIfExpression()
	{
		String mark = "{+?((captureInput==true)):$capture(params)+}";
		Mark theMark = BindML.parseMark( mark );
	    assertTrue( theMark instanceof AddScriptResultIfExpression );
	    AddScriptResultIfExpression mr = (AddScriptResultIfExpression) theMark;
	    assertEquals( -1, mr.getLineNumber() );
	    assertEquals( mark, mr.getText() );
	    assertEquals( "params", mr.getScriptInput() );
	    assertEquals( "capture", mr.getScriptName() );	    
	}
	
	public void testAddFormIfExpression()
	{
		String mark = "{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}";
		Mark theMark = BindML.parseMark( mark );
		AddFormIfExpression afie = (AddFormIfExpression)theMark;
		assertEquals( " logLevel > debug ", afie.getExpression() );
		assertEquals( "LOG.debug({+a+} + {+b+});", afie.getForm().getText() );
	}
	
	public void testAddFormIfExpression_Alt()
	{
		String mark = "{_+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+_}";
		Mark theMark = BindML.parseMark( mark );
		AddFormIfExpression afie = (AddFormIfExpression)theMark;
		assertEquals( " logLevel > debug ", afie.getExpression() );
		assertEquals( "LOG.debug({+a+} + {+b+});", afie.getForm().getText() );
	}
	
	public void testOpenMark()
	{
		try
		{
			BindMLCompiler.fromString( "{#a = 4" );
			fail( "Expected Exception for Open Mark" );
		}
		catch( MarkupException e )
		{
			//expected
			e.printStackTrace();
		}
	}
	public void testDefineVarAsExpressionResult()
	{
		Dom d = BindML.compile( "{#a=((1 + 2))#}{+a+}" );
		assertEquals( "3", Author.code( d ) );		
	}
	
	public void testDefineStaticVarAsExpressionResult()
	{
		Dom d = BindML.compile( "{##a=((1 + 2))##}{+a+}" );
		assertEquals( "3", Author.code( d ) );		
	}
	/** 
	 * these are essentially "functional tests"
	 * verifying we can parse to dom, then taior code and get what we expect
	 * when we tailor the result 
	 */
	public void testAddMarkEval()
	{
		                        //mark      expected  key /value pairs
		//assertDeriveMarkEquals( "{+name+}", "Eric", "name", "Eric" );
		assertDeriveMarkEquals( "{+name*+}", "Eric", "name", "Eric" );		
		assertDeriveMarkEquals( "{+name|default+}", "Eric", "name", "Eric" );
		assertDeriveMarkEquals( "{+name|default+}", "default" );
		assertDeriveMarkEquals( "{+(( 3 + 5 ))+}", "8" );
		
		try
		{
			assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))+}", "theValue", "name", "a" );
			fail( "Expected exception for var not valid " ); 
		}
		catch( VarBindException be )
		{
			//expected
		}
		
		assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))+}", "theValue", "name", "theValue" );
		
		assertDeriveMarkEquals( "{+name:(( name.length() > 2 ))|defaultValue+}", "defaultValue" );
		
		
		try
		{
			assertDeriveMarkEquals( "{+(( A + B | 0 ))+}", "" );
			fail("Expected Exception for not bound A and B vars");
		}
		catch( EvalException ee )
		{ /*expected*/ }		
		try
		{
			assertDeriveMarkEquals( "{+(( A + B | 0 ))+}", "", "A", "1" );
			fail("Expected Exception for not bound B vars");
		}
		catch( EvalException ee )
		{ /*expected*/ }
		
		// FYI we do this |0 'cause it tricks the JS Engine JIT to convert numbers to ints
		// since all numbers in JS are decimal
		assertDeriveMarkEquals( "{+(( A + B | 0  ))+}", "3", "A", 1, "B", 2 );
		
		
		assertDeriveMarkEquals( "{+$count(a)+}", "" );
		assertDeriveMarkEquals( "{+$count(a)+}", "0", "a", new String[0] );
		assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", new String[] {"s"} );
		assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", "s" );
		try
		{
			assertDeriveMarkEquals( "{+$count(a)*+}", "" );
			fail( "expected Exception" );
		}
		catch( NullResult ee )
		{ /*expected*/ }

		
		assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );
		
		assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
		assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );
		
		
		//fprints nothing if neither value
		assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};+}}", "" );
		
		
		assertDeriveMarkEquals( "{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", "" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"airman", 
			"name", "airman" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"sargent", 
			"name", "sargent" );
		
		assertDeriveMarkEquals( 
			"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
			"airman first class", 
			"name", "airman first class" );
		
		try
		{
			assertDeriveMarkEquals( 
				"{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}", 
				"", 
				"name", "havent got one" );
			fail( "Expected Exception" );
		}
		catch( EvalException ee )
		{
			//expected
		}
		
		
		assertDeriveMarkEquals( 
				"{{+:{+fieldType+} {+fieldName+};+}}", 
				"int x;", 
				"fieldType", int.class, "fieldName", "x" );
		
		assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+}; +}}", 
				"int x; String y; ", 
				"fieldType", new Object[]{int.class, "String"}, 
				"fieldName", new Object[]{"x", "y"} );
		
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType*+} {+fieldName+};+}}", "" );
			fail( "expected Exception" );
		}
		catch( NullVar rbn )
		{ /*expected*/ }
		
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName*+};+}}", "" );
			fail( "expected Exception" );
		}
		catch( NullVar rbn )
		{ /*expected*/ }
		try
		{
			assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};*+}}", "" );
			fail( "expected Exception" );
		}
		catch( NullResult rr )
		{ /*expected*/ }
		//assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );
		
	}
	
	public void assertDeriveMarkEquals( String mark, String expected, Object...keyValuePairs )
	{
		assertEquals( expected, Author.code( BindML.compile( mark ), keyValuePairs ) ); 
	}
	
	public void assertVarContextUpdate( 
		String mark, String varName, Object expected, Object...keyValuePairs )
	{
		VarContext vc = VarContext.of( keyValuePairs );
		Author.code( BindML.compile( mark ), vc );
		assertEquals( expected, vc.resolveVar( varName ) );
	}
	
	public void testDeriveMarkFunctional()
	{
		assertDeriveMarkEquals( "{- some text -}", "" );
		
		//  mark,  varName, expected, keyValuePairs )
		assertVarContextUpdate( "{#a=1#}", "a", "1" );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", null );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", 0, "a", new String[0] );
		assertVarContextUpdate( "{#a:$count(a)#}", "a", 1, "a", "A" );
		
		assertVarContextUpdate( "{##a=1##}", "a", "1" );
		
		assertVarContextUpdate( "{##a=1##}", "a", "1" );
		
		
		assertVarContextUpdate( "{##A:3##}{##B:5##}{##a:(( A + B ))##}", "a", "35");
		
		
		assertVarContextUpdate( "{##a:(( 3 + 5 | 0 ))##}", "a", 8 );
		assertVarContextUpdate( "{##a=(( 3 + 5 | 0 ))##}", "a", 8 );
	}
	
	public void testMetadata()
	{
		VarContext vc = VarContext.of();
		DocState ts = Author.bind( BindML.compile( "{@meta:data@}" ), vc );
		
		assertEquals("data", ts.getContext().resolveVar( "meta" ) );		
	}
	
	public void testCompileAllMarks()
	{
		assertTrue( getOnlyMark( BindML.compile( "{+name+}" ) ) instanceof AddVarIsExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name*+}" ) ) instanceof AddVarIsExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name|default+}" ) ) instanceof AddVarIsExpression );
		assertTrue( getOnlyMark( BindML.compile( "{+name:(( ['airman', 'sargent', 'airman first class'].indexOf(name) >= 0 ))+}" ) ) instanceof AddVarIsExpression );
		
		assertTrue( getOnlyMark( BindML.compile( "{+((3 + 5))+}" ) ) instanceof AddExpressionResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{#c:((3 + 5))#}" ) ) instanceof DefineVarAsExpressionResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#c=((3 + 5))#}" ) ) instanceof DefineVarAsExpressionResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{##c:((3 + 5))##}" ) ) instanceof DefineVarAsExpressionResult.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##c=((3 + 5))##}" ) ) instanceof DefineVarAsExpressionResult.StaticVar );
		
		
		assertTrue( getOnlyMark( BindML.compile( "{+$script()+}" ) ) instanceof AddScriptResult );
		assertTrue( getOnlyMark( BindML.compile( "{+$script()*+}" ) ) instanceof AddScriptResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{+$script(params)+}" ) ) instanceof AddScriptResult );
		assertTrue( getOnlyMark( BindML.compile( "{+$script(params)*+}" ) ) instanceof AddScriptResult );
		
		assertTrue( getOnlyMark( BindML.compile( "{+?variable:addThis+}" ) ) instanceof AddTextIfVar );
		assertTrue( getOnlyMark( BindML.compile( "{- some text -}" ) ) instanceof Cut );
		
		assertTrue( getOnlyMark( BindML.compile( "{#a=1#}" ) ) instanceof DefineVar.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a:1#}" ) ) instanceof DefineVar.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a:$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{#a=$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{$$removeEmptyLines()$$}" ) ) instanceof DocDirective );
		assertTrue( getOnlyMark( BindML.compile( "{##a=1##}" ) ) instanceof DefineVar.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a:1##}" ) ) instanceof DefineVar.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a:$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{##a=$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{@meta:data@}" ) ) instanceof SetMetadata );
		assertTrue( getOnlyMark( BindML.compile( "{@meta=data@}" ) ) instanceof SetMetadata );
		
		assertTrue( getOnlyMark( BindML.compile( "{$script()$}" ) ) instanceof RunScript );
		assertTrue( getOnlyMark( BindML.compile( "{(( print(3) ))}" ) ) instanceof EvalExpression );
		
		assertTrue( getOnlyMark( BindML.compile( "{{+:{+fieldType+} {+fieldName+}+}}" ) ) instanceof AddForm );
		assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );
		assertTrue( getOnlyMark( BindML.compile( "{_+:{+fieldType+} {+fieldName+}+_}" ) ) instanceof AddForm );
		assertTrue( getOnlyMark( BindML.compile( "{_+?a==1: implements {+impl+}+_}" ) ) instanceof AddFormIfVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{#assgn:{+fieldName+} = {+fieldValue+};#}}" ) ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{{##assgn:{+fieldName+} = {+fieldValue+};##}}" ) ) instanceof DefineVarAsForm.StaticVar );
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) ) instanceof DefineVarAsForm.InstanceVar );

		assertTrue( getOnlyMark( BindML.compile( "{{#assgn={+fieldName+} = {+fieldValue+};#}}") ) instanceof DefineVarAsForm.InstanceVar );   
		assertTrue( getOnlyMark( BindML.compile( "{{##assgn={+fieldName+} = {+fieldValue+};##}}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}") ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{_##assgn:{+fieldName+} = {+fieldValue+};##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{_#assgn={+fieldName+} = {+fieldValue+};#_}") ) instanceof DefineVarAsForm.InstanceVar );
		assertTrue( getOnlyMark( BindML.compile( "{_##assgn={+fieldName+} = {+fieldValue+};##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{##className:IntFormOf{+count+}##}}") ) instanceof DefineVarAsForm.StaticVar );         	
		assertTrue( getOnlyMark( BindML.compile( "{_##className:IntFormOf{+count+}##_}") ) instanceof DefineVarAsForm.StaticVar );
		
		assertTrue( getOnlyMark( BindML.compile( "{{##className=IntFormOf{+count+}##}}") ) instanceof DefineVarAsForm.StaticVar );        	
		assertTrue( getOnlyMark( BindML.compile( "{_##className=IntFormOf{+count+}##_}") ) instanceof DefineVarAsForm.StaticVar );		
	}
	
}
