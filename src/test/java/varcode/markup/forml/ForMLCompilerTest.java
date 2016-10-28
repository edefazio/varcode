package varcode.markup.forml;

import varcode.context.VarContext;
import varcode.doc.form.VarForm;
import varcode.markup.forml.ForML;
import varcode.markup.forml.ForMLCompiler;
import junit.framework.TestCase;

public class ForMLCompilerTest
	extends TestCase
{
	public static final String N = System.lineSeparator();
	
	public static final String FIELD_FORM = 
		"public String {+field+};" + N;
		    
    public static final String themark = 
         "{{+fields:" + N
		 + FIELD_FORM
		 + "*/" + N    
		 + "/** description of aField */" +N 
		 + "public String aField;" + N    
		 + "/*+}}";

    public void testAddExpressionResult()
    {
    	VarForm vf = (VarForm)ForML.compile( "{+((3+5))+}" );
    	assertEquals( "8", vf.derive(VarContext.of( ) ) );
    	
    	vf = (VarForm)ForML.compile( "{+((a+b))+}" );
    	//this is JavaScript converting var numbers to Floating point
    	assertEquals( "8.0", vf.derive(VarContext.of( "a", 4, "b", 4 ) ) );
    	
    	// if we pass Strings in, it does a String concatenation, not arithmetic addition
    	assertEquals( "44", vf.derive(VarContext.of( "a", "4", "b", "4" ) ) );
    	
    	vf = (VarForm)ForML.compile( "{+((a+b|0))+}" );
    	//this |0 fix converts the expression result of numbers back to int
    	// again this is JavaScripts doing
    	assertEquals( "8", vf.derive( VarContext.of( "a", 4, "b", 4 ) ) );
    	
    }
    
    public void testAddVar()
    {
    	VarForm vf = (VarForm) ForML.compile( FIELD_FORM );
    	assertEquals(
    		"public String A;" + N, 
    		vf.derive( VarContext.of( "field", "A" ) ) );
    }
    
    public void testAddVarName()
    {
    	VarForm vf = (VarForm) ForML.compile( "public String {+name+};" + N );
    	assertEquals(
    		"public String A;" + N, 
    		vf.derive( VarContext.of( "name", "A" ) ) );
    }
    
    public void testFirstCaps()
    {
    	/*
    	VarForm field = 
            (VarForm)ForMLCompiler.INSTANCE.fromString( "{+name+}" );
    	
    	assertEquals("eric", field.derive( VarContext.of("name", "eric") ) );
    	
    	
    	field = 
             (VarForm)ForMLCompiler.INSTANCE.fromString( "{+$count(name)+}" );
    	
    	assertEquals( "1", field.derive( VarContext.of( "name", "eric") ) );
    	*/
    	
    	VarForm field = 
    	    (VarForm)ForMLCompiler.INSTANCE.compile( "{+$^(name)+}" );
    	
    	assertEquals( "Eric", field.derive( VarContext.of( "name", "eric") ) );
    	
    }
}
