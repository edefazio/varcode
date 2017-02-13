/*
 * Copyright 2017 Eric.
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
package varcode.markup;

import varcode.markup.bindml.BindML;
import varcode.markup.mark.Mark;

/**
 * Simple way of Filling a Template (it uses a First Come First Serve 
 * strategy
 * 
 * @see varcode.author.Author;
 * @author M. Eric DeFazio
 */
public enum Fill    
{ 
    ;
    
    /**
     * A Simplified "Fill in the blank" way of processing a 
     * Simple Template (for Simple Templates with only a 
     * few variables to be bound and <B>no VarScripts or Directives</B> )
     * @param template the template  
     * @param bindSeries series of Objects to fill in the document (in order or appearance)
     * @return 
     */ 
    public static String of( Template template, Object...bindSeries )
    {
        BindSeries bs = BindSeries.of( bindSeries );
        Mark.Bind[] bindMarks = template.getBindMarks();
        FillInTheBlanks.BlankBinding bb = template.getBlankBinding();
        Object[] fills = new Object[ bb.getBlanksCount() ]; 
        for( int i = 0; i < bindMarks.length; i++ )
        {
            if( bindMarks[ i ] instanceof Mark.HasVar )
            {                    
                Mark.HasVar hv = (Mark.HasVar)bindMarks[ i ];
                    
                fills[ i ] = bs.resolve( hv.getVarName() );                    
            }
        }
        return bb.bind( fills );        
    }
    
    public static String of( String markup, Object...bindSeries )
    {
        return of( BindML.compile( markup ), bindSeries );            
    }    
}
