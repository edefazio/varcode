package varcode.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.lib.java.CommentEscape;
import varcode.doc.lib.text.PrefixWithLineNumber;
import varcode.doc.Dom;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacException;
import varcode.markup.codeml.CodeML;
import varcode.markup.repo.MarkupRepo;
import varcode.markup.repo.MarkupRepo.MarkupStream;

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
        return of( JavaMarkupRepo.INSTANCE, 
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
        return of( JavaMarkupRepo.INSTANCE, 
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
    	MarkupRepo markupRepo, 
        String markupClassName, 
        String adHocClassName, 
        VarContext context, 
        Directive...directives )
    {       
        MarkupStream markupStream = null;
        
        if( markupClassName.endsWith( ".java" ) ) 
        {
    	    markupStream = markupRepo.markupStream( markupClassName );
        }
        else
        {
            markupStream = markupRepo.markupStream( markupClassName + ".java" );
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
        MarkupRepo markupRepo, 
        Class<?> markupClass, 
        String adHocClassName,
        VarContext context, 
        Directive...directives )
    {
        MarkupStream markupStream = markupRepo.markupStream( 
            markupClass.getCanonicalName() + ".java" );

        Dom dom = CodeML.compile( markupStream ); 
        
        List<Directive> staticDirectives = 
            JavaMarkup.bindStaticFields( markupClass, context );
        
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
            Java.author( adHocClassName, dom, context, directives );
    }

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
    	return Java.loadClass( classLoader, this.adHocJavaFile );
    }
    
    public Class<?> loadClass()
    {
    	try
    	{
    		return Java.loadClass( this.adHocJavaFile );    		
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
        return Java.instance( theClass, constructorArgs );
    }                 
    
    
    public Object instance( AdHocClassLoader classLoader, Object... constructorArgs )
    {
        Class<?> theClass = loadClass( classLoader );
        return Java.instance( theClass, constructorArgs );
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
         * Return the Java Code (class, interface, Enum, etc)
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
