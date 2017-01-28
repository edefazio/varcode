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
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddVar;
import varcode.markup.mark.Mark;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ForMLParserTest
    extends TestCase
{
    public void testForML()
    {
        Mark m = 
            ForMLCompiler.compileMark( "{+a+}", 0 );
        
        assertTrue( m instanceof AddVar );
        
        Form form = ForML.compile( "{+a+}" );
        assertTrue( form instanceof VarForm );
        
        //its blank if there is nothing for a
        assertEquals( "", form.author( ) );
        
        //its 1 if there is 1 instance of a
        assertEquals("1", form.author( "a", 1 ) );
        
        //it iterates the form n times 
        assertEquals("12345", form.author( "a", new int[]{1,2,3,4,5} ) );
    }
}
