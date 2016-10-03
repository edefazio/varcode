/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode;

import varcode.context.VarContext;
import varcode.doc.Directive;

/**
 * Hierarchal Model of structured text 
 * NOT specific or limited to Java or any particular
 * language (C, C++, Javascript) or format, but any
 * type of structured text:
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
     * Authors the code for an entity
     * @param directives optional directives to apply when 
     * authoring the representation
     * @return a textual code representation of the entity
     */ 
	String author( Directive... directives );
    
    /**
     * <UL>
     *  <LI>Builds the template for the entity (it may contain unbound marks like "{+name+}") 
     *  <LI>Compiles the template (using BindML) as a Dom (compile the marks and text)
     *  <LI>Authors the document using the Dom, context and directives
     *  <LI>returns the bound document as a String
     * </UL>
     * 
     * @param context the context containing specialization (functions/data)
     * @param directives directives applied to the 
     * @return the bound element
     */
    String bind( VarContext context, Directive...directives );
 
    /**
     * 
     * @param context
     * @return 
     */
    Model bindIn( VarContext context );
        
    /**
     * A "Brute Force" replace for the content within the template
     * @param target the target string to look for
     * @param replacement the replacement string
     * @return the modified variant, (if it is mutable) or a modified clone
     */
    Model replace( String target, String replacement );     
}
