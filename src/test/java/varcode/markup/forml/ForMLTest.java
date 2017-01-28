/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.markup.forml;

import junit.framework.TestCase;
import varcode.markup.form.Form;
import varcode.markup.form.VarForm;
import varcode.markup.forml.ForML;

/**
 *
 * @author Eric
 */
public class ForMLTest
    extends TestCase
{
    public void testStaticForML()
    {
        Form f = ForML.compile("some static text" );
        assertEquals("some static text", f.author( ) );
    }
    
    public void testForML()
    {
        VarForm vf = (VarForm)ForML.compile( "{+a+}" );
        assertEquals("100", vf.author( "a", 100 ) );
    }
}
