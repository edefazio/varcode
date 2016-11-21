package indepth.varcode.marks;

import junit.framework.TestCase;
import varcode.doc.form.Form;
import varcode.markup.forml.ForML;
import varcode.markup.mark.AddIfVar;

/**
 * "{+?condition:output+}"
 *
 * @author Eric DeFazio
 */
public class AddIfAnyMarkTest
    extends TestCase
{
    public void testForML()
    {
        //create text Mark 
        String markText = ForML.MarkText.addIfVar( "condition", "output" );
        assertEquals("{+?condition:output+}", markText );
        
        AddIfVar ai = (AddIfVar)ForML.parseMark( 
            "{+?condition:output+}" );
        Form f = ForML.compile( "{+?condition:output+}" );
        assertEquals( "", f.compose());
        assertEquals( "output", f.compose("condition", "anything" ) );
    }
    
}
