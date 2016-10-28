package varcode.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.context.eval.EvalException;
import varcode.context.eval.Evaluator;
import varcode.context.eval.VarScript;

/**
 * Algorithms for resolving values for named 
 * vars (key value pairs) {@code VarScript}s, and {@code Directive}s
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Resolve 
{
	;
	
	private static final Logger LOG = 
        LoggerFactory.getLogger( Resolve.class );
	
	public static Method tryAndGetMethod( 
			Class<?> clazz, String name, Class<?>...params )
	{
		try 
		{
			return clazz.getMethod( name, params );
		} 
		catch( NoSuchMethodException e ) 
		{
			return null;
		} 
		catch( SecurityException e ) 
		{
			return null;
		}
	}
	
	public static Class<?> getClassForName( String className )
	{
		try 
		{
			Class<?>c = Class.forName( className ); 
			
			return c;
		} 
		catch ( ClassNotFoundException e ) 
		{
			LOG.debug( "    class \"" + className + "\" not found " );
			return null;
		}
	}
	
	/**
	 * Knows how to resolve the value of a (data) Var given it's name
	 */
	public interface VarResolver		
	{		
		/**
		 * @param context the context to resolve the Var
		 * @param varName the name of the var to resolve
		 * @return the var or null
		 */
		public Object resolveVar( VarContext context, String varName );
	}
	
	/** 
	 * Knows how to resolve a VarScript
	 */
	public interface ScriptResolver
	{	
		public VarScript resolveScript( 
			VarContext context, String scriptName, String scriptInput );
	}
	
	public interface DirectiveResolver
	{
		public Directive resolveDirective ( 
			VarContext context, String directiveName );
	}

	public enum SmartDirectiveResolver
		implements DirectiveResolver
	{
		INSTANCE;	
	
		/** Return the Singleton INSTANCE VarScript */  
		private static VarScript getSingletonField( Class<?> clazz )
		{
			try
			{							
				Field field = clazz.getField( "INSTANCE" );
				if( Modifier.isStatic( field.getModifiers() ) )
				{
					return (VarScript)field.get( null );
				}
				return null;
			}
			catch( Exception e )
			{							
				return null;
			}	
		}
		
	public Directive resolveDirective( 
		VarContext context, String directiveName ) //, String scriptInput ) 
	{
		if( LOG.isTraceEnabled() ) { LOG.trace( "   resolving directive \"" + directiveName + "\""  ); }
		// 1) see if the script is loaded in the context
		String directiveLookupName = "$" + directiveName ;
		if( LOG.isTraceEnabled() ) { LOG.trace( "   1) checking context for \"" + directiveLookupName + "\""  ); }
		Object directive = context.get( directiveLookupName );
		if( directive != null )
		{
			if( directive instanceof Directive )
			{
				LOG.trace( "   Found directive \"" + directiveLookupName + "\" in context " + directive );
				return (Directive)directive;
			}				
		}	
		
		//2) check if there is a Markup Class Registered that has a method of this name
		/*
		Object markupClass = context.get( "markup.class" );
		
		if( markupClass != null )
		{
			if( LOG.isTraceEnabled() ) { LOG.trace( "   2) checking \"" + markupClass + "\" for static method \"" + directiveName + "\""  ); }
			VarScript markupClassScript = findStaticMethod( 
				context,  
				(Class<?>) markupClass, 
				scriptName,
				scriptInput );
			
			if( markupClassScript != null )
			{
				if( LOG.isTraceEnabled() ) { 
					LOG.trace( "   Found VarScript as method \"" + scriptName + "\" from MarkupClass \"" + markupClass + "\""  ); }
			}
			return markupClassScript;
		}
		else
		{
			if( LOG.isTraceEnabled() ) { LOG.trace( "   2) no \"markup.class\" in context provided in context"  ); }
		}
		*/
		
		//I COULD have ScriptBindings where I "manually" Register/
		// assign scripts (i.e. Script)s to names
		// i.e. "java.util.UUID.randomUUID"();
		
		// IF the name contains a '.' (run (2) and (3) 
		int indexOfLastDot = directiveName.lastIndexOf( '.' );
		
		if( indexOfLastDot > 0 )
		{
			String theMethodName = directiveName.substring( 
				indexOfLastDot + 1, 
				directiveName.length() );
			
			String theClassName = directiveName.substring( 0, indexOfLastDot );
			if( LOG.isTraceEnabled() ) { LOG.trace( "   3) checking for class \"" + theClassName + "\" for static method \"" + theMethodName + "\""  ); }
			
			Class<?> clazz = getClassForName( theClassName );
			
			if( clazz != null )
			{	
				if( LOG.isTraceEnabled() ) {
					LOG.trace( "  resolved class \"" + clazz  + "\"" );
				}
				//does the class implement Directive?
				if( Directive.class.isAssignableFrom( clazz  ) )
				{
					if( LOG.isTraceEnabled() ) {
						LOG.trace( "  class \"" + clazz  + "\" is a VarScript" );
					}
					if( clazz.isEnum() )
					{
						if( LOG.isTraceEnabled() ) {
							LOG.trace( "  class \"" + clazz  + "\" is a VarScript & an enum " );
						}
						return (Directive)clazz.getEnumConstants()[ 0 ];
					}
					Object singleton = getSingletonField( clazz );
					if( singleton != null && LOG.isTraceEnabled() ) 
					{
						LOG.trace( "  returning INSTANCE field on \"" + clazz  + "\" as VarScript" );
					}
					try
					{   LOG.trace( "  trying to create (no-arg) instance of \"" + clazz  + "\" as VarScript" );
						return (Directive )clazz.newInstance();
					}
					catch( Exception e )
					{
						LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript" );
						return null;
					}
				}
				else
				{   //found a class, now find the method, (just chooses the first one by this name)
					if( LOG.isTraceEnabled() ) {
						LOG.trace( "  resolving static Method \"" + theMethodName 
							+ "\" on class \"" + clazz + "\"" );
					}
					return findStaticMethod( 
						context, clazz, theMethodName  );
				}					
			}				
		}
		LOG.trace( "  no directive bound for \"" + directiveName  + "\"" );
		return null;			
	}
	
	private static Directive findStaticMethod( 
		VarContext context,  
		Class<?> clazz, 
		String methodName )
	{
		try
		{
			Method m = tryAndGetMethod( 
				clazz, methodName, VarContext.class );
			if( m != null )
			{
				return new Directive.StaticMethodPreProcessAdapter( m, context );
			}
			
			m = tryAndGetMethod( clazz, methodName );
			if( m != null )
			{
				return new Directive.StaticMethodPreProcessAdapter( m );
			}
			return null;				
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
	
	/**
	 * Adapts a Static Method call to the {@code VarScript} interface
	 * so that we might call static methods as if they implemented
	 * {@code VarScript}  
	 *  
	 */
	public static class StaticMethodScriptAdapter
		implements VarScript
	{
		private final Method method;
		
		private final Object[] params;
		
		public StaticMethodScriptAdapter( Method method, Object... params )
		{
			this.method = method;
			if( params.length == 0 )
			{
				this.params = null;
			} 
			else
			{
				this.params = params;
			}			
		}

		public Object eval( VarContext context, String input ) 
		{
			try 
			{
				return method.invoke( null, params );
			} 
			catch( IllegalAccessException e ) 
			{
				throw new EvalException( e );
			} 
			catch( IllegalArgumentException e ) 
			{
				throw new EvalException( e );
			} 
			catch( InvocationTargetException e ) 
			{
				if( e.getCause() instanceof VarException )
				{
					throw (VarException) e.getCause();
				}
				throw new EvalException( e.getCause() );
			}
		}
		
		public void collectAllVarNames( Set<String> collection, String input ) 
		{
			//do nothing
			//return collection;
		}
		
		public String toString()
		{
			return "Wrapper to " + method.toString();
		}		
	}
	

	
	public enum SmartScriptResolver
		implements ScriptResolver
	{
		INSTANCE;
		
		private static VarScript findStaticMethod( 
			VarContext context,  
			Class<?> clazz, 
			String methodName,
			String scriptInput )
		{
			try
			{
				Method m = tryAndGetMethod( 
					clazz, methodName, VarContext.class, String.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, context, scriptInput );
				}
				m = tryAndGetMethod( 
					clazz, methodName, VarContext.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, context );
				}
				m = tryAndGetMethod( 
						clazz, methodName, String.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, scriptInput );
				}
				
				m = tryAndGetMethod( 
						clazz, methodName, Object.class );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m, scriptInput );
				}				
				m = tryAndGetMethod( clazz, methodName );
				if( m != null )
				{
					return new StaticMethodScriptAdapter( m );
				}
				return null;				
			}
			catch( Exception e )
			{
				return null;
			}
		}
		
		/** Return the Singleton INSTANCE VarScript */  
		private static VarScript getSingletonField( Class<?> clazz )
		{
			try
			{							
				Field field = clazz.getField( "INSTANCE" );
				if( Modifier.isStatic( field.getModifiers() ) )
				{
					return (VarScript)field.get( null );
				}
				return null;
			}
			catch( Exception e )
			{							
				return null;
			}	
		}
		
		public VarScript resolveScript( 
			VarContext context, String scriptName, String scriptInput ) 
		{
			if( LOG.isTraceEnabled() ) { LOG.trace( "   resolving script \"" + scriptName + "\""  ); }
			// 1) see if the script is loaded in the context
			String scriptLookupName = "$" + scriptName ;
			if( LOG.isTraceEnabled() ) { LOG.trace( "   1) checking context for \"" + scriptLookupName + "\""  ); }
			Object vs = context.get( scriptLookupName );
			if( vs != null )
			{
				if( vs instanceof VarScript )
				{
					LOG.trace( "   Found script \"" + scriptLookupName + "\" in context " + vs );
					return (VarScript)vs;
				}				
			}	
			
			//2) check if there is a Markup Class Registered that has a method of this name
			
			Object markupClass = context.get( "markup.class" );
			
			if( markupClass != null )
			{
				if( LOG.isTraceEnabled() ) { LOG.trace( "   2) checking \"" + markupClass + "\" for static method \"" + scriptName + "\""  ); }
				VarScript markupClassScript = findStaticMethod( 
					context,  
					(Class<?>) markupClass, 
					scriptName,
					scriptInput );
				
				if( markupClassScript != null )
				{
					if( LOG.isTraceEnabled() ) { 
						LOG.trace( "   Found VarScript as method \"" + scriptName + "\" from MarkupClass \"" + markupClass + "\""  ); }
					return markupClassScript;
				}				
			}
			else
			{
				if( LOG.isTraceEnabled() ) { LOG.trace( "   2) no \"markup.class\" in context provided in context"  ); }
			}
			
			//I COULD have ScriptBindings where I "manually" Register/
			// assign scripts (i.e. Script)s to names
			// i.e. "java.util.UUID.randomUUID"();
			
			
			// IF the name contains a '.' (run (2) and (3) 
			int indexOfLastDot = scriptName.lastIndexOf( '.' );
			
			if( indexOfLastDot > 0 )
			{
				if( LOG.isTraceEnabled() ) { LOG.trace( "   3) checking for class \"" + scriptName + ".class\"" ); }
				Class<?> clazz = getClassForName( scriptName );				
				if( clazz != null && VarScript.class.isAssignableFrom( clazz ) )
				{	
					LOG.trace( "      a) returning VarScript class ");
					if( clazz.isEnum() )
					{
						LOG.trace( "      b) returning VarScript Enum" ); 
						return (VarScript) clazz.getEnumConstants()[ 0 ];
					}
					VarScript instanceField = getSingletonField( clazz );  
					if( instanceField != null )
					{
						LOG.trace( "      c) resolved VarScript INSTANCE Field" ); 
						return instanceField;
					}
				}
				
				
				
				//maybe they passed in a class and method name
				String theMethodName = scriptName.substring( 
					indexOfLastDot + 1, 
					scriptName.length() );
				
				String theClassName = scriptName.substring( 0, indexOfLastDot );
				if( LOG.isTraceEnabled() ) { LOG.trace( "   4) checking for class \"" + theClassName + "\" for static method \"" + theMethodName + "\""  ); }
				
				clazz = getClassForName( theClassName );
				
				if( clazz != null )
				{	
					if( LOG.isTraceEnabled() ) {
						LOG.trace( "  resolved class \"" + clazz  + "\"" );
					}
					//does the class implement VarScript?
					if( VarScript.class.isAssignableFrom( clazz  ) )
					{
						if( LOG.isTraceEnabled() ) {
							LOG.trace( "  class \"" + clazz  + "\" is a VarScript" );
						}
						if( clazz.isEnum() )
						{
							if( LOG.isTraceEnabled() ) {
								LOG.trace( "  class \"" + clazz  + "\" is a VarScript & an enum " );
							}
							return (VarScript)clazz.getEnumConstants()[ 0 ];
						}
						Object singleton = getSingletonField( clazz );
						if( singleton != null && LOG.isTraceEnabled() ) 
						{
							LOG.trace( "  returning INSTANCE field on \"" + clazz  + "\" as VarScript" );
						}
						try
						{   LOG.trace( "  trying to create (no-arg) instance of \"" + clazz  + "\" as VarScript" );
							return (VarScript)clazz.newInstance();
						}
						catch( Exception e )
						{
							LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript" );
							return null;
						}
					}
					else
					{   //found a class, now find the method, (just chooses the first one by this name)
						if( LOG.isTraceEnabled() ) {
							LOG.trace( "  resolving static Method \"" + theMethodName 
								+ "\" on class \"" + clazz + "\"" );
						}
						return findStaticMethod( 
							context, clazz, theMethodName, scriptInput );
					}					
				}				
			}
			LOG.trace( "  no script bound for \"" + scriptName  + "\" with input \""+ scriptInput + "\"" );
			return null;			
		}		
	}
	
	/**
	 * Resolves the Var "through" the {@code ExpressionEvaluator} 
	 * (checking the vars that were originated within the ExpressionEvalator
	 * And the vars that originated within the VarScopeBindings of the VarContext.  
	 */
	public enum SmartVarResolver
		implements VarResolver
	{	
		INSTANCE;
	
		/**
		 * Tries to resolve the object, returning null if the 
		 * Var is not bound (as EITHER:
		 * <UL>
		 *   <LI>a var in the ( Javascript ) Expression engine
		 *   <LI>a value within the {@code ScopeBindings}
		 * </UL>   
		 */
		public Object resolveVar( VarContext context, String varName ) 
		{
			if( varName == null || varName.trim().length() == 0 )
			{
				return null;
			}
			Evaluator ee = context.getExpressionEvaluator();
	
			// run this expression, which will determine if 
			// the varName is "bound" in EITHER: 
			//   the ScopeBindings  (i.e. "{##a:100##}" )
			//   instance "expressions" in JS (i.e. "{((var a = 100;))}" )
			String expressionText = 
				"typeof " + varName + " !== typeof undefined ? true : false;";
	
			//NOTE: this will try to resolve the varName in EITHER the scopeBindings
			// 	or the INSTANCE
			try
			{
				Object isSet = ee.evaluate( 
					context.getScopeBindings(), 
					expressionText );
				
				if( (Boolean)isSet )
				{   //resolve the ACTUAL value (from the varName)
					return ee.evaluate( context.getScopeBindings(),  varName );
				}
			}
			catch( Exception  e )
			{
				//return null;
			}
			
			Object var = context.get( varName );
			if( var != null )
			{
				return var;
			}	
			//TODO ? Check ThreadLocal
			String systemProp = System.getProperty( varName );
			
			return systemProp;
		}
	}	
}
