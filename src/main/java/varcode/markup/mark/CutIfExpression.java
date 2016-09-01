package varcode.markup.mark;

import varcode.buffer.TranslateBuffer;
import varcode.context.VarContext;
import varcode.eval.EvalException;
import varcode.eval.Evaluator;
import varcode.markup.mark.Mark.BlankFiller;

/**
 * Wraps code with a condition:
 * <UL>
 *   <LI> If the condition evaluates to TRUE the code is CUT/REMOVED 
 *   from the tailored code.
 *   <LI> otherwise the code remains intact
 * </UL>
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
// CodeML
// "/*{-?(( typeof removeLog != 'undefined' && removeLog )):*/LOG.info( transactionState );/*-}*/

// The above will CUT the "LOG.info( transactionState );" conditional Text from the document 
// IF there is a var "removeLog" present and it is set to true

public class CutIfExpression
    extends Mark
    implements BlankFiller
{    
	private final String expression;
    
	private final String conditionalText; 
    
    public CutIfExpression( 
        String text, int lineNumber, String expression, String conditionalText )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.conditionalText = conditionalText;
    }
    
    public String getExpression()
    {
        return expression;
    }
    
    public String getConditionalText()
    {
        return conditionalText;
    }
    
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
        Evaluator ce = context.getExpressionEvaluator();
        try
        {
            Object result = ce.evaluate( context.getScopeBindings(), expression );
        
            if( result instanceof Boolean && ( (Boolean)result).booleanValue() )
            {
                return null;                      
            }
            return conditionalText;
        }
        catch( Exception e )
        {   
            throw new EvalException( 
                "Unable to evaluate CutIfExpression : " + N + text + N
               +"on line [" + lineNumber + "]", e );
        }        
    }    
}
