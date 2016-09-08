package varcode.java.model;

import varcode.doc.Directive;
import varcode.doc.lib.text.Prefix;

/**
 * An entity that can represent itself as authored code
 * for instance a method, which has many sub-components 
 * (signature, parameters) that are structured in a way
 * that we can author represent the method (object) as text that
 * can be later compiled and run.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface SelfAuthored 
{
	public static final String N = "\r\n";

	public static final Prefix INDENT = Prefix.INDENT_4_SPACES;
	
	/** 
     * Authors the code to represent the modeled entity
     * @param directives optional directives to apply when authoring the representation
     * @return a textual representation of the entity
     */ 
	public String author( Directive... directives );
	
}
