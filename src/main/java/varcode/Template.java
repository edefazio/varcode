/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.doc.lib.text.Prefix;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * Each component can be lazily bound...where BindML tags:
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
public interface Template
{   
    /** 
     * Strings passed in with this prefix signify they are 
     * Literals and not a String representation of an entity
     * 
     */
    public static final String STRING_LITERAL_PREFIX = "$$";
    
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
    Template bindIn( VarContext context );
        
    /**
     * A "Brute Force" replace for the content within the template
     * @param target the target string to look for
     * @param replacement the replacement string
     * @return the modified variant, (if it is mutable) or a modified clone
     */
    Template replace( String target, String replacement ); 
        
    /**
     * Base way of specializing the Parameterized VarCode
     */
    public static abstract class Base
        implements Template, CodeAuthor
    {        
        /**
         * 
         * @param context contains bound variables and scripts to bind data into
         * the template
         * @param directives pre-and post document directives 
         * @return the populated Template bound with Data from the context
         */
        @Override
        public String bind( VarContext context, Directive...directives )
        {
            Dom dom = BindML.compile( author() ); 
            return Author.code( dom, context, directives );
        }        
    }
}
