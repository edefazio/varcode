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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import varcode.markup.FillInTheBlanks;
import varcode.markup.FillInTheBlanks.BlankBinding;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.HasScript;
import varcode.markup.mark.Mark.HasVar;
import varcode.markup.mark.Mark.HasVars;
import varcode.markup.mark.Mark.MayBeRequired;
import varcode.markup.mark.Mark.Bind;

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
{
    /** ALL {@code Mark}s on the dom */
    private final Mark[] marks;

    /** text and "blanks" where ALL {@code MarkAction}s occur */
    private final BlankBinding markTemplate;

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
    private final Mark.Bind[] bindMarks;

    /** text and "blanks" where bindMarks occur and context params are bound */
    private final FillInTheBlanks.BlankBinding bindMarksBinding;

    /**
     * Creates a {@code Template} associating the {@code fillBlanksDoc} with the 
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
     * @param blankBinding {@code FillInTheBlanks.FillOrder} static text and 
     * blanks in the document
     * @param allMarks the marks that occur within the document (in order)
     * @param allMarkIndicies set bits indicate a {@code Mark} within the content
     */
    public FormTemplate( 
        FillInTheBlanks.BlankBinding blankBinding, 
        Mark[] allMarks,
        BitSet allMarkIndicies )
    {
        this.bindMarksBinding = blankBinding;
        this.marks = allMarks;
        this.markIndicies = allMarkIndicies;
        this.markTemplate =
            BlankBinding.of( bindMarksBinding.getStaticText(), markIndicies );

        List<Bind> embeddedMarkSequence = new ArrayList<Bind>();
        for( int i = 0; i < allMarks.length; i++ )
        {
            if( ( allMarks[ i ] instanceof Bind ) )
            {
                embeddedMarkSequence.add((Bind)allMarks[ i ] );
            }
        }
        this.bindMarks = embeddedMarkSequence
            .toArray(new Mark.Bind[ embeddedMarkSequence.size() ] );
    }
    
    public Mark[] getMarks()
    {
        return marks;
    }

    
    public Bind[] getBindMarks()
    {
        return bindMarks;
    }
    
    public int getBlanksCount()
    {
        return bindMarksBinding.getBlanksCount();
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

    public BitSet getMarkIndicies()
    {
        return this.markIndicies;
    }

    /* (non-Javadoc)
     * @see varcode.VarCodeMark#getFillBlanks()
     */
    public BlankBinding getBlankBinding()
    {
        return bindMarksBinding;
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
        return this.markTemplate.bind( (Object[])markFills );
    }

    @Override
    public String toString()
    {
        return getMarkup() + "\r\n"
            + "/**{- "+ "\r\n"
            + "  ForML Dom" + "\r\n"
            + "    marks  : (" + marks.length + ")" + "\r\n"
            + "    blanks : (" + bindMarks.length + ")" + "\r\n"
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
            if( marks[ i ] instanceof HasVar && marks[ i ] instanceof MayBeRequired )
            {
    		if( ((MayBeRequired)marks[ i ] ).isRequired() 
                    && ((HasVar)marks[ i ] ).getVarName().equals( varName ) ) 
    		{   
                    
                    return true;
    		}
    		// a varName MAY appear in multiple Marks, so we need to check all Marks
                // for whether the VarName is Required
            }             
        }
    	return false;
    }
    
    private static List<String> parseVars( String str )
    {
        List<String> vList = new ArrayList<String>();
        if( str == null )
        {
            return vList;
        }
        String[] vars = str.split( "," );
        
        for( int i = 0; i < vList.size(); i++ )
        {
            vList.add( vars[ i ].trim() );
        }
        return vList;
    }
    
    public Set<String> getVarNames()
    {
        Set<String>varNames = new HashSet <String>();
        for( int i = 0; i < marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasVar )
            {
            	HasVar hv = (HasVar)marks[ i ];
                varNames.add( hv.getVarName() );
            }
            if( marks[ i ] instanceof HasVars )
            {
                Mark.HasVars hvs = (HasVars)marks[ i ];
                varNames.addAll( hvs.getVarNames() );
            }
            if( marks[ i ] instanceof HasScript )
            {
                varNames.addAll( 
                    parseVars( ((HasScript)marks[ i ]).getScriptInput() ) );
            }
        }
        return varNames;
    }    
}
    
