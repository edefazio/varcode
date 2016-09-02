package varcode.doc.lib.text;

import java.util.Map;

import varcode.doc.Directive;
import varcode.doc.DocState;

/**
 * Find->replace that happens Post Tailoring (during Post Processing)
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class PostReplace
	implements Directive.PostProcessor
{
	/** the source string to look for */
	private final String[] lookFor;
	
	/** the target String to replace with*/
    private final String[] replaceWith;
    	
    public PostReplace( String lookFor, String replaceWith )
    {
    	this.lookFor = new String[]{ lookFor };
    	this.replaceWith = new String[]{ replaceWith };    		
    }
    
    public PostReplace( Map<String,String>targetToReplacement )
    {
    	lookFor = targetToReplacement.keySet().toArray( new String[ 0 ] );
    	replaceWith = new String[ lookFor.length ];
    	for( int i = 0; i < lookFor.length; i++ )
    	{
    		replaceWith[ i ] = targetToReplacement.get( lookFor[ i ] );
    	}
    }
    
	public void postProcess( DocState tailorState ) 
	{
		String s = tailorState.getTranslateBuffer().toString();
		for( int i = 0; i < lookFor.length; i++)
		{
			s = s.replace( lookFor[ i ], replaceWith[ i ] );
		}
		
		tailorState.getTranslateBuffer().clear().append( s );
	}    	
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < lookFor.length; i++ )
		{
			sb.append( System.lineSeparator() );
			sb.append("        ");
			sb.append( lookFor[ i ] );
			sb.append( " -> " );
			sb.append( replaceWith[ i ] );			
		}
		return getClass().getName() + sb.toString();
	}
}