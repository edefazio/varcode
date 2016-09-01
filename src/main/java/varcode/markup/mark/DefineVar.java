package varcode.markup.mark;

import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.markup.MarkupParseState;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.IsNamed;


//these are SCOPE instance variables defined at "Tailor time" MUTABLE
/*{#dayFormat:dd}*/
/*{#monthFormat:MM}*/
/*{#yearFormat:yyyy}*/
/*{{#dateFormat:{+yearFormat}-{+monthFormat}-{dayFormat} }}*/
/*{#versionDate:$date({+dateFormat})}*/

/*{#dateFormat:yyyy-MM-dd}*/ //<-- Define  a DateFormat Action
/*{#today:$date({+dateFormat})}*/ //<-- define today as DefineVarAsScriptResult
                                  // the result of calling $date(...)
                                  // using the defined dateFormat

/**
 * Defines the value of a Var as a literal 
 * (either a {@code StaticVar} or {@code InstanceVar} )  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVar
    extends Mark
    implements IsNamed, HasVars
{   
	protected final String varName;
    
	protected final String value;
    
    public DefineVar( 
        String text, 
        int lineNumber, 
        String varName, 
        String value )
    {
        super( text, lineNumber );
        this.varName = varName;
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }

    public String getVarName()
    {
        return varName;
    }

    public void collectVarNames( Set<String>varNames, VarContext context )
    {
       varNames.add( varName );
    }

    public static final class InstanceVar
        extends DefineVar
        implements BoundDynamically
    {
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String name, 
            String value )
        {
            super( text, lineNumber, name, value );
        }
        
        public Object derive( VarContext tailorContext )
        {
            return tailorContext.resolveVar( varName );
        }
        
        public void bind( VarContext tailorContext )
            throws VarException
        {
            tailorContext.set( varName, value, VarScope.INSTANCE );
        }
        
        public String toString()
        {
        	return "\""+ this.text + "\" (" + 
        			DefineVar.class.getSimpleName() + "." + this.getClass().getSimpleName() + 
        		") on line [" + lineNumber + "]";        	
        }       
    }
    
    public static final class StaticVar
        extends DefineVar
        implements BoundStatically
    {
        public StaticVar( 
            String text, 
            int lineNumber, 
            String name, 
            String value )
        {
            super( text, lineNumber, name, value );            
        }
    
        public void onMarkParsed( MarkupParseState parseState )
        {
        	parseState.setStaticVar( varName, value ); 
        }        
        
        public String toString()
        {
        	return "\""+ this.text + "\" (" + 
        			DefineVar.class.getSimpleName() + "." + this.getClass().getSimpleName() + 
        		") on line [" + lineNumber + "]";        	
        }
    }   
}
