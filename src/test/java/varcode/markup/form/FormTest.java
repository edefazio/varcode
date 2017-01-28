 package varcode.markup.form;

import varcode.markup.form.VarForm;
import varcode.markup.form.Form;
import varcode.markup.form.FormTemplate;
import varcode.markup.form.SeriesFormatter;
import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.markup.forml.ForML;

/**
 *
 * @author Eric
 */
public class FormTest 
    extends TestCase
{
    public void testEmpty()
    {
        Form f = ForML.compile( "" );
        assertTrue( f instanceof Form.StaticForm );
        assertEquals("", f.getText() );
        assertEquals("", f.author( ) );
        assertEquals("", f.author( "a", 1) );
        assertEquals(0, f.getAllMarks().length);
        
        //assertTrue( ft.getVarNames().isEmpty());
        //ft.collectVarNames(varNames, context);
    }
    
    public void testTlate()
    {
        Form f = ForML.compile( "{+a+}" );
        assertEquals("java.lang.String", f.author( "a", String.class ) );
        assertEquals("java.lang.Stringjava.lang.Integer", f.author( "a", new Object[]{ String.class, Integer.class} ) );
        
        f = ForML.compile("{+a+}, ");
        assertEquals("java.lang.String", f.author( "a", String.class ) );
        assertEquals("java.lang.String, java.lang.Integer", f.author( "a", new Object[]{ String.class, Integer.class} ) );
        
    }
    public void testRepeat()
    {
        FormTemplate ft = ForML.compileTemplate( "{+a+}" );
        
    }
    public void testNameValue()
    {
	VarForm vf = new VarForm(
            ForML.compileTemplate( "public {+type*+} {+fieldName*+};"),
            new SeriesFormatter.BetweenTwo( System.lineSeparator() ) );
		
		String formed = vf.author(
			VarContext.of(
				"type",	 new Object[]{ 
							int.class,float.class,double.class,short.class,byte.class, long.class },
				"fieldName", new String[]{ "A","B","C","D","E","F" }) );
		System.out.println( formed );
	}
	
	public void testNameValueClass()
	{
		VarForm vf = new VarForm(
				ForML.compileTemplate( "public {+type*+} {+fieldName*+};"),
				new SeriesFormatter.BetweenTwo( " " ) );
			
		String formed = vf.author(
			VarContext.of(
				"type",	 new Object[]{ String.class, String.class },
				"fieldName", new String[]{"A", "B"} ) );
			
		System.out.println("FORMED : "+ formed );
			
		assertEquals( "public java.lang.String A; public java.lang.String B;", formed );
			
		formed = vf.author(
			VarContext.of(
				"type",	 String.class,
				"fieldName", "A") );
		
		//System.out.println( formed );
		
		assertEquals( "public java.lang.String A;", formed );
	}
	
	public void testOnlyVarInline()
	{
		VarForm vf = new VarForm(
			ForML.compileTemplate( "{+a+}"), 
			SeriesFormatter.INLINE );
		
		assertEquals( "" , vf.author( VarContext.of( ) ) );
		assertEquals( "A" , vf.author( VarContext.of("a", "A" ) ) );
		
		assertEquals( "ABCDEF" , vf.author( VarContext.of("a", "ABCDEF" ) ) );
		assertEquals( "ABCDEF" , vf.author( VarContext.of("a", new String[]{"A","B","C","D","E","F" } ) ) );		
	}
	
	public void testTranslate()
	{
		VarForm vf = new VarForm(
			ForML.compileTemplate( "{+a+}"), 
			SeriesFormatter.INLINE );
		
		String formed = vf.author( 
			VarContext.of(
				"a", new Object[]{ 
					int.class,float.class,double.class,short.class,byte.class, long.class } ) );
		
		System.out.println( formed );
		
		assertEquals( "intfloatdoubleshortbytelong" , formed );
		
		vf = new VarForm(
			ForML.compileTemplate( "{+a+}" ), 
			new SeriesFormatter.BetweenTwo(", ") );
		
		formed = vf.author( 
			VarContext.of(
				"a", new Object[]{ 
					int.class,float.class,double.class,short.class,byte.class, long.class } ) );
		
		assertEquals( "int, float, double, short, byte, long" , formed );
	}
}