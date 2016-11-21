package varcode;

import varcode.context.VarContext;
import varcode.doc.Directive;

/**
 * An IR (Intermediate Representation) of a Hierarchal 
 * Model of an entity that is represented in structured text
 * (in short, textual code, File formats, configuration files,
 * HTML, CSS, SQL, SVG, etc.)
 * 
 * The purpose for the model is to provide an intuitive API
 * for querying, and mutating an entity and ultimately the 
 * model (i.e. Java code, an HTML Table, a CSS styleSheet, 
 * a Maven POM file ) can "author" the code 
 * 
 * a good analogy for a Model is a HTML DOM (document object model):
 * from <A HREF="https://www.w3.org/DOM/">w3c.org</A><BR>
 * <BLOCKQUOTE>
 * "The Document Object Model is a platform- and language-neutral 
 * interface that will allow programs and scripts to dynamically 
 * access and update the content, structure and style of documents." 
 * </BLOCKQUOTE>
 * 
 * which models entities 
 * <UL>
 *  <LI>SQL
 *  <LI>IDL Data Format
 *  <LI>Contract
 *  <LI>SVG Graphics
 *  <LI>Schema / DDL
 *  <LI>Property File
 *  <LI>Build Script
 *  <LI>Container Configuration
 *  <LI>etc...
 * </UL>
 * 
 * the structured text can (internally) contain {@code varcode.markup.Mark}s in 
 * {@code varcode.markup.bindml.BindML}
 * <UL>
 *  <LI>"{+name+}"
 *  <LI>"{+((a = b))+}"
 *  <LI>...
 * </UL>
 * are lazily 
 * so we could have a _method:
 * <PRE>
 * _method parameterized = _method.of( 
 *    "public {+returnType+} getByName( String name )" );
 * </PRE>
 * we could generate the code for the method (which does not compile):
 * 
 * <PRE>
 * System.out.println( lazyBindMethod );
 *  //prints:
 * "public {+returnType+} getByName( String name )"
 * </PRE>
 * 
 * ...or LazyBind the method to build the template, compile it to a {@code Dom}
 * and then 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Model
{   
    public static final String N = "\r\n";
	
    /** 
     * Strings passed in with this prefix signify they are 
     * Literals and not a String representation of an entity
     * 
     */
    public static final String STRING_LITERAL_PREFIX = "$$";
    
    /**
     * Binds values from the context into BindML marks within the model
     * @param context providing bindings to be bound within the model
     * @return the mutated model
     */
    Model bind( VarContext context );
        
    /**
     * A "Brute Force" replace for the content within the template
     * @param target the target string to look for
     * @param replacement the replacement string
     * @return the modified variant, (if it is mutable) or a modified clone
     */
    Model replace( String target, String replacement );     
    
    /** 
     * Extended Model interface form modeling a program language
     */ 
    public interface LangModel
        extends Model
    {
        /** 
         * Authors the document bound document as a String
         * 
         * @return document representation of the model
         */    
        String author( );
    
        /** 
         * Authors the document for the model directives
         * and returns the bound document as a String
         * 
         * @param directives optional directives to apply when 
         * authoring the document
         * @return document representation of the model
         */ 
	    String author( Directive... directives );
    }
    
    /**
     * An exception in modeling an entity
     */
    public static class ModelException
        extends VarException
    {
        
        public ModelException( String message, Throwable throwable )
        {
            super( message, throwable );
        }
        
        public ModelException( String message )
        {
            super( message );
        }
        
        public ModelException( Throwable throwable )
        {
            super( throwable );
        }
    }
    
    /** 
     * Signifies an error when attempting to Load a Model
     */
    public class ModelLoadException 
        extends ModelException
    {        
        public ModelLoadException( String message, Throwable throwable ) 
        {
            super( message, throwable );
        }
        
        public ModelLoadException( String message ) 
        {
            super( message );
        }
        
        public ModelLoadException( Throwable throwable ) 
        {
            super( throwable );
        }
    }    
}
