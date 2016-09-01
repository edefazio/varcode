package varcode.doc.lib.text;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.lib.text.FirstCap;

public class FirstCapsTest
	extends TestCase
{	
	public void testFirstCapsArray()
	{
		String[] strs = (String[]) FirstCap.doFirstCaps( new String[] {"a", "bee", "catch", "DEE" });
		assertEquals( strs[0], "A" );
		assertEquals( strs[1], "Bee" );
		assertEquals( strs[2], "Catch" );
		assertEquals( strs[3], "DEE" );		
	}
	
	public void testFirstCapsList()
	{
		List<String> theList = new ArrayList<String>();
		
		theList.add( "a" );
		theList.add( "bee" );
		theList.add( "catch" );
		theList.add( "DEE" );
		
		String[] strs = (String[]) FirstCap.doFirstCaps( theList );
		
		assertEquals( strs[0], "A" );
		assertEquals( strs[1], "Bee" );
		assertEquals( strs[2], "Catch" );
		assertEquals( strs[3], "DEE" );		
	}
	
	
	public void testFirstCapsVar()
	{
		//works on the var "name"
		assertEquals( "Eric", VarContext.of( ).resolveScript( "^", "eric" ).eval(
			VarContext.of("name", "eric" ), "name" ) );		
	}
	
	/*
	public void testFirstCapsChained()
	{
		//works on the var "name" after calling $trim()
	    assertEquals( "Eric", VarContext.of( ).getVarScript( "^" ).eval(
			VarContext.of( "name", "  eric  " ), "$trim(name)" ) );		
	}
	
	*/
	
	/*
	public void testFirstCapsChainedArray()
	{
		//works on the var "name" after calling $trim()
	    String[] arr = (String[]) 
	    	VarContext.of( ).getVarScript( "^" ).eval(
			VarContext.of( "name", new String[]{"  eric  ", " peggy ", "    jerry "} ), 
			"$trim(name)" );
	    
	    assertEquals( "Eric", arr[ 0 ] );
	    assertEquals( "Peggy", arr[ 1 ] );
	    assertEquals( "Jerry", arr[ 2 ] );
	}
	*/
	
	/*
	public void testFirstCapsChainedArray2()
	{ 
	    AddScriptResult m = (AddScriptResult) BindML.parseMark( "{+$^($trim(name))+}" );
	    
	    assertEquals( "$trim(name)", m.getScriptInput() );
	    
	    String[] arr = (String[]) m.derive( 
	    	VarContext.of("name",  new String[]{ "  eric  ", " peggy ", "    jerry " } ) );
	    
	    assertEquals( "Eric", arr[ 0 ] );
	    assertEquals( "Peggy", arr[ 1 ] );
	    assertEquals( "Jerry", arr[ 2 ] );	    
	}
	*/
}
