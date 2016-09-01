package varcode.java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class _nestTest
	extends TestCase
{
	/** each nest component is currently:
	 * <UL>
	 *  <LI>in a separate package
	 *  <LI>has it's own imports
	 * </UL> 
	 */
	public _class CLAZZ = _class.of(
		"ex.varcode.nesttest.clazz", 
		"public class Clazz" )
		.imports( Map.class, HashMap.class )
		.field("public Map<String,Integer> aMap = new HashMap<String,Integer>();");
	
	public _interface INTERFACE = _interface.of( 
		"ex.varcode.nesttest.interf",
		"public interface Interf" )
		.imports( Set.class, HashSet.class )		
		.field("public Set<String> words = new HashSet<String>();");
	
	public _enum ENUM = _enum.of( 
		"ex.varcode.nesttest.en",
		"public enum Enumer" )
		.imports( List.class, ArrayList.class )		
		.field("public List<Integer> counts = new ArrayList<Integer>();");
	
	//verify I could author each and they compile as separate classes
	public void testCompileTopLevel()
	{
		//just verify they each compile
		CLAZZ.toJavaCase( ).loadClass();
		INTERFACE.toJavaCase( ).loadClass();
		ENUM.toJavaCase( ).loadClass();
	}
	
	//verify that I can nest each under another class 
	public void testNestEachOne()
	{
		_class topClass = _class.of(
			"ex.varcode.newpackage", 
			"public class TopClass")
				.nest( CLAZZ ); //this should cause the imports to trikle up 
		assertTrue( topClass.getImports().contains("java.util.Map") ); 
		assertTrue( topClass.getImports().contains("java.util.HashMap") );
		topClass.toJavaCase( ).loadClass();
		
		topClass = _class.of(
			"ex.varcode.newpackage", 
			"public class TopClass")
				.nest( INTERFACE );
		topClass.toJavaCase( ).loadClass();
		
		topClass = _class.of(
			"ex.varcode.newpackage", 
			"public class TopClass")
				.nest( ENUM );
		topClass.toJavaCase( ).loadClass();
	}
	
	// verify I can nest all of these classes within another type
	public void testNestAll()
	{
		_class topClass = _class.of(
			"ex.varcode.newpackage", 
			"public class TopClass")
				.nest( CLAZZ ) 
			    .nest( INTERFACE )
			    .nest( ENUM );
		assertTrue( topClass.getImports().contains("java.util.Map") ); 
		assertTrue( topClass.getImports().contains("java.util.HashMap") );
		assertTrue( topClass.getImports().contains("java.util.Set") ); 
		assertTrue( topClass.getImports().contains("java.util.HashSet") );
		assertTrue( topClass.getImports().contains("java.util.List") ); 
		assertTrue( topClass.getImports().contains("java.util.ArrayList") );
		
		topClass.toJavaCase( ).loadClass();
	}
	
	/** Verify that a nested class can have it's own static block */
	public void testNestStaticBlocks()
	{
		_class top = _class.of("public class TheTop")
			.nest( 
				_class.of("public static class Middle") 
					.staticBlock("System.out.println(\"Nested Static Block\");")
				);
		top.toJavaCase( ).loadClass();
	}
}
