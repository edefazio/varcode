package varcode.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.context.Resolve;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.doc.lib.java.CommentEscape;
import varcode.doc.lib.text.PrefixWithLineNumber;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacException;
import varcode.java.load.JavaSourceLoader;
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
     * The Java Source Code representing a .Java File
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
     
    public static Directive[] MARKUP_DIRECTIVES = new Directive[] {
        JavaLib.INSTANCE,   //load java library (for validation, etc.)	
	CondenseMultipleBlankLines.INSTANCE, //
	new RemoveAllLinesWith(  
            "import varcode", 
            "import junit" )	
	};
    */
    
    /** The name of the */
    //public static final String MARKUP_CLASS_VAR_NAME = "markup.class";
    
    
    
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param resolveClass the class potentially containing context properties
     * @param adHocClassName name of the class that will be created
     * (i.e. "io.varcode.somepackage.AuthoredMap"
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */ 
    public static JavaCase of(
    	Class<?> resolveClass, String adHocClassName, Object... keyValuePairs )
    {
        return of( JavaSourceLoader.INSTANCE, 
            resolveClass, 
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
        return of( JavaSourceLoader.INSTANCE, 
            markupClass, 
            adHocClassName, 
            context, 
            directives );
    } 
    
    /**
     * 
     * @param sourceLoader the repository location of markup (where .java files are) 
     * @param markupClassName the name of the class (marked up with CodeML) 
     * to create the Dom (i.e. "varcode.ex._ValueObject")
     * @param adHocClassName the adhoc class name to author
     * (i.e. varcode.ex.MyValueObject")
     * @param context binding of name values and scripts for authoring
     * @param directives pre and post processing directives 
     * @return the JavaCase
     */
    public static JavaCase of(
    	SourceLoader sourceLoader, 
        String markupClassName, 
        String adHocClassName, 
        VarContext context, 
        Directive...directives )
    {       
        SourceStream markupStream = null;
        
        if( markupClassName.endsWith( ".java" ) ) 
        {
    	    markupStream = sourceLoader.sourceStream( markupClassName );
        }
        else
        {
            markupStream = sourceLoader.sourceStream( markupClassName + ".java" );
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
     * @param sourceLoader the repository location of markup (java source code) 
     * @param markupClass the class containing markup to tailor
     * @param adHocClassName the name of the class (i.e. "ex.varcode.MyEnum")
     * @param context  containing bind key values and functionality for processing
     * @param directives pre and post processing 
     * @return the JavaCase
     */
    public static JavaCase of(
        SourceLoader sourceLoader, 
        Class<?> markupClass, 
        String adHocClassName,
        VarContext context, 
        Directive...directives )
    {
        SourceStream sourceStream = sourceLoader.sourceStream(
            markupClass.getCanonicalName() + ".java" );

        Dom dom = CodeML.compile( sourceStream ); 
        
        List<Directive> staticDirectives = 
            Resolve.bindResolveBaseClass( markupClass, context );
        
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
     * Entity that can build and return a 
     * {@code JavaCase} container of Java source code. 
     * 
     * These are "Top Level Containers"  (_class, _interface, _enum)
     * that can be passed to the runtime Java Compiler as an 
     * implementation of a {@code JavaFileObject} to be compiled
     * 
     * @author M. Eric DeFazio eric@varcode.io
     */
    public interface JavaCaseBuilder 
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
