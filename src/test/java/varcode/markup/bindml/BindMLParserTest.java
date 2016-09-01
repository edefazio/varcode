package varcode.markup.bindml;

import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
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

public class BindMLParserTest 
	extends TestCase
{

	public void testParseDefineStaticVarAsExpressionResultMark()
	{
		Mark m = BindMLParser.parseMark( "{#a=((3+4))#}" );
		assertTrue( m instanceof DefineVarAsExpressionResult );
		
		DefineVarAsExpressionResult dv = (DefineVarAsExpressionResult)m;
		assertEquals( dv.getExpression(), "3+4" );
		
	}
	public void testFirstOpenTag()
	{
		
        assertEquals( "{+", BindMLParser.firstOpenTag( "{+" ) ); //AddVar
        assertEquals( "{+", BindMLParser.firstOpenTag( "{+((" ) );  //AddExpressionResult
        assertEquals( "{+", BindMLParser.firstOpenTag( "{+$" ) );  //AddScriptResult
        assertEquals( "{+", BindMLParser.firstOpenTag( "{+?" ) );  //AddIfVar
        assertEquals( "{{+", BindMLParser.firstOpenTag( "{{+" ) );  //AddForm
        assertEquals( "{{+", BindMLParser.firstOpenTag( "{{+?" ) );  //AddFormIf
    	
        assertEquals( "{_+", BindMLParser.firstOpenTag( "{_+" ) );  //AddFormAlt
        assertEquals( "{_+", BindMLParser.firstOpenTag( "{_+?" ) );  //AddFormIf
    	
        assertEquals( "{-", BindMLParser.firstOpenTag( "{-" ) );  //Cut
        assertEquals( "{#", BindMLParser.firstOpenTag( "{#" ) );  //DefineVar, 
                                                                  //DefineVarAsScriptResult.InstanceVar
                                                                  //DefineVarAsExpression.InstanceVar 
        assertEquals( "{$$", BindMLParser.firstOpenTag( "{$$" ) );  //TailorDirective
        assertEquals( "{{#", BindMLParser.firstOpenTag( "{{#" ) ); //DefineForm (Instance)        	
        assertEquals( "{_#", BindMLParser.firstOpenTag( "{_#" ) ); //DefineForm (Instance)
        
        assertEquals( "{##", BindMLParser.firstOpenTag( "{##" ) );  //DefineVar.StaticVar, 
                                                                    //DefineVarAsScriptResult.StaticVar
                                                                    //DefineVarAsExpression.StaticVar 
        assertEquals( "{{##", BindMLParser.firstOpenTag( "{{##" ) ); //DefineForm (Static)        	
        assertEquals( "{_##", BindMLParser.firstOpenTag( "{_##" ) ); //DefineForm (Static)
                 
        assertEquals( "{@", BindMLParser.firstOpenTag( "{@" ) ); //SetMetadata
        assertEquals( "{$", BindMLParser.firstOpenTag( "{$" ) ); //EvalScript
        assertEquals( "{((", BindMLParser.firstOpenTag( "{((" ) ); //EvalExpression
	}
	
	public void testMatchTag()
	{
        assertEquals( "+}", BindMLParser.matchCloseTag("{+" ) ); //AddVar         
        assertEquals( "+}", BindMLParser.matchCloseTag( "{+$" ) );  //AddScriptResult
        assertEquals( "+}", BindMLParser.matchCloseTag( "{+((" ) );  //AddExpressionResult
        assertEquals( "+}", BindMLParser.matchCloseTag( "{+?" ) );  //AddIfVar
        assertEquals( "+}}", BindMLParser.matchCloseTag( "{{+" ) );  //AddForm
        assertEquals( "+}}", BindMLParser.matchCloseTag( "{{+?" ) );  //AddFormIf
    	
        assertEquals( "+_}", BindMLParser.matchCloseTag( "{_+" ) );  //AddFormAlt
        assertEquals( "+_}", BindMLParser.matchCloseTag( "{_+?" ) );  //AddFormIf
    	
        assertEquals( "-}", BindMLParser.matchCloseTag( "{-" ) );  //Cut
        assertEquals( "#}", BindMLParser.matchCloseTag( "{#" ) );  //DefineInstanceVar, DefineVarAsScriptResult
        assertEquals( "$$}", BindMLParser.matchCloseTag( "{$$" ) );  //TailorDirective
        assertEquals( "#}}", BindMLParser.matchCloseTag( "{{#" ) ); //DefineForm (Instance)        	
        assertEquals( "#_}", BindMLParser.matchCloseTag( "{_#" ) ); //DefineForm (Instance)
        
        assertEquals( "##}", BindMLParser.matchCloseTag( "{##" ) );  //DefineStaticVar, DefineStaticVarAsScriptResult
        assertEquals( "#}}", BindMLParser.matchCloseTag( "{{#" ) ); //DefineForm (Static)        	
        assertEquals( "##_}", BindMLParser.matchCloseTag( "{_##" ) ); //DefineForm (Static)
        
        assertEquals( "@}", BindMLParser.matchCloseTag( "{@" ) );
        assertEquals( ")$}", BindMLParser.matchCloseTag( "{$" ) );
        assertEquals( "))}", BindMLParser.matchCloseTag( "{((" ) );
	}
	
	
	
	public void testParseTags()
	{
		assertTrue( BindMLParser.parseMark("{+name+}" ) instanceof AddVarIsExpression ); //AddVar       
		assertTrue( BindMLParser.parseMark("{+name:(( name.length() > 3 ))+}" ) instanceof AddVarIsExpression ); //AddVar
		assertTrue( BindMLParser.parseMark("{+$script()+}" ) instanceof AddScriptResult );  //AddScriptResult
		assertTrue( BindMLParser.parseMark("{+((a + b + c))+}" ) instanceof AddExpressionResult );  //AddExpressionResult
		assertTrue( BindMLParser.parseMark("{+?vari:addThis+}" ) instanceof AddTextIfVar);  //AddIfVar
		
		    	
		assertTrue( BindMLParser.parseMark("{- some text -}" ) instanceof Cut );  //Cut
		assertTrue( BindMLParser.parseMark("{#a=1#}" ) instanceof DefineVar.InstanceVar );  //DefineInstanceVar, DefineVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{#a:$count(a)#}" ) instanceof DefineVarAsScriptResult.InstanceVar );  //DefineVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{#c:(( Math.sqrt( a * a + b * b ) ))#}" ) instanceof DefineVarAsExpressionResult.InstanceVar );  //TailorDirective
		assertTrue( BindMLParser.parseMark("{#c=(( Math.sqrt( a * a + b * b ) ))#}" ) instanceof DefineVarAsExpressionResult.InstanceVar );  //TailorDirective
		
		assertTrue( BindMLParser.parseMark("{$$removeEmptyLines()$$}" ) instanceof DocDirective );  //TailorDirective
		
		assertTrue( BindMLParser.parseMark("{##a=1##}" ) instanceof DefineVar.StaticVar);  //DefineStaticVar, DefineStaticVarAsScriptResult		
		assertTrue( BindMLParser.parseMark("{##a:$count(blah)##}" ) instanceof DefineVarAsScriptResult.StaticVar );  //DefineStaticVar, DefineStaticVarAsScriptResult
		assertTrue( BindMLParser.parseMark("{##c:(( Math.sqrt( a * a + b * b ) ))##}" ) instanceof DefineVarAsExpressionResult.StaticVar );
		assertTrue( BindMLParser.parseMark("{##c=(( Math.sqrt( a * a + b * b ) ))##}" ) instanceof DefineVarAsExpressionResult.StaticVar );
		
		
		assertTrue( BindMLParser.parseMark("{@meta:data@}" ) instanceof SetMetadata );
		assertTrue( BindMLParser.parseMark("{$script()$}" ) instanceof RunScript );
		assertTrue( BindMLParser.parseMark("{(( a + b ))}" ) instanceof EvalExpression );
		
		assertTrue( BindMLParser.parseMark("{{+:{+fieldType+} {+fieldName+}+}}" ) instanceof AddForm );  //AddForm
		assertTrue( BindMLParser.parseMark("{{+?a==1: implements {+impl+}+}}" ) instanceof AddFormIfVar );  //AddFormIf
		    	
		assertTrue( BindMLParser.parseMark("{_+:{+fieldType+} {+fieldName+}+_}" ) instanceof AddForm );  //AddFormAlt
		assertTrue( BindMLParser.parseMark("{_+?a==1: implements {+impl+}+_}" ) instanceof AddFormIfVar );  //AddFormIf
		
		assertTrue( BindMLParser.parseMark("{{#assgn:{+fieldName+} = {+fieldValue+};#}}" ) instanceof DefineVarAsForm.InstanceVar); //DefineForm (Instance)  
		assertTrue( BindMLParser.parseMark("{{##assgn:{+fieldName+} = {+fieldValue+};##}}" ) instanceof DefineVarAsForm.StaticVar); //DefineForm (Static)
		assertTrue( BindMLParser.parseMark("{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) instanceof DefineVarAsForm.InstanceVar); //DefineForm (Instance)
		        
		assertTrue( BindMLParser.parseMark( "{+(( Math.sqrt( a * a + b * b ) ))+}" ) instanceof AddExpressionResult );
        
		assertTrue( BindMLParser.parseMark("{{##className:IntFormOf{+count+}##}}" ) instanceof DefineVarAsForm.StaticVar ); //DefineForm (Static)        	
		assertTrue( BindMLParser.parseMark("{_##className:IntFormOf{+count+}##_}" ) instanceof DefineVarAsForm.StaticVar ); //DefineForm (Static)		
	}
	
	public static final String N = System.lineSeparator();
	
	public void testParseRequiredFormTag()
	{
		String mark = "{{+:{+fieldType+} {+fieldName+}*+}}";
		Mark ma = BindMLParser.INSTANCE.of( mark );
		assertTrue( ma instanceof AddForm );
		AddForm af = (AddForm)ma;
		assertTrue( af.isRequired() );		
	}
	
	public void testParseRequiredFormAltTag()
	{
		String mark = "{_+:{+fieldType+} {+fieldName+}*+_}";
		Mark ma = BindMLParser.INSTANCE.of( mark );
		assertTrue( ma instanceof AddForm );
		AddForm af = (AddForm)ma;
		assertFalse( af.isRequired() );		
	}
	
	/*
	public static class PrefixLines
		implements VarScript
	{
		private final Prefix prefix;
		
		public PrefixLines( String prefix )
		{
			this.prefix = new Prefix( prefix );
		}
		
		@Override
		public ScriptInputParser getInputParser() 
		{
			return VarScript.STRING_INPUT;
		}

		@Override
		public Object eval( VarContext context, String input ) 
		{
			return this.prefix.eval(context, input)
		}
		
	}
	*/
	
}
