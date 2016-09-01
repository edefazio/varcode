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
package varcode.dom;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import varcode.context.VarContext;
import varcode.doc.FillInTheBlanks;
import varcode.doc.FillInTheBlanks.FillTemplate;
import varcode.form.Form;
import varcode.markup.MarkupTemplate;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.HasForm;
import varcode.markup.mark.Mark.HasVars;

/**
 * <A HREF="https://en.wikipedia.org/wiki/Document_Object_Model">Document Object Model</A>-
 * like model from the compiled (<CODE>BindML, CodeML</CODE>) Markup. It provides an API 
 * consisting of {@code Mark}s for manipulating the text of a Document.
 * <UL>
 *   <LI>BindML is a Markup Language for "Authoring" structured text 
 *   <CODE>"{+type+} {+name+}={+value+};"</CODE>
 *     
 *   <LI>CodeML is a Markup Language for "Tailoring" code 
 *       it  "hides" Marks within comments of source code of languages 
 *       (Java, Javascript, C, C++, C#, D, F, ...)
 *       ...(using /&#42; &#42;/) For example: 
 *       <PRE>"public class /&#42;{+className&#42;/_Clazz/&#42;+}&#42;/ {}";</PRE>
 *       NOTE:
 *       <UL>
 *       <LI>the Javac Compiler disregards the Mark-comments, and can compile and 
 *       create a class (that contains mark-comments)
 *       <LI>that the CodeMLCompiler will parse/understand the mark comments,
 *       while disregarding any code not contained in a mark. 
 *       (This allows the CodeMLCompiler/CodeMLParser to be used to parse 
 *       code in many languages while also does not interfere with the "target"
 *       language compiler ( Javac, GCC, etc.) 
 * </UL>  
 * <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A>.
 * <BR><BR>
 * 
 * Contains the static text (Code) along with {@code Mark}s 
 * to Specialize / Tailor new source code. 
 * <PRE><CODE>
 * Dom dom = BindML.compile(
 *     "public class {+className+} {}" );
 * 
 * String tailored = Tailor.code( dom, 
 *     VarContext.of( 
 *         "className", "MyClass" ) ); // = "public class MyClass {}";
 *           
 * Dom dom = CodeML.compile( 
 *     "public class /*{+className* /className/*}* /{ }" ); 
 * 
 * String tailored = Tailor.code( dom, 
 *     VarContext.of( 
 *         "className", "MyClass" ) ); // = "public class MyClass {}";
 * </PRE>
 */
public class Dom 
    implements MarkupTemplate
{	

	/** 
	 * Creates and returns a NEW Dom that merges all of the  the {@code tailMarks} 
	 * (after all other Marks on the Dom)...
	 * 
	 * @param sourceDom the originalDom
	 * @param prefixMarks Marks that are added to the beginning of the Document 
	 * (BEFORE ALL other Marks)
	 * @return the Dom
	 
	public static Dom merge( Dom...domsInOrder )
	{
		List<Mark>allMarks = new ArrayList<Mark>();
		
		for(int i = 0; i < domsInOrder.length; i++ )
		{
			
		}
	}
	*/
	
	/** ALL {@code Mark}s on the document */
	private final Mark[] marks;  
	
	/** 
	 * Static Text and "blanks" where ALL {@code MarkActions}s occur
	 * (Useful if we want to "derive" the original markup text  
	 * {@code MarkAction}s) 
	 */ 	 
	private final FillTemplate marksTemplate;
     
	/**
	 * All Text and {@code MarkAction}s that Fill Blanks (with text)
	 * as they appear in the var source to be populated when tailoring 
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
	private final Mark.BlankFiller[] blankFillMarks;
	
	/** static text and "blanks" corresponding to the {@code FillAction}s */
	private final FillTemplate blankFillMarksTemplate;
	
	/** static Data bindings collected when parsing the markup */
	private final VarContext domContext;
	
	/**
	 * Creates a {@code Dom} containing {@code Mark}s and text 
	 * 
	 * <UL>
	 *  <LI>{@code AddVar}
	 *  <LI>{@code AddScriptResult}
     *  <LI>{@code ReplaceWithScriptResult}
     *  <LI>{@code IfAdd}
     *  <LI>{@code IfAddWithForm}
	 *  <LI>{@code Replace}
	 *  <LI>{@code ReplaceWithForm}
	 * </UL>   
	 * 
	 * (Other {@code Mark}s like {@code Cut}, {@code CutComment} , 
	 * {@code CutJavaDoc} contain information that is not included
	 * in the tailored source. 
	 * 
	 * @param fillTemplate {@code FillInTheBlanks.FillOrder} static text and 
	 * blanks in the document
	 * @param marks the marks that occur within the document (in order)
	 * @param allMarkLocations the set bits marking the character index of Marks within the text
	 * @param staticBindings statically defined vars, forms, scripts for the Markup
	 * @param metadata metadata about the 
	 */
	public Dom(
	    FillInTheBlanks.FillTemplate fillTemplate, 
	    Mark[] marks,
	    BitSet allMarkLocations, 
	    VarContext domContext )
	{
		this.blankFillMarksTemplate = fillTemplate;
		this.marks = marks;
		//this.staticBindings = staticBindings;
		//this.metadataBindings = metadataBindings;
		this.domContext = domContext;
		this.marksTemplate = 
		    FillTemplate.of( 
		    	blankFillMarksTemplate.getStaticText(), 
		        allMarkLocations );
		             
		List<BlankFiller> blankFillMarksList = new ArrayList<BlankFiller>();
		for( int i = 0; i < marks.length; i++ )
		{
			if( ( marks[ i ] instanceof BlankFiller ) )
			{
				blankFillMarksList.add( (BlankFiller)marks[ i ] );
			}
		}
		this.blankFillMarks = blankFillMarksList.toArray( 
		    new Mark.BlankFiller[ blankFillMarksList.size() ] );
	}
	
	public Dom( 
		Mark[] marks,
		FillInTheBlanks.FillTemplate marksTemplate,
		Mark.BlankFiller[] blankFillMarks,
		FillInTheBlanks.FillTemplate blankFillMarksTemplate, 
		VarContext domContext )
	{
		this.blankFillMarksTemplate = blankFillMarksTemplate;
		this.blankFillMarks = blankFillMarks;
		this.marksTemplate = marksTemplate;
		this.marks = marks;
		this.domContext = domContext;
		//this.staticBindings = staticBindings;
		//this.metadataBindings = metadataBindings;
	}
	
	public Mark[] getMarks()
	{
	    return marks;
	}
	
	public BlankFiller[] getBlankFillers()
	{
	    return blankFillMarks;
	}
	
	public int getBlanksCount()
	{	    
	    return blankFillMarksTemplate.getBlanksCount();
	}
	
	/**
	 * Contains any statically defined values for the Markup:
	 * <UL>
	 *   <LI>/ *{##name:value##}* /
	 *   <LI>/ *{{##dateFormat:{+year+}-{+month+}-{+day+}##}}* /
	 * </UL>     
	 * @return
	 
	public VarBindings getStaticBindings()
	{
		return this.staticBindings;
	}

	public VarBindings getMetadataBindings()
	{
		return this.metadataBindings;
	}
	*/
	
	public VarContext getDomContext()
	{
		return this.domContext;
	}
	
	public Form[] getForms()
	{
	    List<Form> theForms = new ArrayList<Form>(); 
        for( int i = 0; i < this.marks.length; i++ )
        {
            if( marks[ i ] instanceof Mark.HasForm)
            {
                HasForm rf = (HasForm)marks[ i ];
                
                theForms.add( rf.getForm() );
            }
        }
        return theForms.toArray( new Form[ 0 ] );
	}
	
	/* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getAllMarkIndexes()
     */
	public BitSet getMarkIndicies()
	{
	    return this.marksTemplate.getBlanks();
	}
	
	public Set<String>getAllVarNames( VarContext context )
	{
		Set<String>varNames = new HashSet<String>();
		
		for( int i = 0; i < marks.length; i++ )
		{
		    if( marks[ i ] instanceof Mark.HasVars )
		    {
		        HasVars hv = (HasVars)marks[ i ];
		        hv.collectVarNames( varNames, context );
		    }
		}
		return varNames;
	}
	    
	/* (non-Javadoc)
     * @see io.varcode.VarCodeMark#getFillBlanks()
     */
	public FillTemplate getFillTemplate()
	{
	    return blankFillMarksTemplate;
	}
	
	public FillTemplate getAllMarksTemplate()
	{
		return this.marksTemplate;
	}
	
    /**
     * Gets the "Original" Markup Text (including the {@code Mark}s).
     * That was parsed to create the {@code Dom}
     * 
     * @return A Markup
     */
    public String getMarkupText()
    {
         String[] markFills = new String[ getMarkIndicies().cardinality() ];
         for( int i = 0; i < markFills.length; i++ )
         {
             markFills[ i ] = marks[ i ].getText();
         }
         return this.marksTemplate.fill( (Object[])markFills );         
    }
    
	public String toString()
	{
	    return getMarkupText() + System.lineSeparator()
	      + "/*{- SUMMARY " + System.lineSeparator() 
	      + "  MARKS  : (" + marks.length + ")" + System.lineSeparator()          
          + "  BLANKS : (" + blankFillMarks.length+")" + System.lineSeparator()
	      + "-}*/";
	}
}
