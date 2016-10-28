package varcode.markup.mark;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.lib.text.FirstCap;
import varcode.markup.VarNameAudit;
import varcode.markup.codeml.CodeMLParser;
import varcode.markup.mark.DefineVarAsScriptResult;
import varcode.markup.mark.DefineVarAsScriptResult.InstanceVar;
import varcode.context.eval.VarScript;
import junit.framework.TestCase;

public class DefineVarAsScriptResultTest
    extends TestCase
{
    public void testRunAndUpdateScript()
    {        
        DefineVarAsScriptResult.InstanceVar anon = 
          new InstanceVar(
              "/*{#Fields:$firstCap(fields)}*/", 
              0, 
              "Fields", 
              "firstCap", 
              "fields",
              false );
        
        //create the context
        VarContext vc = VarContext.of();
        //I might need to create an implementer of VarContext that automagically 
        //adds things to GLOBAL scope
        //
        vc.set( "firstCap", FirstCap.INSTANCE, VarScope.GLOBAL );
        
        //add a Fields
        vc.set( "fields", new String[]{"aa",  "bb", "cc"}, VarScope.PROJECT );
        
        
        //verify that I can derive the values
        String[] derived = (String[]) anon.derive( vc );
        
        
        assertEquals( derived[ 0 ], "Aa" );
        assertEquals( derived[ 1 ], "Bb" );
        assertEquals( derived[ 2 ], "Cc" );
        
        //derive AND THEN update the Context Bindings
        anon.bind( vc );
        
        //verify that the varContext has the new array in INSTANCE scope
        String[] instance = 
            (String[])vc.get( "Fields", VarScope.INSTANCE );
        
        assertEquals( instance[ 0 ], "Aa" );
        assertEquals( instance[ 1 ], "Bb" );
        assertEquals( instance[ 2 ], "Cc" );
    }
    
    public void testDeriveScript()
    {
        String dateFormat = "yyyy-MM-dd";
        
        DefineVarAsScriptResult.InstanceVar df = 
            CodeMLParser.DefineInstanceVarAsScriptResultMark.of(
            //this "MEANS" set the value today to the result of the date script
            "/*{#today:$date(" + dateFormat + ")#}*/", 
            0, 
            VarNameAudit.BASE );
        
        assertEquals( "date", df.scriptName );        
        assertEquals( df.lineNumber, 0 );
        assertEquals( df.varName, "today" );
        assertEquals( df.scriptInput, dateFormat );
        
        assertEquals( "/*{#today:$date(" + dateFormat + ")#}*/", df.text );
        //assertEquals( ")}*/", df.getCloseTag() );
        VarContext dc = VarContext.of( 
            "date",
            new VarScript()
            {
                public Object eval( VarContext context, String format )
                {
                    if ( format != null && format.trim().length() > 0 )
                    {
                        SimpleDateFormat sdf = 
                            new SimpleDateFormat( format );
                        return sdf.format( new Date() );
                    }
                    return new Date().toString();
                }
                
				
				public void collectAllVarNames( Set<String> collection, String input ) 
				{
				}
            } );
                
        df.bind( dc );
        
        String derivedDate = (String) dc.resolveVar( "today" ); 
        assertTrue( derivedDate != null );
        //System.out.println( derivedDate );
        
        //verify that I can parse the date
        SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
        try
        {
            sdf.parse( derivedDate );
        }
        catch( ParseException e )
        {
            fail( "Date was not formatted with the Format \""+dateFormat+"\"" );
        }
    }
}
