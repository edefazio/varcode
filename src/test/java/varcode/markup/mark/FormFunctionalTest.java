package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.markup.form.VarForm;
import varcode.markup.forml.ForML;
import junit.framework.TestCase;

/**
 * Tests some real-world scenarios for generating code
 *
 * @author
 */
public class FormFunctionalTest
    extends TestCase
{
    public static String N = System.lineSeparator();

    public void testConditionalFormAdds()
    {

        String formText
            = "{+access+}{+?isStatic: static +}{+?isFinal: final +} class {+className*+}" + N
            + "{{+?extendsFrom:    extends {+extendsFrom+}" + N + "+}}"
            + "{{+?implementsFrom:    implements {_+:{+implementsFrom}, +_} +}}" + N;

        VarForm vf = (VarForm)ForML.compile( formText );
        System.out.println( vf );
        System.out.println( vf.getAllMarks()[ 1 ] );
        System.out.println( vf.author( VarContext.of( "className", "MyClass" ) ) );

        System.out.println( vf.author(
            VarContext.of(
                "access", "public",
                "className", "MyClass"
            ) ) );
    }

    public void testIfAdd()
    {
        String formText = "{+?isStatic==true: static+}";

        VarForm vf = (VarForm)ForML.compile( formText );
        //FormMarkupParser.IfAddMark.OPEN_TAG
        //System.out.println( vf );
        assertEquals( "", vf.author( VarContext.of() ) );
        assertEquals( "", vf.author( VarContext.of( "isStatic", "false" ) ) );
        assertEquals( " static", vf.author( VarContext.of( "isStatic", true ) ) );
        assertEquals( " static", vf.author( VarContext.of( "isStatic", "true" ) ) );
        assertEquals( " static", vf.author( VarContext.of( "isStatic", Boolean.TRUE ) ) );
    }

    public void testConditionalAdds()
    {
        String formText
            = "{+access+}{+?isStatic==true: static +}{+?isFinal==true: final +} class {+className*+}";

        VarForm vf = (VarForm)ForML.compile( formText );
        System.out.println( vf );

        System.out.println( vf.getAllMarks()[ 1 ] );
        System.out.println( vf.author( VarContext.of( "className", "MyClass" ) ) );

        System.out.println( vf.author(
            VarContext.of(
                "access", "public",
                "className", "MyClass"
            ) ) );
    }
}
