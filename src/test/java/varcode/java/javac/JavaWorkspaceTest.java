package varcode.java.javac;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.Java;
import varcode.java.javac.JavaWorkspace.CompiledWorkspace;
import varcode.java.javac.JavaWorkspace.SourceWorkspace;

public class JavaWorkspaceTest
	extends TestCase
{

	public void testCompileEmptyWorkspace()
	{
		SourceWorkspace sw = new SourceWorkspace( "NO files" );		
		try
		{
			sw.compile( );
			fail( "Expected Exception for no source files");
		}
		catch( VarException ve )
		{
			//expected
		}
	}
	
	public void testCompileWorkspace()
	{
		SourceWorkspace sw = new SourceWorkspace("Single File");
		
		sw.addJavaSource( "A", "public class A {}");
		CompiledWorkspace cw = sw.compile( );
		assertEquals( "A", cw.getClass( "A" ).getSimpleName() );
	}
	
	
	//This illustrates WHY we need a Workspace to begin with, here
	// we have (2) classes A_ReliesOn_B, and B_ReliesOn_A, 
	//
	// we want the compiler to be able to Load these classes at the same time 
	public void testCompileBijectiveDependency()
	{
		SourceWorkspace sw = new SourceWorkspace("Bijective Dependency");
		sw.addJavaSource("A_ReliesOn_B", 
			"public class A_ReliesOn_B {" + N +
		    N +
		    "    public static final B_ReliesOn_A INSTANCE = " + N +
			"        new B_ReliesOn_A( null );" +  N +
		    "    " + N +
		    "    public B_ReliesOn_A b;" + N +
		    N +
			"    public A_ReliesOn_B( B_ReliesOn_A b) {" + N +
			"        this.b = b;" + N +
			"    }" + N +
			N +
			"    public void setB(B_ReliesOn_A b) {" + N +
			"        this.b = b;" + N +
			"    }" + N + 
			"}");
		sw.addJavaSource("B_ReliesOn_A", 
			"public class B_ReliesOn_A {" + N +
			N +
			"    public static final A_ReliesOn_B INSTANCE = " + N +
			"        new A_ReliesOn_B( null );" +  N +
			"    " + N +
			"    public A_ReliesOn_B a;" + N + 
			N +
			"    public B_ReliesOn_A( A_ReliesOn_B a) {" + N +
			"        this.a = a;" + N +
			"    }" + N +
			N +
			"    public void setA(A_ReliesOn_B a) {" + N +
			"        this.a = a;" + N +
			"    }" + N + 			
			"}");
		CompiledWorkspace cw = sw.compile( );
		assertTrue( cw.getClassNames().contains("A_ReliesOn_B") );
		assertTrue( cw.getClassNames().contains("B_ReliesOn_A") );
		
		Object a = Java.instance( cw.getClass("A_ReliesOn_B"), new Object[]{null});
		Object b = Java.instance( cw.getClass("B_ReliesOn_A"), new Object[]{null});
		
		Java.invoke( a, "setB", b );
		Java.invoke( b, "setA", a );
		
	}
	
	public void testOneFile()
	{
		SourceWorkspace sws = JavaWorkspace.of( "A alone" );
		sws.addJavaSource( "A", "public class A {}" );
		CompiledWorkspace cw = sws.compile( );
		Class<?> AClass = cw.getClass( "A" );
		assertTrue( AClass != null );
	}
	
	public void testOneFileCompileFailure()
	{
		try
		{
			JavaWorkspace.of( "EXPECT COMPILER EXCEPTION" )
		    	.addJavaSource( "A", "asdfklhjasdjklf" )
		    	.compile( );
			fail( "Expected Compiler Exception" );
		}
		catch( JavacException ce )
		{
			ce.printStackTrace();
			System.out.println( ce );
		}
	}

	public static String N = System.lineSeparator();
	
	public void testTwoClasses()
	{
		CompiledWorkspace cw = 
			JavaWorkspace.of( "A AND B")
			    .addJavaSource( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addJavaSource( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    public B( )" 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.getClass("A") );
		assertNotNull( cw.getClass("B") );
	}

	// verify that if: 
	// A is dependent on B and 
	// B is dependent on A 
	// ...it still compiles
	public void testTwoClassesCycle()
	{
		CompiledWorkspace cw = 
			JavaWorkspace.of( "A AND B")
			    .addJavaSource( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" + N 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addJavaSource( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    private A a;" + N
			       +"    public B( )" +N 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.getClass( "A" ) );
		assertNotNull( cw.getClass("B") );
	}
	
	public void testCompilerException()
	{
		SourceWorkspace sw = 
			JavaWorkspace.of( "EXCEPTION ON LINE 3" )
			.addJavaSource( "ExceptionLine3",
				"public class ExceptionLine3 {" + N 
			   +"    public int a = 0;" + N
			   +"    public Exception here; " + N
			   +"}" );
		
		try
		{
			sw.compile( ); 
		}
		catch( JavacException e )
		{
			//System.out.println( e );
			//verify that the exception returns a messae
			assertTrue( e.getMessage().contains( "line[ 3 ]" ) );
			assertTrue( e.getMessage().contains( "Exception here;" ) );
		}
	}
	
	public void testTwoClassesFailCompilerOption()
	{
		SourceWorkspace sw = JavaWorkspace.of( "COMPILER EXCEPTION EXPECTED")
	    .addJavaSource( 
	        "A", 
	        "public class A" + N 
	       +"{" + N
	       +"    private B theB;" + N
	       +"    public A( B theB )" + N 
	       +"    {" + N 
	       +"        this.theB = theB;" + N
	       +"    }" + N 
	       +"}" )
	   .addJavaSource( 
	       "B",
	       "import java.util.*;" + N 
	       +"public class B" + N
	       +"{" + N
	       +"    public List<String> a;" + N
	       +"    public B( )" +N 
	       +"    {" + N 
	       +"    }" + N 
	       +"}" );
		
		//this wil compile just fine if Source compatibility >= 1.5
		assertNotNull( sw.compile(JavacOptions.JavaSourceVersion.MajorVersion._1_5 ) );
				
		try
		{   //this will fail (since I have a Generic and source version 1.4 compiler option
			sw.compile( JavacOptions.JavaSourceVersion.MajorVersion._1_4 );			
			fail( "expected Compiler Exception using Generics for  Source Version 1.4" );
		}
		catch( JavacException e )
		{
			System.out.print( e );
		}		
	}
}
