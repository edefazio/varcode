package varcode.java.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import varcode.VarException;
import varcode.java.Java;

public class test 
{
	private Object instance;
	
	public static test with( Object instance )
	{
		return new test( instance );
	}
	
	public test toString( String expected )
	{
		if( instance.toString().equals( expected ) )
		{
			return this;
		}
		throw new VarException("Expected \""+ expected+"\" got \""+instance.toString()+"\"");
	}
	
	private test( Object instance )
	{
		this.instance = instance;
	}
	
	public test fail( Class<?> exceptionClass, String methodName, Object... arguments )
	{
		Method m = 
			Java.getMethod( 
				this.instance.getClass().getDeclaredMethods(), methodName, arguments );
		
		Object o = null;
		if( m == null )
		{
			throw new VarException( "Could not find method with name \""+methodName+"\"" );
		}
		try
		{	
			o = m.invoke( instance, arguments );
			throw new VarException("expected Exception, instead got result "+ o );
		}
		catch( InvocationTargetException ite )
		{
			if( ite.getCause().getClass().isAssignableFrom( exceptionClass) )
			{
				return this;
			}
			throw new VarException("Threw Exception "+ite.getCause()+" expected exception of "+ exceptionClass );
		}
		catch( Exception e )
		{			
			throw new VarException("Threw Exception "+e+" expectyed exception of "+ exceptionClass );
		}
		
	}
	
	public test fail( String methodName, Object...arguments )
	{
		Method m = 
			Java.getMethod( 
				this.instance.getClass().getDeclaredMethods(), methodName, arguments );
		
		Object o = null;
		if( m == null )
		{
			throw new VarException( "Could not find method with name \""+methodName+"\"" );
		}
		try
		{	
			o = m.invoke( instance, arguments );			
		}
		catch( Exception e )
		{
			return this;
		}
		throw new VarException("expected Exception, insterad got "+ o );
	}
	
	//call the method and ensure it doesnt throw 
	public test ok( String methodName, Object...arguments )
	{
		Method m = 
			Java.getMethod( 
				this.instance.getClass().getDeclaredMethods(), methodName, arguments );
		if( m == null )
		{
			throw new VarException( "Could not find method with name \""+methodName+"\"" );
		}
		try
		{	
			m.invoke( instance, arguments );
			return this;
		}	
		catch( Exception e )
		{
			throw new VarException( "Error invoking method with name \""+methodName+"\"" );
		}			
	}
	
	public test is( Object expected, String methodName, Object...arguments )
	{
		Method m = 
			Java.getMethod( 
				this.instance.getClass().getDeclaredMethods(), methodName, arguments );
		if( m == null )
		{
			throw new VarException( "Could not find method with name \""+methodName+"\"" );
		}
		try
		{
			Object actual = m.invoke( instance, arguments);
			if( actual == null )
			{
				if( expected != null )
				{
					throw new VarException("Expected \""+expected+"\" got null");
				}
				return this;
			}
			else
			{
				if( actual.equals( expected ) )
				{
					return this;
				}
				throw new VarException("Expected \""+expected+"\" got \""+ actual + "\"");
			}
		}
		catch( Exception e )
		{
			throw new VarException( "Error invoking method with name \""+methodName+"\"" );
		}		
	}
	
	public static class TargetClass
	{
		private final int value;
	
		public TargetClass( int value )
		{
			this.value = value;
		}
	
		public static String getId()
		{
			return "ID";
		}
	
		public int getValue()
		{	
			return this.value;
		}
	
		public void methodThrows()
		{
			throw new VarException("throwing this exception");
		}
	}
	
	public static void main(String[] args)
	{
		test.with( new TargetClass( 100 ) )
	    	.is( "ID", "getId" )
	    	.is( 100, "getValue" )
	    	.ok("getId" ) 
	    	.ok("getValue" )
	    	.fail("methodThrows" )
	    	.fail( VarException.class, "methodThrows" );
	}
}