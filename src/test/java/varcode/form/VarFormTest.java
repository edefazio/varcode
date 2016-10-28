package varcode.form;

import varcode.doc.form.SeriesFormatter;
import varcode.doc.form.VarForm;
import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.markup.forml.ForMLCompiler;

public class VarFormTest
	extends TestCase
{
	
	public void testNameValue()
	{
		VarForm vf = new VarForm(
			ForMLCompiler.compileTemplate( "public {+type*+} {+fieldName*+};"),
			new SeriesFormatter.BetweenTwo( System.lineSeparator() ) );
		
		String formed = vf.derive(
			VarContext.of(
				"type",	 new Object[]{ 
							int.class,float.class,double.class,short.class,byte.class, long.class },
				"fieldName", new String[]{ "A","B","C","D","E","F" }) );
		System.out.println( formed );
	}
	
	public void testNameValueClass()
	{
		VarForm vf = new VarForm(
				ForMLCompiler.compileTemplate( "public {+type*+} {+fieldName*+};"),
				new SeriesFormatter.BetweenTwo( " " ) );
			
		String formed = vf.derive(
			VarContext.of(
				"type",	 new Object[]{ String.class, String.class },
				"fieldName", new String[]{"A", "B"} ) );
			
		//System.out.println( formed );
			
		assertEquals( "public String A; public String B;", formed );
			
		formed = vf.derive(
			VarContext.of(
				"type",	 String.class,
				"fieldName", "A") );
		
		//System.out.println( formed );
		
		assertEquals( "public String A;", formed );
	}
	
	public void testOnlyVarInline()
	{
		VarForm vf = new VarForm(
			ForMLCompiler.compileTemplate( "{+a+}"), 
			SeriesFormatter.INLINE );
		
		assertEquals( "" , vf.derive( VarContext.of( ) ) );
		assertEquals( "A" , vf.derive( VarContext.of("a", "A" ) ) );
		
		assertEquals( "ABCDEF" , vf.derive( VarContext.of("a", "ABCDEF" ) ) );
		assertEquals( "ABCDEF" , vf.derive( VarContext.of("a", new String[]{"A","B","C","D","E","F" } ) ) );		
	}
	
	public void testTranslate()
	{
		VarForm vf = new VarForm(
			ForMLCompiler.compileTemplate( "{+a+}"), 
			SeriesFormatter.INLINE );
		
		String formed = vf.derive( 
			VarContext.of(
				"a", new Object[]{ 
					int.class,float.class,double.class,short.class,byte.class, long.class } ) );
		
		System.out.println( formed );
		
		assertEquals( "intfloatdoubleshortbytelong" , formed );
		
		vf = new VarForm(
			ForMLCompiler.compileTemplate( "{+a+}" ), 
			new SeriesFormatter.BetweenTwo(", ") );
		
		formed = vf.derive( 
			VarContext.of(
				"a", new Object[]{ 
					int.class,float.class,double.class,short.class,byte.class, long.class } ) );
		
		assertEquals( "int, float, double, short, byte, long" , formed );
	}
}
