package varcode.markup.mark;

import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.IsNamed;

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
// "/*{-?removeLog:*/LOG.info( transactionState );/*-}*/

// "/*{-?removeLog=true:*/LOG.info( transactionState );/*-}*/
// "/*{-?removeLog==true:*/LOG.info( transactionState );/*-}*/

// The above will CUT the "LOG.info( transactionState );" conditional Text from the document 
// IF there is a var "removeLog" present and it is set to true

public class CutIfVarIs
    extends Mark
    implements BlankFiller, Mark.WrapsText, IsNamed
{    
	private final String varName;
	private final String targetValue; 
    
	private final String conditionalText; 
    
    public CutIfVarIs( 
        String text, int lineNumber, String varName, String targetValue, String conditionalText )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.targetValue = targetValue;
        this.conditionalText = conditionalText;
    }
    
    public String getVarName()
    {
    	return this.varName;
    }
    
    public String getTargetValue()
    {
    	return this.targetValue;
    }
    
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive( context ) );
    }
    
    public Object derive( VarContext context )
    {
    	Object val = context.resolveVar( varName );
    	if( val == null )
    	{
    		//we NEVER cut if the val for var is null
    		return conditionalText;
    	}
    	if( this.targetValue == null )
    	{   //we dont have a targetValue (which means ANY non null value for var means CUT)
    		return "";
    	}
    	if( val.equals( this.targetValue ) )
    	{   //we have a val for var, and target value, if they are equal CUT 
    		return "";
    	}
    	return conditionalText;    	        
    }

	
	public String getWrappedText() 
	{
		return conditionalText;
	}    
}
