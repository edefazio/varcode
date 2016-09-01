package varcode.java;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class ReflectTest
	extends TestCase
{

	public static String simpleNoArgMethod()
	{
		return "HEY";
	}
	
	public static String simpleOneArgMethod( String arg)
	{
		return "HEY" + arg;
	}
	
	public int instanceMethod()
	{
		return 1;
	}
	
	public int instanceMethodWithArg( int arg )
	{
		return arg + 10;
	}
	
	public void testGetMethod()
	{
		Method m = Reflect.getMethod( 
			this.getClass().getMethods(), "instanceMethod" );
		
		assertNotNull( m );
		
		m = Reflect.getMethod( 
			this.getClass().getMethods(), "instanceMethodWithArg", int.class );
		
		assertNotNull( m );
	}
	public void testInvokeMethod()
	{
		assertNotNull( Java.describeEnvironment() );
		
		
		assertEquals( "HEY", 
			Java.invokeStatic( ReflectTest.class, "simpleNoArgMethod" ) );
		
		assertEquals( "HEYarg", 
			Java.invokeStatic( ReflectTest.class, "simpleOneArgMethod", "arg" ) );
		
		assertEquals( 1, 
			Java.invoke( this, "instanceMethod" ) );
		
		assertEquals( 11, 
			Java.invoke( this, "instanceMethodWithArg", 1 ) );
	}
}
