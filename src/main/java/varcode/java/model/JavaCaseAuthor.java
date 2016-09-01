package varcode.java.model;

import varcode.doc.Directive;
import varcode.java.JavaCase;

/**
 * A "builder"-like entity that is procedurally / dynamically built
 * to programmatically build and output Java source code. 
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
}
