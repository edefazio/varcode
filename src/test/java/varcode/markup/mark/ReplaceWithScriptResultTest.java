package varcode.markup.mark;

import java.util.Set;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.markup.VarNameAudit;
import varcode.markup.codeml.CodeMLParser;
import varcode.context.eval.VarScript;

public class ReplaceWithScriptResultTest
    extends TestCase
{
    public static final String N = System.lineSeparator();
    
    public void testOnlyOneQuotes()
    {
        String mark = "/*{+$removeSpaces(*/\"  A B C D E F G  \"/*)+}*/";
    
        ReplaceWithScriptResult rwsr = 
            (ReplaceWithScriptResult) CodeMLParser.INSTANCE.of( mark );
        
        VarContext vc = VarContext.of( 
            "removeSpaces", 
            new VarScript() 
            {
                public Object eval( VarContext context, String input )
                {
                    String res = input.replace( " ", "" );
                    res = res.replace( "\"", "" );
                    //System.out.println( res );
                    return res;
                }     
                
				public void collectAllVarNames( Set<String> collection, String input ) 
				{					
				}
            } );
        
        assertEquals( "ABCDEFG",  rwsr.derive( vc ) );         
    }
    
    public void testOnlyOneQuotesRequired()
    {
        String mark = "/*{+$removeSpaces(*/\"  A B C D E F G  \"/*)*+}*/";
    
        ReplaceWithScriptResult rwsr = 
            (ReplaceWithScriptResult) CodeMLParser.INSTANCE.of( mark );
        
        VarContext vc = VarContext.of( 
            "removeSpaces", 
            new VarScript() 
            {
                public Object eval( VarContext context, String input )
                {
                    String res = input.replace( " ", "" );
                    res = res.replace( "\"", "" );
                    //System.out.println( res );
                    return res;
                }            
                
				public void collectAllVarNames( Set<String> collection, String input ) 
				{
					
				}
            } );
        assertEquals( "ABCDEFG",  rwsr.derive( vc ) );         
    }
    
    public void testAPI()
    {
        //this is how we might functionally wrap code and replace 
        //it with parameters, it calls the tab() function passing in
        // [0] the content  
        
        /*{$tab(*/
        //for( int i=0; i < 100; i++ )
        //{
            
        //}
        /*,spaces=8,alignLeft=true)}*/
        
        /*{$bin(*/
        //int value = 12345;
        /*,decimalComment=true)}*/
        //int value2 = Integer.parseInt( "11000000111001", 2 ); // 12345
    }
    
    public void testParams()
    {
        /*{$tab(*/
        //  a bunch of code
        //  that uses tabs, but could be replaced based on the Environment 
        //  Settings for what a "tab" is 
        /*,sep=1,b=3)}*/
    }
    public void testIt()
    {
        String printStatement = "System.out.println( \"Hey, this is real\" );";
        
        String openTag = "/*{+$removePrint(*/";
        
        String code    = "    for( int i = 0; i < 100; i++ ){" + N
                       + "   "+ printStatement + N
                       + "    }";
        
        String endTag =  "/*)+}*/";
        
        ReplaceWithScriptResult frc = CodeMLParser.ReplaceWithScriptResultMark.of( 
            openTag + code + endTag, 
            12, 
            VarNameAudit.BASE );
        
        assertTrue( frc.text.equals( openTag + code + endTag ) );
        
        //assertEquals( frc.getCloseTag(), endTag );
        
        String res = (String)
            frc.derive( 
                VarContext.of( 
                    "removePrint",
                    ToNothing.INSTANCE ) );
        
        System.out.println( res );
        
        //System.out.println( res.replace( N, "N" ).replace( ' ', 'x' ) );
        
        code    = "    for( int i = 0; i < 100; i++ ){" + N
                        + "   "+ printStatement + N
                        + "   "+ printStatement + N
                        + "    }";
        
        frc = CodeMLParser.ReplaceWithScriptResultMark.of( 
            openTag + code + endTag, 
            12, 
            VarNameAudit.BASE );
        
        res = (String) frc.derive( 
            VarContext.of( 
                "removePrint",
                ToNothing.INSTANCE ) );
        //System.out.println( res );
       // System.out.println( res.replace( N, "N" ).replace( ' ', 'x' ) );
    }
    
    enum ToNothing
    	implements VarScript
    {
    	INSTANCE;
    	
    	public Object eval(VarContext context, String input) 
    	{
    		return "";
    	}
    	
    	public void collectAllVarNames( Set<String> collection, String input ) 
    	{
    	}
    }
}