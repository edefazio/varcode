package varcode.markup.bindml;

import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddIfVar;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.Mark;
//import varcode.markup.mark.SetMetadata;
import varcode.markup.mark.AuthorDirective;
import junit.framework.TestCase;
import varcode.markup.bindml.BindMLCompiler;
import varcode.markup.mark.AddScriptResultIfVar;
import varcode.markup.mark.AddVar;

public class BindMLParserTest
    extends TestCase
{
    public void testFirstOpenTag()
    {
        assertEquals( "{+", BindMLCompiler.firstOpenTag( "{+" ) ); //AddVar        
        assertEquals( "{+", BindMLCompiler.firstOpenTag( "{+$" ) );  //AddScriptResult
        assertEquals( "{+", BindMLCompiler.firstOpenTag( "{+?" ) );  //AddIfVar
        assertEquals( "{{+", BindMLCompiler.firstOpenTag( "{{+" ) );  //AddForm
        assertEquals( "{{+", BindMLCompiler.firstOpenTag( "{{+?" ) );  //AddFormIf

        assertEquals( "{_+", BindMLCompiler.firstOpenTag( "{_+" ) );  //AddFormAlt
        assertEquals( "{_+", BindMLCompiler.firstOpenTag( "{_+?" ) );  //AddFormIf

        //assertEquals( "{-", BindMLCompiler.firstOpenTag( "{-" ) );  //Cut
        //assertEquals( "{#", BindMLCompiler.firstOpenTag( "{#" ) );  //DefineVar, 
        //DefineVarAsScriptResult.InstanceVar
        //DefineVarAsExpression.InstanceVar 
        assertEquals( "{$$", BindMLCompiler.firstOpenTag( "{$$" ) );  //TailorDirective
        //assertEquals( "{{#", BindMLCompiler.firstOpenTag( "{{#" ) ); //DefineForm (Instance)        	
        //assertEquals( "{_#", BindMLCompiler.firstOpenTag( "{_#" ) ); //DefineForm (Instance)

        //assertEquals( "{##", BindMLCompiler.firstOpenTag( "{##" ) );  //DefineVar.StaticVar, 
        //DefineVarAsScriptResult.StaticVar
        //DefineVarAsExpression.StaticVar 
        //assertEquals( "{{##", BindMLCompiler.firstOpenTag( "{{##" ) ); //DefineForm (Static)        	
        //assertEquals( "{_##", BindMLCompiler.firstOpenTag( "{_##" ) ); //DefineForm (Static)

        //assertEquals( "{@", BindMLCompiler.firstOpenTag( "{@" ) ); //SetMetadata
        assertEquals( "{$", BindMLCompiler.firstOpenTag( "{$" ) ); //EvalScript
    }

    public void testMatchTag()
    {
        assertEquals( "+}", BindMLCompiler.matchCloseTag( "{+" ) ); //AddVar         
        assertEquals( "+}", BindMLCompiler.matchCloseTag( "{+$" ) );  //AddScriptResult
        
        assertEquals( "+}", BindMLCompiler.matchCloseTag( "{+?" ) );  //AddIfVar
        assertEquals( "+}}", BindMLCompiler.matchCloseTag( "{{+" ) );  //AddForm
        assertEquals( "+}}", BindMLCompiler.matchCloseTag( "{{+?" ) );  //AddFormIf

        assertEquals( "+_}", BindMLCompiler.matchCloseTag( "{_+" ) );  //AddFormAlt
        assertEquals( "+_}", BindMLCompiler.matchCloseTag( "{_+?" ) );  //AddFormIf

        //assertEquals( "-}", BindMLCompiler.matchCloseTag( "{-" ) );  //Cut
        //assertEquals( "#}", BindMLCompiler.matchCloseTag( "{#" ) );  //DefineInstanceVar, DefineVarAsScriptResult
        assertEquals( "$$}", BindMLCompiler.matchCloseTag( "{$$" ) );  //TailorDirective
        //assertEquals( "#}}", BindMLCompiler.matchCloseTag( "{{#" ) ); //DefineForm (Instance)        	
        //assertEquals( "#_}", BindMLCompiler.matchCloseTag( "{_#" ) ); //DefineForm (Instance)

        //assertEquals( "##}", BindMLCompiler.matchCloseTag( "{##" ) );  //DefineStaticVar, DefineStaticVarAsScriptResult
        //assertEquals( "#}}", BindMLCompiler.matchCloseTag( "{{#" ) ); //DefineForm (Static)        	
        //assertEquals( "##_}", BindMLCompiler.matchCloseTag( "{_##" ) ); //DefineForm (Static)

        //assertEquals( "@}", BindMLCompiler.matchCloseTag( "{@" ) );
        assertEquals( ")$}", BindMLCompiler.matchCloseTag( "{$" ) );
    }

    public void testParseTags()
    {
        assertTrue( BindMLCompiler.parseMark( "{+var+}" ) instanceof AddVar ); //AddVar       
        assertTrue( BindMLCompiler.parseMark( "{+var*+}" ) instanceof AddVar ); //AddVar       
        assertTrue( BindMLCompiler.parseMark( "{+var|default+}" ) instanceof AddVar ); //AddVar       
        
        assertTrue( BindMLCompiler.parseMark( "{+$script()+}" ) instanceof AddScriptResult );  //AddScriptResult
        
        assertTrue( BindMLCompiler.parseMark( "{+?var:Add this Text+}" ) instanceof AddIfVar );  //AddIfVar
        assertTrue( BindMLCompiler.parseMark( "{+?var:$script()+}" ) instanceof AddScriptResultIfVar );  //AddIfVar

        //assertTrue( BindMLCompiler.parseMark( "{- some text -}" ) instanceof Cut );  //Cut
        //assertTrue( BindMLCompiler.parseMark( "{#a=1#}" ) instanceof DefineVar.InstanceVar );  //DefineInstanceVar, DefineVarAsScriptResult
        //assertTrue( BindMLCompiler.parseMark( "{#a:$count(a)#}" ) instanceof DefineVarAsScriptResult.InstanceVar );  //DefineVarAsScriptResult
        
        assertTrue( BindMLCompiler.parseMark( "{$$removeEmptyLines()$$}" ) instanceof AuthorDirective );  //TailorDirective

        //assertTrue( BindMLCompiler.parseMark( "{##a=1##}" ) instanceof DefineVar.StaticVar );  //DefineStaticVar, DefineStaticVarAsScriptResult		
        //assertTrue( BindMLCompiler.parseMark( "{##a:$count(blah)##}" ) instanceof DefineVarAsScriptResult.StaticVar );  //DefineStaticVar, DefineStaticVarAsScriptResult
        

        //assertTrue( BindMLCompiler.parseMark( "{@meta:data@}" ) instanceof SetMetadata );
        assertTrue( BindMLCompiler.parseMark( "{$script()$}" ) instanceof RunScript );
        

        
        assertTrue( BindMLCompiler.parseMark( "{{+:{+fieldType+} {+fieldName+}+}}" ) instanceof AddForm );  //AddForm
        assertTrue( BindMLCompiler.parseMark( "{{+?a: implements {+impl+}+}}" ) instanceof AddFormIfVar );  //AddFormIf
        assertTrue( BindMLCompiler.parseMark( "{{+?a==1: implements {+impl+}+}}" ) instanceof AddFormIfVar );  //AddFormIf

        assertTrue( BindMLCompiler.parseMark( "{_+:{+fieldType+} {+fieldName+}+_}" ) instanceof AddForm );  //AddFormAlt
        assertTrue( BindMLCompiler.parseMark( "{_+?a==1: implements {+impl+}+_}" ) instanceof AddFormIfVar );  //AddFormIf

        //assertTrue( BindMLCompiler.parseMark( "{{#assgn:{+fieldName+} = {+fieldValue+};#}}" ) instanceof DefineVarAsForm.InstanceVar ); //DefineForm (Instance)  
        //assertTrue( BindMLCompiler.parseMark( "{{##assgn:{+fieldName+} = {+fieldValue+};##}}" ) instanceof DefineVarAsForm.StaticVar ); //DefineForm (Static)
        //assertTrue( BindMLCompiler.parseMark( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) instanceof DefineVarAsForm.InstanceVar ); //DefineForm (Instance)

        

        //assertTrue( BindMLCompiler.parseMark( "{{##className:IntFormOf{+count+}##}}" ) instanceof DefineVarAsForm.StaticVar ); //DefineForm (Static)        	
        //assertTrue( BindMLCompiler.parseMark( "{_##className:IntFormOf{+count+}##_}" ) instanceof DefineVarAsForm.StaticVar ); //DefineForm (Static)		
    }

    public static final String N = System.lineSeparator();

    public void testParseRequiredFormTag()
    {
        String mark = "{{+:{+fieldType+} {+fieldName+}*+}}";
        Mark ma = BindMLCompiler.parseMark( mark );
        assertTrue( ma instanceof AddForm );
        AddForm af = (AddForm)ma;
        assertTrue( af.isRequired() );
    }

    public void testParseRequiredFormAltTag()
    {
        String mark = "{_+:{+fieldType+} {+fieldName+}*+_}";
        Mark ma = BindMLCompiler.parseMark( mark );
        assertTrue( ma instanceof AddForm );
        AddForm af = (AddForm)ma;
        assertFalse( af.isRequired() );
    }

}
