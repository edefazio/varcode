package varcode.doc.form;

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
    /** All Var names of the Form
     * @param varNames
     * @param context */
    void collectVarNames( Set<String>varNames, VarContext context );
    
    /** 
     * gets all Marks of the Form
     * @return  all Marks in the form
     */
    Mark[] getAllMarks();
    
    /** 
     * gets the text used to make the {@code Form}
     * @return the Text of the Form
     */ 
    String getText();
    
    /** 
     * Compose the content and return it as a String
     * @param context the context to compose the form
     * @return the String document based on data in context
     */
    String compose( VarContext context );
    
    /**
     * Derive the form given the key-value pairs as input
     * @param keyValuePairs
     * @return the String representing the form 
     */
    String compose( Object...keyValuePairs );
        
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

        @Override
        public void collectVarNames( Set<String> varNames, VarContext context )
        {
            //do nothing... static forms have no var names
        }

        @Override
        public String toString()
        {
            return "STATIC FORM :" + "\r\n" + text;
        }

        /** Gets the form in textual form */
        @Override
        public String getText()
        {
            return text;
        }

        @Override
        public String compose( VarContext context )
        {
            return text;
        }

        @Override
        public String compose( Object... keyValuePairs )
        {
            return text;
        }
        
        @Override
        public Mark[] getAllMarks()
        {
           return new Mark[ 0 ];
        }        
    }
}
