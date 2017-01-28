/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this toState file, choose Tools | Templates
 * and open the toState in the editor.
 */
package varcode.author;

import varcode.markup.Template;
import junit.framework.TestCase;
import varcode.author.lib.RemoveEmptyLines;
import varcode.author.lib.StripMarks;
import varcode.context.VarContext;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class AuthorBindMLTest
    extends TestCase
{

    /**
     * use Directive PreProcessor Marks {$$preprocessor$$} within a Frame to
     * effect the Authored document
     */
    public void testMarkPostProcessor()
    {
        //ADD {$$removeEmptyLines$$} POST processor, which will take the 
        // Authored OUTPUT (AFTER Specializing) and remove EMPTY LINES

        assertEquals( "", Author.toString(
            System.lineSeparator() + "{$$removeEmptyLines$$}"
            + System.lineSeparator() ) );
    }

    public void testParamPostProcessor()
    {
        assertEquals( "a" + System.lineSeparator(),
            Author.toString(
                System.lineSeparator()
                + "a" + System.lineSeparator()
                + System.lineSeparator(),
                VarContext.of(),
                RemoveEmptyLines.INSTANCE ) ); //pass in  PostProcessor Directive
    }

    /**
     * use Directive PostProcessor Marks {$$postprocessor$$} within a Frame to
     * effect the Authored document
     */
    public void testMarkPreProcessor()
    {
        //Author WITHOUT the preprocessor
        assertEquals( "static100", Author.toString( "static{+a+}",
            "a", 100 ) );

        //Add the {$$stripMarks$$} Preprocessor
        // stripMarks will remove the "{+a+}" mark
        // BEFORE the value 100 is bound to the toState
        assertEquals( "static", Author.toString( "{$$stripMarks$$}static{+a+}",
            "a", 100 ) );
    }

    public void testParameterPreProcessor()
    {
        Template template = BindML.compile( "{+a+}" );
        //by processing the toState normally, it will print the var "a"
        assertEquals( "100", Author.toString( template, VarContext.of( "a", 100 ) ) );

        //a Pre-processing Directive param will strip the marks 
        //and change output (alternativelty we could have added the 
        // PreProcessor as a Mark (like above)        
        assertEquals( "",
            Author.toString( template, VarContext.of( "a", 100 ), StripMarks.INSTANCE ) );
    }

    public void testAuthorForm()
    {
        assertEquals( "", Author.toString( "{{+:{+a+}+}}" ) );
        assertEquals( "1", Author.toString( "{{+:{+a+}+}}", "a", 1 ) );
        assertEquals( "11", Author.toString( "{{+:{+a+}+}}", "a", new int[]
        {
            1, 1
        } ) );
        assertEquals( "1", Author.toString( "{{+:{+a+}, +}}", "a", 1 ) );
        assertEquals( "1, 1", Author.toString( "{{+:{+a+}, +}}", "a", new int[]
        {
            1, 1
        } ) );
    }

    /*
    public void testAuthorExpression()
    {
        assertEquals("2", Author.toString("{+((1 + 1))+}" ) );
        
        //expression with vars
        assertEquals("2", Author.toString("{+((a + a | 0))+}", "a", 1 ) );
    }
     */
 /*
    public void testAuthorDefine()
    {
        //define instance var
        assertEquals("", Author.toString( "{#a:1#}" ) );        
        assertEquals("1", Author.toString( "{#a:1#}{+a+}" ) );
        
        //define static var
        assertEquals("", Author.toString( "{##a:1##}" ) );        
        assertEquals("1", Author.toString( "{##a:1##}{+a+}" ) );
        
    }
     */
    public void testAuthor()
    {
        // static toState w/ no marks equals itself
        assertEquals( "static", Author.toString( "static" ) );

        // "" if mark var is not found
        assertEquals( "", Author.toString( "{+a+}" ) );

        // ""  if mark value is null
        assertEquals( "", Author.toString( "{+a+}",
            "a", null ) );

        assertEquals( "value",
            Author.toString( "{+a+}",
                "a", "value" ) );

        //marks serialize correctly
        assertEquals( "1",
            Author.toString( "{+a+}",
                "a", 1 ) );

        //static and mark
        assertEquals( "1static",
            Author.toString( "{+a+}static",
                "a", 1 ) );

        //duplicate back to back marks
        assertEquals( "11",
            Author.toString( "{+a+}{+a+}",
                "a", 1 ) );

        //calltoString on Object
        assertEquals( "ToStringCalled",
            Author.toString( "{+a+}",
                "a", new CustomObject() )
        );
    }

    public static int callThisMethodWhenAuthoring()
    {
        return 100;
    }

    public void testCallStaticMethod()
    {
        assertEquals( "100", Author.toString( "{+$callThisMethodWhenAuthoring()+}",
            VarContext.of().setResolveBase( AuthorBindMLTest.class ) ) );
    }

    public void testArrayVars()
    {
        //print arrays (primitives)
        assertEquals( "1, 2",
            Author.toString( "{+a+}",
                "a", new int[]
                {
                    1, 2
            } )
        );

        //print arrays (custom Objects)
        assertEquals( "ToStringCalled, ToStringCalled",
            Author.toString( "{+a+}",
                "a", new Object[]
                {
                    new CustomObject(), new CustomObject()
            } )
        );

        //print arrays mixed
        assertEquals( "1, ToStringCalled",
            Author.toString( "{+a+}",
                "a", new Object[]
                {
                    1, new CustomObject()
            } )
        );
    }

    /**
     * test authoring using built-in varScripts
     */
    public void testAuthorBuiltInScripts()
    {
        Author.toString( "{+$>(a)+}",
            "    1, 1", new Object[]
            {
                1, 1
            } );
    }

    static class CustomObject
    {
        public String toString()
        {
            return "ToStringCalled";
        }
    }
}
