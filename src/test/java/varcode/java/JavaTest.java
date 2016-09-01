package varcode.java;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.dom.Dom;
import varcode.java.javac.InMemoryJavaCode;
import varcode.java.javac.JavacOptions;
import varcode.markup.bindml.BindML;

public class JavaTest
	extends TestCase
{
	public void testComposeAndLoadClassNoMarks()
	{
		System.out.println( Java.describeEnvironment() );
		Dom emptyMarks = BindML.compile( "public class A { }" );
		InMemoryJavaCode javaCode = 
			Java.author( "A", emptyMarks, VarContext.of( ) );
		
		Class<?> clazz = Java.loadClass( javaCode );
		assertEquals( "A", clazz.getName() );
		
		//now try with some compiler options (compile with Java 1.3 compatibility)
		clazz = Java.loadClass( 
			javaCode, 
			JavacOptions.Flags.ALL_DEBUG_INFORMATION,  
			JavacOptions.JavaSourceVersion.MajorVersion._1_3 );
		assertEquals( "A", clazz.getName() );		
	}
	
	
}

