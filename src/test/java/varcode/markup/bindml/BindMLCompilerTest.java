package varcode.markup.bindml;

import varcode.context.VarBindException.NullResult;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.author.Author;
import varcode.markup.Template;

import varcode.markup.MarkupException;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddIfVar;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.Mark;

import varcode.markup.mark.AuthorDirective;
import junit.framework.TestCase;
import varcode.markup.mark.AddScriptResultIfVar;

public class BindMLCompilerTest
    extends TestCase
{
    private static Mark getOnlyMark( Template markup )
    {
        return markup.getMarks()[ 0 ];
    }

    public void testSimpleMark()
    {
        Mark m = BindMLCompiler.parseMark( "{+a+}" );
        System.out.println( m.getClass() );
        System.out.println( m );
    }

    public void testAddScriptResultIfVar()
    {
        String mark = "{+?env:$>(input)+}";
        Mark m = BindMLCompiler.parseMark( mark );
        assertTrue( m instanceof AddScriptResultIfVar );

        AddScriptResultIfVar asr = (AddScriptResultIfVar)m;

        assertEquals( "env", asr.getVarName() );
        assertEquals( ">", asr.getVarScriptName() );
        assertEquals( "input", asr.getVarScriptInput() );
        assertEquals( null, asr.getTargetValue() );
        //String s = Compose.asString( d, "env", "YES" );
        //assertEquals( "    YES", s );

        String mark2 = "{+?env==test:$>(input)+}";
        Mark m2 = BindMLCompiler.parseMark( mark2 );
        assertTrue( m2 instanceof AddScriptResultIfVar );
        asr = (AddScriptResultIfVar)m2;

        assertEquals( "env", asr.getVarName() );
        assertEquals( ">", asr.getVarScriptName() );
        assertEquals( "input", asr.getVarScriptInput() );
        assertEquals( "test", asr.getTargetValue() );
    }

    public void testAddForm()
    {
        String form = "{{+:{+type+} {+value+}, +}}";
        Mark m = BindMLCompiler.parseMark( form );
        assertTrue( m instanceof AddForm );
        AddForm af = (AddForm)m;
        assertEquals( "", af.derive( VarContext.of() ) );
        assertEquals( "int 3", af.derive( VarContext.of( "type", int.class, "value", 3 ) ) );
        assertEquals( "int 3, java.lang.String 4", af.derive(
            VarContext.of( "type", new Class[]{  int.class, String.class}, 
                "value", new Object[] { 3, 4 } ) ) );
    }

    public void testOpenMark()
    {
        try
        {
            BindML.compile("{+a = 4" );
            fail( "Expected Exception for Open Mark" );
        }
        catch( MarkupException e )
        {
            //expected
            e.printStackTrace();
        }
    }

    /**
     * these are essentially "functional tests" verifying we can parse to dom,
     * then taior code and get what we expect when we tailor the result
     */
    public void testAddMarkEval()
    {
        //mark      expected  key /value pairs
        //assertDeriveMarkEquals( "{+name+}", "Eric", "name", "Eric" );
        assertDeriveMarkEquals( "{+name*+}", "Eric", "name", "Eric" );
        assertDeriveMarkEquals( "{+name|default+}", "Eric", "name", "Eric" );
        assertDeriveMarkEquals( "{+name|default+}", "default" );

        // FYI we do this |0 'cause it tricks the JS Engine JIT to convert numbers to ints
        // since all numbers in JS are decimal
        //assertDeriveMarkEquals( "{+(( A + B | 0  ))+}", "3", "A", 1, "B", 2 );
        assertDeriveMarkEquals( "{+$count(a)+}", "" );
        assertDeriveMarkEquals( "{+$count(a)+}", "0", "a", new String[ 0 ] );
        assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", new String[]
        {
            "s"
        } );
        assertDeriveMarkEquals( "{+$count(a)+}", "1", "a", "s" );
        try
        {
            assertDeriveMarkEquals( "{+$count(a)*+}", "" );
            fail( "expected Exception" );
        }
        catch( NullResult ee )
        {
            /*expected*/ }

        assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
        assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
        assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );

        assertDeriveMarkEquals( "{+?a:writeThis+}", "" );
        assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "anythingNonNull" );
        assertDeriveMarkEquals( "{+?a:writeThis+}", "writeThis", "a", "" );

        //fprints nothing if neither value
        assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};+}}", "" );

        assertDeriveMarkEquals(
            "{{+:{+fieldType+} {+fieldName+};+}}",
            "int x;",
            "fieldType", int.class, "fieldName", "x" );

        assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+}; +}}",
            "int x; String y; ",
            "fieldType", new Object[]
            {
                int.class, "String"
            },
            "fieldName", new Object[]
            {
                "x", "y"
            } );

        try
        {
            assertDeriveMarkEquals( "{{+:{+fieldType*+} {+fieldName+};+}}", "" );
            fail( "expected Exception" );
        }
        catch( NullVar rbn )
        {
            /*expected*/ }

        try
        {
            assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName*+};+}}", "" );
            fail( "expected Exception" );
        }
        catch( NullVar rbn )
        {
            /*expected*/ }
        try
        {
            assertDeriveMarkEquals( "{{+:{+fieldType+} {+fieldName+};*+}}", "" );
            fail( "expected Exception" );
        }
        catch( NullResult rr )
        {
            /*expected*/
        }
        //assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );

    }

    public void assertDeriveMarkEquals( String mark, String expected,
        Object... keyValuePairs )
    {
        assertEquals( expected, Author.toString( BindML.compile( mark ), keyValuePairs ) );
    }

    public void assertVarContextUpdate(
        String mark, String varName, Object expected, Object... keyValuePairs )
    {
        VarContext vc = VarContext.of( keyValuePairs );
        Author.toString( BindML.compile( mark ), vc );
        assertEquals( expected, vc.resolveVar( varName ) );
    }


    /*
    public void testMetadata()
    {
        VarContext vc = VarContext.of();
        AuthorState ts = Author.toState( BindML.compile( "{@meta:data@}" ), vc );

        assertEquals( "data", ts.getContext().resolveVar( "meta" ) );
    }
    */

    public void testCompileAllMarks()
    {

        assertTrue( getOnlyMark( BindML.compile( "{+$script()+}" ) ) instanceof AddScriptResult );
        assertTrue( getOnlyMark( BindML.compile( "{+$script()*+}" ) ) instanceof AddScriptResult );

        assertTrue( getOnlyMark( BindML.compile( "{+$script(params)+}" ) ) instanceof AddScriptResult );
        assertTrue( getOnlyMark( BindML.compile( "{+$script(params)*+}" ) ) instanceof AddScriptResult );

        assertTrue( getOnlyMark( BindML.compile( "{+?variable:addThis+}" ) ) instanceof AddIfVar );
        assertTrue( getOnlyMark( BindML.compile( "{+?variable:$script()+}" ) ) instanceof AddScriptResultIfVar );
        //assertTrue( getOnlyMark( BindML.compile( "{- some text -}" ) ) instanceof Cut );

//        assertTrue( getOnlyMark( BindML.compile( "{#a=1#}" ) ) instanceof DefineVar.InstanceVar );
 //       assertTrue( getOnlyMark( BindML.compile( "{#a:1#}" ) ) instanceof DefineVar.InstanceVar );
  //      assertTrue( getOnlyMark( BindML.compile( "{#a:$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
   //     assertTrue( getOnlyMark( BindML.compile( "{#a=$count(a)#}" ) ) instanceof DefineVarAsScriptResult.InstanceVar );
        assertTrue( getOnlyMark( BindML.compile( "{$$removeEmptyLines()$$}" ) ) instanceof AuthorDirective );
        //assertTrue( getOnlyMark( BindML.compile( "{##a=1##}" ) ) instanceof DefineVar.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{##a:1##}" ) ) instanceof DefineVar.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{##a:$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{##a=$count(blah)##}" ) ) instanceof DefineVarAsScriptResult.StaticVar );

        //assertTrue( getOnlyMark( BindML.compile( "{@meta:data@}" ) ) instanceof SetMetadata );
        //assertTrue( getOnlyMark( BindML.compile( "{@meta=data@}" ) ) instanceof SetMetadata );

        assertTrue( getOnlyMark( BindML.compile( "{$script()$}" ) ) instanceof RunScript );

        assertTrue( getOnlyMark( BindML.compile( "{{+:{+fieldType+} {+fieldName+}+}}" ) ) instanceof AddForm );
        assertTrue( getOnlyMark( BindML.compile( "{{+?a==1: implements {+impl+}+}}" ) ) instanceof AddFormIfVar );
        assertTrue( getOnlyMark( BindML.compile( "{_+:{+fieldType+} {+fieldName+}+_}" ) ) instanceof AddForm );
        assertTrue( getOnlyMark( BindML.compile( "{_+?a==1: implements {+impl+}+_}" ) ) instanceof AddFormIfVar );

        //assertTrue( getOnlyMark( BindML.compile( "{{#assgn:{+fieldName+} = {+fieldValue+};#}}" ) ) instanceof DefineVarAsForm.InstanceVar );
        //assertTrue( getOnlyMark( BindML.compile( "{{##assgn:{+fieldName+} = {+fieldValue+};##}}" ) ) instanceof DefineVarAsForm.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) ) instanceof DefineVarAsForm.InstanceVar );

        //assertTrue( getOnlyMark( BindML.compile( "{{#assgn={+fieldName+} = {+fieldValue+};#}}" ) ) instanceof DefineVarAsForm.InstanceVar );
        //assertTrue( getOnlyMark( BindML.compile( "{{##assgn={+fieldName+} = {+fieldValue+};##}}" ) ) instanceof DefineVarAsForm.StaticVar );

        //assertTrue( getOnlyMark( BindML.compile( "{_#assgn:{+fieldName+} = {+fieldValue+};#_}" ) ) instanceof DefineVarAsForm.InstanceVar );
        //assertTrue( getOnlyMark( BindML.compile( "{_##assgn:{+fieldName+} = {+fieldValue+};##_}" ) ) instanceof DefineVarAsForm.StaticVar );

        //assertTrue( getOnlyMark( BindML.compile( "{_#assgn={+fieldName+} = {+fieldValue+};#_}" ) ) instanceof DefineVarAsForm.InstanceVar );
        //assertTrue( getOnlyMark( BindML.compile( "{_##assgn={+fieldName+} = {+fieldValue+};##_}" ) ) instanceof DefineVarAsForm.StaticVar );

       // assertTrue( getOnlyMark( BindML.compile( "{{##className:IntFormOf{+count+}##}}" ) ) instanceof DefineVarAsForm.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{_##className:IntFormOf{+count+}##_}" ) ) instanceof DefineVarAsForm.StaticVar );

        //assertTrue( getOnlyMark( BindML.compile( "{{##className=IntFormOf{+count+}##}}" ) ) instanceof DefineVarAsForm.StaticVar );
        //assertTrue( getOnlyMark( BindML.compile( "{_##className=IntFormOf{+count+}##_}" ) ) instanceof DefineVarAsForm.StaticVar );
    }

}
