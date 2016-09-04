package varcode.java.model;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.javac.AdHocClassLoader;
import varcode.java.javac.Workspace;

/**
 * 
 * @author eric
 *
 */
public class _cloneTest
	extends TestCase
{
	/** 
	 * Here I create an Original Class with some existing
	 * components 
	 */
	public static final _class ORIGINAL_CLASS = 
		_class.of("ex.varcode", "public class _Original")
			.field( "public static String s = \"Hello\";" );
	
	/** 
	 * Here I create an Original Class with some existing
	 * components 
	 */
	public static final _enum ORIGINAL_ENUM = 
		_enum.of("ex.varcode", "public enum _Original")
			.field( "public static String s = \"Hello Enum\";" );
	
	public void testCloneEnum()
	{
		//verify I can create the code and it'll compile to a class
		String originalSource = ORIGINAL_ENUM.toJavaCase( ).toString();
		
		
		//NOW CLONE the code model 
		_enum clone = _enum.from( ORIGINAL_ENUM );
		
		//...mutate the code...		
		clone.replace( "_Original", "Cloned" );
		
		//  change the package
		clone.packageName( "ex.varcode.cloned" );
		
		//  add a field
		clone.field( "public static final int ID = 200;" );
		
		//VERIFY that the originalSource has NOT changed, i.e. we apply
		// changes ONLY to the clone model and NOT the original
		assertEquals( originalSource, ORIGINAL_ENUM.toJavaCase( ).toString() );
		
		
		JavaCase cloneCase = clone.toJavaCase( );
		JavaCase originalCase = ORIGINAL_ENUM.toJavaCase( );
				
		//now compile the modified clone and the original
		//CompiledWorkspace cw = JavaWorkspace.compileNow( cloneCase, originalCase );
		
        AdHocClassLoader adHocClassLoader = 
            Workspace.compileNow( cloneCase, originalCase );
        
		Class<?> cloneClass = adHocClassLoader.findClass( 
            cloneCase.getClassName() );
        
		Class<?> originalClass = adHocClassLoader.findClass( 
            originalCase.getClassName() );
		
		//TODO for some reason getting the package (from the compiled class)
		//  returns null 
		//   MAYBE this is a classloader issue??		
		//System.out.println( originalClass.getPackage() );
		//System.out.println( cloneClass.getPackage() );
		
		//verify the original did not change
		//assertEquals( "ex.varcode", originalClass.getPackage().getName() );
		assertEquals( "Hello Enum", Java.getFieldValue( originalClass, "s" )); 
				
		//verify the clone got the changes
		//assertEquals( "ex.varcode.cloned", cloneClass.getPackage().getName() );
		assertEquals( 200, Java.getFieldValue( cloneClass, "ID" ));
		assertEquals( "Hello Enum", Java.getFieldValue( cloneClass, "s" )); //got this from original
		
		//verify the original DID NOT get the clones mutations
		try
		{  
			Java.getFieldValue( originalClass, "ID" );
			fail("original class was mutated when clone was mutated");
		}
		catch( VarException ve )
		{
			//expected
		}
				
	}
	
	public void testCloneClass()
	{
		//verify I can create the code and it'll compile to a class
		String originalSource = ORIGINAL_CLASS.toJavaCase( ).toString();
		
		
		//NOW CLONE the code model 
		_class clone = _class.from( ORIGINAL_CLASS );
		
		//it and mutate the code...
		clone.replace( "_c", "Cloned" );
		
		//  change the package
		clone.packageName("ex.varcode.cloned");
		//  add a field
		clone.field( "public static final int ID = 100;" );
		
		//VERIFY that the originalSource has NOT changed, i.e. we apply
		// changes ONLY to the clone model and NOT the original
		assertEquals( originalSource, ORIGINAL_CLASS.toJavaCase( ).toString() );
		
		
		JavaCase cloneCase = clone.toJavaCase( );
		JavaCase originalCase = ORIGINAL_CLASS.toJavaCase( );
				
		//now compile the modified clone and the original
		//CompiledWorkspace cw = JavaWorkspace.compile( cloneCase, originalCase );
		AdHocClassLoader adHocClassLoader = 
            Workspace.compileNow( cloneCase, originalCase );
        
		Class<?> cloneClass = adHocClassLoader.findClass( cloneCase.getClassName() );
		Class<?> originalClass = adHocClassLoader.findClass( originalCase.getClassName() );
		
		//TODO for some reason getting the package (from the compiled class)
		//  returns null 
		//   MAYBE this is a classloader issue??		
		//System.out.println( originalClass.getPackage() );
		//System.out.println( cloneClass.getPackage() );
		
		//verify the original did not change
		//assertEquals( "ex.varcode", originalClass.getPackage().getName() );
		assertEquals( "Hello", Java.getFieldValue( originalClass, "s" )); 
				
		//verify the clone got the changes
		//assertEquals( "ex.varcode.cloned", cloneClass.getPackage().getName() );
		assertEquals( 100, Java.getFieldValue( cloneClass, "ID" ));
		assertEquals( "Hello", Java.getFieldValue( cloneClass, "s" )); //got this from original
		
		//verify the original DID NOT get the clones mutations
		try
		{  
			Java.getFieldValue( originalClass, "ID" );
			fail("original class was mutated when clone was mutated");
		}
		catch( VarException ve )
		{
			//expected
		}
				
	}
}
