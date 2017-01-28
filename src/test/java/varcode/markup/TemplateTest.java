/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this toState file, choose Tools | Templates
 * and open the toState in the editor.
 */
package varcode.markup;

import varcode.markup.Template;
import junit.framework.TestCase;
import varcode.author.Author;
import varcode.author.AuthorState;

/**
 *
 * @author Eric
 */
public class TemplateTest
     extends TestCase
{
    
    public void testAuthorState()
    {
        AuthorState as = Author.toState( "" );
        assertEquals( 0, as.getDirectives().length );
        Template template = as.getTemplate();
        assertEquals(0, template.getAllMarksTemplate().getBlanksCount() );
        assertEquals(0, template.getBindMarks().length );
        assertEquals(0, template.getBlankBinding().getBlanksCount() );
        assertEquals(0, template.getMarkIndicies().cardinality() );
        assertEquals(0, template.getForms().length );
        assertEquals("", template.toSourceText() );
        
        
        assertEquals( 0, as.getPreProcessors().length );
        assertEquals( 0, as.getPostProcessors().length );
        
        
        //simple fill variable
        as = Author.toState("{+a+}", "a", 1 );
        assertEquals( 0, as.getDirectives().length );
        template = as.getTemplate();
        assertEquals(1, template.getAllMarksTemplate().getBlanksCount() );
        assertEquals(1, template.getBindMarks().length );
        assertEquals(1, template.getBlankBinding().getBlanksCount() );
        assertEquals(1, template.getMarkIndicies().cardinality() );
        assertEquals(0, template.getForms().length );
        assertEquals("{+a+}", template.toSourceText() );
        
        assertEquals( 0, as.getPreProcessors().length );
        assertEquals( 0, as.getPostProcessors().length );        
    }
}
