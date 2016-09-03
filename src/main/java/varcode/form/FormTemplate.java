/*
 * Copyright 2015 M. Eric DeFazio.
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
package varcode.form;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import varcode.context.VarContext;
import varcode.doc.FillInTheBlanks;
import varcode.doc.FillInTheBlanks.FillTemplate;
import varcode.markup.MarkupTemplate;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.IsNamed;
import varcode.markup.mark.Mark.MayBeRequired;

/**
 * Variable {@code VarForm}'s {@code Dom}
 *   
 * "Compiled" object instance for the {@code Markup} 
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A>.
 * <BR><BR>
 *    
 * Contains the static text (Code) along with {@code Mark}s 
 * to Specialize / Tailor new source code. 
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class FormTemplate
    implements MarkupTemplate
{
    /** ALL {@code Mark}s on the dom */
    private final Mark[] marks;

    /** text and "blanks" where ALL {@code MarkAction}s occur */
    private final FillTemplate markTemplate;

    /**
     * The character locations of *ALL* marks within the document. 
     * (even {@code Mark}s that are not bound to the tailored Template)
     * 
     * These {@code Mark}s: 
     * <UL>
     *   <LI>{@code CutComment} 
     *   <LI>{@code CutJavaDocComment}
     *   <LI>{@code CutCode}
     * </UL>
     * 
     * ...Are not "Bound" into the "Tailored" document, 
     * (and not Modeled within a {@code FillInTheBlanks.FillOrder} ) 
     * we keep track of the character index (within the varcode) 
     * where these {@code Mark}s occurred.
     */
    private final BitSet markIndicies;

    /**
     * The sequence of {@code BindMark}s as they appear in the Template 
     * to be populated when tailoring 
     * i.e.
     * <PRE>
     * I _____________________, do solemnly swear to tell the truth. 
     *        (fullName)
     * </PRE>    
     * 
     * ...where "fullName" is the {@code BindMark}s' name".<BR> 
     * 
     * NOTE: MAY CONTAIN DUPLICATE NAMES, for instance, given the sequence:
     * <PRE>
     * _________, is the number of the counting, again the number is _______.
     *   count                                                        count
     * </PRE>
     * the name "count" appears (2) times at [0] and at [1].
     */
    private final Mark.BlankFiller[] blankFillerMarks;

    /** text and "blanks" where bindMarks occur and context params are bound */
    private final FillInTheBlanks.FillTemplate blankFillerMarksTemplate;

    /**
     * Creates a {@code Dom} associating the {@code fillBlanksDoc} with the 
     * {@code orderedBindings} that are {@code Mark.IsNamed}.
     * 
     * NOTE: Only {@code MarkAction}s that are {@code Embed} are associated with 
     * the blanks in the document
     * <UL>
     *  <LI>{@code Add}
     *  <LI>{@code AddScriptResult}
     *  <LI>{@code ReplaceWithScriptResult}
     *  <LI>{@code IfAdd}
     *  <LI>{@code IfAddWithForm}
     *  <LI>{@code Replace}
     *  <LI>{@code ReplaceWithForm}
     * </UL>   
     * 
     * (Other {@code MarkActions}s like {@code Cut}, {@code CutComment} , 
     * {@code CutJavaDoc} contain information that is not included
     * in the tailored source. 
     * 
     * @param template {@code FillInTheBlanks.FillOrder} static text and 
     * blanks in the document
     * @param allMarksSequence the marks that occur within the document (in order)
     * @param allMarkLocations set bits indicate a {@code Mark} within the content
     */
    public FormTemplate( 
        FillInTheBlanks.FillTemplate template, 
        Mark[] allMarksSequence,
        BitSet allMarkLocations )
    {
        this.blankFillerMarksTemplate = template;
        this.marks = allMarksSequence;
        this.markIndicies = allMarkLocations;
        this.markTemplate =
            FillTemplate.of( blankFillerMarksTemplate.getStaticText(), markIndicies );

        List<BlankFiller> embeddedMarkSequence = new ArrayList<BlankFiller>();
        for( int i = 0; i < allMarksSequence.length; i++ )
        {
            if( ( allMarksSequence[ i ] instanceof BlankFiller ) )
            {
                embeddedMarkSequence.add( (BlankFiller)allMarksSequence[ i ] );
            }
        }
        this.blankFillerMarks = embeddedMarkSequence
            .toArray( new Mark.BlankFiller[ embeddedMarkSequence.size() ] );
    }

    
    public Mark[] getMarks()
    {
        return marks;
    }

    
    public BlankFiller[] getBlankFillers()
    {
        return blankFillerMarks;
    }

    /**
     * @see varcode.VarCodeMark#getBlanksCount()
     */    
    public int getBlanksCount()
    {
        return blankFillerMarksTemplate.getBlanksCount();
    }

    public Form[] getForms()
    {
        List<Form> theForms = new ArrayList<Form>();
        for( int i = 0; i < this.marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasForm )
            {
                HasForm rf = (HasForm)marks[ i ];

                theForms.add( rf.getForm() );
            }
        }
        return theForms.toArray( new Form[ 0 ] );
    }

    /**
     * @see varcode.VarCodeMark#getAllMarkIndexes()
     */    
    public BitSet getMarkIndicies()
    {
        return this.markIndicies;
    }

    /* (non-Javadoc)
     * @see varcode.VarCodeMark#getFillBlanks()
     */
    public FillTemplate getFillTemplate()
    {
        return blankFillerMarksTemplate;
    }

    /**
     * Gets the "Original" Text (including the {@code Mark}s).
     * @return A String representing the original text markup
     */
    public String getMarkup()
    {
        String[] markFills = new String[ this.markIndicies.cardinality() ];
        for( int i = 0; i < markFills.length; i++ )
        {
            markFills[ i ] = marks[ i ].getText();
        }
        return this.markTemplate.fill( (Object[])markFills );
    }

    public String toString()
    {
        return getMarkup() + System.lineSeparator() 
            + "/**{- "+ System.lineSeparator()
            + "  ForML Dom" + System.lineSeparator()
            + "    marks  : (" + marks.length + ")" + System.lineSeparator() 
            + "    blanks : (" + blankFillerMarks.length + ")" + System.lineSeparator()
            + "-}*/";
    }

    /**
     * is the Var varName a required Var
     * @param varName
     * @return true if the varName is required in any Mark of the Form 
     */
    public boolean isRequiredVar( String varName )
    {
    	 for( int i = 0; i < marks.length; i++ )
         {
    		 if( marks[ i ] instanceof IsNamed && marks[ i ] instanceof MayBeRequired )
    		 {
    			 if( ((MayBeRequired)marks[ i ] ).isRequired() 
    				&& ((IsNamed)marks[ i ] ).getVarName().equals( varName ) ) 
    			 {   
    				 //System.out.println(marks[ i ] +" IS REQURIED ");
    				 return true;
    			 }
    			 // a varName MAY appear in multiple Marks, so we need to check all Marks
    			 // for whether the VarName is Required
    		 }             
         }
    	 return false;
    }
    
    public void collectVarNames( Set<String>varNames, VarContext context )
    {
        for( int i = 0; i < marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasVars )
            {
            	Mark.HasVars hv = (Mark.HasVars)marks[ i ];                
                hv.collectVarNames( varNames, context );                                
            }
        }
    }
}