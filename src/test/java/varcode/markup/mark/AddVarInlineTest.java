package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.markup.VarNameAudit;
import varcode.markup.codeml.CodeMLParser;
import junit.framework.TestCase;

public class AddVarInlineTest
    extends TestCase
{
   
    
    public void testRequried()
    {
        String mark = "{+name*+}";
        AddVarIsExpression i = CodeMLParser.AddVarInlineMark.of( mark, 0, VarNameAudit.BASE );
        
        assertTrue(  i.isRequired() );
        assertTrue( "a".equals(  i.derive( VarContext.of( "name", "a" ) ) ) );
        assertTrue( i.getVarName().equals( "name" ) );
        
        try
        {
            i.derive( VarContext.of(  ) );
            fail ("expected exception");
        }
        catch( VarException cme )
        {
            //expected
        }
    }
    
}
