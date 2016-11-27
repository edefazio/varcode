package varcode.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.context.Var;
import varcode.context.VarBindings;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.context.eval.VarScript;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.doc.lib.java.CommentEscape;
import varcode.doc.lib.java.JavaLib;
import varcode.doc.lib.text.CondenseMultipleBlankLines;
import varcode.doc.lib.text.PrefixWithLineNumber;
import varcode.doc.lib.text.RemoveAllLinesWith;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacException;
import varcode.java.load.BaseSourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;
import varcode.markup.codeml.CodeML;

/**
 * Represents the combination of {@code Dom} and {@code VarContext} to 
 * authored / tailored Java Source code.
 * 
 * Stores the Java Code as an <CODE>AdHocJavaFile</CODE> object 
 * which can be compiled in memory and/or exported as a String or File.
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( JavaCase.class );
     
    /** 
     * The authored / tailored Java Source Code 
     * manufactured from ({@code Dom} + {@code VarContext}) 
     */
    private final AdHocJavaFile adHocJavaFile;
    
        /**
     * Simplifies Marking up a Class that intends on being tailored.
     * 
     * Specifically:
     * <UL>
     *  <LI>sets the LANG to Java and adds all the java-related scripts and for use by Marks
     *  <LI>adds REQUIRED {@code ReplaceWithVar} Marks on the class name
     *  <LI>adds REQUIRED {@code ReplaceWithVar} Marks on the package name
     *  <LI>condenses multiple blank lines to a single blank line (in Post-Process)
     *  <LI>removes import statements for any used varcode classes or junit classes
     * </UL>
	 */
	public static Directive[] MARKUP_DIRECTIVES = new Directive[] {
		JavaLib.INSTANCE,   //load java library (for validation, etc.)	
		CondenseMultipleBlankLines.INSTANCE, //
		new RemoveAllLinesWith(  
			"import varcode", 
			"import junit" )	
	};
	
    /** The name of the */
    public static final String MARKUP_CLASS_VAR_NAME = "markup.class";
    
	/**
     * When Tailoring from a Class, populate the VarContext
     * with static Fields of the class that are public & static &
     * <UL>
     *   <LI>{@code VarScript}s
     *   <LI>{@code Var}s (or {@code Var}[]) 
     *   <LI>{@code VarBindings}
     *   <LI>{@code Directive}s (or {@code Directive}[])
     *</UL>    
     * 	
     * @param markupClass
     * @param context the context to bind static bindings to
     * @return a List of Directives that are from static fields
     */
    public static List<Directive> bindStaticFields( 
    	Class<?> markupClass, VarContext context )
    {
    	VarBindings staticBindings = context.getOrCreateBindings( VarScope.STATIC );
    	staticBindings.put( MARKUP_CLASS_VAR_NAME, markupClass );
    	
    	List<Directive> directives = new ArrayList<Directive>();
        
    	Field[] fields = markupClass.getFields();
        for( int i = 0; i < fields.length; i++ )
        {
        	LOG.trace( "LOOKING THROUGH [" + fields.length + "] fields for tailor components" );
        	if( Modifier.isStatic( fields[ i ].getModifiers() ) 
        		&& Modifier.isPublic( fields[ i ].getModifiers() ) )
        	{
        		Class<?> fieldType = fields[ i ].getType();
        		if( fieldType.isAssignableFrom( VarBindings.class ) )
        		{   //add VarBindings at Static scope        			
        			staticBindings.merge(
                                    (VarBindings)_Java.getStaticFieldValue( fields[ i ] ) );
        			LOG.trace( "Adding static filed bindings " +
                                    _Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( Var.class ) )
        		{   //Add Vars at "Static" scope
                            staticBindings.put( 
                                (Var)_Java.getStaticFieldValue( fields[ i ] ) );
                            LOG.trace( "Adding static Var " +
        			_Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( VarScript.class ) )
        		{   //Add Vars at "Static" scope
                            staticBindings.put( fields[ i ].getName(),  
        			(VarScript)_Java.getStaticFieldValue( fields[ i ] ) );
                            LOG.trace( "Adding static VarScript \"" +fields[ i ].getName()+"\" "
                                    +(VarScript)_Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isAssignableFrom( Directive.class ) )
        		{
                            directives.add( 
                                (Directive)_Java.getStaticFieldValue( fields[ i ] ) );
                            LOG.trace( "Adding Directive \"" +fields[ i ].getName()+"\" "
            			+ (Directive)_Java.getStaticFieldValue( fields[ i ] ) );
        		}
        		else if( fieldType.isArray() ) 
        		{   //arrays of Directives and Vars
                            if( fieldType.getComponentType().isAssignableFrom( Directive.class ) )
                	{
                            Directive[] dirs = 
        			(Directive[])_Java.getStaticFieldValue( fields[ i ] );
                            directives.addAll( Arrays.asList(dirs) );
                            if( LOG.isTraceEnabled() )
                            {
        			for( int j = 0; j < dirs.length; j++ )
        			{
                                    LOG.trace( "Added static Directive [" + j +"]"+ dirs[ j ]);
        					}
        				}
        			}
        			if( fieldType.getComponentType().isAssignableFrom( Var.class ) )
                	{
        				Var[] vars = (Var[])_Java.getStaticFieldValue( fields[ i ] );
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
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param markupClass the class (containing CodeML markup)
     * @param adHocClassName name of the class that will be created
     * (i.e. "io.varcode.somepackage.AuthoredMap"
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */ 
    public static JavaCase of(
    	Class<?> markupClass, String adHocClassName, Object... keyValuePairs )
    {
        return of( BaseSourceLoader.INSTANCE, 
            markupClass, 
            adHocClassName, 
            VarContext.of( keyValuePairs ) );
    }
    
    public String getClassName()
    {
    	return adHocJavaFile.getClassName();
    }
    
    public static JavaCase of(
    	Class<?> markupClass, 
        String adHocClassName, 
        VarContext context, 
        Directive...directives )
    {
        return of( BaseSourceLoader.INSTANCE, 
            markupClass, 
            adHocClassName, 
            context, 
            directives );
    } 
    
    /**
     * 
     * @param markupRepo the repository location of markup (where .java files are) 
     * @param markupClassName the name of the class (marked up with CodeML) 
     * to create the Dom (i.e. "varcode.ex._ValueObject")
     * @param adHocClassName the adhoc class name to author
     * (i.e. varcode.ex.MyValueObject")
     * @param context binding of name values and scripts for authoring
     * @param directives pre and post processing directives 
     * @return the JavaCase
     */
    public static JavaCase of(
    	SourceLoader markupRepo, 
        String markupClassName, 
        String adHocClassName, 
        VarContext context, 
        Directive...directives )
    {       
        SourceStream markupStream = null;
        
        if( markupClassName.endsWith( ".java" ) ) 
        {
    	    markupStream = markupRepo.sourceStream( markupClassName );
        }
        else
        {
            markupStream = markupRepo.sourceStream( markupClassName + ".java" );
        } 
        if( markupStream == null )
        {
            throw new VarException( 
                "Could not find \"" + markupClassName + "\" Markup Class" );
        } 
    	Dom dom = CodeML.compile( markupStream ); 
    	       
    	return new JavaCase( 
            adHocClassName, dom,  context, directives );
    }
    
    /**
     * Using the sourcePath resolve the source for {@code clazz}, and create
     * a new Case based on a {@code VarContext} containing {@code pairs} 
     * (pairs is data passed in as alternating key,value, key, value)
     *  
     * @param markupRepo the repository location of markup (java source code) 
     * @param markupClass the class containing markup to tailor
     * @param adHocClassName the name of the class (i.e. "ex.varcode.MyEnum")
     * @param context  containing bind key values and functionality for processing
     * @param directives pre and post processing 
     * @return
     */
    public static JavaCase of(
        SourceLoader markupRepo, 
        Class<?> markupClass, 
        String adHocClassName,
        VarContext context, 
        Directive...directives )
    {
        SourceStream markupStream = markupRepo.sourceStream( 
            markupClass.getCanonicalName() + ".java" );

        Dom dom = CodeML.compile( markupStream ); 
        
        List<Directive> staticDirectives = 
            bindStaticFields( markupClass, context );
        
        List<Directive> allDirectives = new ArrayList<Directive>();
        
        //we ALWAYS want to escape "/+*" and "*+/"
        allDirectives.add( CommentEscape.INSTANCE );
        allDirectives.addAll( Arrays.asList( directives ) );
        allDirectives.addAll( staticDirectives );

        return new JavaCase( 
            adHocClassName, 
            dom, 
            context, 
            allDirectives.toArray( new Directive[ 0 ] ) );
    }

    public static JavaCase of(
    	String adHocClassName, 
        Dom dom, 
        VarContext context, 
        Directive...directives )
    {
    	return new JavaCase( 
            adHocClassName, dom, context, directives );
    }
        
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param adHocClassName the target class name of the authored source
     * @param dom the Dom containing the document structure
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */
    public static JavaCase of(
        String adHocClassName, Dom dom, Object... keyValuePairs )
    {
    	return new JavaCase( 
            adHocClassName, dom, VarContext.of( keyValuePairs) );
    }
        
    private JavaCase( 
        String adHocClassName, 
        Dom dom, 
        VarContext context, 
        Directive...directives )
    {    	    	
    	this.adHocJavaFile = 
            _Java.author( adHocClassName, dom, context, directives );
    }

    @Override
    public String toString()
    {
    	return this.adHocJavaFile.getCode();
    }
    
    public AdHocJavaFile javaCode()
    {
        return this.adHocJavaFile;
    }
        
    public Class<?> loadClass( AdHocClassLoader classLoader )
    {
    	return _Java.loadClass( classLoader, this.adHocJavaFile );
    }
    
    public Class<?> loadClass()
    {
    	try
    	{
    		return _Java.loadClass( this.adHocJavaFile );    		
    	}
    	catch( JavacException je)
    	{   //if an error occurs compiling the source, 
    		//prefix the source with line numbers and print it out to help debugging
    		String codePrefixed = 
    			PrefixWithLineNumber.doPrefix( this.adHocJavaFile.getCode() );
    		LOG.error( codePrefixed );
    		throw je;
    	}
    }
        
    /**
     * <UL>
     * <LI>Load the "tailored" class into a classLoader 
     * <LI>use reflection to find the appropriate constructor (given the parameters)
     * <LI>call the constructor to create a new instance and return it
     * </UL>
     * 
     * @param constructorArgs
     * @return
     */
    public Object instance( Object... constructorArgs )
    {
        Class<?> theClass = loadClass();
        return _Java.instance( theClass, constructorArgs );
    }                 
    
    
    public Object instance( AdHocClassLoader classLoader, Object... constructorArgs )
    {
        Class<?> theClass = loadClass( classLoader );
        return _Java.instance( theClass, constructorArgs );
    }
    
    /**
     * Entity that can author and return a 
     * {@code JavaCase} container of Java source code. 
     * 
     * These are "Top Level Containers"  (_class, _interface, _enum)
     * that can be passed to the Java Compiler as an implementation of a 
     * {@code JavaFileObject} to be compiled
     * 
     * @author M. Eric DeFazio eric@varcode.io
     */
    public interface JavaCaseAuthor 
    {
        /**
         * Return the Java Code (class, interface, Enum, etc)
         * that represents this abstraction
         * 
         * @param directives directives applied to 
         * @return
         */
        public JavaCase toJavaCase( Directive...directives );
    
        /**
         * Return the Java Code (class, interface, enum, etc)
         * that represents this abstraction
         * 
         * @param context the context providing bound variables and functionality
         * to author the document
         * @param directives directives applied pre and post code/document creation 
         * @return the JavaCase
         */
        public JavaCase toJavaCase( 
            VarContext context, Directive...directives );
    }
}
