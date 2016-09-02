package varcode.doc.lib.text;

import java.util.Set;

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.markup.ParseState.Lines;
import varcode.script.VarScript;

/**
 * Accepts String (code) as input, removes any empty lines 
 * (those with only whitespace and carriage returns)
 * then returns the String (code) as output.
 * Can be called as a Directive 
 * (which will remove all empty lines within the source 
 * in a postprocess manner (AFTER Tailoring):

/*{#$removeEmptyLines()}*/

/** Can be called as a VarScript to replace Wrapped Code:*/
/*{+$removeEmptyLines(*/

   //this

   // has

   // empty lines
/*)}*/
/* 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum RemoveEmptyLines
    implements VarScript, Directive.PostProcessor
{
	INSTANCE;

    /**
     * Parse the String into multiple lines and return the lines
     * (Blank lines with ONLY carriage returns are omitted)
     * @param source
     * @return
     */
    public static String[] separateOmitBlanks( String source )
    {            
        if( source == null )
        {
            return null;                
        }
        if ( source.isEmpty() )
        {
            return new String[ 0 ];
        }
        return source.split( "\\r?\\n" );           
    }
    
    public static final String from( String sourceCode )
    {
        String[] lines = separateOmitBlanks( sourceCode );
        StringBuilder sb = new StringBuilder();
        
        for( int i = 0; i < lines.length; i++ )
        {
            if( !( lines[ i ].trim().length() == 0 ) )
            {
                sb.append( lines[ i ] );
                if( i < lines.length - 1)
                {
                    sb.append( System.lineSeparator() );
                }                
            }            
        }
        String res = sb.toString(); 
        if( ( lines.length > 1 ) && Lines.countTotal( res ) == 1 )
        {
            return res + System.lineSeparator();
        }
        return res;
    }
    
    public Object eval( VarContext context, String input )
    {
        return from( input );        
    }
	
	public void postProcess( DocState tailorState ) 
	{		
		String theResult = RemoveEmptyLines.from( 
            tailorState.getTranslateBuffer().toString() );
		tailorState.getTranslateBuffer().clear();
		tailorState.getTranslateBuffer().append( theResult );
	}
	
	public void collectAllVarNames( Set<String> collection, String input ) 
	{
		collection.add( input );
	}
	
	public String toString()
	{
		return this.getClass().getName() ;
	}
}
