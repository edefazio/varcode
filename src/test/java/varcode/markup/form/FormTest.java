 package varcode.markup.form;

import junit.framework.TestCase;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.markup.forml.ForML;

/**
 *
 * @author Eric
 */
public class FormTest 
    extends TestCase
{
    public void testGetVars()
    {
        Form f = ForML.compile("{+a+}" );
        assertTrue( f.getVarNames().contains( "a" ) );
        assertEquals( 3, f.getCardinality( VarContext.of("a", new int[3] ) ) );
    }
    
    public void testGetVarsScript()
    {
        Form f = ForML.compile("{+$#(a)+}");
        VarForm vf = (VarForm)f;
        
        assertEquals( 1, vf.getVarNames().size() );
        assertTrue( vf.getVarNames().contains( "a" ) );
    }
    
    public void testFormCardinaliity()
    {
        assertEquals(3, ForML.compile( "{+a+}" ).getCardinality(VarContext.of( "a", new int[3] ) ) );
        
        
        Form f = ForML.compile( "System.out.println( \"Hello {+$[#](helloCount)+}\" );" );
        //System.out.println( 
        assertTrue( f.getClass().isAssignableFrom( VarForm.class) );
        VarForm vf = (VarForm)f;
        
        
        assertEquals( 3, vf.getCardinality( VarContext.of( "helloCount", new int[3] ) ) );
        
    }
    /**
     * Instead of having the Form return a single String
     * have the form return a String Array of results
     */
    public void testFormAuthorSeries()
    {
        Form f = ForML.compile( "{+a+}" );        
        String[] series = f.authorSeries( Context.EMPTY );        
        assertEquals( 0, series.length );
        
        series = f.authorSeries( VarContext.of( "a", 1 ) );        
        assertEquals( 1, series.length );
        assertEquals( "1", series[0] );
        System.out.println( series[0]);
        
        series = f.authorSeries( VarContext.of( "a", new int[]{1, 2} ) );        
        assertEquals( 2, series.length );
        assertEquals( "1", series[0] );
        assertEquals( "2", series[1] );
    }
    
    public void testFormAuthorSeriesComplex()
    {
        Form f = ForML.compile( "public {+type+} {+name+};" );        
        String[] series = f.authorSeries( Context.EMPTY );        
        assertEquals( 0, series.length );
        
        series = f.authorSeries( VarContext.of( "type", int.class, "name", "x" ) );        
        assertEquals( 1, series.length );
        assertEquals( "public int x;", series[0] );
        System.out.println( series[0]);
        
        series = f.authorSeries( VarContext.of( 
            "type", new Class[]{int.class, String.class}, 
            "name", new String[]{"x", "name"} ) );        
        assertEquals( 2, series.length );
        assertEquals( "public int x;", series[0] );
        assertEquals( "public java.lang.String name;", series[1] );
    }
    
    
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
