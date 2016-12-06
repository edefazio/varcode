package varcode.doc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.eval.EvalException;

/**
 * LifeCycle Code run: 
 * <UL>
 *  <LI>before to authoring the Document 
 *  <LI>after authoring the Document
 * </UL>
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Directive
{
    /** Perform some actions BEFORE the vars are derived and bound/filled into the document */ 
    public interface PreProcessor
	extends Directive
    {
	/**
         * manipulate the DocState BEFORE the vars are derived and bound/filled 
         * into the document 
         * @param docState the state of the document
         */
	void preProcess( DocState docState);
    }
	
    /** Perform some actions AFTER the vars are bound to the document */
    public interface PostProcessor
	extends Directive
    {
        /** 
         * Perform some actions AFTER the vars are bound to the document 
         * @param docState the state of the document
         */
	void postProcess( DocState docState );
    }
		
    /**
     * Adapts a Static Method call to the {@code Directive.PreProcessor} interface
     * so that we might call static methods as if they implemented
     * {@code VarScript}  
     */
    public static class StaticMethodPreProcessAdapter
    	implements Directive.PreProcessor
    {
	private final Method method;
		
	private final Object[] params;
		
	public StaticMethodPreProcessAdapter( 
            Method method, Object... params )
	{
            this.method = method;
            if( params.length == 0 )
            {
		this.params = null;
            } 
            else
            {
		this.params = params;
            }			
	}

	public Object eval( VarContext context ) 
            throws EvalException
	{
            try 
            {
		return method.invoke( null, params );
            } 
            catch( IllegalAccessException e ) 
            {
		throw new EvalException( e );
            } 
            catch( IllegalArgumentException e ) 
            {
		throw new EvalException( e );
            } 
            catch( InvocationTargetException e ) 
            {
		if( e.getCause() instanceof VarException )
                {
                    throw (VarException)e.getCause();
		}
                throw new EvalException( e.getCause() );
            }
	}
		
        @Override
	public String toString()
	{
            return "Pre Processor to " + method.toString();
	}

        @Override
	public void preProcess( DocState docState ) 
	{
            eval( docState.getContext() );
	}		
    }
	
    /**
     * Adapts a Static Method call to the {@code VarScript} interface
     * so that we might call static methods as if they implemented
     * {@code VarScript}  
     */
    public static class StaticMethodPostProcessAdapter
	implements Directive.PostProcessor
    {
	private final Method method;
		
	private final Object[] params;
		
	public StaticMethodPostProcessAdapter( 
            Method method, Object... params )
	{
            this.method = method;
            if( params.length == 0 )
            {
		this.params = null;
            } 
            else
            {
		this.params = params;
            }			
	}

	public Object eval( VarContext context ) 
            throws EvalException
	{
            try 
            {
		return method.invoke( null, params );
            } 
            catch( IllegalAccessException e ) 
            {
		throw new EvalException( e );
            } 
            catch( IllegalArgumentException e ) 
            {
		throw new EvalException( e );
            } 
            catch( InvocationTargetException e ) 
            {
		if( e.getCause() instanceof VarException )
		{
                    throw (VarException) e.getCause();
		}
		throw new EvalException( e.getCause() );
            }
	}
		
        @Override
	public String toString()
	{
            return "Static Method Directive Post Processor to " + method.toString();
	}

        @Override
	public void postProcess( DocState docState ) 
	{
            eval( docState.getContext() );			
	}		
    }
}