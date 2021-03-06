/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.markup.form;

/**
 * When multiple Form instances are put together as a Series, 
 * a strategy for strings to separate each of the forms.
 * 
 * For instance, with a {@code Form} like: <PRE>
 * "{{+:{+type+} {+name+};
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
 * 
 * ...we want the series to be formattted as:
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
    /** format the Series of Form instances as a single String
     * @param seriesOfFormInstances realized forms
     * @return how the forms are "stitched together"
     */ 
    public String format( String[] seriesOfFormInstances );
    
    public String getText();
    
    public static final Inline INLINE = new Inline();
    
    public static class Inline
        implements SeriesFormatter
    {
        
        private Inline()
        { }
        
        @Override
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
        
        @Override
        public String toString()
        {
            return "INLINE";
        }
        
        @Override
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
        
        @Override
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
        
        @Override
        public String toString()
        {
            return "After Each (\"" + afterAllForms + "\")";
        }

        @Override
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
        
        @Override
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

        @Override
        public String getText()
        {
            return betweenForms;
        }                
    }
}
