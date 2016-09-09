package varcode;

import varcode.doc.Directive;
import varcode.doc.lib.text.Prefix;

/**
 * An entity that can represent itself as "code".
 * for instance a a Java method, which has many sub-components 
 * (signature, parameters) that are structured in a way
 * that we can author represent the method (object) as text.
 * 
 * NOTE: this is NOT specific or limited to Java or any particular
 * language (C, C++, javascript) or format, but "code" can be any
 * <UL>
 *  <LI>Structured text
 *  <LI>Data Format
 *  <LI>Contract
 *  <LI>Schema
 *  <LI>Property File
 *  <LI>Build Script
 *  <LI>etc...
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface CodeAuthor 
{
	public static final String N = "\r\n";

	public static final Prefix INDENT = Prefix.INDENT_4_SPACES;
	
	/** 
     * Authors the code for an entity
     * @param directives optional directives to apply when 
     * authoring the representation
     * @return a textual code representation of the entity
     */ 
	public String author( Directive... directives );
	
}
