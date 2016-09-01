package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.markup.codeml.CodeMLParseState;
import varcode.markup.mark.DefineVar;
import junit.framework.TestCase;

public class DefineVarTest
    extends TestCase
{

    /** Verify that it overwrites a previous static value */
    public void testStaticVar()
    {
        
        //(technically, if we wanted to preserve "a", we'd
        // set it as VarScope.CORE_LIBRARY and allow it to be overriden
        // in static scope
        CodeMLParseState cms = new CodeMLParseState();
        
        // Initialize the static variable "a" to be 1 
        cms.getParseContext().set( "a", "1", VarScope.STATIC );
        
        //verify the value is set
        assertEquals( "1", cms.getParseContext().resolveVar( "a" ) );
        
        //Create a MarkAction taht sets the static value of var "a" to "2"
        DefineVar.StaticVar dsv = new DefineVar.StaticVar( 
            "/**{#a=2}*/", 0, "a", "2" );
        
        // bind the new value to the Markup Context
        dsv.onMarkParsed( cms );
        
        //verify the value of "a" is "2"
        assertEquals( "2", cms.getParseContext().resolveVar( "a" ) );        
    }
    
    public void testInstanceVar()
    {
        //initialize a VarContext, set "a" = 1
        VarContext varContext = VarContext.ofScope( VarScope.INSTANCE, "a", "1" );
        
        //verify the a is 1
        assertEquals( "1", varContext.resolveVar( "a" ) );
        
        //Create a MarkAction that sets the instance value of var "a" to "2"
        DefineVar.InstanceVar div = new DefineVar.InstanceVar( 
            "/*{#a=2}*/", 0, "a", "2" );
        
        div.bind( varContext );
        
        //verify the a is 2
        assertEquals( "2", varContext.resolveVar( "a" ) );
    }
    
}
