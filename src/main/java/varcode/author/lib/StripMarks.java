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
package varcode.author.lib;

import java.util.BitSet;

import varcode.context.VarContext;
import varcode.author.AuthorState;
import varcode.author.PreProcessor;
import varcode.markup.FillInTheBlanks;
import varcode.markup.FillInTheBlanks.BlankBinding;
import varcode.markup.Template;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.WrapsText;

/**
 * Creates a new {@code DocState} where all of the {@code Mark}s are removed and
 * replaced with the text {@code Dom}
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum StripMarks
    implements PreProcessor
{
    INSTANCE;

    /**
     * 
     * @param template the template containing text and marks
     * @return a String representing the template without any marks
     */
    public static String stripFrom( Template template )
    {
        BlankBinding allMarksTemplate = template.getAllMarksTemplate();
        Mark[] marks = template.getMarks();

        int blanksCount = allMarksTemplate.getBlanksCount();
        StringBuilder sb = new StringBuilder();
        sb.append( allMarksTemplate.getTextBeforeBlank( 0 ) );
        for( int i = 0; i < blanksCount; i++ )
        {
            if( marks[ i ] instanceof WrapsText )
            {
                WrapsText wc = (WrapsText)marks[ i ];
                sb.append( wc.getWrappedText() );
            }
            sb.append( allMarksTemplate.getTextAfterBlank( i ) );
        }
        return sb.toString();
    }

    public static String stripAndCut( Template template )
    {
        BlankBinding markBinding = template.getAllMarksTemplate();
        Mark[] marks = template.getMarks();

        int blanksCount = markBinding.getBlanksCount();
        StringBuilder sb = new StringBuilder();
        sb.append( markBinding.getTextBeforeBlank( 0 ) );
        for( int i = 0; i < blanksCount; i++ )
        {
            if( marks[ i ] instanceof WrapsText )
            {
                WrapsText wc = (WrapsText)marks[ i ];
                sb.append( wc.getWrappedText() );
            }
            sb.append(markBinding.getTextAfterBlank( i ) );
        }
        return sb.toString();
    }

    @Override
    public void preProcess( AuthorState authorState )
    {
        String markupWithoutMarks
            = stripFrom( authorState.getTemplate() );

        VarContext context = VarContext.of();
        //context.mergeBindings( authorState.getTemplate().getParseContext() );

        Template templateSansMarks
            = new Template(
                new FillInTheBlanks.Builder( markupWithoutMarks ).compile(),
                new Mark[ 0 ],
                new BitSet() ); //,context );

        authorState.setTemplate( templateSansMarks );
    }

    @Override
    public String toString()
    {
        return this.getClass().getName()
            + ": (removes ALL marks from the Template)";
    }
}
