package varcode.java;

import varcode.java.load.BaseSourceLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.Dom;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacOptions;
import varcode.java.adhoc.Workspace;
import varcode.markup.MarkupException;
import varcode.markup.codeml.CodeML;
import varcode.load.SourceLoader.SourceStream;
import varcode.load.SourceLoader;

/**
 * ***********************************************************
 * TODO: The things around Reflection and invoking VarArg methods is
 * BROKEN and UGLY
 * 
 * I need to fix this, I tried using commons-lang reflection but their implementation
 * as of 3.4 ALSO doesnt handle Reflective VarArg Method invocation (there
 * is a patch coming in 3.5, but still)
 * 
 * So its something I'll revisit eventually
 * ***************************************************************
 * 
 * Java Meta-programming convenience API to: 
 * <OL>
 * <LI>Author AdHoc Java Source Code
 *   <UL>
 *     <LI>resolve and read in the (.java) source code for a java (.class) 
 *       <I><B>at Runtime</B></I>
 *     <LI>compile the CodeML of a .java source file to a {@code Dom}
 *     <LI>author a new {@code AdHocJavaFile} using a {@code Dom} and {@code VarContext}
 *   </UL>
 * <LI>Compile {@code AdHocJavaFile}s to bytecode (.class) using javac at runtime
 * <LI>Load {@code AdHocClass}es into a new {@code AdHocClassLoader}
 * <LI>use (construct new instances, call methods on) AdHoc compiled classes.   
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Java 
{
	;
	
    private static final Logger LOG = 
    	LoggerFactory.getLogger( Java.class );
    
    private static void addPropertyIfNonNull( StringBuilder sb, String propertyName )
	{
		String propertyValue = System.getProperty( propertyName );
		if( propertyValue != null && propertyValue.trim().length() > 0 )
		{
			sb.append( propertyName );
			sb.append( " = " );
			sb.append( propertyValue );
			sb.append( "\r\n" );
		}		
	}
	
    /**
     * Given a Type return the classes (as Strings) 
     * that compose this Type
     * for 
     * @param type
     * @return 
     */
    public static String[] classesFromType( Type type )
    {
        String s = type.toString();
        s = s.replace( "<", " ");
        s = s.replace( ">", " ");
        s = s.replace( ",", " ");
        return s.split( " " );              
    }
    
	/**
	 * Describes the Current Java Environment (at Runtime)
	 * 
	 * @return a String that details particulars about the Java Runtime
	 */
	public static String describeEnvironment()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "--- Current Java Environment --- " );
		sb.append( "\r\n" );
		addPropertyIfNonNull( sb, "java.vm.name" );
		addPropertyIfNonNull( sb, "java.runtime.version" );
		addPropertyIfNonNull( sb, "java.library.path" );
		addPropertyIfNonNull( sb, "java.vm.version" );
		addPropertyIfNonNull( sb, "sun.boot.library.path" );
		sb.append( "--------------------------------- " );
		sb.append( "\r\n" );
		return sb.toString();
	}
	
	 /**
     * Using the default Java Environment (for where the source code of a Class should
     * be at runtime), read in the Java Source for the given {@code markupClass} to 
     * compile the {@code Dom} and return it.<BR><BR>
     * 
     * NOTE: the markupClazz MUST BE a TOP LEVEL class (not an inner class)
     * 
     * @param markupClass the class marked up with CodeML marks to be compiled to a Dom
     * @return the dom the Dom representation of the Java source document
     */
    public static Dom compileCodeML( Class<?> markupClass )
    	throws MarkupException
    {
         return compileCodeML( BaseSourceLoader.INSTANCE, markupClass );
    }
    
    /**
     * Using the MarkupRepo for associating where the source code ".java" file of 
     * a Class should be at runtime), read in the .java file for the given 
     * {@code markupClass} to compile the {@code Dom} and return it.<BR><BR>
     * 
     * NOTE: the markupClazz MUST BE a TOP LEVEL class (not an inner class)
     * @param markupRepo where to find the markupClass
     * @param markupClazz the class marked up with CodeML marks to be compiled to a Dom
     * @return the dom the Dom representation of the Java source document
     */
    public static Dom compileCodeML(
        SourceLoader markupRepo, Class<?> markupClazz )
    { 
    	SourceStream markupStream = markupRepo.sourceStream( 
            markupClazz.getCanonicalName() + ".java" );
                
        Dom dom = CodeML.compile( markupStream );
        LOG.debug( "Compiled Dom from \"" + markupClazz + "\"" );
        return dom;
    }
    
    public static final String JAVA_CLASS_NAME = "fullyQualifieldJavaClassName";
    
    public static final String JAVA_SIMPLE_CLASS_NAME = "className";
    
    public static final String JAVA_PACKAGE_NAME = "packageName";
    
    
    public static AdHocJavaFile author( String className, Dom dom, Object...keyValuePairs )
    {
        return author( className, dom, VarContext.of( keyValuePairs ) );
    }
	/**
     * Authors and returns an {@code AdHocJavaFile} with name {@code className} 
     * using a {@code Dom} and based on the specialization provided in the
     * {@code context} and {@code directives}
     * 
     * @param className the class name to be authored (i.e. "io.varcode.ex.MyClass") 
     * @param dom the document object template to be specialized
     * @param context the specialization/functionality to be applied to the Dom
     * @param directives optional pre and post processing commands 
     * @return an AdHocJavaFile
     */
    public static AdHocJavaFile author(
    	String className, Dom dom, VarContext context, Directive...directives )    
    {       	
    	DocState docState = 
        	new DocState( dom, context, directives ); 
        
    	String[] pckgClass = 
            JavaNaming.ClassName.extractPackageAndClassName( className );
    	
    	context.set( JAVA_CLASS_NAME, className, VarScope.INSTANCE );
    	context.set( JAVA_PACKAGE_NAME, pckgClass[ 0 ], VarScope.INSTANCE );
    	context.set( JAVA_SIMPLE_CLASS_NAME, pckgClass[ 1 ], VarScope.INSTANCE );
    	
        //author the Document by binding the {@code Dom} with the {@code context}
        docState = Compose.toState( docState );
        
        if( pckgClass[ 0 ] != null )
        {
            AdHocJavaFile adHocJavaFile =
                new AdHocJavaFile( 
                    pckgClass[ 0 ], 
                    pckgClass[ 1 ], 
                    docState.getTranslateBuffer().toString() );
                
            LOG.debug( "Authored : \"" + pckgClass[ 0 ] + "." + pckgClass[ 1 ] + ".java\"" );
            return adHocJavaFile;
        }
        LOG.debug( "Authored : \"" + pckgClass[ 1 ] + ".java\"" );
        return new AdHocJavaFile( 
            pckgClass[ 1 ], docState.getTranslateBuffer().toString() ); 
    }
    
    /**
     * 
     * @param javaFile
     * @param compilerOptions Optional Compiler Arguments (@see JavacOptions)
     * @return
     */
    public static Class<?> loadClass( 
    	AdHocJavaFile javaFile,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        AdHocClassLoader adHocClassLoader = new AdHocClassLoader();
        return loadClass( adHocClassLoader, javaFile, compilerOptions );        
    }
    
    /**
     * Compiles the javaFile and loads the Class into the 
     * {@code adHocClassLoader}
     * 
     * @param adHocClassLoader the classLoader to load the compiled classes
     * @param javaFile file containing at least one top level Java class
     * (and potentially many nested classes)
     * @param compilerOptions options passed to the Runtime Javac compiler
     * @return the Class (loaded in the ClassLoader)
     */
    public static Class<?> loadClass( 
    	AdHocClassLoader adHocClassLoader, 
    	AdHocJavaFile javaFile,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        Workspace ws = new Workspace( adHocClassLoader );
        ws.addCode( javaFile );
        adHocClassLoader = ws.compile( compilerOptions );
        return adHocClassLoader.find( javaFile.getClassName() );
	}
    
    /**
     * Construct a "new" java instance by calling the constructor
     * @param constructor the constructor
     * @param args arguments passed in the constructor
     * @return  the new instance
     */
    private static Object construct( 
        Constructor<?> constructor, Object[] args )
    {
        try
        {
      		if( args.length > 0 )
       		{
       			LOG.debug("Calling constructor >" 
       			    + constructor + " with [" + args.length + "] arguments" );
                return constructor.newInstance(args );
       		}
       		else
       		{
       			LOG.debug( "Calling no-arg constructor > " + constructor );
                return constructor.newInstance(  );
       		}            
            //
        }
        catch( InstantiationException e )
        {
            throw new VarException( "Instantiation Exception to construct", e );
        }
        catch( IllegalAccessException e ) 
        {
            throw new VarException( "Illegal Access to construct", e );
        }
        catch( IllegalArgumentException e ) 
        {
            throw new VarException( "Illegal Argument to construct", e );
        }
        catch( InvocationTargetException e ) 
        {
            throw new VarException( "Invocation Target Exception for construct", e.getCause() );
        }         
    }        
    
    /** 
     * <UL>
     * <LI>creates an instance of the tailored class constructor 
     * (given the constructor params)
     * <LI>returns an instance of the Tailored Class.
     * </UL>
     * @param theClass the class to create an instance of
     * @param constructorArgs params passed into the constructor
     * @return an Object instance of the tailored class
     */
    public static Object instance( 
        Class<?> theClass, Object... constructorArgs )
    {
        Constructor<?>[] constructors = theClass.getConstructors();
        List<Constructor<?>> sameArgCount = new ArrayList<Constructor<?>>();
         
        if( constructors.length == 1 )
        {
         	return construct( constructors[ 0 ], constructorArgs );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            //noinspection Since15
            if( constructors[ i ].getParameters().length == constructorArgs.length )
           	{
          		sameArgCount.add( constructors[ i ] );
           	}
        }
        if( sameArgCount.size() == 1 )
        {
         	return construct(sameArgCount.get( 0 ), constructorArgs );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            //Class<?>[] paramTypes = constructors[ i ].getParameterTypes();
            //if( allArgsAssignable( paramTypes, constructorArgs ) )
            if( allArgsAssignable( constructors[ i ], constructorArgs ) )    
            {
             	return construct( constructors[ i ], constructorArgs );
            }
        }
        throw new VarException( "Could not find a matching constructor for input" );
    }

    /**
     * Invokes the instance method and returns the result
     * @param target the target instance to invoke the method on
     * @param methodName the name of the method
     * @param arguments the parameters to pass to the method
     * @return the result of the call
     */
    public static Object invoke( 
    	Object target, String methodName, Object... arguments )
    {       
        Method method = null;        
    	try            
        {            
    		if( target instanceof Class )
    		{
    		    return invokeStatic((Class<?>)target, methodName, arguments );
    		}
             
    		method = getMethod( 
                target.getClass().getMethods(), methodName, arguments );
    		 
            if( method == null )
            {
                throw new VarException(
                    "Could not find method \"" + methodName + "\" on \"" + target + "\"" );
            }
            return method.invoke( target, arguments );
        }
        catch( SecurityException iae )
        { 
              throw new VarException( 
                  "Security Exception not call " + method, iae );
        }        
        catch( IllegalAccessException iae ) 
        {
            throw new VarException(
                 "Illegal Access Could not call " + method, iae );
        }
        catch( IllegalArgumentException iae ) 
        {
            throw new VarException(
                "IllegalArgument Could not call "+ method, iae );
        }
        catch( InvocationTargetException iae ) 
        {
            throw new VarException(
                "Invocation Target Exception on" + method, iae.getCause() );
        }
     }
    
    public static Object getStaticField( Class<?> clazz, String fieldName )
    {
    	try
		{    		
			Field f = clazz.getField( fieldName );
			if( LOG.isDebugEnabled() ) 
            { 
                LOG.debug( "Getting field \"" + f ); 
            }			
			return f.get( null );
		}
		catch( NoSuchFieldException e )
		{
			throw new VarException( "No Such Field \"" + fieldName + "\"", e );
		}
        catch (SecurityException e) 
        {
            throw new VarException( "Security Exception on Field \""+fieldName+"\"", e );
        }
        catch (IllegalArgumentException e) {
            throw new VarException( e );
        }
        catch (IllegalAccessException e) {
            throw new VarException( e );
        }
    }
    
    public static void setFieldValue( Object instance, String fieldName, Object value )
    {
        try 
		{
			Field f = instance.getClass().getField( fieldName );			
			f.set( instance, value );
		}
        catch( NoSuchFieldException e )
        {
            throw new VarException(
                "No Such Field \"" + fieldName + "\" as "+ value, e );
        }
        catch( SecurityException e ) 
        {
            throw new VarException(
                "Security Exception for \"" + fieldName + "\" as "+ value, e );
        }
        catch( IllegalArgumentException e ) 
        {
            throw new VarException(
               "IllegalArgument for \"" + fieldName + "\" as "+ value, e );
        }
        catch( IllegalAccessException e )  
        {
            throw new VarException(
                "Illegal Access to set \"" + fieldName + "\" as "+ value, e );
        }
    }
    /**
     * Gets the value of the Field 
     * @param instanceOrClass
     * @param fieldName
     * @return
     */
    public static Object getFieldValue( Object instanceOrClass, String fieldName )
    {        
    	if( instanceOrClass instanceof Class )
    	{
            if( LOG.isDebugEnabled() ) 
            { 
                LOG.debug( "getting static field \"" + fieldName + "\"" );
            }
    		return getStaticField( (Class<?>)instanceOrClass, fieldName );
    	}
    	try 
		{
			Field f = instanceOrClass.getClass().getField( fieldName );			
			return f.get( instanceOrClass );
		}
    	catch( IllegalAccessException iae )
    	{
    		throw new VarException( "Illegal Access for \"" + fieldName + "\"", iae );
    	}
    	catch( NoSuchFieldException e ) 
		{
			throw new VarException( "No such Field exception for \"" + fieldName + "\"", e );
		} 		    	
        catch( SecurityException e ) 
        {
            throw new VarException( "SecurityException for field \"" + fieldName + "\"", e );
        }
        catch( IllegalArgumentException e ) 
        {
            throw new VarException( "Illegal Argument for field \"" + fieldName + "\"", e );
        } 		    	
    }
    
    public static Object invokeStatic( 
        Class<?> clazz, String methodName, Object... args )
    {
    	try
        {
    		Method method = getMethod( clazz.getMethods(), methodName, args );
            if( method == null )
            {
            	throw new VarException(
                    "Could not find method \"" + methodName + "\" on \"" 
                    + clazz.getName() + "\"" );
            }
            return method.invoke(clazz, args );            
        }
        catch( IllegalAccessException iae )
        {
        	throw new VarException( 
                "Could not call \"" + clazz.getName() + "." + methodName + "();\"", iae );
        }
        catch( IllegalArgumentException iae )
        {
        	throw new VarException( 
                "Could not call \"" + clazz.getName() + "." + methodName + "();\"", iae );
        }
        catch( InvocationTargetException ite )
        {   
        	throw new VarException( 
        		ite.getTargetException().getMessage() + " calling \"" + clazz.getName() + "." + methodName + "();\"", ite.getTargetException() );            
        }
    }
    
    private static final Map<Class<?>, Set<Class<?>>> SOURCE_CLASS_TO_TARGET_CLASSES= 
        new HashMap<Class<?>, Set<Class<?>>>();
        
    static
    {
        Set<Class<?>>byteMapping = new HashSet<Class<?>>();
        byteMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   byte.class, Byte.class, short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class} ) );
            
        SOURCE_CLASS_TO_TARGET_CLASSES.put( byte.class, byteMapping );
            
        Set<Class<?>>ByteMapping = new HashSet<Class<?>>();
        ByteMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   byte.class, Byte.class, short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class} ) );
            
        SOURCE_CLASS_TO_TARGET_CLASSES.put( Byte.class, ByteMapping );
        
        Set<Class<?>>shortMapping = new HashSet<Class<?>>();
        shortMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class } ) );
         
        SOURCE_CLASS_TO_TARGET_CLASSES.put( short.class, shortMapping );
           
        Set<Class<?>>ShortMapping = new HashSet<Class<?>>();
        ShortMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   short.class, Short.class, int.class, 
                    Integer.class, long.class, Long.class } ) );
         
        SOURCE_CLASS_TO_TARGET_CLASSES.put( Short.class, ShortMapping );
        
        Set<Class<?>>intMapping = new HashSet<Class<?>>();
        intMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   int.class, Integer.class, long.class, Long.class } ) );
         
        SOURCE_CLASS_TO_TARGET_CLASSES.put( int.class, intMapping );
         
        Set<Class<?>>longMapping = new HashSet<Class<?>>();
        longMapping.addAll( 
            Arrays.asList( 
                new Class<?>[] 
                {   long.class, Long.class } ) );
            
        SOURCE_CLASS_TO_TARGET_CLASSES.put( long.class, longMapping );
    }
        
    protected static boolean translatesTo( Object source, Class<?>target )
    {
        if( source.getClass() == target )
        {
            return true;
        }
        if( target.isPrimitive() )
        {
            if( source instanceof Long || source.getClass() == long.class )
            {
                return target == long.class;
            }
            if( source instanceof Integer || source.getClass() == int.class )
            {
                return target == long.class || target == int.class;
            }
            if( source instanceof Short  || source.getClass() == short.class )
            {
                return target == long.class || target == int.class || target ==  short.class;
            }
            if( source instanceof Byte || source.getClass() == byte.class )
            {
                return target == long.class || target == int.class || target ==  short.class || target == byte.class;
            }
            if( source instanceof Character  || source.getClass() == char.class )
            {
                return target == char.class;
            }
            if( source instanceof Boolean  || source.getClass() == boolean.class )
            {
                return target == boolean.class;
            }
            if( source instanceof Float  || source.getClass() == float.class )
            {
                return target == float.class;
            }
            if( source instanceof Double  || source.getClass() == double.class )
            {
                return target == double.class;
            }
        }
        Set<Class<?>> clazzes = SOURCE_CLASS_TO_TARGET_CLASSES.get( source.getClass() );
        if( clazzes != null )
        {
            return clazzes.contains( target );
        }
        return false;
    }
        
    /**
     * is this arg assignable to the target class?
     * @param targetArg the target method class
     * @param arg
     * @return 
     */
    protected static boolean isArgAssignable( Class<?> targetArg, Object arg )
    {
        if( arg == null )
        {
            return true;
        }
        //System.out.println( "ARG IS " + arg );
        
        if( targetArg.isPrimitive() || arg.getClass().isPrimitive() )
        {
            //System.out.println( arg + " is *** primitive " + targetArg );
            //return translatesTo( arg, targetArg );
            return translatesTo( arg, targetArg ); 
        }
        if( arg instanceof Class )
        {
            boolean isInst = ((Class)arg).isAssignableFrom( targetArg );
            //System.out.println( arg + " is *** instance of " + targetArg +" "+ isInst );
            return isInst;
        }
        if( arg.getClass().isInstance( targetArg ) )
        {
            //System.out.println( arg.getClass() + " is *** instance of " + targetArg );
            //System.out.println( arg + " is *** instance of " + targetArg );
            return true;
        }
        
        return false;        
    }

    /** 
     * gotta check if varargs 
     */
    protected static boolean allArgsAssignable( Method method, Object...args )
    {
        if( method.isVarArgs() )
        {
            //System.out.println( "VARARGS" + method ); 
            Type[] at = method.getParameterTypes();
            int minArgs = at.length -1;
            //System.out.println( "MINARGS" + minArgs ); 
            
            if( minArgs == 0 )
            {
                if( args.length == 0 )
                {
                    //System.out.println( "NO ARGS!" );
                    return true;
                }
                for( int i = 0; i < args.length; i++ )
                {
                    //System.out.println( "TRYING " + args[ i ] );
                    if( ! isArgAssignable( at[ i ].getClass(), args[ i ] ) );
                }
            }    
            /*
            for(int i=0; i< minArgs; i++ )
            {
                if( isArgsAssignable( at[i], args[i] );
            }
            if( minArgsCount > 1 && args.length < minArgsCount )
            {
                return false;
            }
            for( int i = 0; i < minArgsCount -1; i++ )
            {
                System.out.println(" TARGET "+ method.)
            }
            
            else
            {
                return true;
            } 
            */
            return false;
        }
        else
        {
            return allArgsAssignable( method.getParameterTypes(), args );
        }
    }
    
    protected static boolean allArgsAssignable( Constructor ctor, Object...args )
    {
        if( ctor.isVarArgs() )
        {
            //System.out.println( "VARARGS " + ctor ); 
            if( args.length > 0 )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return allArgsAssignable( ctor.getParameterTypes(), args );
        }
    }
    /**
     * Try and "match" the arguments of a method with the arguments provided
     * 
     * TODO I NEED TO HANDLE VARARGS
     * 
     * @param target the target arguments
     * @param args the actual arguments
     * @return 
     */
    protected static boolean allArgsAssignable( Class<?>[] target, Object... args )
    {
        if( args == null || args.length == 0 )
        {
            return target.length == 0;             
        }
        if( target == null )
        {
            return args.length == 0; 
        }
        if( target.length == 0 )
        {
            return args.length == 0;
        }        
        if( target.length == args.length )
        {   //they have the same number of arguments, but are they type compatible?
            //System.out.println( "Same Arg count" );
            for( int pt = 0; pt < target.length; pt++ )
            {
                //System.out.println( "test target "+ target[ pt ]+" against args "+ args[pt] );
                //if( !isArgAssignable( args[ pt ].getClass(), target[ pt ] ) )
                if( args[ pt ] == null )
                {
                    continue;
                }
                if( target[ pt ].equals( args[ pt ].getClass() ) )
                {
                    continue;
                }
                if( !isArgAssignable( target[ pt ], args[ pt ] ) )
                {
                    return false;
                }
            }
            return true;
        }
        //TODO handle varargs
        
        return false;
    }

    /**
     * matches and returns a static method with the methodName that matches 
     * arguments
     * 
     * @param methods all methods to search
     * @param methodName the target method name
     * @param args the arguments to the method
     * @return the java.lang.reflect.Method
     */
    public static Method getStaticMethod( 
        Method[] methods, String methodName, Object[] args )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( Modifier.isStatic( methods[ i ].getModifiers() )
                && methods[ i ].getName().equals( methodName ) )
            {
                //if( allArgsAssignable( methods[ i ].getParameterTypes(), args ) )
                if( allArgsAssignable( methods[ i ], args ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    /** 
     * given a Class, find all methods with a given name and return them
     * @param clazz
     * @param methodName
     * @return  all methods of a given name
     */
    public static List<Method> getMethods( Class clazz, String methodName )
    {
        List<Method> methodsOfName = new ArrayList<Method>();
        Method[] methods = clazz.getMethods();
        for( int i=0; i< methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                methodsOfName.add( methods[ i ] );                
            }
        }
        return methodsOfName;
    }
    
    public static Method getMethod( 
        Method[] methods, String methodName, Object... args )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {                
                //System.out.println( "FOUND METHOD " );
                //    "Try Method " + methods[ i ]+" for "+  args[0] +" " + args[0].getClass() );
                //if( allArgsAssignable( methods[ i ].getParameterTypes(), args ) )
                if( allArgsAssignable( methods[ i ], args ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }
    
	public static Object getStaticFieldValue( Class<?> clazz, String fieldName ) 
	{
		try 
		{
			Field f = clazz.getField( fieldName );
            return getStaticFieldValue( f );
		} 
		catch( NoSuchFieldException e ) 
		{
			throw new VarException( "No Such field \"" + fieldName + "\"", e );
		} 		
        catch( SecurityException e ) 
        {
            throw new VarException( "Security Exception for field \"" + fieldName + "\"", e );
        }
        catch( IllegalArgumentException e ) 
        {
            throw new VarException( "Illegal Argument for field \"" + fieldName + "\"", e );
        }        	
	}
	
    public static Object getStaticFieldValue( Field field )
	{
    	try 
	    {
	    	return field.get( null );
	    } 
	    catch( IllegalArgumentException e ) 
	    {
            throw new VarException( "Illegal Argument for field "+ field, e );
	    } 	    
        catch (IllegalAccessException e) 
        {
            throw new VarException( "Illegal Acccess for field "+ field, e );
        } 	    
    }
}
