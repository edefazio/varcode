package varcode.form;

import java.util.Collections;
import java.util.Set;

import varcode.context.VarContext;
import varcode.markup.mark.Mark;

/**
 * Abstracts over Static and dynamic Forms (Text)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 * 
 * @see VarForm Form containing static text wixed with variables 
 * @see StaticForm unchanging, immutable form (static String)
 * 
 */
public interface Form
{
    /** All Var names of the Form */
    public void collectVarNames( Set<String>varNames, VarContext context );
    
    /** gets all Marks of the Form */
    public Mark[] getAllMarks();
    
    /** gets the text used to make the {@code Form} */ 
    public String getText();
    
    /** tailor the content and return it as a String */
    public String derive( VarContext context );
    
    /** A Static Form (No variables/variability) */
    public static class StaticForm
        implements Form
    {
        public static final Set<String> NO_VARS = 
            Collections.emptySet();
        
        public final String text;
    
        public StaticForm( String text )
        {
            this.text = text;
        }

        public void collectVarNames( Set<String> varNames, VarContext context )
        {
            //do nothing
        }

        public String toString()
        {
            return "STATIC FORM :" + System.lineSeparator() + text;
        }

        /** Gets the form in textual form */
        public String getText()
        {
            return text;
        }

        public String getName()
        {
            return null;
        }

        public String derive( VarContext context )
        {
            return text;
        }

        public Mark[] getAllMarks()
        {
           return new Mark[ 0 ];
        }        
    }
}
