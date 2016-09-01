package varcode.markup.mark;

import varcode.context.VarBindings;
import varcode.context.VarScope;
import varcode.markup.MarkupParseState;

/**
 * Populates values on the {@code MetaData} object when the varcode source
 * is being parsed by the {@code MarkupCompiler} 
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class SetMetadata
    extends Mark 
    implements Mark.BoundStatically
{
    private final String name;
    
    private final String value;
    
    public SetMetadata( 
        String text, 
        int lineNumber,
        String name,
        String value )
    {
        super( text, lineNumber );
        this.name = name;
        this.value = value;        
    }

    public void onMarkParsed( MarkupParseState parseState )
    {
    	VarBindings vb = 
    		parseState.getParseContext().getOrCreateBindings( VarScope.METADATA );
    	vb.put( name, value );          	
    }
    
    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}