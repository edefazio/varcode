package varcode.java.javac;

import java.util.ArrayList;
import varcode.VarException;
import varcode.java.javac.JavacOptions.DebugOptions;
import junit.framework.TestCase;

public class AdHocJavacTest
	extends TestCase
{

	public static final String N = System.lineSeparator();
	
	public void testSimpleWithCompilerFlag()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		AdHocJavac.compile(new AdHocClassLoader(), 
			new ArrayList(){{ add(new AdHocJavaFile( className, code ));}},
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}

	public void testSimpleWithMultipleCompilerFlags()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		AdHocJavac.compile(new AdHocClassLoader(), 
			new ArrayList(){{ add(new AdHocJavaFile( className, code )); }} ,
			JavacOptions.Flags.ALL_DEBUG_INFORMATION,
			JavacOptions.Flags.NO_ANNOTATION_PROCESSING,
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}
	
	public void testSimpleWithCompilerOptions()
	{
		final String className = "MyClass";
		final String code = "public class MyClass{}";
		
		AdHocJavac.compile(new AdHocClassLoader(), 
			new ArrayList(){{ add(new AdHocJavaFile( className, code ) );}},
			//compiles with Java 1.3 source code compatibility
			JavacOptions.JavaSourceVersion.MajorVersion._1_3 );
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
		AdHocJavac.compile(new AdHocClassLoader(), 
			new ArrayList(){{ add(new AdHocJavaFile( className, code ) );}},
			//compiles with Java 1.3 source code compatibility			
			JavacOptions.DebugOptions.of(
				DebugOptions.KeyWord.VARS,
				DebugOptions.KeyWord.LINES ) );
			
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
			AdHocJavac.compile(new AdHocClassLoader(), 
				new ArrayList(){{ add(new AdHocJavaFile( className, code ) );}},
				//compiles with Java 1.3 source code compatibility			
				JavacOptions.JavaSourceVersion.MajorVersion._1_3 );
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
			
			AdHocJavac.compile(
                new AdHocClassLoader(), 
				new ArrayList(){{ add(new AdHocJavaFile( className, code ) );}},
				JavacOptions.JavaSourceVersion.MajorVersion._1_3 );
				//JavacCompiler.Flags.VERSION );
			fail( "Expected Exception for Trying to Compile annotations with old Java Version" );
		}
		catch( VarException e )
		{
			//e.printStackTrace();
		}
	}
}
