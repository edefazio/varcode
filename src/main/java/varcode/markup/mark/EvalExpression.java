package varcode.markup.mark;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.eval.EvalException;
import varcode.markup.mark.Mark.Derived;
import varcode.markup.mark.Mark.HasExpression;

/* BindML */
// {(( expression ))}
// ---            ---
// OPEN TAG       CLOSE TAG

/* CodeML */
/*{(( expression ))}*/ 
/**
 * Evaluates an INSTANCE expression (usually for loading one or more JavaScript functions
 * that may be called later
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class EvalExpression
    extends Mark
    implements HasExpression, Derived
{
    /** the expression to evaluate */
    private final String expression;
    
    // (( Math.PI * r * r ))
    // /*{(( Math.PI * r * r ))*}*/ //REQUIRED
    public EvalExpression( 
    	String text, int lineNumber, String expression )
    {
        super( text, lineNumber );
        this.expression = expression;        
    }

    public Object derive( VarContext context )
    	throws EvalException
    {
    	try
    	{
    		context.getExpressionEvaluator().evaluate( 
    			context.getScopeBindings(), expression );
    	}
    	catch( Throwable t )
    	{
    		if( t instanceof VarException )
    		{   //we already created an exception and or wrapper or re-throw (no wrapping)    			
    			throw (VarException)t;
    		}
    		//something unexpected happened, I need to wrap and throw 
    		throw new EvalException(
    			"Expression \"" + expression + "\" for mark :" + N + text + N
                +" on line [" + lineNumber + "] failed", t );
    	}
        return null;
    }

	public String getExpression() 
	{
		return expression;
	}
}
