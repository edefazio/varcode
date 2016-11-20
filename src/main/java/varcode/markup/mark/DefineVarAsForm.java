package varcode.markup.mark;

import java.util.Set;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.form.Form;
import varcode.markup.MarkupParseState;
import varcode.markup.mark.Mark.Derived;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.IsNamed;

/*{{#className*:IntFrameBoxOf{+fieldCount}}}*/
/*{{#FieldParams...*:IntFieldBox {+FieldName...}, }}*/
/*{{#Params...*:{+FieldName}, }}*/
/*{{#params...*:{+fieldName}, }}*/

/**
 * Derives a "Form" that is assigned to a name
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public abstract class DefineVarAsForm
    extends Mark
    implements IsNamed, HasForm, HasVars, Derived
{       
	protected final boolean isRequired;      
	protected final String varName;
	protected final Form form;
    
    public DefineVarAsForm(
        String text, 
        int lineNumber,
        String varName,    
        Form form,        
        boolean isRequired)
    {
        super( text, lineNumber );
        this.varName = varName;
        this.form = form;    
        this.isRequired = isRequired;
    }
    
    public String getVarName()
    {
        return varName;
    }

    public Form getForm()
    {
        return form;
    }
    
    public String derive( VarContext context )
    {
        try
        {
            return form.compose( context );            
        }
        catch( Exception cme )
        {
            throw new VarException (
                "Unable to derive DefineVarAsForm \"" + varName + "\" for mark "
              + N + text + N +" on line [" + lineNumber + "]", cme );
        }        
    }
    
    public void collectVarNames( Set<String>varNames, VarContext context )
    {
       varNames.add( varName );
    }
    
    public static final class InstanceVar
        extends DefineVarAsForm
        implements BoundDynamically, Bind
    {
        public InstanceVar( 
            String text, 
            int lineNumber, 
            String name, 
            Form form,
            boolean isRequired )
        {
            super( text, lineNumber, name, form, isRequired );
        }
        
        public void bind( VarContext context )
            throws VarException
        {
            String derived = derive( context );
            if( derived != null && derived.length() > 0 )
            {   //we only set things if they are non-null 
            	context.set( varName, derived, VarScope.INSTANCE );
            }
        }
        
        public String toString()
        {
        	return "\""+ this.text + "\" (" + 
        			DefineVarAsForm.class.getSimpleName() + "." + this.getClass().getSimpleName() + 
        		") on line [" + lineNumber + "]";        	
        }
    }
    
    public static final class StaticVar
        extends DefineVarAsForm
        implements BoundStatically
    {
        public StaticVar(
            String text, 
            int lineNumber, 
            String name, 
            Form form )
        {
            super( text, lineNumber, name, form, true );
        }
        
        public StaticVar( 
            String text, 
            int lineNumber, 
            String name, 
            Form form,
            boolean isRequired)
        {
            super( text, lineNumber, name, form, isRequired );
        }
        
        
        public void onMarkParsed( MarkupParseState parseState )
        {
        	 String derived = derive( parseState.getParseContext() );
             if( derived != null && derived.length() > 0 )
             {   //we only set things if they are non-null 
            	 parseState.setStaticVar( varName, derived );
             }  
        }
        
        public String toString()
        {
        	return "\""+ this.text + "\" (" + 
        			DefineVarAsForm.class.getSimpleName() + "." + this.getClass().getSimpleName() + 
        		") on line [" + lineNumber + "]";        	
        }
    }
}
