package _testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import varcode.LangTest;
import varcode.buffer.ClassToStringTranslatorTest;
import varcode.buffer.TranslateBufferTest;
import varcode.context.ResolveTest;
import varcode.context.SmartScriptResolverTest;
import varcode.context.VarBindingsTest;
import varcode.context.VarContextTest;
import varcode.context.VarScopeBindingsTest;
import varcode.doc.AuthorTest;
import varcode.doc.FillInTheBlanksTest;
import varcode.doc.lib.CountIndexTest;
import varcode.doc.lib.CountTest;
import varcode.doc.lib.RowifyTest;
import varcode.doc.lib.SHA1ChecksumTest;
import varcode.doc.lib.text.CondenseMultipleBlankLinesTest;
import varcode.doc.lib.text.FirstCapsTest;
import varcode.doc.lib.text.PrintAsLiteralTest;
import varcode.doc.lib.text.RemoveAllLinesWithTest;
import varcode.doc.lib.text.SameCountTest;
import varcode.doc.lib.text.StripMarksTest;
import varcode.dom.DomTest;
import varcode.eval.Eval_JavaScriptTest;
import varcode.form.BetweenTokensTest;
import varcode.form.SeparateFormsTest;
import varcode.form.VarFormTest;
import varcode.java.JavaCaseTest;
import varcode.java.JavaTest;
import varcode.java.JavaMarkupRepoTest;
import varcode.java.JavaNamingTest;
import varcode.java.ReflectTest;
import varcode.java._JavaCaseClassNameTest;
import varcode.java._JavaCase_AllDirectivesTest;
import varcode.java.javac.InMemoryJavacTest;
import varcode.java.javac.JavaWorkspaceTest;
import varcode.markup.MarkupParserTest;
import varcode.markup.VarNameAuditTest;
import varcode.markup.bindml.BindMLCompilerTest;
import varcode.markup.bindml.BindMLFunctionalTests_AddVar;
import varcode.markup.bindml.BindMLFunctionalTests_DefineVar;
import varcode.markup.bindml.BindMLParserTest;
import varcode.markup.bindml.TestRequiredBindML;
import varcode.markup.codeml.CodeMLCompilerTest;
import varcode.markup.codeml.CodeMLFunctionalTests_AddVar;
import varcode.markup.codeml.CodeMLFunctionalTests_DefineVar;
import varcode.markup.codeml.CodeMLParserMarkTest;
import varcode.markup.codeml.CodeMLParserTest;
import varcode.markup.codeml.CodeMLStateTest;
import varcode.markup.codeml.TestRequiredCodeML;
import varcode.markup.forml.ForMLCompilerTest;
import varcode.markup.forml.ForMLParserTest;
import varcode.markup.forml.ForMLTest;
import varcode.markup.forml.FormTest;
import varcode.markup.mark.DocDirectiveTest;
import varcode.markup.mark.AddExpressionResultTest;
import varcode.markup.mark.AddFormIfExpressionTest;
import varcode.markup.mark.AddFormIfVarTest;
import varcode.markup.mark.AddFormTest;
import varcode.markup.mark.AddIfConditionTest;
import varcode.markup.mark.AddIfVarTest;
import varcode.markup.mark.AddScriptResultTest;
import varcode.markup.mark.AddVarExpressionTest;
import varcode.markup.mark.AddVarInlineTest;
import varcode.markup.mark.AddVarOneOfTest;
import varcode.markup.mark.AddVarTest;
import varcode.markup.mark.CutCommentTest;
import varcode.markup.mark.CutIfTest;
import varcode.markup.mark.CutTest;
import varcode.markup.mark.DefineVarAsExpressionResultTest;
import varcode.markup.mark.DefineVarAsFormTest;
import varcode.markup.mark.DefineVarAsScriptResultTest;
import varcode.markup.mark.DefineVarTest;
import varcode.markup.mark.EvalExpressionTest;
import varcode.markup.mark.FormFunctionalTest;
import varcode.markup.mark.ReplaceWithExpressionResultTest;
import varcode.markup.mark.ReplaceWithFormTest;
import varcode.markup.mark.ReplaceWithScriptResultTest;
import varcode.markup.mark.ReplaceWithVarTest;
import varcode.markup.mark.RunScriptTest;
import varcode.markup.repo.PathWalkTest;
import varcode.markup.repo.UrlRepoTest;

public class AllTestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite( AllTestSuite.class.getName() );
        // $JUnit-BEGIN$
	suite.addTestSuite( LangTest.class );
	suite.addTestSuite( ClassToStringTranslatorTest.class );
	suite.addTestSuite( TranslateBufferTest.class );

	suite.addTestSuite( ResolveTest.class );
	suite.addTestSuite( SmartScriptResolverTest.class );
	suite.addTestSuite( VarBindingsTest.class );
	suite.addTestSuite( VarContextTest.class );
	suite.addTestSuite( VarScopeBindingsTest.class );				
		
		
	suite.addTestSuite( AuthorTest.class );
	suite.addTestSuite( FillInTheBlanksTest.class );
	 	
	suite.addTestSuite( _JavaCase_AllDirectivesTest.class );
	suite.addTestSuite( _JavaCaseClassNameTest.class );
	suite.addTestSuite( JavaCaseTest.class );
	suite.addTestSuite( JavaTest.class );
        suite.addTestSuite( JavaMarkupRepoTest.class );
	suite.addTestSuite( JavaNamingTest.class );
	suite.addTestSuite( ReflectTest.class );
		
	suite.addTestSuite( InMemoryJavacTest.class );
	suite.addTestSuite( JavaWorkspaceTest.class );
		
	suite.addTestSuite( DomTest.class );
		
	suite.addTestSuite( MarkupParserTest.class );
	suite.addTestSuite( PathWalkTest.class );
	//suite.addTestSuite( UrlRepoTest.class );
	suite.addTestSuite( VarNameAuditTest.class );
		
		
	suite.addTestSuite( BindMLCompilerTest.class );		
	suite.addTestSuite( BindMLFunctionalTests_AddVar.class );
	suite.addTestSuite( BindMLFunctionalTests_DefineVar.class );
	suite.addTestSuite( CodeMLFunctionalTests_AddVar.class );
	suite.addTestSuite( BindMLParserTest.class );
	suite.addTestSuite( TestRequiredBindML.class );

		
	suite.addTestSuite( CodeMLCompilerTest.class );
	suite.addTestSuite( CodeMLFunctionalTests_AddVar.class );
	suite.addTestSuite( CodeMLFunctionalTests_DefineVar.class );
	suite.addTestSuite( CodeMLParserMarkTest.class );
	suite.addTestSuite( CodeMLParserTest.class );		
	suite.addTestSuite( CodeMLStateTest.class );
	suite.addTestSuite( TestRequiredCodeML.class );

        suite.addTestSuite( BetweenTokensTest.class );
	suite.addTestSuite( SeparateFormsTest.class );
		
	suite.addTestSuite( ForMLCompilerTest.class ); 
	suite.addTestSuite( ForMLParserTest.class );
	suite.addTestSuite( ForMLTest.class );
	suite.addTestSuite( FormTest.class );
	suite.addTestSuite( VarFormTest.class );
		
	suite.addTestSuite( AddExpressionResultTest.class );
	suite.addTestSuite( AddFormIfExpressionTest.class );
	suite.addTestSuite( AddFormIfVarTest.class );
	suite.addTestSuite( AddFormTest.class );
	suite.addTestSuite( AddIfConditionTest.class );
	suite.addTestSuite( AddIfVarTest.class );
	suite.addTestSuite( AddScriptResultTest.class );
	suite.addTestSuite( AddVarExpressionTest.class );
	suite.addTestSuite( AddVarInlineTest.class );
	suite.addTestSuite( AddVarOneOfTest.class );
	suite.addTestSuite( AddVarTest.class );
	
	suite.addTestSuite( CutCommentTest.class );
	suite.addTestSuite( CutIfTest.class );
	suite.addTestSuite( CutTest.class );
	
        suite.addTestSuite( DefineVarAsExpressionResultTest.class );
	suite.addTestSuite( DefineVarAsFormTest.class );
	suite.addTestSuite( DefineVarAsScriptResultTest.class );
	suite.addTestSuite( DefineVarTest.class );
		
	suite.addTestSuite( EvalExpressionTest.class );
	suite.addTestSuite( RunScriptTest.class );
	
	suite.addTestSuite( FormFunctionalTest.class );
		
	suite.addTestSuite( ReplaceWithExpressionResultTest.class );
	suite.addTestSuite( ReplaceWithFormTest.class );
	suite.addTestSuite( ReplaceWithScriptResultTest.class );
	suite.addTestSuite( ReplaceWithVarTest.class );
	
	suite.addTestSuite( DocDirectiveTest.class );
	suite.addTestSuite( Eval_JavaScriptTest.class );
	
	suite.addTestSuite( CondenseMultipleBlankLinesTest.class );
	suite.addTestSuite( PrintAsLiteralTest.class );
	suite.addTestSuite( RemoveAllLinesWithTest.class );
	suite.addTestSuite( SameCountTest.class );
	suite.addTestSuite( StripMarksTest.class );
	
	suite.addTestSuite( SHA1ChecksumTest.class );
	suite.addTestSuite( FirstCapsTest.class );
	suite.addTestSuite( CountTest.class );
	suite.addTestSuite( CountIndexTest.class );
	suite.addTestSuite( RowifyTest.class );	
	// $JUnit-END$

	return suite;
    }
}
