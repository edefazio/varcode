package varcode.java.javac;

import varcode.VarException;
import varcode.java.javac.InMemoryJavaClassLoader;
import varcode.java.javac.InMemoryJavaCode;
import varcode.java.javac.InMemoryJavac;
import varcode.java.javac.JavacOptions;
import varcode.java.javac.JavacOptions.DebugOptions;
import junit.framework.TestCase;

public class InMemoryJavacTest
	extends TestCase
{

	public static final String N = System.lineSeparator();
	
	public void testSimpleWithCompilerFlag()
	{
		String className = "MyClass";
		String code = "public class MyClass{}";
		
		InMemoryJavac.compile(
			new InMemoryJavaClassLoader(), 
			new InMemoryJavaCode( 
				className, 
				code ),
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}

	public void testSimpleWithMultipleCompilerFlags()
	{
		String className = "MyClass";
		String code = "public class MyClass{}";
		
		InMemoryJavac.compile(
			new InMemoryJavaClassLoader(), 
			new InMemoryJavaCode( 
				className, 
				code ),
			JavacOptions.Flags.ALL_DEBUG_INFORMATION,
			JavacOptions.Flags.NO_ANNOTATION_PROCESSING,
			JavacOptions.Flags.STORE_FORMAL_PARAMETER_NAMES_FOR_REFLECTION );
	}
	
	public void testSimpleWithCompilerOptions()
	{
		String className = "MyClass";
		String code = "public class MyClass{}";
		
		InMemoryJavac.compile(
			new InMemoryJavaClassLoader(), 
			new InMemoryJavaCode( 
				className, 
				code ),
			//compiles with Java 1.3 source code compatibility
			JavacOptions.JavaSourceVersion.MajorVersion._1_3 );
	} 

	public void testSimpleCompilerOptions()
	{
		String className = "MyClass";
		String code = 
		    "import java.util.*;" + N 	
		  + "public class MyClass" + N
		  + "{" + N
		  + "    public static void main (String[] args)" + N
		  + "    { " + N 
		  + "        Set<String> set = new HashSet<String>();"
		  + "    }"  + N
		  + "}";
		InMemoryJavac.compile(
			new InMemoryJavaClassLoader(), 
			new InMemoryJavaCode( 
					className, 
					code ),
			//compiles with Java 1.3 source code compatibility			
			JavacOptions.DebugOptions.of(
				DebugOptions.KeyWord.VARS,
				DebugOptions.KeyWord.LINES ) );
			
	}
	
	public void testSimpleWithFailedCompilerOptions()
	{
		String className = "MyClass";
		String code = 
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
			InMemoryJavac.compile(
				new InMemoryJavaClassLoader(), 
				new InMemoryJavaCode( 
						className, 
						code ),
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
		String className = "MyClass";
		String code = 
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
			
			InMemoryJavac.compile(
					new InMemoryJavaClassLoader(), 
					new InMemoryJavaCode( 
						className, 
						code ), 
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
