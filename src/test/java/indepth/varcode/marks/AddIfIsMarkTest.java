package indepth.varcode.marks;

import junit.framework.TestCase;
import varcode.doc.form.Form;
import varcode.markup.forml.ForML;
import varcode.markup.mark.AddIfVar;

/**
 *
 * @author Eric DeFazio
 */
public class AddIfIsMarkTest
    extends TestCase
{
    public void testForML()
    {
        //create text Mark 
        String markText = ForML.MarkText.addIfVar( "condition==true", "output" );
        assertEquals("{+?condition==true:output+}", markText );
        
        AddIfVar ai = (AddIfVar)ForML.parseMark( 
            "{+?condition==true:output+}" );
        Form f = ForML.compile( "{+?condition==true:output+}" );
        assertEquals( "", f.compose());
        assertEquals( "output", f.compose("condition", true ) );
        assertEquals( "output", f.compose("condition", "true" ) );
        assertEquals( "", f.compose("condition", false ) );
    }
}
