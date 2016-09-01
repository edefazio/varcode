package varcode.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.lib.java.CommentEscape;
import varcode.doc.lib.text.PrefixWithLineNumber;
import varcode.dom.Dom;
import varcode.java.javac.InMemoryJavaClassLoader;
import varcode.java.javac.InMemoryJavaCode;
import varcode.java.javac.JavacException;
import varcode.markup.codeml.CodeML;
import varcode.markup.repo.MarkupRepo;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * Represents the combination of {@code Dom} and {@code VarContext} to 
 * represent authored / tailored Java Source code.
 * 
 * Stores the JavaCode as an <CODE>InMemoryJavaCode</CODE> object 
 * which can be compiled in memory or exported
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
    private final InMemoryJavaCode javaCode;
    
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param markupClazz the clazz (whos source is VarSource)
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */ 
    public static final JavaCase of( 
    		Class<?> markupClazz, String fullyQualifiedTargetClassName, Object... keyValuePairs )
    {
        return of( JavaMarkupRepo.INSTANCE, markupClazz, fullyQualifiedTargetClassName, VarContext.of( keyValuePairs ) );
    }
    
    public String getClassName()
    {
    	return javaCode.getClassName();
    }
    
    public static final JavaCase of( 
    	Class<?> markupClazz, String fullyQualifiedTargetClassName, VarContext context, Directive...directives )
    {
        return of( JavaMarkupRepo.INSTANCE, markupClazz, fullyQualifiedTargetClassName, context, directives );
    } 
    
    /**
     * 
     * @param markupRepo the repository location of markup (java source code) 
     * @param fullClassName (i.e. "java.lang.String.java")
     * @param keyValuePairs key/Values to comprise the {@code VarContext}
     * @return
     */
    public static final JavaCase of( 
    	MarkupRepo markupRepo, String fullClassName, String fullyQualifiedClasName, VarContext context, Directive...directives )
    {
    	 MarkupStream markupStream = markupRepo.markupStream( fullClassName );
    	 
    	 Dom markup = CodeML.compile( markupStream ); 
    	       
    	 return new JavaCase( fullyQualifiedClasName, markup,  context, directives );
    }
    
    /**
     * Using the sourcePath resolve the source for {@code clazz}, and create
     * a new Case based on a {@code VarContext} containing {@code pairs} 
     * (pairs is data passed in as alternating key,value, key, value)
     *  
     * @param sourcePath the path to use to resolve the source
     * @param markupClass the class
     * @param keyValuePairs
     * @return
     */
    public static final JavaCase of( 
        MarkupRepo markupRepo, 
        Class<?> markupClass, 
        String fullyQualifiedTargetClassName,
        VarContext varContext, 
        Directive...directives )
    {
        MarkupStream markupStream = markupRepo.markupStream( 
            markupClass.getCanonicalName() + ".java" );

        Dom dom = CodeML.compile( markupStream ); 
        
        List<Directive> staticDirectives = JavaMarkup.bindStaticFields( markupClass, varContext );
        List<Directive> allDirectives = new ArrayList<Directive>();
        
        allDirectives.add( CommentEscape.INSTANCE );
        allDirectives.addAll( Arrays.asList( directives ) );
        allDirectives.addAll( staticDirectives );

        return new JavaCase( fullyQualifiedTargetClassName, dom, varContext, allDirectives.toArray( new Directive[ 0 ] ) );
    }

    public static final JavaCase of( 
    	String fullyQualifiedTargetClassName, Dom dom, VarContext context, Directive...directives )
    {
    	return new JavaCase( fullyQualifiedTargetClassName, dom, context, directives );
    }
        
    /**
     * Using the Class, lookup the source for the class (assuming it is in the
     * Standard {@code JavaSrcPath}
     * @param clazz the clazz (whos source is VarSource)
     * @param keyValuePairs key-value pairs of vars and scripts 
     * @return the Case (representing the Markup and VarContext) 
     */
    public static final JavaCase of( String fullyQualifiedTargetClassName, Dom dom, Object... keyValuePairs )
    {
    	return new JavaCase( fullyQualifiedTargetClassName, dom, VarContext.of( keyValuePairs) );
    }
        
    private JavaCase( String fullyQualifiedTargetClassName, Dom dom, VarContext context, Directive...directives )
    {    	
    	
    	this.javaCode = Java.author( fullyQualifiedTargetClassName, dom, context, directives );
    }

    public String toString()
    {
    	return this.javaCode.getCode();
    }
    
    public InMemoryJavaCode javaCode()
    {
        return this.javaCode;
    }
        
    public Class<?> loadClass( InMemoryJavaClassLoader classLoader )
    {
    	return Java.loadClass( 
    		classLoader, this.javaCode );
    }
    
    public Class<?> loadClass()
    {
    	try
    	{
    		return Java.loadClass( this.javaCode );    		
    	}
    	catch( JavacException je)
    	{   //if an error occurs compiling the source, 
    		//prefix the source with line numbers and print it out to help debugging
    		String codePrefixed = 
    			PrefixWithLineNumber.doPrefix( this.javaCode.getCode() );
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
}
