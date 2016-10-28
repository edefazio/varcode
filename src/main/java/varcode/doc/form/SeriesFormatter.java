package varcode.doc.form;

/**
 * When multiple Form instances are put together as a Series, 
 * a strategy for strings to separate each of the forms.
 * 
 * For instance, with a {@code Form} like: <PRE>
 * "?{{+:{+type+} {+name+};
 * +}}"</PRE>
 * 
 * where :<PRE>
 * VarContext vc = VarContext.of(
 *     "type", new Class[]{ int.class, int.class},
 *     "name", new String[] {"x", "y"} ); </PRE>
 *     
 * we want: <PRE>
 * "int x;
 *  int y; </PRE>
 * ...if however we have a {@code Form} like: <PRE>
 * "{{+:{+type+} {+name+},+}}"</PRE>
 * 
 * where :<PRE>
 * VarContext vc = VarContext.of(
 *     "type", new Class[]{ int.class, int.class},
 *     "name", new String[] {"x", "y"} ); </PRE>
 * ...we want
 * <PRE>"int x, int y"</PRE>
 * NOTE: the "," separator ONLY appears after the first Form in the Series.
 *   
 * 
 * There are usually (2) Strategies
 * <UL>
 *   <LI> (default) AlwaysAfter - add some static String after each Form... 
 *   for instance: with<PRE><CODE> 
 *   CodeForm form = CodeForm.of( 
 *       "int {+fieldName}; " );</CODE></PRE>
 *   
 *       
 *   if we are populating (1) fieldName:<BR><PRE><CODE>
 *   StringBuilder sb = new new StringBuilder();
 *   String one = form.tailorAll( Pairs.of( "fieldName", "count" ), sb );
 *    //one = "int count; "</CODE></PRE>
 *    
 *   ...if we have multiple field Names with the form: <PRE><CODE>
 *   String threeFields = 
 *       form.tailorAll( 
 *           Pairs.of( "fieldName", new String[]{"one", "two", "three"} ), sb );
 *   //threeFields = int one; int two; int three;        
 *   </CODE></PRE>        
 *    
 *   <LI> OnlyBetween - here we add separators "between" each of the form 
 *   instances (a good simple example is an arguments list)
 *   
 * </UL>        
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface SeriesFormatter
{
    /** format the Series of Form instances as a single String */ 
    public String format( String[] seriesOfFormInstances );
    
    public String getText();
    
    public static final Inline INLINE = new Inline();
    
    public static class Inline
        implements SeriesFormatter
    {
        
        private Inline()
        { }
        
        public String format( String[] forms )
        {
            if( forms == null )
            {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < forms.length; i++ )
            {
                sb.append( forms[ i ] );                
            }
            return sb.toString();  
        }
        
        public String toString()
        {
            return "INLINE";
        }
        
        public String getText()
        {
            return "";
        }        
    }
    
    /**
     * Text after EACH Form instance in a Series of Forms.
     * 
     * For instance:
     * I might have a variable declaration as a Form:
     * <PRE>
     * "{+type} {+name}{{+?value: = {+value} }};" + System.lineSeparator();
     * </PRE>
     * example instances: <PRE>
     * "int angleCount = 4; // <-carriage return
     * "
     * "String name; // <-carriage return
     * "</PRE>
     * in this case, the String : 
     * <PRE>";"+ System.lineSeparator();</PRE>
     * are applied ALWAYS AFTER EACH Form, where each form is:
     * <PRE>"{+type} {+name}{{+?value: = {+value} }}"</PRE>
     * 
     * NOTE: we "strip" the 
     * <PRE>";"+ System.lineSeparator()</PRE> 
     * when processing each Form instance and 
     * "add it back in" with the {@code AlwaysAfterEach}.  
     */
    public class AfterEach 
        implements SeriesFormatter     
    {
        private final String afterAllForms;
        
        public AfterEach( String afterAllForms )
        {
            this.afterAllForms = afterAllForms;
        }
        
        public String format( String[] forms )
        {
            if( forms == null )
            {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < forms.length; i++ )
            {
                sb.append( forms[ i ] );
                sb.append( afterAllForms );                
            }
            return sb.toString();  
        }
        
        public String toString()
        {
            return "After Each (\"" + afterAllForms + "\")";
        }

        public String getText()
        {
            return afterAllForms;
        }
    }
    
    /**
     * Text that is added BETWEEN two Form instance in a Series of Forms.
     * 
     * For instance:
     * I might have a variable parameter list:
     * <PRE>
     * "{+type} {+name}, "
     * </PRE>
     * example instances: <PRE>int a, String name</PRE>
     * 
     * in this case, the String : 
     * <PRE>", "</PRE>
     * are CONDITIONALLY applied ONLY BETWEEN two forms:
     * 
     * So if we have a Series of Forms:<PRE>
     * String[] forms = 
     *   { "int count", "String name", "Date date"};</PRE> 
     * 
     * we put them together in a Series :<BR>
     * <PRE>"int count, String name, Date date"
     * //             ^^           ^^
     * //             ||           ||
     * //              OnlyBetweenTwo 
     * </PRE>  
     */
    public class BetweenTwo
        implements SeriesFormatter
    {
        private final String betweenForms;
        
        public BetweenTwo( String betweenForms )
        {
            this.betweenForms = betweenForms;
        }
        
        public String format( String[] forms )
        {
            if( forms == null )
            {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < forms.length; i++ )
            {
                if( i > 0 )
                {
                    sb.append( betweenForms );
                }
                sb.append( forms[ i ] );                
            }
            return sb.toString();
        }

        public String getText()
        {
            return betweenForms;
        }                
    }
}
