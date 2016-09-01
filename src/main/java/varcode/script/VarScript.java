package varcode.script;

import java.util.Set;

import varcode.context.VarContext;

/**
 * Interface for a script 
 * (some functional code that accepts an input and evaluates an output)
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface VarScript
{
    /** Evaluate the script given the context and input and return the result */
    public Object eval( VarContext context, String input );
    
    /** Appends all of the varNames within the Script given the input */
    public void collectAllVarNames( Set<String> varNames, String input );
}
