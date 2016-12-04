package varcode.java.adhoc;

import java.util.ArrayList;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang.macro._autoEnum;

public class WorkspaceTest
	extends TestCase
{
    
    /** Verify that I cant Redefine an existing class */
    public void testWorkspaceRedefineExistingClass()
    {
        //create a new class with the same name as this class 
        //(varcode.java.adhoc.WorkspaceTest)
        _class c = _class.of(
            this.getClass().getPackage().getName(), 
            "public class "+this.getClass().getSimpleName() )
            .field("public static final int ID = 200;");
        
        Workspace ws = Workspace.of( c );
        
        AdHocClassLoader ah = ws.compile( );
        
        //
        Class cl = ah.find( WorkspaceTest.class.getCanonicalName() );
        
        assertEquals(200, _Java.getFieldValue( cl, "ID" ) );
        
    }
    
    /**
     * Test Workspace 
     */
    public void testIncrementallyAddWorkspace()
    {
        _autoEnum auto = _autoEnum.of( "ex.varcode.e.MyEnum" )
            .property( int.class, "age")
            .value( "Eric", 42 );
        
        AdHocClassLoader cl = Workspace.compileNow( auto.toJavaCase( ) );
        
        assertEquals( 42,  
            _Java.invoke( 
                cl.find("ex.varcode.e.MyEnum").getEnumConstants()[ 0 ], "getAge" ) );
        
        auto.value( "Blah", 22 );
        
        //create a new workspace with the SAME classloader
        Workspace ws = new Workspace( cl ); 
        try
        {
            //try to add a Class with the same name
            ws.addCases( auto.toJavaCase( ) );
            
            fail(
                "expected Exception trying to add Class to the workspace that already exists ");
        }
        catch( VarException ve )
        {
            //expected 
        }
    }
    
    // creates two workspaces that contain
    // a Java Class with the same name
    // each workspace has a separate classLoader to load the class
    // so there is no conflict
    public void testDualWorkspaces()
    {
        Workspace a1 = new Workspace()
            .addCode( "A", "public class A{ public static final int ID = 1; }" );
        
        Workspace a2 = new Workspace(  )
            .addCode( "A", "public class A{ public static final int ID = 2; }" );
        
        AdHocClassLoader a1cl = a1.compile( );
        
        AdHocClassLoader a2cl = a2.compile( );
        
        int a1id = (Integer)_Java.getFieldValue( 
            a1cl.find( "A" ), "ID" );
        
        int a2id = (Integer)_Java.getFieldValue( 
            a2cl.find( "A" ), "ID" );
        
        assertEquals( 1, a1id );
        assertEquals( 2, a2id );
    }
    //Fails when we try to redefine an exisitng class in the workspace
    public void testFailOnRedefineClass( String[] args )
    {
        Workspace w = new Workspace();
        w.addCode( "A", 
          "public class A{ public static final int ID = 1; }" );
        ClassLoader c = w.compile( );
        
        try
        {    
            Class cl = c.loadClass( "A" );
            System.out.println( "THE VALUE IS " + 
                _Java.getFieldValue( cl, "ID" ) );
        }
        catch( Exception e )
        {
            fail( e.getMessage() );
        }
        try
        {
            w.addCode( "A",
                "public class A{ public static final int ID=2;}" );    
            fail("expected Exception for attempt to redefine existing class");
        }
        catch( VarException ve )
        {
            //expected
        }
        /*
        c = w.compileC( );
        try
        {    
            Class cl = c.loadClass( "A" );
            System.out.println( "THE VALUE IS " + 
                Java.getFieldValue( cl, "ID" ) );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
*/
    }
    
	public void testCompileEmptyWorkspace()
	{
		Workspace sw = new Workspace(  );		
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
		Workspace sw = new Workspace( );
		
		sw.addCode( "A", "public class A {}");
		AdHocClassLoader ah = sw.compile( );
		assertEquals( "A", ah.find( "A" ).getSimpleName() );
	}
	
	
	//This illustrates WHY we need a Workspace to begin with, here
	// we have (2) classes A_ReliesOn_B, and B_ReliesOn_A, 
	//
	// we want the compiler to be able to Load these classes at the same time 
	public void testCompileBijectiveDependency()
	{
		Workspace sw = new Workspace( );
		sw.addCode("A_ReliesOn_B", 
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
		sw.addCode("B_ReliesOn_A", 
			"public class B_ReliesOn_A {" + N +
			N +
			"    public static final A_ReliesOn_B INSTANCE = " + N +
			"        new A_ReliesOn_B( null );" +  N +
			"    " + N +
			"    public A_ReliesOn_B a;" + N + 
			N +
			"    public B_ReliesOn_A( A_ReliesOn_B a ) {" + N +
			"        this.a = a;" + N +
			"    }" + N +
			N +
			"    public void setA( A_ReliesOn_B a ) {" + N +
			"        this.a = a;" + N +
			"    }" + N + 			
			"}");
		AdHocClassLoader ah = sw.compile( );
		assertNotNull( ah.find("A_ReliesOn_B") );
		assertNotNull( ah.find("B_ReliesOn_A") );
		
		Object a = _Java.instance( ah.find("A_ReliesOn_B"), new Object[]{null});
		Object b = _Java.instance( ah.find("B_ReliesOn_A"), new Object[]{null});
		
		_Java.invoke( a, "setB", b );
		_Java.invoke( b, "setA", a );
		
	}
	
	public void testOneFile()
	{
		Workspace sws = new Workspace();
		sws.addCode( "A", "public class A {}" );
        
		AdHocClassLoader ah = sws.compile( );
		Class<?> AClass = ah.find( "A" );
		assertTrue( AClass != null );
	}
	
	public void testOneFileCompileFailure()
	{
		try
		{
			new Workspace()
		    	.addCode( "A", "asdfklhjasdjklf" )
		    	.compile( );
			fail( "Expected Compiler Exception" );
		}
		catch( JavacException ce )
		{
			//ce.printStackTrace();
			//System.out.println( ce );
		}
	}

	public static String N = "\r\n";
	
	public void testTwoClasses()
	{
            AdHocClassLoader cw = 
		new Workspace()
                    .addCode( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addCode( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    public B( )" 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.find( "A" ) );
		assertNotNull( cw.find( "B" ) );
	}

	// verify that if: 
	// A is dependent on B and 
	// B is dependent on A 
	// ...it still compiles
	public void testTwoClassesCycle()
	{
		AdHocClassLoader cw = 
			new Workspace()
			    .addCode( 
			        "A", 
			        "public class A" + N 
			       +"{" + N
			       +"    private B theB;" + N
			       +"    public A( B theB )" + N 
			       +"    {" + N 
			       +"        this.theB = theB;" + N
			       +"    }" + N 
			       +"}" )
			   .addCode( 
			       "B",
			       "public class B" + N
			       +"{" + N
			       +"    private A a;" + N
			       +"    public B( )" +N 
			       +"    {" + N 
			       +"    }" + N 
			       +"}" )
			   .compile( );
		assertNotNull( cw.find( "A" ) );
		assertNotNull( cw.find( "B" ) );
	}
	
	public void testCompilerException()
	{
		Workspace sw = 
                    new Workspace()
                .addCode( "ExceptionLine3",
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
		Workspace sw = new Workspace()
	    .addCode( 
	        "A", 
	        "public class A" + N 
	       +"{" + N
	       +"    private B theB;" + N
	       +"    public A( B theB )" + N 
	       +"    {" + N 
	       +"        this.theB = theB;" + N
	       +"    }" + N 
	       +"}" )
	   .addCode( 
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
		assertNotNull( sw.compile(JavacOptions.SourceVersion.MajorVersion._1_5 ) );
				
		try
		{   //this will fail (since I have a Generic and source version 1.4 compiler option
			sw.compile( JavacOptions.SourceVersion.MajorVersion._1_4 );			
			fail( "expected Compiler Exception using Generics for  Source Version 1.4" );
		}
		catch( JavacException e )
		{
			//System.out.print( e );
		}		
	}
    
    public void testSimpleWithCompilerFlag()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		Workspace.compileNow( 
			new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code ));}},
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}

	public void testSimpleWithMultipleCompilerFlags()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		Workspace.compileNow( 
			new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code )); }} ,
			JavacOptions.Flags.ALL_DEBUG_INFORMATION,
			JavacOptions.Flags.NO_ANNOTATION_PROCESSING,
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}
	
	public void testSimpleWithCompilerOptions()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		Workspace.compileNow(
			new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code ) );}},
			//compiles with Java 1.3 source code compatibility
			JavacOptions.SourceVersion.MajorVersion._1_3 );
	} 

	public void testSimpleCompilerOptions()
	{
		final String className = "MyClass";
		final String code = 
		    "import java.util.*;" + N 	
		  + "public class MyClass" + N
		  + "{" + N
		  + "    public static void main (String[] args)" + N
		  + "    { " + N 
		  + "        Set<String> set = new HashSet<String>();"
		  + "    }"  + N
		  + "}";
		Workspace.compileNow(
			new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code ) );}},
			//compiles with Java 1.3 source code compatibility			
			JavacOptions.DebugOptions.of(
			JavacOptions.DebugOptions.KeyWord.VARS,
			JavacOptions.DebugOptions.KeyWord.LINES ) );
			
	}
	
	public void testSimpleWithFailedCompilerOptions()
	{
		final String className = "MyClass";
		final String code = 
		    "import java.util.*;" + N 	
		  + "public class MyClass" + N
		  + "{" + N
		  + "    public static void main (String[] args)" + N
		  + "    { " + N 
		  + "        Set<String> set = new HashSet<String>();"
		  + "    }"  + N
		  + "}";
		try
		{
			Workspace.compileNow(  
				new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code ) );}},
				//compiles with Java 1.3 source code compatibility			
				JavacOptions.SourceVersion.MajorVersion._1_3 );
			fail( "expected Exception for Source not compatible " );	
		}
		catch( VarException ve )
		{
			//System.err.println( ve );
			//expected
			//ve.printStackTrace();
		}
	}
	
	/** Figure out how well the Diagnostics return errors */
	public void testErrorMessages()
	{
		final String className = "MyClass";
		final String code = 
			"public class MyClass" + N
		   +"{" + N
		   +"    @Deprecated" + N
		   +"    public static void main(String[]args) " + N
		   +"    {"
		   +"        int j = 1 / A;" + N
		   +"    }" + N
		   +"}";
		
		try
		{
			Workspace.compileNow( 
				new ArrayList<AdHocJavaFile>(){{ add(new AdHocJavaFile( className, code ) );}},
				JavacOptions.SourceVersion.MajorVersion._1_3 );
				//JavacCompiler.Flags.VERSION );
			fail( "Expected Exception for Trying to Compile annotations with old Java Version" );
		}
		catch( VarException e )
		{
			//e.printStackTrace();
		}
	}
}
