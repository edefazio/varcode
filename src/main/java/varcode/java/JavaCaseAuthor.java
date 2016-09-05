package varcode.java;

import varcode.doc.Directive;

/**
 * Entity that has the ability to author (output)
 * a {@code JavaCase} Java source code. 
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
