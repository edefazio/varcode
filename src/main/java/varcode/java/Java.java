package varcode.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.buffer.TranslateBuffer;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.dom.Dom;
import varcode.java.javac.InMemoryJavaClassLoader;
import varcode.java.javac.InMemoryJavaCode;
import varcode.java.javac.InMemoryJavac;
import varcode.java.javac.JavacOptions;
import varcode.markup.MarkupException;
import varcode.markup.codeml.CodeML;
import varcode.markup.repo.MarkupRepo;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 *  
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
			sb.append( System.lineSeparator() );
		}		
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
		sb.append( System.lineSeparator() );
		addPropertyIfNonNull( sb, "java.vm.name" );
		addPropertyIfNonNull( sb, "java.runtime.version" );
		addPropertyIfNonNull( sb, "java.library.path" );
		addPropertyIfNonNull( sb, "java.vm.version" );
		addPropertyIfNonNull( sb, "sun.boot.library.path" );
		sb.append( "--------------------------------- " );
		sb.append( System.lineSeparator() );
		return sb.toString();
	}
	
	 /**
     * Using the default Java Environment (for where the source code of a Class should
     * be at runtime), read in the Java Source for the given {@code markupClass} to 
     * compile the {@code Dom} and return it.<BR><BR>
     * 
     * NOTE: the markupClazz MUST BE a TOP LEVEL class (not an inner class)
     * 
     * @param markupClazz the class marked up with CodeML marks to be compiled to a Dom
     * @return the dom the Dom representation of the Java source document
     */
    public static final Dom compileDom( Class<?> markupClazz )
    	throws MarkupException
    {
         return compileDom( JavaMarkupRepo.INSTANCE, markupClazz );
    }
    
    /**
     * Using the MarkupRepo for associating where the source code ".java" file of 
     * a Class should be at runtime), read in the .java file for the given 
     * {@code markupClass} to compile the {@code Dom} and return it.<BR><BR>
     * 
     * NOTE: the markupClazz MUST BE a TOP LEVEL class (not an inner class)
     * 
     * @param markupClazz the class marked up with CodeML marks to be compiled to a Dom
     * @return the dom the Dom representation of the Java source document
     */
    public static final Dom compileDom( MarkupRepo markupRepo, Class<?> markupClazz )
    { 
    	MarkupStream markupStream = markupRepo.markupStream( 
            markupClazz.getCanonicalName() + ".java" );
                
        Dom dom = CodeML.compile( markupStream );
        LOG.debug( "Compiled Dom from \""+ markupClazz +"\"" );
        return dom;
    }
    
    public static final String JAVA_CLASS_NAME = "fullyQualifieldJavaClassName";
    
    public static final String JAVA_SIMPLE_CLASS_NAME = "className";
    
    public static final String JAVA_PACKAGE_NAME = "packageName";
    
	/**
     * 
     * @param fullyQualifiedJavaClassName (i.e. "io.varcode.ex.MyClass") 
     * @param dom the document template to be filled
     * @param varContext containing the specialization to be applied to the Dom
     * @param directives optional pre and post processing commands 
     * @return an InMemoryJavaCode
     */
    public static InMemoryJavaCode author(
    	String fullyQualifiedJavaClassName, 	
    	Dom dom, VarContext context, Directive...directives )    
    {       	
    	DocState docState = 
        	new DocState( dom, context, directives ); 
    	String[] pckgClass = JavaNaming.ClassName.extractPackageAndClassName(fullyQualifiedJavaClassName);
    	
    	context.set( JAVA_CLASS_NAME, fullyQualifiedJavaClassName, VarScope.INSTANCE );
    	context.set( JAVA_PACKAGE_NAME, pckgClass[ 0 ], VarScope.INSTANCE );
    	context.set( JAVA_SIMPLE_CLASS_NAME, pckgClass[ 1 ], VarScope.INSTANCE );
    	
        Author.bind( docState );
        
        return CodeFactory.doCreate( fullyQualifiedJavaClassName, docState );            
    }
    
    /**
     * Author the Java Source Code, 
     * Compile the Tailored Source Code into a Class
     * Load the Class into a ClassLoader
     * return  
     * @param context
     * @param codeExporter
     * @return
     */
    public static Class<?> loadClass(
    	String fullyQualifiedJavaClassName,
        Dom markup, 
        VarContext context, 
        InMemoryJavaClassLoader memClassLoader,
        JavacOptions.CompilerOption...compilerOptions )
    {
        InMemoryJavaCode javaCode = author( fullyQualifiedJavaClassName, markup, context );
        
        List<InMemoryJavaCode> codeList = new ArrayList<InMemoryJavaCode>();
        
        Map<String, Class<?>> codeMap = 
        	InMemoryJavac.compileLoadClasses( memClassLoader, codeList, compilerOptions );
        return codeMap.get( javaCode.getClassName() ); 
    }
    
    /**
     * 
     * @param javaCode
     * @param compilerOptions Optional Compiler Arguments (@see JavacOptions)
     * @return
     */
    public static Class<?> loadClass( 
    	InMemoryJavaCode javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        InMemoryJavaClassLoader inMemClassLoader = new InMemoryJavaClassLoader();
        return loadClass( inMemClassLoader, javaCode, compilerOptions );        
    }
    
    public static Map<String, Class<?>> loadClasses(
    	List<InMemoryJavaCode> javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
    	InMemoryJavaClassLoader inMemClassLoader = new InMemoryJavaClassLoader();
        return loadClasses( inMemClassLoader, javaCode, compilerOptions );        
    }
    
    public static Map<String, Class<?>> loadClasses(
    	InMemoryJavaClassLoader classLoader,	
       	List<InMemoryJavaCode> javaCode,
       	JavacOptions.CompilerOption...compilerOptions )
    {
    	Map<String, Class<?>> compiled = 
    		InMemoryJavac.compileLoadClasses(
    			classLoader, 
    			javaCode, 
    			compilerOptions );
    	return compiled;        
    }
    
    public static Class<?> loadClass( 
    	InMemoryJavaClassLoader classLoader, 
    	InMemoryJavaCode javaCode,
    	JavacOptions.CompilerOption...compilerOptions )
    {
    	List<InMemoryJavaCode> codeList = new ArrayList<InMemoryJavaCode>();
        codeList.add( javaCode );
        Map<String, Class<?>> codeMap = 
        	InMemoryJavac.compileLoadClasses( 
        		classLoader, 
        		codeList, 
        		compilerOptions );
        
        LOG.debug( "Loaded Class \"" + javaCode.getClassName() + "\"" );
        return codeMap.get( javaCode.getClassName() );       
	}
    
    private static Object construct( Constructor<?> constructor, Object[] arguments )
    {
      	 try
         {
      		 if( arguments.length > 0 )
       		 {
       			 LOG.debug( "Calling constructor >" 
       			     + constructor + " with [" + arguments.length + "] arguments" );
       		 }
       		 else
       		 {
       			 LOG.debug( "Calling constructor > " + constructor );
       		 }
             return constructor.newInstance( arguments );
         }
         catch( Exception e )
         {
             throw new VarException( e );
         }         
    }        
    
    
    /** 
     * <UL>
     * <LI>creates an instance of the tailored class constructor 
     * (given the constructor params)
     * <LI>returns an instance of the Tailored Class.
     * </UL>
     * 
     * @param constructorParams params passed into the constructor
     * @return an Object instance of the tailored class
     */
    public static Object instance( 
        Class<?> theClass, Object... constructorParams )
    {
        Constructor<?>[] constructors = theClass.getConstructors();
        List<Constructor<?>> sameArgCount = new ArrayList<Constructor<?>>();
         
        if( constructors.length == 1 )
        {
         	return construct( constructors[ 0 ], constructorParams );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
         	if( constructors[ i ].getParameters().length == constructorParams.length )
           	{
          		sameArgCount.add( constructors[ i ] );
           	}
        }
        if( sameArgCount.size() == 1 )
        {
         	return construct( sameArgCount.get( 0 ), constructorParams );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            Class<?>[] paramTypes = constructors[ i ].getParameterTypes();
            if( Reflect.allParamsAssignable( paramTypes, constructorParams ) )
            {
             	return construct( constructors[ i ], constructorParams );
            }
        }
        throw new VarException( "Could not find a matching constructor for input" );
    }

   
    /**
     * Invokes the instance method and returns the result
     * @param instance the target instance to invoke the method on
     * @param methodName the name of the method
     * @param params the parameters to pass to the method
     * @return the result of the call
     */
    public static Object invoke( 
    	Object target, String methodName, Object... params )
    {         	 
    	 try
         {
    		 Method method = null;
    		 
    		 if( target instanceof Class )
    		 {
    			 return invokeStatic( (Class<?>)target, methodName, params );
    		 }     		 
    		 else
    		 {	
    			 method = Reflect.getMethod( target.getClass().getMethods(), methodName, params );
    		 }
             if( method == null )
              {
                  throw new VarException(
                      "Could not find method \"" + methodName + "\" on \"" + target + "\"" );
              }
              return method.invoke( target, params );
         }
         catch( Exception iae )
         { 
              throw new VarException( 
                  "Could not call \"" + target + "." + methodName + "();", iae );
         }
     }
    
    public static Object getStaticField( Class<?> clazz, String fieldName )
    {
    	try
		{    		
			Field f = clazz.getField( fieldName );
			if( LOG.isDebugEnabled() ) { LOG.debug( "Getting field \"" + f ); }			
			return f.get( null );
		}
		catch( Exception e )
		{
			throw new VarException( e );
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
    		return getStaticField( (Class<?>)instanceOrClass, fieldName );
    	}
    	try 
		{
			Field f = instanceOrClass.getClass().getField( fieldName );			
			return f.get( instanceOrClass );
		}
    	catch(IllegalAccessException iae )
    	{
    		iae.printStackTrace();
    		throw new VarException( iae );
    	}
    	catch( Exception e ) 
		{
			throw new VarException( e );
		} 		    	
    }
    
    public static Object invokeStatic( 
        Class<?> clazz, String methodName, Object... params )
    {
    	try
        {
    		Method method = Reflect.getMethod( 
                clazz.getMethods(), methodName, params );
            if( method == null )
            {
            	throw new VarException(
                    "Could not find method \"" + methodName + "\" on \"" 
                    + clazz.getName() + "\"" );
            }
            return method.invoke( clazz, params );            
        }
        catch( IllegalAccessException iae )
        {
        	throw new VarException( 
               "Could not call \"" + clazz.getName() + "." + methodName + "();", iae );
        }
        catch( IllegalArgumentException iae )
        {
        	throw new VarException( 
                    "Could not call \"" + clazz.getName() + "." + methodName + "();", iae );
        }
        catch( InvocationTargetException ite )
        {   
        	throw new VarException( 
        		ite.getTargetException().getMessage() + " calling \"" + clazz.getName() + "." + methodName + "();", ite.getTargetException() );            
        }
    }
    
    /**
     * For the record, I'm not too fond of having any "factories"
     * in the code, but (unfortunately) as it turns out I can't know
     * (apriori) what the "tailored" class name is BEFORE tailoring,
     * and I need a mutable abstraction to follow through the lifecycle
     * from the time the code is generated to the time it is exported 
     * or compiled.
     */
    public static class CodeFactory
    {   
    	public static final CodeFactory INSTANCE = new CodeFactory();
    	
    	public static InMemoryJavaCode doCreate( String fullyQualifiedJavaClassName, DocState tailorState )
    	{
    		return doCreate( 
    			fullyQualifiedJavaClassName,	
    			tailorState.getDom(), 
    			tailorState.getContext(), 
    			tailorState.getTranslateBuffer() ); 
    	}
    	
    	public static InMemoryJavaCode doCreate( 
    		String fullyQualifiedJavaClassName, Dom dom, VarContext context, TranslateBuffer buffer )
    	{   		
    		String tailoredSource = buffer.toString();
    		
    		String[] packageClassName = JavaNaming.ClassName.extractPackageAndClassName(fullyQualifiedJavaClassName);
    		String packageName = packageClassName[ 0 ];
    		String theClassName = packageClassName[ 1 ];
 
            if( packageName != null )
            {
                InMemoryJavaCode tailoredJavaSource =
                    new InMemoryJavaCode( packageName, theClassName, tailoredSource );
                
                LOG.debug( "Authored : \"" + packageName + "." + theClassName + ".java\"" );
                return tailoredJavaSource;
            }
            LOG.debug( "Authored : \"" + theClassName + "\"" );
            return new InMemoryJavaCode( theClassName, tailoredSource );            
    	}
    }
}
