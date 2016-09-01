package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.markup.forml.ForMLParser;
import varcode.markup.mark.AddScriptResult;
import varcode.script.VarScript;

import java.util.Set;

import junit.framework.TestCase;

public class AddScriptResultTest
    extends TestCase
{
	/*
	public void testAddScriptResult()
	{
		 AddScriptResult asr = 
		     ForMLParser.AddScriptResultMark.of( 
		         "{+$quote($count(a))+}", 0 );
		assertEquals( "\"1\"", asr.derive( VarContext.of( "a", "1" ) ) );		
	}
	*/

    /**
     * Verify that if the Script Fails we get the (wrapped) exception back
     */
    public void testScriptFails()
    {
        AddScriptResult fac = 
            ForMLParser.AddScriptResultMark.of( 
                 "{+$fail(name=this   is  tab separated   )+}", 
                 0 );
        try
        {
            fac.derive( VarContext.of( "fail", new VarScript()
            {                
                public Object eval( VarContext context, String input )
                {
                    throw new RuntimeException( "I ONLY FAIL" );
                }
                
				public void collectAllVarNames( Set<String> collection, String input ) 
				{
				}
            } ) );
            fail( "Expected Exception" );
        }
        catch( Exception e )
        {
            //expected
        }
    }
    
    public void testSimple()
    {
        AddScriptResult fac = 
            ForMLParser.AddScriptResultMark.of( 
                "{+$tab(name=this   is  tab separated   )+}", 
                0 );
        assertEquals( "tab", fac.getScriptName() );
        System.out.println(  fac.getScriptInput() );
        assertEquals( "name=this   is  tab separated   ", 
            fac.getScriptInput() );
        assertEquals( fac.getScriptInput(), "name=this   is  tab separated   ");
        
    }
}
