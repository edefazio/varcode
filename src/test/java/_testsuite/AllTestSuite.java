package _testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;
import use.java.lang.chap1.AuthorNewCode_Describe;
import use.java.lang.chap1.TailorExistingCode_Describe;
import howto.java.author.detail._1_Author_class_enum_interface;
import howto.java.macro.AutoDto;
import tutorial.varcode.chapx.appendix._1_AdHocInvoke;
import tutorial.varcode.chapx.appendix.Chap1_ModelDefaultMethods;
import tutorial.varcode.chapx.appendix.CodeMLMarkupModelTest;

import varcode.LangTest;
import varcode.context.ResolveTest;
import varcode.context.SmartScriptResolverTest;
import varcode.context.VarBindingsTest;
import varcode.context.VarContextTest;
import varcode.context.VarScopeBindingsTest;
import varcode.context.eval.Eval_JavaScriptTest;
import varcode.doc.AuthorTest;
import varcode.doc.FillInTheBlanksTest;
import varcode.doc.dom.DomTest;
import varcode.doc.form.BetweenTokensTest;
import varcode.doc.form.SeparateFormsTest;
import varcode.doc.form.VarFormTest;
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
import varcode.doc.translate.ClassToStringTranslatorTest;
import varcode.doc.translate.TranslateBufferTest;
import varcode.java.JavaCaseTest;
import varcode.java.JavaNamingTest;
import varcode.java._JavaTest;
import varcode.java._JavaCaseClassNameTest;
import varcode.java.MOVE_THIS_TO_JAVA_JavaCase_AllDirectivesTest;
import varcode.java.adhoc.WorkspaceTest;
import varcode.java.lang._annotationTest;
import varcode.java.lang._argumentsTest;
import varcode.java.lang._bindNestTest;
import varcode.java.lang._classTest;
import varcode.java.lang._codeTest;
import varcode.java.lang._constructorTest;
import varcode.java.lang._constructorsTest;
import varcode.java.lang._doTest;
import varcode.java.lang._enumTest;
import varcode.java.lang._extendsTest;
import varcode.java.lang._fieldsTest;
import varcode.java.lang._forTest;
import varcode.java.lang._ifTest;
import varcode.java.lang._implementsTest;
import varcode.java.lang._importsTest;
import varcode.java.lang._interfaceTest;
import varcode.java.lang._javadocTest;
import varcode.java.lang._methodsTest;
import varcode.java.lang._modifiersTest;
import varcode.java.lang._packageTest;
import varcode.java.lang._parametersTest;
import varcode.java.lang._staticBlockTest;
import varcode.java.lang._threadTest;
import varcode.java.lang._throwsTest;
import varcode.java.lang._tryTest;
import varcode.java.lang._whileTest;
import varcode.java.macro._autoDtoTest;
import varcode.java.macro._autoEnumTest;
import varcode.java.macro._autoExternalizableTest;
import varcode.java.lang.minTest;
import varcode.java.ast.JavaASTParserTest;
import varcode.java.load.JavaTailorTest;
import varcode.java.lang.JavaMetaLangCompilerTest;
import varcode.java.load.lang.JavaMetaLangLoaderNestedTest;
import varcode.java.load.lang.JavaMetaLangLoaderTest;
import varcode.java.load.lang._LoadTest;
import varcode.java.lang._fieldTest;
import varcode.java.lang._methodTest;
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
import varcode.markup.mark.DocDirectiveTest;
import varcode.markup.mark.EvalExpressionTest;
import varcode.markup.mark.FormFunctionalTest;
import varcode.markup.mark.ReplaceWithExpressionResultTest;
import varcode.markup.mark.ReplaceWithFormTest;
import varcode.markup.mark.ReplaceWithScriptResultTest;
import varcode.markup.mark.ReplaceWithVarTest;
import varcode.markup.mark.RunScriptTest;
import varcode.source.JavaMarkupRepoTest;
import varcode.source.PathWalkTest;

public class AllTestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite( AllTestSuite.class.getName() );
        // $JUnit-BEGIN$
        suite.addTestSuite(AuthorNewCode_Describe.class );
        suite.addTestSuite(TailorExistingCode_Describe.class );
        
        suite.addTestSuite(_1_Author_class_enum_interface.class );
        suite.addTestSuite(AutoDto.class );
        
        suite.addTestSuite(_1_AdHocInvoke.class );
        
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
	 	
        suite.addTestSuite(MOVE_THIS_TO_JAVA_JavaCase_AllDirectivesTest.class );
        suite.addTestSuite( _JavaCaseClassNameTest.class );
        suite.addTestSuite( JavaCaseTest.class );
        suite.addTestSuite(_JavaTest.class );
        suite.addTestSuite( JavaMarkupRepoTest.class );
        suite.addTestSuite( JavaNamingTest.class );
		
        //suite.addTestSuite( AdHocJavacTest.class );
        suite.addTestSuite( WorkspaceTest.class );
        
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

        suite.addTestSuite( _annotationTest.class );
        suite.addTestSuite( _argumentsTest.class );
        suite.addTestSuite( _bindNestTest.class );
        
        suite.addTestSuite( _classTest.class );
        suite.addTestSuite( _codeTest.class );
        suite.addTestSuite( _constructorTest.class );
        suite.addTestSuite( _constructorsTest.class );
        suite.addTestSuite( _doTest.class );
        suite.addTestSuite( _enumTest.class );                
        suite.addTestSuite( _extendsTest.class );        
        suite.addTestSuite( _fieldTest.class ); 
        suite.addTestSuite( _fieldsTest.class );
        suite.addTestSuite( _forTest.class );
        suite.addTestSuite( _ifTest.class );
        
        suite.addTestSuite( _implementsTest.class );
        suite.addTestSuite( _importsTest.class );
        suite.addTestSuite( _interfaceTest.class );        
        suite.addTestSuite( _javadocTest.class );
        
        suite.addTestSuite( _methodTest.class );
        suite.addTestSuite( _methodsTest.class );
        suite.addTestSuite( _modifiersTest.class );
        
        suite.addTestSuite( _packageTest.class );
        suite.addTestSuite( _parametersTest.class );
        suite.addTestSuite( _staticBlockTest.class );
        suite.addTestSuite( _threadTest.class );
        suite.addTestSuite( _throwsTest.class );        
        suite.addTestSuite( _tryTest.class ); 
        suite.addTestSuite( _whileTest.class );
        
        suite.addTestSuite( minTest.class );
                
        suite.addTestSuite(_autoDtoTest.class );
        suite.addTestSuite(_autoEnumTest.class );
        suite.addTestSuite(_autoExternalizableTest.class );
                
        //Functional / Integration Tests
        suite.addTestSuite( JavaASTParserTest.class );        
        suite.addTestSuite(JavaMetaLangCompilerTest.class );
        
        suite.addTestSuite( JavaTailorTest.class );
        suite.addTestSuite(JavaMetaLangLoaderNestedTest.class );
        suite.addTestSuite(JavaMetaLangLoaderTest.class );
        suite.addTestSuite( _LoadTest.class );
        
        suite.addTestSuite( Chap1_ModelDefaultMethods.class );
        suite.addTestSuite( CodeMLMarkupModelTest.class );
        // $JUnit-END$
        return suite;
    }
}
