package varcode.doc.lib.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import junit.framework.TestCase;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.lib.java.JavaContextValidator;

public class JavaContextValidatorTest
	extends TestCase
{
	
	public void testValidateContext()
	{
		JavaContextValidator.validateClassName(
			VarContext.of("className", "SomeValidClass_Name" ) );
		
		JavaContextValidator.validateClassName(
			VarContext.of("cn", "SomeValidClassanem" ), "cn" );
		
		JavaContextValidator.validateClassName(
			VarContext.of("cn", "SomeValidClassanem" ), "cn" );
		
		JavaContextValidator.validateIdentifier(VarContext.of("id", "someId" ), "id" );
		
		//w3e can validate an array of identifiers
		JavaContextValidator.validateIdentifier(
			VarContext.of("id", new String[]{"a", "b", "c"} ), "id" );
		
		//w3e can validate a collection of identifiers
		ArrayList<String>arrList = new ArrayList<String>();
		String[] arr =  {"a", "b", "c"};
		arrList.addAll( Arrays.asList(arr) );
		
		JavaContextValidator.validateIdentifier(
			VarContext.of("id",  arr), "id" );
		
		JavaContextValidator.validateType(
			VarContext.of("type",  int.class), "type" );
		
		JavaContextValidator.validateType(
			VarContext.of("type",  new Class[]{int.class, float.class, Date.class, UUID.class}), "type" );
		
		JavaContextValidator.validateType(
				VarContext.of("type", 
					new Object[]{int.class, String.class, "String", float.class, Date.class, UUID.class}), "type" );
		
	}
	
	public void testInvalidContext()
	{
		try
		{
			JavaContextValidator.validateIdentifier(
				VarContext.of("id", new String[]{"a", "b", "c", "int"} ), "id" );
			fail("Expected Exception");
		}
		catch( VarException ve )
		{
			
		}
		
		//w3e can validate a collection of identifiers
		ArrayList<String>arrList = new ArrayList<String>();
			String[] arr =  {"a", "b", "c"};
			arrList.addAll( Arrays.asList(arr) );
			arrList.add( "^%#@" );
			
		try
		{
			JavaContextValidator.validateIdentifier(
				VarContext.of("id",  arrList), "id" );
			fail("expected Exception ");
		}
		catch( VarException ve )
		{
			//expected
		}
		
		try
		{
			JavaContextValidator.validateType(
				VarContext.of("type", 
					new Object[]{int.class, "1289734", UUID.class}), "type" );
			fail("expected Exception ");
		}
		catch( VarException ve )
		{
			//expected
		}
			
		try
		{
			JavaContextValidator.validateIdentifier(VarContext.of( ), "id" );
		}
		catch( VarException ve )
		{
			
		}
		
		try
		{
			JavaContextValidator.validateClassName(VarContext.of( ));
		}
		catch( VarException ve )
		{
			
		}
	}

}
