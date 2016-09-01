package varcode.java;

import varcode.java.JavaNaming;
import junit.framework.TestCase;

public class JavaNamingTest
	extends TestCase
{
	public void testPathNameToFileName()
	{
		//full path
		String fileName = "C:\\Dev\\java\\workspace\\com\\mycompany\\Demo.java";
		assertTrue( 
		    "Demo".equals( JavaNaming.ClassName.fromPath( fileName ) ) );
		
		//no path
		fileName = "something.java";
		assertTrue( 
		    "something".equals( JavaNaming.ClassName.fromPath( fileName ) ) );
		
		//System.out.println ("CLASS NAME \""+ Code.Class.validateClassNameFromPathName( fileName ) +"\"");
	}

	public void testPackageNameToPath()
	{
		String packageName = "example.netflix.somepackage";
		assertTrue( "example\\netflix\\somepackage\\".equals( 
		    JavaNaming.PackageName.toPath( packageName ) ) );
		
		assertTrue( "\\".equals( JavaNaming.PackageName.toPath( "" ) ) );
	}
	
	public void testClassName()
	{
		String name = JavaNaming.ClassName.convertName( "(*&!Q#*&^*!@&^" );
		//System.out.println( name );
		assertTrue( "____Q_________".equals( name ) );
		
	}
	
	
	public void testIdentifier()
	{
		String name = "AAKLSDJAKLSJD";
		assertEquals( name, JavaNaming.IdentifierName.validate(name) );
	}
	/*
	public void testPackageName()
	{
		System.out.println( Code.Package.Naming.convertName( "io.semiotics" ) );
		
		assertTrue( Code.Package.Naming.convertName( "io.semiotics" ).equals( "io.semiotics" ) );
		
		
		//check reservd words
		System.out.println( Code.Package.Naming.convertName( "int.semiotics" ) );
		assertTrue( Code.Package.Naming.convertName( "int.semiotics" ).equals( "int_.semiotics" ) );
		
		//check special characters
		System.out.println( Code.Package.Naming.convertName( "int&.semiotics" ) );
		assertTrue( Code.Package.Naming.convertName( "int&.semiotics" ).equals( "int_.semiotics" ) );
		
	}
	*/
	    
	/*
	public void testStaticField()
	{
		String name = "length";
		String value = "3";
		
		String code = Code.StaticField.declarePublicFinal( name, value );
		
		System.out.println( code );
	}
	*/
}
