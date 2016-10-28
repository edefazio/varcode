package varcode.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
import varcode.context.Resolve.SmartScriptResolver;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.context.eval.VarScript;

public class ResolveTest
	extends TestCase
{
	static final SmartScriptResolver ssr = SmartScriptResolver.INSTANCE;
	
	public void testSmartScriptResolver()
	{
		assertEquals( 
			null, ssr.resolveScript( VarContext.of( ), "not found", null ) );
		
		assertNotNull(
			ssr.resolveScript( 
				VarContext.of( "A", new VarScript()
				{
					public Object eval(VarContext context, String input) 
					{
						return null;
					}			
					
					public void collectAllVarNames( Set<String> collection, String input ) 
					{
					}
				})
			, "A", null ) );
		
		assertNotNull(
			ssr.resolveScript( VarContext.of( ), "java.util.UUID.randomUUID", null ) );
	}
	
	public void testSmartVarResolver()
	{
		
	    //verify bindings in the Context take precedence
		System.setProperty( "A", "Setski" );
		assertEquals( 1, 
			Resolve.SmartVarResolver.INSTANCE.resolveVar( VarContext.of( "A", 1 ), "A" ) );
		
		//verify we can get System Properties
		System.setProperty( "A", "Setski" );
		assertEquals( "Setski", 
			Resolve.SmartVarResolver.INSTANCE.resolveVar( VarContext.of( ), "A" ) );
		
		//we can get values that are set in expressions
		Dom d = BindML.compile("{((A=1))}{+A+}");
		assertEquals("1", Compose.asString( d ) ); //verify I can resolve a variable that is set as expression
		
		d = BindML.compile("{+A+}");
		TreeSet<Integer> ts = new TreeSet<Integer>();
		ts.add(1);
		ts.add(2);
		ts.add(3);
		ts.add(4);
		assertEquals("1, 2, 3, 4", Compose.asString( d , "A", ts ) ); 
		
		//I need a Better way of printing Maps 
		// right now = {A=1, B=2, C=3}
		// should be A=1; B=2; C=3;
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("A", 1);
		m.put("B", 2);
		m.put("C", 3);
		System.out.println(Compose.asString( d, "A", m ) );
		
		d = BindML.compile("{((A=[1,2,3,4]))}{+A+}");
		assertEquals("1, 2, 3, 4", Compose.asString( d ) ); //verify I can resolve a variable that is set as expression
		
				
		
	}
}
