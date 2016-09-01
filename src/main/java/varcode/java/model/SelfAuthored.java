package varcode.java.model;

import varcode.doc.Directive;
import varcode.doc.lib.text.Prefix;

public interface SelfAuthored 
{
	public static final String N = System.lineSeparator();

	public static final Prefix INDENT = Prefix.INDENT_4_SPACES;
	
	/** authors the code to represent the modeled entity*/ 
	String toCode( Directive... directives );
	
}
