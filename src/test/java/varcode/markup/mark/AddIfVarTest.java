package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.markup.VarNameAudit;
import varcode.markup.codeml.CodeMLParser;
import junit.framework.TestCase;

public class AddIfVarTest
    extends TestCase
{

    public static final String N = System.lineSeparator();
    
    /**
     * Here we Write the Conditional Block to 
     * add Logger imports IF the name log is ANYTHING but null
     */
    public void testLogExists()
    {
        String logImports = 
            "import org.slf4j.Logger;" + System.lineSeparator()
            +  "import org.slf4j.LoggerFactory;";
            
        String mark = 
            "/*{+?log:" + 
            logImports + "}*/";
            
        AddTextIfVar logImportsIf = CodeMLParser.AddIfVarMark.of( mark, 0, VarNameAudit.BASE );
        System.out.println( "NAME =\"" + logImportsIf.getVarName() +"\"" );
        assertTrue( logImportsIf.getVarName().equals( "log" ) );
        assertTrue( null == logImportsIf.getTargetValue() );
        
        System.out.println( logImportsIf.derive( VarContext.of("log", "anything") ) );
        assertTrue( logImports.equals(
            logImportsIf.derive( VarContext.of("log", "anything") ) ) );
            
        assertEquals( null,
            logImportsIf.derive( VarContext.of( ) ) );       
    }
    
    /**
     * 
     */
    public void testLogIsTargetValue()
    {
        String logStatement = "    LOG.trace(\"Got in method\"); ";
        String mark = "/*{+?log==trace:" + logStatement + "}*/";
        AddTextIfVar logIfTrace = CodeMLParser.AddIfVarMark.of( mark, 0, VarNameAudit.BASE );
        assertTrue( logIfTrace.getVarName().equals( "log" ) );
        assertTrue( logIfTrace.getTargetValue().equals( "trace" ) );
        
        //verify when the log value == trace the log message is written
        assertTrue( logStatement.equals(
                logIfTrace.derive( VarContext.of("log", "trace") ) ) );
        
        //verify when the log value is not equal then it is NOT written
        assertEquals( null,  
                logIfTrace.derive( VarContext.of("log", "error") ) );
        
        //if the variable is not found at all, NOT written
        assertEquals( null, 
                logIfTrace.derive( VarContext.of( ) ) );
    }
        
    public static final String INTERNAL_METHOD =
        "    public static class QueryInnerClass" + N
       +"    {" + N
       +"         public List<String> matchStartsWith(" + N 
       +"             List<String> stringList, " + N
       +"             String startsWith )" + N
       +"         {" + N
       +"             List<String> found = new ArrayList<String>();" + N
       +"             for( int i = 0; i < stringList.length(); i++ )" + N
       +"             {" + N
       +"                 String candidate = stringList.get( i );" + N 
       +"                 if( candidate.startsWith( startsWith )" + N
       +"                 {" + N
       +"                     found.add( candidate );" + N
       +"                 }" + N        
       +"             }" + N
       +"             return found;" + N
       +"         }" + N
       +"    }" + N;
    
    public static final String CONDITIONAL_INNER_CLASS =                    
        "/*{+?" + "java7Query" + ':'
      + INTERNAL_METHOD + "}*/";
    
    public void testCondInnerClass()
    {
        AddTextIfVar bc = CodeMLParser.AddIfVarMark.of( 
            CONDITIONAL_INNER_CLASS, 
            1, 
            VarNameAudit.BASE );
        //System.out.println( "\"" + bc.name + "\"" );
        //System.out.println( "\"" + bc.getBlock() + "\"" );
        
        assertEquals( null, bc.derive( VarContext.of() ) );
        //assertTrue( ((String)bc.derive( VarContext.of() ) ).length() == 0 );
        assertTrue( 
            bc.derive( 
                VarContext.of( "java7Query", "yep" ) ).equals( INTERNAL_METHOD ) );
        
        assertTrue( bc.getText().equals( CONDITIONAL_INNER_CLASS ) );
        
        assertTrue( bc.getConditionalText().equals( INTERNAL_METHOD ) );        
    }
    
    /** 
     * Verify that (although we are passing in a primitive bool
     * we "evaluate" it as a String ("true" == true)
     */
    public void testToString()
    {
        AddTextIfVar ifAdd = new AddTextIfVar( 
            "/*+?method=true:printit();*}/", 
            0, 
           "method",
           "true",
           "printIt();" );
        assertEquals("printIt();", ifAdd.derive( VarContext.of( "method", true ) ) );        
    }
    
}
