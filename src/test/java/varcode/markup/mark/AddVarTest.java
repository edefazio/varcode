package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.markup.VarNameAudit;
import varcode.markup.bindml.BindMLParser;
import varcode.markup.codeml.CodeMLParser;
import junit.framework.TestCase;

public class AddVarTest
    extends TestCase
{

	public void testEmptyStringDefault()
	{
		AddVarIsExpression i = CodeMLParser.AddVarExpressionMark.of( 
	         "/*{+name|+}*/", 
	         0, 
	         VarNameAudit.BASE );
		assertEquals("", i.derive(VarContext.of( ) ) ) ;
		
		AddVarIsExpression d = BindMLParser.AddVarExpressionMark.of( 
		     "{+name|+}", 
		     0, 
		     VarNameAudit.BASE );
		assertEquals("", d.derive(VarContext.of( ) ) ) ;
	}
    public void testRequired()
    {
        //NOTE: here I want the name with the first character uppercase
        AddVarIsExpression i = CodeMLParser.AddVarExpressionMark.of( 
            "/*{+name*+}*/", 
            0, 
            VarNameAudit.BASE );
        
        assertTrue( i.isRequired() );
        assertEquals( i.getVarName(),  "name" );
        
        assertEquals( "eric", i.derive( VarContext.of( "name", "eric" ) ) );
        
        //assertEquals( "Eric", i.derive( VarContext.of( "name", "Eric" ) ) );
        
        try
        {
            i.derive( VarContext.of( ) );
            fail("Expected Exception for Missing Required Field ");
        }
        catch( VarException cme )
        {
            //expected
        }
    }
    
    
}
