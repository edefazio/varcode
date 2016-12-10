package varcode.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.context.eval.EvalException;
import varcode.context.eval.Evaluator;
import varcode.context.eval.VarScript;

/**
 * Interfaces and Implementations for resolving references to entities by name
 * <UL>
 *  <LI>vars (key value pairs)  (for marks like "{+PREFIX+}")
 *     need to Resolve the value of a var named "PREFIX"
 *  <LI>methods / {@code VarScript}s, (for marks like "{+$indent(a)+}")
 *     need to Resolve the method/script named "indent(...)"
 *  <LI>{@code DocDirective}s (for marks like "{removeBlankLines()$$}")
 *     need to Resolve the {@code DocDirective} (pre or post processor) 
 *     named "directive()"
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Resolve 
{
    ; //singleton enum idiom
	
    private static final Logger LOG = 
        LoggerFactory.getLogger( Resolve.class );
	
    
    /**
     * The Resolver is generally considered a "lazy pull" model, 
     * where it will lazily lookup / bind a component based on a name.
     * (Var, VarScipt, Directive, etc.).  Sometimes, however we want to
     * seed or Push a specific Binding
     */
    public interface ResolveBinder
    {
        public List<Var> getVars();
        
        public List<Directive> getDirectives();
        
        public List<VarBindings> getVarBindings();
        
        public List<VarScript> getVarScripts();
    }
    /** 
     * name of a Property in the {@code VarContext} that specifies a Class that 
     * can define: 
     * <UL>
     *   <LI>vars (static variables linked/used in Compose(ing) documents
     *   <LI>methods (static methods used in Compose(ing) documents
     *   <LI>directives (static classes used in Compos(ing) documents
     * </UL>
     * for example:
     * 
     * public class TheResolveClass 
     * {
     *     public static final String someVar = "Eric";
     * 
     *     public static final String salutation( VarContext context, String varName )
     *     {
     *         //this doesnt "really" make sense, we are just getting the
     *         // value of someVar, ("Eric") here, but I'm just trying to illustrate 
     *         // how to Resolve uses the "resolve.baseclass" property to 
     *         // resolve BOTH methods and variable values
     *         // i.e. for : "{+salutation(someVar)+}" 
     *         String theValue = (String)context.getValue( varName );         
     *         return "Dear Mr. " + theValue.trim();
     *     }
     * 
     *     public static void main( String[] args )
     *     {
     *         Dom d = BindML.compile( "{+salutation(someVar)+}" );
     *         
     *         //here we set the resolve.baseclass, which signifies
     *         // that I can uses static variables, methods and class instances
     *         // that are resident on the TheResolveClass.class 
     *         // when processing the mark "{+salutation(someVar)+}" 
     *         //    I need to resolve a method/script named : "salutation"
     *         //    I need to resolve a var named "someVar"     
     *         // ...both are found as static fields /methods on TheResolveClass 
     *         VarContext vc = VarContext.of( 
     *             "resolve.baseclass", TheResolveClass.class );
     * 
     *         System.out.println( Compose.toString( d, vc ) );
     *     }
     * }
     */
    public static final String BASECLASS_PROPERTY = "resolve.baseclass";
    
    /**
     * Does reflective Lookups, and Swallows Exceptions (May log) 
     * Silently returns
     */
    public static class SilentReflection
    {
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
         * Gets the value of a static field from a Field
         * @param field the field definition
         * @return the value of the static field
         */
        public static Object getStaticFieldValue( Field field )
        {
            try 
            {
                return field.get( null );
            } 
            catch( IllegalArgumentException e ) 
            {
                return null;
                //throw new VarException( "Illegal Argument for field " + field, e );
            } 	    
            catch( IllegalAccessException e ) 
            {
                return null;
                //throw new VarException( "Illegal Acccess for field " + field, e );
            } 	    
        }
    }
    
    /**
     * Binds the appropriate "properties" 
     * ({@Directive}s {@VarScript}s, {@code Var}s, {@code VarBinding}s
     * into the VarContext that are on the resolveClass.
     * 
     * When a ResolveClass is provided to the context, 
     * it can contain "properties" that should be populated the VarContext
     * with static Fields of the class that are public & static
     * <UL>
     *   <LI>{@code VarScript}s
     *   <LI>{@code Var}s (or {@code Var}[]) 
     *   <LI>{@code VarBindings}
     *   <LI>{@code Directive}s (or {@code Directive}[])
     *</UL>    
     * 	
     * @param resolveClass
     * @param context the context to bind static bindings to
     * @return a List of Directives that are from static fields
     */
    public static List<Directive> bindResolveBaseClass( 
    	Class<?> resolveClass, VarContext context )
    {
    	VarBindings staticBindings = 
            context.getOrCreateBindings( VarScope.STATIC );

    	staticBindings.put( Resolve.BASECLASS_PROPERTY, resolveClass );
        
    	List<Directive> directives = new ArrayList<Directive>();
        
    	Field[] fields = resolveClass.getFields();
        for( int i = 0; i < fields.length; i++ )
        {
            LOG.trace( "LOOKING THROUGH [" + fields.length + "] fields for context components" );
            if( Modifier.isStatic( fields[ i ].getModifiers() ) 
        	&& Modifier.isPublic( fields[ i ].getModifiers() ) )
            {
        	Class<?> fieldType = fields[ i ].getType();
        	if( fieldType.isAssignableFrom( VarBindings.class ) )
        	{   //add VarBindings at Static scope 
                    VarBindings vb = 
                        (VarBindings)SilentReflection.getStaticFieldValue( fields[ i ] ); 
                    staticBindings.merge( vb );
                    LOG.trace( "Adding static filed bindings " + vb );
        	}
        	else if( fieldType.isAssignableFrom( Var.class ) )
        	{   //Add Vars at "Static" scope
                    Var theVar = (Var)SilentReflection.getStaticFieldValue( fields[ i ] ); 
                    staticBindings.put( theVar );
                    LOG.trace( "Adding static Var " + theVar );
        	}
        	else if( fieldType.isAssignableFrom( VarScript.class ) )
        	{   //Add Vars at "Static" scope
                    VarScript vScript = 
                        (VarScript)SilentReflection.getStaticFieldValue( fields[ i ] );
                    staticBindings.put( fields[ i ].getName(),  vScript );
                    LOG.trace( "Adding static VarScript \"" 
                        + fields[ i ].getName()+ "\" " + vScript );
        	}
        	else if( fieldType.isAssignableFrom( Directive.class ) )
        	{
                    Directive dir = (Directive)SilentReflection.getStaticFieldValue( fields[ i ] );
                    directives.add( dir );
                    LOG.trace( "Adding Directive \"" + fields[ i ].getName() + "\" " + dir );
        	}
        	else if( fieldType.isArray() ) 
        	{   //arrays of Directives and Vars
                    if( fieldType.getComponentType().isAssignableFrom( 
                        Directive.class ) )
                    {
                        Directive[] dirs = 
                            (Directive[])SilentReflection.getStaticFieldValue( fields[ i ] );
                        directives.addAll( Arrays.asList(dirs) );
                        if( LOG.isTraceEnabled() )
                        {
                            for( int j = 0; j < dirs.length; j++ )
                            {
                                LOG.trace( 
                                    "Added static Directive [" + j + "]"+ dirs[ j ] );
                            }
        		}
                    }
                    if( fieldType.getComponentType().isAssignableFrom( Var.class ) )
                    {
        		Var[] vars = (Var[])SilentReflection.getStaticFieldValue( fields[ i ] );
                        for( int j = 0; j < vars.length; j++ )
                        {
                            staticBindings.put( vars[ i ] );
                        }
                    } 
        	}    		
            }        	
        }
        return directives;
    }
    
	
    /**
     * Knows how to resolve the value of a (data) var byName name
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
     * Knows how to resolve a VarScript or static Java method
     * based on the scriptName and scriptInput String
     */
    public interface ScriptResolver
    {	
	public VarScript resolveScript( 
            VarContext context, String scriptName, String scriptInput );
    }
	
    /**
     * Resolves a {@code DocDirective} instance from a name.
     * For instance for a mark like:<PRE> 
     * "{$$removeEmptyLines()$$}"</PRE>
     * 
     * tries to resolve a {@code DocDirective} by name "removeEmptyLines()"
     *  
     */
    public interface DirectiveResolver
    {
        public Directive resolveDirective ( 
            VarContext context, String directiveName );
    }

        
    /**
     * Resolves a {@code DocDirective} instance from a name.
     * For instance for a mark like:<PRE> 
     * "{$$removeEmptyLines()$$}"</PRE>
     * 
     * tries to resolve a {@code DocDirective} by name "removeEmptyLines()"
     *  
     */
    public enum SmartDirectiveResolver
        implements DirectiveResolver
    {
        INSTANCE;	
	
	/** Return the Singleton INSTANCE VarScript */  
	private static VarScript getSingletonINSTANCEField( Class<?> clazz )
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
            catch( NoSuchFieldException e )
            {							
		return null;
            }	
            catch( SecurityException e ) 
            {
                return null;
            }
            catch( IllegalArgumentException e ) 
            {
                return null;
            }
            catch( IllegalAccessException e ) 
            {
                return null;
            }	
	}
		
        @Override
	public Directive resolveDirective( 
            VarContext context, String directiveName ) //, String scriptInput ) 
	{
            if( LOG.isTraceEnabled() ) 
            {    
                LOG.trace( "   resolving directive \"" + directiveName + "\""  ); 
            }
            
            // 1) see if the script is loaded in the context
            String directiveLookupName = "$" + directiveName ;
            if( LOG.isTraceEnabled() ) 
            { 
                LOG.trace( "   1) checking context for \"" + directiveLookupName + "\""  ); 
            }
            Object directive = context.get( directiveLookupName );
            if( directive != null )
            {
		if( directive instanceof Directive )
		{
                    LOG.trace( "   Found directive \"" + directiveLookupName + 
                        "\" in context " + directive );
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
                
            /** Fully qualifying a directive as a static method call? */    
            int indexOfLastDot = directiveName.lastIndexOf( '.' );
		
            if( indexOfLastDot > 0 )
            {
		String theMethodName = directiveName.substring( 
                    indexOfLastDot + 1, 
                    directiveName.length() );
			
		String theClassName = directiveName.substring( 0, indexOfLastDot );
		if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   3) checking for class \"" + theClassName + 
                        "\" for static method \"" + theMethodName + "\""  ); 
                }
			
		Class<?> clazz = SilentReflection.getClassForName( theClassName );
			
		if( clazz != null )
		{	
                    if( LOG.isTraceEnabled() ) 
                    {
			LOG.trace( "  resolved class \"" + clazz  + "\"" );
                    }
                    //does the class implement Directive?
                    if( Directive.class.isAssignableFrom( clazz  ) )
                    {
			if( LOG.isTraceEnabled() ) 
                        {
                            LOG.trace( "  class \"" + clazz  + "\" is a VarScript" );
			}
			if( clazz.isEnum() )
			{
                            if( LOG.isTraceEnabled() ) 
                            {
				LOG.trace( "  class \"" + clazz  + "\" is a VarScript & an enum " );
                            }
                            return (Directive)clazz.getEnumConstants()[ 0 ];
			}
			Object singleton = getSingletonINSTANCEField( clazz );
			if( singleton != null && LOG.isTraceEnabled() ) 
			{
                            LOG.trace( "  returning INSTANCE field on \"" + clazz  + "\" as VarScript" );
			}
			try
			{   
                            LOG.trace( "  trying to create (no-arg) instance of \"" + clazz  + "\" as VarScript" );
                            return (Directive )clazz.newInstance();
			}
			catch( InstantiationException e )
			{
                            LOG.trace( "  instantiation method failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript", e );
                            return null;
			}
                        catch( IllegalAccessException e ) 
                        {
                            LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript" );
                            return null;
                        }
                    }
                    else
                    {   //found a class, now find the method, (just chooses the first one by this name)
			if( LOG.isTraceEnabled() ) 
                        {
                            LOG.trace( "  resolving static Method \"" + theMethodName 
				+ "\" on class \"" + clazz + "\"" );
			}
			return findStaticMethodAsDirective( 
                            context, clazz, theMethodName  );
                    }					
		}				
            }
            LOG.trace( "  no directive bound for \"" + directiveName  + "\"" );
            return null;			
	}
	
	private static Directive findStaticMethodAsDirective( 
            VarContext context,  
            Class<?> clazz, 
            String methodName )
	{
            try
            {
		Method m = SilentReflection.tryAndGetMethod( 
                    clazz, methodName, VarContext.class );
		if( m != null )
		{
                    return new Directive.StaticMethodPreProcessAdapter( 
                         m, context );
		}
			
		m = SilentReflection.tryAndGetMethod( clazz, methodName );
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

        @Override
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
		
        @Override
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
            //do nothing
            //return collection;
	}
		
        @Override
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
		Method m = SilentReflection.tryAndGetMethod( 
                    clazz, methodName, VarContext.class, String.class );
                if( m != null )
		{
                    return new StaticMethodScriptAdapter( m, context, scriptInput );
		}
		m = SilentReflection.tryAndGetMethod( clazz, methodName, VarContext.class );
		if( m != null )
		{
                    return new StaticMethodScriptAdapter( m, context );
		}
		m = SilentReflection.tryAndGetMethod( clazz, methodName, String.class );
		if( m != null )
		{
                    return new StaticMethodScriptAdapter( m, scriptInput );
		}
				
		m = SilentReflection.tryAndGetMethod( clazz, methodName, Object.class );
		if( m != null )
		{
                    return new StaticMethodScriptAdapter( m, scriptInput );
		}				
		m = SilentReflection.tryAndGetMethod( clazz, methodName );
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
            catch( NoSuchFieldException e )
            {							
		return null;
            }	
            catch (SecurityException e) 
            {
                return null;
            }
            catch (IllegalArgumentException e) 
            {
                return null;
            }
            catch (IllegalAccessException e) 
            {
                return null;
            }	
	}
		
        @Override
	public VarScript resolveScript( 
            VarContext context, String scriptName, String scriptInput ) 
	{
            if( LOG.isTraceEnabled() ) 
            { 
                LOG.trace( "   resolving script \"" + scriptName + "\""  ); 
            }
            
            // 1) see if the script is loaded in the context
            String scriptLookupName = "$" + scriptName ;
            if( LOG.isTraceEnabled() ) 
            { 
                LOG.trace( "   1) checking context for \"" + scriptLookupName + "\""  ); 
            }
            Object vs = context.get( scriptLookupName );
            if( vs != null )
            {
		if( vs instanceof VarScript )
                {
                    LOG.trace( "   Found script \"" + scriptLookupName + 
                        "\" in context " + vs );
                    return (VarScript)vs;
		}				
            }	
			
            //2) check if there is a Resolve Base Class Registered that has a 
            //method of this name
            Object resolveBaseClass = context.get( Resolve.BASECLASS_PROPERTY );
			
            if( resolveBaseClass != null )
            {
		if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace("   2) checking Resolve.BaseClass \"" + 
                        resolveBaseClass + "\" for static method \"" + scriptName + "\""  ); 
                }
		VarScript resolveBaseStaticMethod = findStaticMethod( context,  
                    (Class<?>) resolveBaseClass, 
                    scriptName,
                    scriptInput );
				
		if( resolveBaseStaticMethod != null )
		{
                    if( LOG.isTraceEnabled() ) 
                    { 
			LOG.trace("   Found VarScript as method \"" 
                            + scriptName + "\" from \"resolve.baseclass\" = \"" 
                            + resolveBaseClass + "\""  ); 
                    }
                    return resolveBaseStaticMethod;
		}				
            }
            else
            {
                if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   2) no \"resolve.baseclass\" property set in VarContext"  ); 
                }
            }
			
            //I COULD have ScriptBindings where I "manually" Register/
            // assign scripts (i.e. Script)s to names
            // i.e. "java.util.UUID.randomUUID"();
			
			
            // IF the name contains a '.' (run (2) and (3) 
            int indexOfLastDot = scriptName.lastIndexOf( '.' );
			
            if( indexOfLastDot > 0 )
            {
                if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   3) checking for fully qulified script name with class \"" + scriptName + ".class\"" ); 
                }
		Class<?> clazz = SilentReflection.getClassForName( scriptName );				
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
		if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   4) checking for class \"" + theClassName 
                        + "\" for static method \"" + theMethodName + "\""  ); 
                }
				
                clazz = SilentReflection.getClassForName( theClassName );
				
		if( clazz != null )
		{	
                    if( LOG.isTraceEnabled() ) 
                    {
			LOG.trace( "  resolved class \"" + clazz  + "\"" );
                    }
                    //does the class implement VarScript?
                    if( VarScript.class.isAssignableFrom( clazz  ) )
                    {
			if( LOG.isTraceEnabled() ) 
                        {
                            LOG.trace( "  class \"" + clazz  + "\" is a VarScript" );
			}
			if( clazz.isEnum() )
			{
                            if( LOG.isTraceEnabled() ) 
                            {
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
			{   
                            LOG.trace( "  trying to create (no-arg) instance of \"" + clazz  + "\" as VarScript" );
                            return (VarScript)clazz.newInstance();
			}
			catch( InstantiationException e )
			{
                            LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript", e );
                            return null;
			}
                        catch( IllegalAccessException e ) 
                        {
                            LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz  + "\" as VarScript", e );
                            return null;
                        }
                    }
                    else
                    {   //found a class, now find the method, (just chooses the first one by this name)
			if( LOG.isTraceEnabled() ) 
                        {
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
            @Override
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
