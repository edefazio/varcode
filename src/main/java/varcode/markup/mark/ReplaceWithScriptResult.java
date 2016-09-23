package varcode.markup.mark;

import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.eval.EvalException;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasScript;
import varcode.markup.mark.Mark.MayBeRequired;
import varcode.markup.mark.Mark.WrapsText;
import varcode.script.VarScript;

/**
 * Replaces "Wrapped Context" with the result of calling a Script
 * @author M. Eric DeFazio eric@varcode.io
 */

/*{+$tabsToSpaces(*/
//      a bunch of code
//      that uses tabs, but could be replaced based on the Environment 
//      Settings for what a "tab" is 
/*)}*/
public class ReplaceWithScriptResult
    extends Mark
    implements BlankFiller, WrapsText, HasScript, MayBeRequired
{    
    /** the name associated with the script */
    private final String scriptName;
    
    /** Content wrapped between the open and close tags */
    private final String wrappedContent;
    
    private final boolean isRequired;
     
    public ReplaceWithScriptResult( 
        String text, 
        int lineNumber, 
        String scriptName, 
        String wrappedContent, 
        boolean isRequired )
    {
        super( text, lineNumber );
        this.scriptName = scriptName;
        this.wrappedContent = wrappedContent;
        this.isRequired = isRequired;
    }
    
    public String getVarName()
    {
        return scriptName;
    }
    
    public void fill( VarContext context, TranslateBuffer buffer )
    {
        buffer.append( derive(context ) );
    }
    
    public Object derive( VarContext context )
    {
        VarScript theScript = context.resolveScript( scriptName, wrappedContent );
        if( theScript != null )
        {
            try
            {            
                return theScript.eval( context, wrappedContent );
            }
            catch( Throwable t )
            {
                throw new EvalException( 
                    "Error evaluating mark: " + N + text + N 
                  + "with script \"" + scriptName 
                  + "\" on line [" + lineNumber + "] with content :" + N 
                  + wrappedContent + N, t );
            }
        }
        throw new VarBindException( 
        	"Could not resolve script \""+ this.scriptName+"\" for mark "
            + N + text + N + "on line [" + lineNumber + "] ");
    }
    
    public String getWrappedText()
    {
        return this.wrappedContent;
    }

    public String getScriptName()
    {
        return scriptName;
    }
    
    public String getScriptInput()
    {
        return this.wrappedContent;
    }
	
	public boolean isRequired() 
	{
		return isRequired;
	}
}   
