package varcode.eval;

import javax.script.Bindings;

import varcode.VarException;
import varcode.context.VarContext;

/**
 * Evaluates a String expression using the Context (for parameters, etc.)
 * (By Default, uses Java's build in JavaScript Engine to evaluate)
 * 
 * For BindML:
 * <UL> 
 *  <LI>AddExpressionResult {+((expression))+} i.e. {+((5 + 4))+} (produces "9")
 *  <LI>DefineVarAsExpressionResult {#name:((expression))#} i.e. {#bitCount:(( (signBits + mantissaBits + exponentBits) | 0 ))#} 
 *  adds the var values of sign, mantissa, and exponent (the | 0 is because javascript will
 *  convert he var values to 48-bit floating point numbers, and "| 0" will cast back to int
 *  <LI>AddIfExpression {+?((expression)):text+}
 *  i.e. {+?(( log.equals('debug') || log.equals('info') )):log.debug("this is a info/debug log");+}
 * </UL> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Evaluator
{	
	public Object evaluate( VarContext context, String expressionText )
		throws VarException;
	
    /** Evaluates the Expression and returns the result */
    public Object evaluate( Bindings bindings, String expressionText )
    	throws VarException;
    
    /** Check if a String is a reserved word in the Expression engine */
    public boolean isReservedWord( String name );
}
