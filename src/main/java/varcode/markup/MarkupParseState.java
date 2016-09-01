package varcode.markup;

import varcode.context.VarContext;

/**
 * Parse State as Markup is being read/Parsed/Compiled into the {@code Dom} 
 * 
 * State that allows immutable variables and scripts to be 
 * assigned at parse/compile-time.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface MarkupParseState
	extends ParseState
{
	/** sets the value of a Static var to a Value at "compile-time" */
    void setStaticVar( String varName, Object value );
    	        
    /** 
     * Statically defined vars that have been encountered during 
     * Parsing/Compilation of the {@code Markup} to the {@code Dom}.
     * 
     * <I>(This allows static vars that are defined to reference/use/access 
     * previously defined static vars that have been defined when the 
     * Markup is being read/ parsed/ compiled into the Dom.</I>   
     */
     VarContext getParseContext();
     
}