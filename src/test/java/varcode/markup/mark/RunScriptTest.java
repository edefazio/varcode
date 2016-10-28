package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.markup.codeml.CodeMLParser;
import varcode.markup.mark.RunScript;
import varcode.context.eval.VarScript;

import java.util.Set;

import junit.framework.TestCase;

public class RunScriptTest
    extends TestCase
{

    public void testScript()
    {
        RunScript sm = 
            (RunScript) CodeMLParser.parseMark( "/*{$scriptName(input)$}*/" );
        
        assertEquals("scriptName", sm.getScriptName());
        assertEquals("input", sm.getScriptInput() );
        
        VarContext vc = VarContext.of( 
            "scriptName", 
            new VarScript()
            {
				public Object eval( VarContext context, String input ) 
				{
					return input;
				}            	
				
				public void collectAllVarNames( Set<String> collection, String input ) 
				{
				}
            });
        
        sm.derive( vc );
        
        
        vc = VarContext.of( 
            "scriptName", 
            new VarScript()
            {
				public Object eval( VarContext context, String input ) 
				{
					throw new VarException("Throw me");
				}       
				
				public void collectAllVarNames( Set<String> collection, String input ) 
				{
					
				}
            });
        
        try
        {
            sm.derive( vc );
            fail("expected exception"); 
        }
        catch( Exception e )
        {
            //System.out.println( e );
        }        
    }
    
//    public void testValidate()
//    {
//        EvalScript sm = 
//            (EvalScript) CodeMLParser.parseMark( "/*{$min1Max8(input)$}*/" );
//        
//         assertEquals("min1Max8", sm.getScriptName());
//         assertEquals("input", sm.getScriptInput() );
//         
//         VarContext vc = 
//             VarContext.of( 
//                 "min1Max8", new ValidateMinMaxCount( 1, 8 ),
//                 "input", "A" );
//                    
//         sm.derive( vc );
//         
//         vc = VarContext.of( 
//             "min1Max8", new ValidateMinMaxCount( 1, 8 ) );
//         try
//         {
//             sm.derive( vc );
//             fail("Expected exception");
//         }
//         catch( Exception e )
//         {
//             //expected
//             System.out.println( e );             
//         }
//         
//         vc = VarContext.of( 
//             "min1Max8", new ValidateMinMaxCount( 1, 8 ),
//             "input", new int[]{1,2,3,4,5,6,7,8,9});
//         
//         try
//         {
//             sm.derive( vc );
//             fail("Expected exception");
//         }
//         catch( Exception e )
//         {
//             System.out.println( e );
//         }
//         
//         
//    }
}
