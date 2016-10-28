package varcode.markup.mark;

import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.context.eval.EvalException;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasExpression;
import varcode.markup.mark.Mark.WrapsText;

/**
 * Replaces "wrapped" data with that of the result of an expression
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
//  /*{+(( (a + b / 2) |0 ))*/ 37 /*}*/
public class ReplaceWithExpressionResult
    extends Mark
    implements BlankFiller, WrapsText, HasExpression
{    
	/** the expression to be evaluated */
    private final String expression;
    
    /** Content wrapped between the open and close tags */
    private final String wrappedContent;
    
    public ReplaceWithExpressionResult( 
        String text, 
        int lineNumber, 
        String expression, 
        String wrappedContent )
    {
        super( text, lineNumber );
        this.expression = expression;
        this.wrappedContent = wrappedContent; 
    }
    
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive(context ) );
    }
    
    public Object derive( VarContext context )
    {
        try
        {
        	return context.getExpressionEvaluator().evaluate( 
        		context.getScopeBindings(), expression );
        }
        catch( Throwable t )
        {
        	throw new EvalException( 
        		"Error evaluating mark: " + N + text + N 
               + "with expression \"" + expression 
               + "\" on line [" + lineNumber + "] with content :" + N 
                  + wrappedContent + N, t );
        }        
    }
    
    public String getWrappedText()
    {
        return this.wrappedContent;
    }

    public String getExpression()
    {
        return expression;
    }    
}   
