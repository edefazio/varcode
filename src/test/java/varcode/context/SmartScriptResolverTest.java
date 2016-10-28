package varcode.context;

import java.util.UUID;

import varcode.context.Resolve.SmartScriptResolver;
import varcode.doc.lib.text.FirstCap;
import varcode.markup.VarNameAudit;
import varcode.context.eval.VarScript;
import junit.framework.TestCase;

public class SmartScriptResolverTest
	extends TestCase
{
	private static final SmartScriptResolver csr = SmartScriptResolver.INSTANCE;
	
	//just call the UUID method
	public void testNoArgStaticMethod()
	{
		VarContext vc = VarContext.of( );
		
		//verify I can call a no-arg Static method		
		VarScript vs = csr.resolveScript( 
			vc, 
			"java.util.UUID.randomUUID",
			null );
		
		//System.out.println( vs );		
		assertTrue( vs.eval( vc, null ) instanceof UUID );
		
		//verify that the SmartScriptResolver is "plugged in" via the bootstrap
		
		vs = vc.resolveScript( 
				"java.util.UUID.randomUUID",
				null );
		
		assertTrue( vs.eval( vc, null ) instanceof UUID );
		
	}
	
	// USED BELOW
	public static final class NoArgStaticMethod
	{
		public static Object doIt()
		{
			return "HI";
		}		
	}
	
	public void testInnerClassNoArgStaticMethod() 
	{
		VarContext vc = VarContext.of();
		//System.out.println ( NoArgStaticMethod.class.getName()+".doIt" );
		VarScript vs = csr.resolveScript( 
			vc, 
			NoArgStaticMethod.class.getName() + ".doIt", 
			null );
		
		assertEquals( "HI", vs.eval( vc, null ) );
		
		vs = vc.resolveScript( 
			NoArgStaticMethod.class.getName() + ".doIt", 
			null );
		assertEquals( "HI", vs.eval( vc, null ) );
	}
	
	//USED BELOW
	public static final class StringArgStaticMethod
	{
		public static Object doIt( String string )
		{
			return "SO I got this String "+ string;
		}
	}	
	
	public void testStringArgStaticMethod()
	{
		VarContext vc = VarContext.of();
		VarScript vs = csr.resolveScript( 
			vc, 
			StringArgStaticMethod.class.getName() +".doIt",
			"A" );
		
		//System.out.println( vs );		
		assertEquals( "SO I got this String A", vs.eval( vc, null ) );	
		
		vs = vc.resolveScript(  
				StringArgStaticMethod.class.getName() +".doIt",
				"A" );
			
		//System.out.println( vs );		
		assertEquals( "SO I got this String A", vs.eval( vc, null ) );
	}
	
	//USED BELOW
	public static final class ContextArgStaticMethod
	{
		public static Object contextMethod( VarContext context )
		{
			return context.getVarNameAudit().getClass();
		}
	}
	
	public void testVarContextArgStaticMethod()
	{
		VarContext vc = VarContext.of();
		VarScript vs = csr.resolveScript( 
			vc, 
			ContextArgStaticMethod.class.getName() +".contextMethod", null );
		
		assertEquals( VarNameAudit.StandardVarName.class, vs.eval( vc, null ) );
	}
	
	
	//USED BELOW
	public static final class ContextStringArgStaticMethod
	{
		public static Object contextStringMethod( VarContext context, String input )
		{
			return context.getVarNameAudit().getClass().getName() + input;
		}
	}
	
	public void testVarContextStringArgStaticMethod()
	{
		VarContext vc = VarContext.of();
		VarScript vs = csr.resolveScript( 
			vc, 
			ContextStringArgStaticMethod.class.getName() +".contextStringMethod", "HEYO" );
		
		assertEquals( 
			VarNameAudit.StandardVarName.class.getName() + "HEYO", vs.eval( vc, null ) );
	}
	
	
	//USED BELOW
	public static final class ObjectArgStaticMethod
	{
		public static Object objectMethod( Object input )
		{
			return input.toString();
		}
	}
	
	public void testVarObjectArgStaticMethod()
	{
		VarContext vc = VarContext.of();
		VarScript vs = csr.resolveScript( 
			vc, 
			ObjectArgStaticMethod.class.getName() +".objectMethod", "HEYO" );
		
		assertEquals( "HEYO", vs.eval( vc, null ) );
	}
	
	public void testVarContextScript()
	{
		VarContext context = VarContext.of();
		assertEquals( FirstCap.INSTANCE, 
			csr.resolveScript( context , "^", null ) );		
	}
}
