package varcode.context.eval;

import varcode.VarException;

/**
 * An error occurred when evaluating a Script or an Expression
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class EvalException
    extends VarException
{
	private static final long serialVersionUID = 1147321819344016462L;

	public EvalException( String message, Throwable throwable )
    {
		super( message, throwable );
    }
	
    public EvalException( String message )
    {
    	super( message );
    }
    
    public EvalException( Throwable t )
    {
    	super( t );
    }
}