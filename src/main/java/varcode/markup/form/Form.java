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

import java.util.Collections;
import java.util.Set;
import varcode.context.Context;
import varcode.markup.mark.Mark;

/**
 * Abstracts over Static and dynamic Forms (Text)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 * 
 * @see VarForm Form containing static text mixed with variables 
 * @see StaticForm unchanging, immutable form (static String)
 * 
 */
public interface Form
    extends Mark.HasVars
{
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
     * Author a Series each element will be a separate array 
     * @param context
     * @return 
     */
    String[] authorSeries( Context context );
    
    /** 
     * Compose the content and return it as a String
     * @param context the context to compose the form
     * @return the String document based on data in context
     */
    String author( Context context );
    
    /**
     * Derive the form given the key-value pairs as input
     * @param keyValuePairs
     * @return the String representing the form 
     */
    String author( Object...keyValuePairs );
        
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
        public String toString()
        {
            return "STATIC FORM :" + "\r\n" + text;
        }

        @Override
        public String[] authorSeries( Context context )
        {
            return new String[] { text };
        }
        
        /** Gets the form in textual form */
        @Override
        public String getText()
        {
            return text;
        }

        @Override
        public String author( Context context )
        {
            return text;
        }

        @Override
        public String author( Object... keyValuePairs )
        {
            return text;
        }
        
        @Override
        public Mark[] getAllMarks()
        {
           return new Mark[ 0 ];
        }        

        @Override
        public Set<String> getVarNames() 
        {
            return NO_VARS;
        }
    }
}
