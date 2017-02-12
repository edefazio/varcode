/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.author;

import junit.framework.TestCase;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class AuthorTest
    extends TestCase
{

    
    public void testAuthorNoMarks()
    {
        assertEquals( "", 
            Author.toString( "", VarContext.of() ) );
        
        assertEquals( "Static Text", 
            Author.toString( "Static Text", VarContext.of() ) );
    }
    
    /**
     * Binding Marks
   <LI><CODE>"{+name+}"</CODE>  {@link varcode.markup.mark.AddVar}
 * <LI><CODE>"{+$script()+}"</CODE>   {@link varcode.markup.mark.AddScriptResult}
 * <LI><CODE>"{+?var:addThis+}"</CODE>  {@link varcode.markup.mark.AddIfVar}
 * <LI><CODE>"{{+:{+fieldType+} {+fieldName+}+}}"</CODE> {@link varcode.markup.mark.AddForm}
 * <LI><CODE>"{_+:{+fieldType+} {+fieldName+}+_}"</CODE> {@link varcode.markup.mark.AddForm}
 * <LI><CODE>"{{+?a==1: implements {+impl+}+}}"</CODE> {@link varcode.markup.mark.AddFormIfVar}
 * <LI><CODE>"{_+?a==1: implements {+impl+}+_}"</CODE> {@link varcode.markup.mark.AddFormIfVar}
 * </UL>
 * 
 * Other marks
 * <UL>
 * <LI><CODE>"{$print(a)$}"</CODE>  {@link varcode.markup.mark.RunScript}		
 * <LI><CODE>"{$$removeEmptyLines()$$}"</CODE>  {@link varcode.markup.mark.AuthorDirective}		
 * </UL>
     */
    public void testAuthorOnlyMarks()
    {
        assertEquals( "", 
            Author.toString( "{+a+}", VarContext.of() ) );
        
        assertEquals( "1", 
            Author.toString( "{+a+}", "a", 1  ) );
        
        //run Script
        assertEquals( "", 
            Author.toString( "{+$>(a)+}", VarContext.of( ) ) );
        
        assertEquals( "    1", //indent the value of a 4 spaces
            Author.toString( "{+$>(a)+}", "a", 1 ) );
        
        assertEquals( "", 
            Author.toString( "{+?a:printThis+}",VarContext.of(  ) ) );
        
        assertEquals( "printThis", 
            Author.toString( "{+?a:printThis+}", "a", 1 ) );
        
        //Form
        assertEquals( "",
            Author.toString( "{{+:{+a+}+}}", VarContext.of() ));
        
        assertEquals( "1",
            Author.toString( "{{+:{+a+}+}}", "a", 1 ));
        
        assertEquals( "12",
            Author.toString( "{{+:{+a+}+}}", "a", new int[]{1,2} ));
        
        //Form (alt {_ _} notation)
        assertEquals( "",
            Author.toString( "{_+:{+a+}+_}", VarContext.of() ));
        
        assertEquals( "1",
            Author.toString( "{_+:{+a+}+_}", "a", 1 ));
        
        assertEquals( "12",
            Author.toString( "{_+:{+a+}+_}", "a", new int[]{1,2} ));
        
        //Conditional Form
        assertEquals( "",
            Author.toString( "{{+?a:{+a+}+}}", VarContext.of() ));
        
        assertEquals( "", 
            Author.toString( "{{+?a:{+a+}+}}", VarContext.of( "a", new int[0]) ));
        
        assertEquals( "1",
            Author.toString( "{{+?a:{+a+}+}}", "a", 1 ) );
        
        assertEquals( "anything",
            Author.toString( "{{+?a:{+b+}+}}", "a", 1, "b", "anything" ) );
        
        assertEquals("", //NOTE: the "{$...$}" RunScript Mark is different from the
                         //          "{+$...$}" AddScriptResult Mark
            Author.toString( "{$>(a)$}", VarContext.of() ) );
        
        
        assertEquals("", //NOTE: the "{$...$}" RunScript Mark is different from the
                         //          "{+$...$}" AddScriptResult Mark
            Author.toString( "{$>(a)$}", VarContext.of("a", 1) ) );
        
        
        assertEquals("", //Embedded Post Processing Directive 
            Author.toString( "{$$removeEmptyLines()$$}", VarContext.of( ) ) );
        
        assertEquals("", //Embedded Post Processing Directive 
            Author.toString( //verify it is removing all the empty lines
                System.lineSeparator() + "{$$removeEmptyLines()$$}" 
                + System.lineSeparator()
                +System.lineSeparator(), 
                VarContext.of( ) ) );
        
        try
        {
            Author.toString( "{+a*+}", VarContext.of() );
            fail( "expected VarBindException for missing required value" );
        }
        catch( VarBindException vbe )
        {
            //expected
        }
    }
    
    public void testAllMarks()
    {
        Template t = BindML.compile( 
            "{+a+}"+ 
            "{+$>(a)+}" + 
            "{+?a:B+}" + 
            "{{+:{+a+}+}}" + 
            "{{+?a:{+b+}+}}" +
            "{$>(a)$}" +
            "{$$removeEmptyLines()$$}" );
            
        assertEquals( "", Author.toString( t, VarContext.of( ) ) );
        assertEquals( "1    1B1bee", 
            Author.toString( t, "a", 1, "b", "bee" ) );
    }
}
