package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.doc.form.Form;
import varcode.markup.codeml.CodeMLParser;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddFormIfVar;
import junit.framework.TestCase;

public class AddFormIfVarTest
    extends TestCase
{
    public static final String N = System.lineSeparator();

    public void testCodeForm()
    {
        String form = 
            "import {+logger+};" + N 
          + "import {+loggerFactory+};" + N;
        
        String mark = 
            "/*{{+?a=true:"
            + form
            + "+}}*/";
        
        //make a form that is 
        AddFormIfVar ifform = 
        	CodeMLParser.AddFormIfVarMark.of(
                VarContext.of(  ),
                mark, 
                0, 
                ForMLCompiler.INSTANCE );
        
        //verify the form is bindable 
        Form fp = ifform.getForm();
        
        String res = fp.compose( 
            VarContext.of( 
                "logger", "org.slf4j.Logger",
                "loggerFactory", "org.slf4j.LoggerFactory"
            ) );
        
        System.out.println("\""+ res +"\"" );
        
        assertEquals(             
               "import org.slf4j.Logger;" + N
            +  "import org.slf4j.LoggerFactory;", 
            res  );
    }
    
    public void testBindFormPattern()
    {
        String form = 
            "import {+logger+};" + N 
          + "import {+loggerFactory+};" + N;
                    
        String mark = 
            "/*{{+?a=true:"
            + form + "+}}*/";
        
        AddFormIfVar ifform = 
           CodeMLParser.AddFormIfVarMark.of(
               VarContext.of(  ),
               mark, 
               0,
               ForMLCompiler.INSTANCE );
        
        String bound = (String)ifform.derive( 
            VarContext.of( 
                "a", "true", 
                "logger", "org.slf4j.Logger",
                "loggerFactory", "org.slf4j.LoggerFactory" ) );
        
        System.out.println("BOUND \"" + bound +"\"");
        
        assertEquals(             
               "import org.slf4j.Logger;" + N
            +  "import org.slf4j.LoggerFactory;", bound );
        
        bound = (String)ifform.derive( 
            VarContext.of( 
                "a", "false", 
                "logger", "org.slf4j.Logger",
                "loggerFactory", "org.slf4j.LoggerFactory"
                ) );
        //
        assertEquals( 
            "verify if the condition is not met, the form is not processed",
            null, bound);
    }
}
